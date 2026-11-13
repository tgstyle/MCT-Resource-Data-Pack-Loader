package mctmods.resourcedatapackloader.common.blocks.portal;

import mctmods.resourcedatapackloader.common.blocks.BlockVariants;
import mctmods.resourcedatapackloader.common.blocks.ContentBlock;
import mctmods.resourcedatapackloader.common.gates.ContentGates;
import mctmods.resourcedatapackloader.common.gates.ContentTeleporter;
import mctmods.resourcedatapackloader.common.gates.PortalStorage;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;
import mctmods.resourcedatapackloader.common.portals.ContentPortals;
import mctmods.resourcedatapackloader.common.portals.PortalFit;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@SuppressWarnings("deprecation") public class ContentPortalBlock extends ContentBlock {
    public static final int MAX_VARIANTS = 16;
    private static final Map<UUID, Long> RECENT = new HashMap<>();
    private final PortalDef portal;

    public static ContentPortalBlock create(BlockDef def) {
        if (def.portal == null) {
            ContentLog.LOGGER.error("Block {} is a portal but has no 'portal' section, so it has nowhere to lead", def.registryName);
            return null;
        }
        return BlockVariants.construct(def, ContentPortalBlock::new);
    }

    protected ContentPortalBlock(BlockDef def) {
        super(def);
        this.portal = def.portal;
    }

    @Override public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (world.isRemote) { return; }
        PortalStorage.add(world, pos, null);
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) { PortalStorage.remove(world, pos); }
        super.breakBlock(world, pos, state);
    }

    @Override public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        if (world.isRemote) { return; }
        PortalStorage.add(world, pos, placer instanceof EntityPlayer ? placer.getUniqueID() : null);
    }

    @Override public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote && !mayBreak(world, pos, state, player)) {
            player.sendStatusMessage(new TextComponentTranslation("rdpl.portal.owned"), true);
            return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override public boolean canDropFromExplosion(@Nonnull Explosion explosion) { return !portal.owned; }

    @Override public void onBlockExploded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
        if (!world.isRemote && owned(world, pos)) { return; }
        super.onBlockExploded(world, pos, explosion);
    }

    @Override public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity exploder, @Nonnull Explosion explosion) {
        if (owned(world, pos)) { return Float.MAX_VALUE; }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    private boolean owned(World world, BlockPos pos) { return portalFor(world.getBlockState(pos)).owned && PortalStorage.owner(world, pos) != null; }

    private boolean mayBreak(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!portalFor(state).owned || player.capabilities.isCreativeMode) { return true; }
        UUID owner = PortalStorage.owner(world, pos);
        return owner == null || owner.equals(player.getUniqueID());
    }

    private PortalDef portalFor(IBlockState state) {
        BlockVariant variant = getDef().at(getMetaFromState(state));
        return variant.portal == null ? portal : variant.portal;
    }

    @Override public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
        if (world.isRemote || !(entity instanceof EntityPlayer)) { return; }
        PortalDef held = portalFor(state);
        if (!held.walkIn) { return; }
        travel(world, (EntityPlayer) entity, held, state, pos);
    }

    @Nullable @Override public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return portalFor(state).walkIn ? NULL_AABB : super.getCollisionBoundingBox(state, world, pos); }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos from) {
        super.neighborChanged(state, world, pos, block, from);
        if (world.isRemote) { return; }
        ContentPortals.Binding binding = ContentPortals.forBlock(state.getBlock());
        if (binding == null || ContentPortals.standing(world, pos, binding)) { return; }
        ContentLog.LOGGER.debug("The frame around the portal at {} no longer holds, so it goes out", pos);
        world.setBlockToAir(pos);
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) { return true; }
        travel(world, player, portalFor(state), state, pos);
        return true;
    }

    private void travel(World world, EntityPlayer player, PortalDef shown, IBlockState state, BlockPos pos) {
        if (!(player instanceof EntityPlayerMP)) { return; }
        if (recently(player, shown)) { return; }
        ContentPortals.Binding binding = ContentPortals.forBlock(state.getBlock());
        PortalFit fit = binding == null ? null : ContentPortals.fitAt(world, pos, binding);
        if (!shown.mounted && player.isRiding()) {
            player.sendStatusMessage(new TextComponentTranslation("rdpl.portal.mounted"), true);
            return;
        }
        if (shown.useRange > 0 && !near(player, pos, state, fit, shown.useRange)) {
            player.sendStatusMessage(new TextComponentTranslation("rdpl.portal.far"), true);
            return;
        }
        GateDef gate = shown.gate.isEmpty() ? null : ContentGates.find(shown.gate);
        if (gate != null && !ContentGates.admits(player, gate)) {
            ContentGates.refuse(player, gate);
            return;
        }
        int target = player.dimension == shown.dimension ? ContentTeleporter.target(player, shown) : shown.dimension;
        if (target == player.dimension) { return; }
        sound(world, player, shown);
        RECENT.put(player.getUniqueID(), world.getTotalWorldTime());
        ContentTeleporter.remember(player, player.dimension, pos);
        if (player.dimension != shown.dimension) { ContentTeleporter.cameFrom(player, shown.dimension, player.dimension); }
        player.changeDimension(target, new ContentTeleporter(shown, state, binding == null || binding.portal.noReturn() ? null : fit));
    }

    private boolean near(EntityPlayer player, BlockPos pos, IBlockState state, @Nullable PortalFit fit, int range) {
        List<BlockPos> holes = fit == null ? Collections.singletonList(pos) : fit.holes;
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (BlockPos hole : holes) {
            low = Math.min(low, hole.getY());
            high = Math.max(high, hole.getY());
        }
        if (state.getBlock().getMetaFromState(state) == ContentPortals.FLAT) { high = low + 1; }
        int feet = player.getPosition().getY();
        if (feet < low || feet > high) { return false; }
        double reach = (double) range * range;
        for (BlockPos hole : holes) {
            double dx = player.posX - (hole.getX() + 0.5D);
            double dz = player.posZ - (hole.getZ() + 0.5D);
            if (dx * dx + dz * dz <= reach) { return true; }
        }
        return false;
    }

    public static void forget(UUID player) { RECENT.remove(player); }

    private boolean recently(EntityPlayer player, PortalDef shown) {
        Long last = RECENT.get(player.getUniqueID());
        if (last == null) { return false; }
        long since = player.world.getTotalWorldTime() - last;
        return since >= 0L && since < shown.cooldown;
    }

    private void sound(World world, EntityPlayer player, PortalDef shown) {
        if (shown.sound.isEmpty()) { return; }
        SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(shown.sound));
        if (event == null) { return; }
        world.playSound(null, player.getPosition(), event, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
