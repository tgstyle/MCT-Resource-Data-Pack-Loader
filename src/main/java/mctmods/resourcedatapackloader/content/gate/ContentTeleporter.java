package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortal;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentTeleporter implements ITeleporter {
    private static final int SEARCH_LOW = 4;
    private static final String PORTALS = "rdplPortals";
    private final PortalDef def;
    private final IBlockState portalState;

    public ContentTeleporter(PortalDef def, IBlockState portalState) {
        this.def = def;
        this.portalState = portalState;
    }

    @Override public void placeEntity(World world, Entity entity, float rotationYaw) {
        int dimension = world.provider.getDimension();
        BlockPos mapped = scale(world, new BlockPos(entity.posX, entity.posY, entity.posZ));
        BlockPos linked = remembered(entity, dimension);
        if (linked != null && !(world.getBlockState(linked).getBlock() instanceof ContentBlockPortal)) {
            forget(entity, dimension);
            linked = null;
        }
        if (linked == null) { linked = PortalStorage.nearest(world, mapped); }
        BlockPos portalPos = linked != null ? linked : landing(world, mapped);
        if (!(world.getBlockState(portalPos).getBlock() instanceof ContentBlockPortal)) {
            if (def.platform) { support(world, portalPos); }
            world.setBlockState(portalPos, arriving(), 2);
        }
        clearAbove(world, portalPos);
        PortalStorage.add(world, portalPos, entity.getUniqueID());
        remember(entity, dimension, portalPos);
        entity.setLocationAndAngles(portalPos.getX() + 0.5D, portalPos.getY() + 1.0D, portalPos.getZ() + 0.5D, rotationYaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }

    public static void remember(Entity entity, int dimension, BlockPos pos) {
        NBTTagCompound persisted = entity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        NBTTagCompound portals = persisted.getCompoundTag(PORTALS);
        portals.setLong(String.valueOf(dimension), pos.toLong());
        persisted.setTag(PORTALS, portals);
        entity.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    private static void forget(Entity entity, int dimension) {
        NBTTagCompound persisted = entity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        NBTTagCompound portals = persisted.getCompoundTag(PORTALS);
        portals.removeTag(String.valueOf(dimension));
        persisted.setTag(PORTALS, portals);
        entity.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    @Nullable private BlockPos remembered(Entity entity, int dimension) {
        NBTTagCompound portals = entity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(PORTALS);
        String key = String.valueOf(dimension);
        if (!portals.hasKey(key)) { return null; }
        return BlockPos.fromLong(portals.getLong(key));
    }

    private BlockPos scale(World world, BlockPos from) {
        double factor = world.provider.getMovementFactor();
        if (factor == 1.0D) { return from; }
        return new BlockPos(from.getX() * factor, from.getY(), from.getZ() * factor);
    }

    private BlockPos landing(World world, BlockPos from) {
        BlockPos ground = world.getTopSolidOrLiquidBlock(new BlockPos(from.getX(), 0, from.getZ()));
        if (ground.getY() > 0) { return ground; }
        return new BlockPos(from.getX(), clamp(world, from.getY()), from.getZ());
    }

    private static int clamp(World world, int y) { return Math.max(SEARCH_LOW, Math.min(world.getHeight() - 4, y)); }

    private IBlockState arriving() { return portalState; }

    private void support(World world, BlockPos portalPos) {
        Block floor = block(def.platformBlock);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos at = portalPos.add(dx, -1, dz);
                if (!world.isBlockLoaded(at) || !world.isAirBlock(at)) { continue; }
                world.setBlockState(at, floor.getDefaultState(), 2);
            }
        }
    }

    private void clearAbove(World world, BlockPos portalPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 1; dy <= 2; dy++) { clear(world, portalPos.add(dx, dy, dz)); }
            }
        }
    }

    private void clear(World world, BlockPos pos) {
        IBlockState found = world.getBlockState(pos);
        if (found.getBlock() == Blocks.AIR) { return; }
        if (found.getBlock() instanceof ContentBlockPortal) { return; }
        world.setBlockToAir(pos);
    }

    private static Block block(String name) {
        if (name.isEmpty()) { return Blocks.STONE; }
        ResourceLocation key = new ResourceLocation(name);
        Block found = ForgeRegistries.BLOCKS.containsKey(key) ? ForgeRegistries.BLOCKS.getValue(key) : null;
        if (found != null) { return found; }
        ContentLog.LOGGER.error("Portal platform block {} is not registered, using stone", name);
        return Blocks.STONE;
    }
}
