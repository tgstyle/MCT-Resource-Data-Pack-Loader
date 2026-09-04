package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortal;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.content.portal.ContentPortals;
import mctmods.resourcedatapackloader.content.portal.PortalFit;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registries;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentTeleporter implements ITeleporter {
    private static final int SEARCH_LOW = 4;
    private static final String PORTALS = "rdplPortals";
    private final PortalDef def;
    private final IBlockState portalState;
    @Nullable private final PortalFit fit;

    public ContentTeleporter(PortalDef def, IBlockState portalState, @Nullable PortalFit fit) {
        this.def = def;
        this.portalState = portalState;
        this.fit = fit;
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
        BlockPos portalPos;
        if (linked != null && world.getBlockState(linked).getBlock() instanceof ContentBlockPortal) { portalPos = linked; }
        else {
            portalPos = landing(world, mapped);
            if (!rebuild(world, portalPos)) {
                if (def.platform) { support(world, portalPos); }
                world.setBlockState(portalPos, arriving(), 2);
                clearAbove(world, portalPos);
            }
        }
        PortalStorage.add(world, portalPos, entity.getUniqueID());
        remember(entity, dimension, portalPos);
        BlockPos step = stepOut(world, portalPos);
        entity.setLocationAndAngles(step.getX() + 0.5D, step.getY(), step.getZ() + 0.5D, rotationYaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }

    private BlockPos stepOut(World world, BlockPos portalPos) {
        IBlockState held = world.getBlockState(portalPos);
        if (!(held.getBlock() instanceof ContentBlockPortal)) { return portalPos.up(); }
        int meta = held.getBlock().getMetaFromState(held);
        if (meta == ContentPortals.FLAT) { return portalPos.up(); }
        EnumFacing[] sides = meta == ContentPortals.ALONG_X
                ? new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH }
                : new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST };
        for (EnumFacing side : sides) {
            BlockPos beside = portalPos.offset(side);
            if (!world.isBlockLoaded(beside)) { continue; }
            if (!world.isAirBlock(beside) || !world.isAirBlock(beside.up())) { continue; }
            if (!world.getBlockState(beside.down()).getMaterial().isSolid()) { continue; }
            return beside;
        }
        return portalPos;
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

    private static int clamp(World world, int y) { return MathHelper.clamp(y, SEARCH_LOW, world.getHeight() - 4); }

    private IBlockState arriving() { return portalState; }

    private boolean rebuild(World world, BlockPos landing) {
        if (fit == null || fit.holes.isEmpty()) { return false; }
        BlockPos anchor = lowest(fit.holes);
        List<BlockPos> holes = new ArrayList<>();
        Map<BlockPos, IBlockState> edges = new LinkedHashMap<>();
        for (BlockPos hole : fit.holes) { holes.add(shift(hole, anchor, landing)); }
        for (Map.Entry<BlockPos, IBlockState> edge : fit.edge.entrySet()) { edges.put(shift(edge.getKey(), anchor, landing), edge.getValue()); }
        for (BlockPos at : holes) {
            if (!world.isBlockLoaded(at)) { return false; }
        }
        for (BlockPos at : edges.keySet()) {
            if (!world.isBlockLoaded(at)) { return false; }
        }
        if (def.platform) { footing(world, edges.keySet(), holes); }
        for (BlockPos at : holes) { world.setBlockToAir(at); }
        for (Map.Entry<BlockPos, IBlockState> edge : edges.entrySet()) { world.setBlockState(edge.getKey(), edge.getValue(), 2); }
        for (BlockPos at : holes) { world.setBlockState(at, arriving(), 2); }
        ContentLog.LOGGER.debug("Built a frame of {} on arrival at {}, {} edge block(s) around {} of portal", fit.frame.name, landing, edges.size(), holes.size());
        return true;
    }

    private static BlockPos shift(BlockPos from, BlockPos anchor, BlockPos landing) {
        return landing.add(from.getX() - anchor.getX(), from.getY() - anchor.getY(), from.getZ() - anchor.getZ());
    }

    private static BlockPos lowest(List<BlockPos> holes) {
        BlockPos found = holes.get(0);
        for (BlockPos hole : holes) {
            if (hole.getY() < found.getY()) { found = hole; }
        }
        return found;
    }

    private void footing(World world, Iterable<BlockPos> edges, Iterable<BlockPos> holes) {
        Block floor = block(def.platformBlock);
        int bed = Integer.MAX_VALUE;
        Set<BlockPos> columns = new LinkedHashSet<>();
        for (BlockPos at : edges) {
            bed = Math.min(bed, at.getY());
            columns.add(new BlockPos(at.getX(), 0, at.getZ()));
        }
        for (BlockPos at : holes) {
            bed = Math.min(bed, at.getY());
            columns.add(new BlockPos(at.getX(), 0, at.getZ()));
        }
        if (bed == Integer.MAX_VALUE) { return; }
        for (BlockPos column : columns) {
            BlockPos at = new BlockPos(column.getX(), bed - 1, column.getZ());
            if (!world.isBlockLoaded(at) || world.getBlockState(at).getMaterial().isSolid()) { continue; }
            world.setBlockState(at, floor.getDefaultState(), 2);
        }
    }

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
        Block found = Registries.find(ForgeRegistries.BLOCKS, key);
        if (found != null) { return found; }
        ContentLog.LOGGER.error("Portal platform block {} is not registered, using stone", name);
        return Blocks.STONE;
    }
}
