package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.block.ContentPortalBlock;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.content.portal.PortalFit;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ContentTeleporter implements ITeleporter {
    private static final int SEARCH_LOW = 4;
    private static final String PORTALS = "rdplPortals";
    private final PortalDef def;
    private final BlockState portalState;
    @Nullable private final PortalFit fit;

    private ContentTeleporter(PortalDef def, BlockState portalState, @Nullable PortalFit fit) {
        this.def = def;
        this.portalState = portalState;
        this.fit = fit;
    }

    public static void travel(ServerPlayer player, PortalDef portal, BlockState state, BlockPos pos, @Nullable PortalFit fit) {
        ResourceLocation here = player.level().dimension().location();
        ResourceLocation targetId = here.equals(portal.dimension()) ? portal.returnDimension() : portal.dimension();
        if (targetId.equals(here)) { return; }
        ServerLevel target = player.server.getLevel(ResourceKey.create(Registries.DIMENSION, targetId));
        if (target == null) {
            ContentLog.LOGGER.error("A portal at {} leads to {}, which is not a loaded dimension, so nobody goes anywhere", pos, targetId);
            return;
        }
        remember(player, here, pos);
        player.changeDimension(target, new ContentTeleporter(portal, state, fit));
    }

    @Override public PortalInfo getPortalInfo(Entity entity, ServerLevel destination, Function<ServerLevel, PortalInfo> fallback) {
        BlockPos step = arrive(destination, entity);
        return new PortalInfo(new Vec3(step.getX() + 0.5D, step.getY(), step.getZ() + 0.5D), Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }

    @Override public boolean playTeleportSound(ServerPlayer player, ServerLevel source, ServerLevel destination) { return false; }

    private BlockPos arrive(ServerLevel level, Entity entity) {
        ResourceLocation dimension = level.dimension().location();
        BlockPos mapped = scale(entity, level);
        BlockPos linked = remembered(entity, dimension);
        if (linked != null && !(level.getBlockState(linked).getBlock() instanceof ContentPortalBlock)) {
            forget(entity, dimension);
            linked = null;
        }
        if (linked == null) { linked = PortalStorage.nearest(level, mapped); }
        BlockPos portalPos;
        if (linked != null && level.getBlockState(linked).getBlock() instanceof ContentPortalBlock) { portalPos = linked; }
        else {
            portalPos = landing(level, mapped);
            if (!rebuild(level, portalPos)) {
                if (def.platform()) { support(level, portalPos); }
                level.setBlock(portalPos, portalState, 2);
                clearAbove(level, portalPos);
            }
        }
        PortalStorage.add(level, portalPos, entity.getUUID());
        remember(entity, dimension, portalPos);
        return stepOut(level, portalPos);
    }

    private static BlockPos stepOut(ServerLevel level, BlockPos portalPos) {
        BlockState held = level.getBlockState(portalPos);
        if (!(held.getBlock() instanceof ContentPortalBlock)) { return portalPos.above(); }
        Direction.Axis axis = held.getValue(ContentPortalBlock.AXIS);
        if (axis == Direction.Axis.Y) { return portalPos.above(); }
        Direction[] sides = axis == Direction.Axis.X ? new Direction[] { Direction.NORTH, Direction.SOUTH } : new Direction[] { Direction.WEST, Direction.EAST };
        for (Direction side : sides) {
            BlockPos beside = portalPos.relative(side);
            if (!level.isLoaded(beside)) { continue; }
            if (!level.isEmptyBlock(beside) || !level.isEmptyBlock(beside.above())) { continue; }
            if (!level.getBlockState(beside.below()).isFaceSturdy(level, beside.below(), Direction.UP)) { continue; }
            return beside;
        }
        return portalPos;
    }

    public static void remember(Entity entity, ResourceLocation dimension, BlockPos pos) {
        CompoundTag portals = entity.getPersistentData().getCompound(PORTALS);
        portals.putLong(dimension.toString(), pos.asLong());
        entity.getPersistentData().put(PORTALS, portals);
    }

    private static void forget(Entity entity, ResourceLocation dimension) {
        CompoundTag portals = entity.getPersistentData().getCompound(PORTALS);
        portals.remove(dimension.toString());
        entity.getPersistentData().put(PORTALS, portals);
    }

    @Nullable private static BlockPos remembered(Entity entity, ResourceLocation dimension) {
        CompoundTag portals = entity.getPersistentData().getCompound(PORTALS);
        String key = dimension.toString();
        return portals.contains(key) ? BlockPos.of(portals.getLong(key)) : null;
    }

    private static BlockPos scale(Entity entity, ServerLevel destination) {
        double factor = DimensionType.getTeleportationScale(entity.level().dimensionType(), destination.dimensionType());
        return BlockPos.containing(entity.getX() * factor, entity.getY(), entity.getZ() * factor);
    }

    private static BlockPos landing(ServerLevel level, BlockPos from) {
        BlockPos ground = ContentDimensions.landing(level, new BlockPos(from.getX(), 0, from.getZ()), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        if (ground.getY() > level.getMinBuildHeight() + 1) { return ground; }
        return new BlockPos(from.getX(), Mth.clamp(from.getY(), level.getMinBuildHeight() + SEARCH_LOW, level.getMaxBuildHeight() - 4), from.getZ());
    }

    private boolean rebuild(ServerLevel level, BlockPos landing) {
        if (fit == null || fit.holes().isEmpty()) { return false; }
        BlockPos anchor = lowest(fit.holes());
        List<BlockPos> holes = new ArrayList<>();
        Map<BlockPos, BlockState> edges = new LinkedHashMap<>();
        for (BlockPos hole : fit.holes()) { holes.add(shift(hole, anchor, landing)); }
        for (Map.Entry<BlockPos, BlockState> edge : fit.edge().entrySet()) { edges.put(shift(edge.getKey(), anchor, landing), edge.getValue()); }
        for (BlockPos at : holes) {
            if (!level.isLoaded(at)) { return false; }
        }
        for (BlockPos at : edges.keySet()) {
            if (!level.isLoaded(at)) { return false; }
        }
        if (def.platform()) { footing(level, edges.keySet(), holes); }
        for (BlockPos at : holes) { level.removeBlock(at, false); }
        for (Map.Entry<BlockPos, BlockState> edge : edges.entrySet()) { level.setBlock(edge.getKey(), edge.getValue(), 2); }
        for (BlockPos at : holes) { level.setBlock(at, portalState, 2); }
        ContentLog.LOGGER.debug("Built a frame of {} on arrival at {}, {} edge block(s) around {} of portal", fit.frame().name(), landing, edges.size(), holes.size());
        return true;
    }

    private static BlockPos shift(BlockPos from, BlockPos anchor, BlockPos landing) { return landing.offset(from.getX() - anchor.getX(), from.getY() - anchor.getY(), from.getZ() - anchor.getZ()); }

    private static BlockPos lowest(List<BlockPos> holes) {
        BlockPos found = holes.get(0);
        for (BlockPos hole : holes) {
            if (hole.getY() < found.getY()) { found = hole; }
        }
        return found;
    }

    private void footing(ServerLevel level, Iterable<BlockPos> edges, Iterable<BlockPos> holes) {
        Block floor = block(def.platformBlock());
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
            if (!level.isLoaded(at) || level.getBlockState(at).isFaceSturdy(level, at, Direction.UP)) { continue; }
            level.setBlock(at, floor.defaultBlockState(), 2);
        }
    }

    private void support(ServerLevel level, BlockPos portalPos) {
        Block floor = block(def.platformBlock());
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos at = portalPos.offset(dx, -1, dz);
                if (!level.isLoaded(at) || !level.isEmptyBlock(at)) { continue; }
                level.setBlock(at, floor.defaultBlockState(), 2);
            }
        }
    }

    private static void clearAbove(ServerLevel level, BlockPos portalPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 1; dy <= 2; dy++) { clear(level, portalPos.offset(dx, dy, dz)); }
            }
        }
    }

    private static void clear(ServerLevel level, BlockPos pos) {
        BlockState found = level.getBlockState(pos);
        if (found.isAir() || found.getBlock() instanceof ContentPortalBlock) { return; }
        level.removeBlock(pos, false);
    }

    private static Block block(String name) {
        if (name.isEmpty()) { return Blocks.STONE; }
        ResourceLocation key = ResourceLocation.tryParse(name);
        Block found = Registered.find(ForgeRegistries.BLOCKS, key);
        if (found != null) { return found; }
        ContentLog.LOGGER.error("Portal platform block {} is not registered, using stone", name);
        return Blocks.STONE;
    }
}
