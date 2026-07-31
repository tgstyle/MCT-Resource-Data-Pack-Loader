package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.gate.ContentTeleporter;
import mctmods.resourcedatapackloader.content.gate.PortalStorage;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ContentBlockPortal extends ContentBlock {
    public static final int MAX_VARIANTS = 16;
    private static final Map<UUID, Long> RECENT = new HashMap<>();
    private final PortalDef portal;

    public static ContentBlockPortal create(BlockDef def) {
        if (def.portal == null) {
            ContentLog.LOGGER.error("Block {} is a portal but has no 'portal' section, so it has nowhere to lead", def.registryName);
            return null;
        }

        beginConstruction(def);
        try { return new ContentBlockPortal(def); }
        finally { endConstruction(); }
    }

    protected ContentBlockPortal(BlockDef def) {
        super(def);
        this.portal = def.portal;
    }

    @Override public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (world.isRemote) { return; }

        PortalStorage.add(world, pos, null);
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) {
            PortalStorage.remove(world, pos);
        }

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

    private boolean mayBreak(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!portalFor(state).owned || player.capabilities.isCreativeMode) { return true; }

        UUID owner = PortalStorage.owner(world, pos);
        return owner == null || owner.equals(player.getUniqueID());
    }

    private PortalDef portalFor(IBlockState state) {
        BlockVariant variant = getDef().at(getMetaFromState(state));
        return variant.portal == null ? portal : variant.portal;
    }

    @Override public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) { return true; }

        travel(world, player, portalFor(state), state, pos);
        return true;
    }

    private void travel(World world, EntityPlayer player, PortalDef portal, IBlockState state, BlockPos pos) {
        if (!(player instanceof EntityPlayerMP)) { return; }
        if (recently(player, portal)) { return; }

        GateDef gate = portal.gate.isEmpty() ? null : ContentGates.find(portal.gate);
        if (gate != null && !ContentGates.unlocked(player, gate)) {
            ContentGates.refuse(player, gate);
            return;
        }

        int target = player.dimension == portal.dimension ? portal.returnDimension : portal.dimension;
        if (target == player.dimension) { return; }

        sound(world, player, portal);
        RECENT.put(player.getUniqueID(), world.getTotalWorldTime());
        ContentTeleporter.remember(player, player.dimension, pos);
        player.changeDimension(target, new ContentTeleporter(portal, state));
    }

    private boolean recently(EntityPlayer player, PortalDef portal) {
        Long last = RECENT.get(player.getUniqueID());
        if (last == null) { return false; }

        long since = player.world.getTotalWorldTime() - last;
        return since >= 0L && since < portal.cooldown;
    }

    private void sound(World world, EntityPlayer player, PortalDef portal) {
        if (portal.sound.isEmpty()) { return; }

        SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(portal.sound));
        if (event == null) { return; }

        world.playSound(null, player.getPosition(), event, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

}
