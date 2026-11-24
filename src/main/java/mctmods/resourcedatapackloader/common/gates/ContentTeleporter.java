package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortalBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;
import mctmods.resourcedatapackloader.common.portals.PortalFit;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

public final class ContentTeleporter implements ITeleporter {
    private static final int SEARCH_LOW = 4;
    private static final String PORTALS = "rdplPortals";
    private static final String ORIGINS = "rdplPortalOrigins";
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
        ResourceLocation targetId = target(player, portal);
        if (targetId.equals(here)) { return; }
        ServerLevel target = player.server.getLevel(ResourceKey.create(Registries.DIMENSION, targetId));
        if (target == null) {
            ContentLog.LOGGER.error("A portal at {} leads to {}, which is not a loaded dimension, so nobody goes anywhere", pos, targetId);
            return;
        }
        remember(player, here, pos);
        if (!here.equals(portal.dimension())) { cameFrom(player, portal.dimension(), here); }
        player.changeDimension(target, new ContentTeleporter(portal, state, fit));
    }

    public static ResourceLocation target(ServerPlayer player, PortalDef portal) {
        if (!player.level().dimension().location().equals(portal.dimension())) { return portal.dimension(); }
        return portal.returnToOrigin() ? origin(player, portal) : portal.returnDimension();
    }

    private static ResourceLocation origin(ServerPlayer player, PortalDef portal) {
        ResourceLocation stored = stored(player, portal.dimension());
        if (stored == null || stored.equals(portal.dimension()) || player.server.getLevel(ResourceKey.create(Registries.DIMENSION, stored)) == null) { return portal.returnDimension(); }
        return stored;
    }

    private static void cameFrom(Entity entity, ResourceLocation destination, ResourceLocation origin) { PlayerPersisted.section(entity, ORIGINS).putString(destination.toString(), origin.toString()); }

    @Nullable private static ResourceLocation stored(Entity entity, ResourceLocation destination) {
        CompoundTag origins = PlayerPersisted.read(entity, ORIGINS);
        String key = destination.toString();
        return origins.contains(key, Tag.TAG_STRING) ? ResourceLocation.tryParse(origins.getString(key)) : null;
    }

    @Override public PortalInfo getPortalInfo(Entity entity, ServerLevel destination, Function<ServerLevel, PortalInfo> fallback) {
        BlockPos step = arrive(destination, entity);
        return new PortalInfo(Vec3.atBottomCenterOf(step), Vec3.ZERO, entity.getYRot(), entity.getXRot());
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
        else if (def.sameLanding()) { portalPos = sameSpot(level, mapped); }
        else {
            portalPos = landing(level, mapped);
            boolean adrift = def.platform() && !occupied(level, portalPos.below());
            if (unbuilt(level, portalPos, adrift)) {
                if (adrift) { support(level, portalPos); }
                level.setBlock(portalPos, portalState, Block.UPDATE_CLIENTS);
                clearAbove(level, portalPos);
            }
        }
        PortalStorage.add(level, portalPos, entity.getUUID());
        remember(entity, dimension, portalPos);
        return stepOut(level, portalPos);
    }

    private BlockPos sameSpot(ServerLevel level, BlockPos mapped) {
        int height = roomHeight();
        Block block = block(def.platformBlock());
        int low = level.getMinBuildHeight() + SEARCH_LOW + 1;
        int high = level.getMinBuildHeight() + level.dimensionType().logicalHeight() - height - 1;
        BlockPos portalPos = new BlockPos(mapped.getX(), Mth.clamp(mapped.getY() - (standsOn(portalState) ? 1 : 0), low, high), mapped.getZ());
        Room room = room(portalPos);
        carve(level, portalPos, room, height);
        if (def.platform()) { floorRoom(level, room, portalPos.getY(), block); }
        shell(level, room, portalPos.getY(), height, block);
        if (unbuilt(level, portalPos, false)) { level.setBlock(portalPos, portalState, Block.UPDATE_CLIENTS); }
        if (def.torches() && def.platform()) { torches(level, room, portalPos.getY()); }
        return portalPos;
    }

    private int roomHeight() {
        if (fit == null || fit.holes().isEmpty()) { return 3; }
        int span = highest(fit.holes()).getY() - lowest(fit.holes()).getY() + 2;
        return standsOn(portalState) ? Math.max(span, 3) : span;
    }

    private static boolean standsOn(BlockState state) { return state.getValue(ContentPortalBlock.AXIS) == Direction.Axis.Y || (state.getBlock() instanceof ContentPortalBlock block && block.getDef().fullCube()); }

    private Room room(BlockPos portalPos) {
        List<BlockPos> placed = new ArrayList<>();
        Set<BlockPos> edges = new LinkedHashSet<>();
        if (fit != null && !fit.holes().isEmpty()) {
            BlockPos anchor = lowest(fit.holes());
            for (BlockPos hole : fit.holes()) { placed.add(shift(hole, anchor, portalPos)); }
            for (BlockPos edge : fit.edge().keySet()) {
                BlockPos at = shift(edge, anchor, portalPos);
                placed.add(at);
                edges.add(at);
            }
        }
        if (placed.isEmpty()) { placed.add(portalPos); }
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos at : placed) {
            minX = Math.min(minX, at.getX() - 1);
            maxX = Math.max(maxX, at.getX() + 1);
            minZ = Math.min(minZ, at.getZ() - 1);
            maxZ = Math.max(maxZ, at.getZ() + 1);
        }
        return new Room(minX, maxX, minZ, maxZ, edges);
    }

    private void carve(ServerLevel level, BlockPos portalPos, Room room, int height) {
        int top = portalPos.getY() + height;
        for (int x = room.minX(); x <= room.maxX(); x++) {
            for (int z = room.minZ(); z <= room.maxZ(); z++) {
                for (int y = portalPos.getY(); y < top; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    hold(level, at);
                    BlockState found = level.getBlockState(at);
                    if (found.isAir() || found.getBlock() instanceof ContentPortalBlock || room.edges().contains(at) || kept(level, at, found)) { continue; }
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                }
            }
        }
    }

    private void floorRoom(ServerLevel level, Room room, int floorY, Block block) {
        for (int x = room.minX(); x <= room.maxX(); x++) {
            for (int z = room.minZ(); z <= room.maxZ(); z++) {
                BlockPos at = new BlockPos(x, floorY - 1, z);
                hold(level, at);
                if (!occupied(level, at)) { level.setBlock(at, block.defaultBlockState(), Block.UPDATE_CLIENTS); }
            }
        }
    }

    private void shell(ServerLevel level, Room room, int floorY, int height, Block block) {
        int top = floorY + height;
        for (int x = room.minX() - 1; x <= room.maxX() + 1; x++) {
            for (int z = room.minZ() - 1; z <= room.maxZ() + 1; z++) {
                boolean inside = x >= room.minX() && x <= room.maxX() && z >= room.minZ() && z <= room.maxZ();
                for (int y = floorY; y <= top; y++) {
                    if (inside && y < top) { continue; }
                    BlockPos at = new BlockPos(x, y, z);
                    hold(level, at);
                    BlockState found = level.getBlockState(at);
                    if (found.getBlock() instanceof LiquidBlock && !kept(level, at, found)) { level.setBlock(at, block.defaultBlockState(), Block.UPDATE_CLIENTS); }
                }
            }
        }
    }

    private static void torches(ServerLevel level, Room room, int floorY) {
        BlockState torch = Blocks.TORCH.defaultBlockState();
        for (BlockPos at : List.of(new BlockPos(room.minX(), floorY, room.minZ()), new BlockPos(room.maxX(), floorY, room.maxZ()))) {
            hold(level, at);
            if (level.isEmptyBlock(at) && torch.canSurvive(level, at)) { level.setBlock(at, torch, Block.UPDATE_CLIENTS); }
        }
    }

    private static boolean kept(ServerLevel level, BlockPos at, BlockState state) { return state.hasBlockEntity() || state.getDestroySpeed(level, at) < 0.0F; }

    private static BlockPos highest(List<BlockPos> holes) {
        BlockPos found = holes.get(0);
        for (BlockPos hole : holes) {
            if (hole.getY() > found.getY()) { found = hole; }
        }
        return found;
    }

    private static BlockPos stepOut(ServerLevel level, BlockPos portalPos) {
        BlockState held = level.getBlockState(portalPos);
        if (!(held.getBlock() instanceof ContentPortalBlock) || standsOn(held)) { return portalPos.above(); }
        Direction[] sides = held.getValue(ContentPortalBlock.AXIS) == Direction.Axis.X ? new Direction[] { Direction.NORTH, Direction.SOUTH } : new Direction[] { Direction.WEST, Direction.EAST };
        for (Direction side : sides) {
            BlockPos beside = portalPos.relative(side);
            if (!level.isLoaded(beside)) { continue; }
            if (!level.isEmptyBlock(beside) || !level.isEmptyBlock(beside.above())) { continue; }
            if (!level.getBlockState(beside.below()).isFaceSturdy(level, beside.below(), Direction.UP)) { continue; }
            return beside;
        }
        return portalPos;
    }

    public static void remember(Entity entity, ResourceLocation dimension, BlockPos pos) { PlayerPersisted.section(entity, PORTALS).putLong(dimension.toString(), pos.asLong()); }

    private static void forget(Entity entity, ResourceLocation dimension) { PlayerPersisted.section(entity, PORTALS).remove(dimension.toString()); }

    @Nullable private static BlockPos remembered(Entity entity, ResourceLocation dimension) {
        CompoundTag portals = PlayerPersisted.read(entity, PORTALS);
        String key = dimension.toString();
        return portals.contains(key) ? BlockPos.of(portals.getLong(key)) : null;
    }

    private static BlockPos scale(Entity entity, ServerLevel destination) {
        double factor = DimensionType.getTeleportationScale(entity.level().dimensionType(), destination.dimensionType());
        return BlockPos.containing(entity.getX() * factor, entity.getY(), entity.getZ() * factor);
    }

    private static BlockPos landing(ServerLevel level, BlockPos from) {
        BlockPos ground = ContentDimensions.top(level, new BlockPos(from.getX(), 0, from.getZ()), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        if (ground.getY() > level.getMinBuildHeight()) { return ground; }
        return new BlockPos(from.getX(), Mth.clamp(from.getY(), level.getMinBuildHeight() + SEARCH_LOW, level.getMaxBuildHeight() - 4), from.getZ());
    }

    private static void hold(ServerLevel level, BlockPos at) { level.getChunkAt(at); }

    private boolean unbuilt(ServerLevel level, BlockPos landing, boolean adrift) {
        if (fit == null || fit.holes().isEmpty()) { return true; }
        BlockPos anchor = lowest(fit.holes());
        List<BlockPos> holes = new ArrayList<>();
        Map<BlockPos, BlockState> edges = new LinkedHashMap<>();
        for (BlockPos hole : fit.holes()) { holes.add(shift(hole, anchor, landing)); }
        for (Map.Entry<BlockPos, BlockState> edge : fit.edge().entrySet()) { edges.put(shift(edge.getKey(), anchor, landing), edge.getValue()); }
        List<BlockPos> under = new ArrayList<>(edges.keySet());
        under.addAll(holes);
        for (BlockPos at : under) { hold(level, at); }
        if (adrift) { footing(level, under); }
        for (BlockPos at : holes) { level.removeBlock(at, false); }
        for (Map.Entry<BlockPos, BlockState> edge : edges.entrySet()) { level.setBlock(edge.getKey(), edge.getValue(), Block.UPDATE_CLIENTS); }
        for (BlockPos at : holes) { level.setBlock(at, portalState, Block.UPDATE_CLIENTS); }
        ContentLog.LOGGER.debug("Built a frame of {} on arrival at {}, {} edge block(s) around {} of portal", fit.frame().name(), landing, edges.size(), holes.size());
        return false;
    }

    private static BlockPos shift(BlockPos from, BlockPos anchor, BlockPos landing) { return landing.offset(from.getX() - anchor.getX(), from.getY() - anchor.getY(), from.getZ() - anchor.getZ()); }

    private static BlockPos lowest(List<BlockPos> holes) {
        BlockPos found = holes.get(0);
        for (BlockPos hole : holes) {
            if (hole.getY() < found.getY()) { found = hole; }
        }
        return found;
    }

    private void footing(ServerLevel level, List<BlockPos> under) {
        Block floor = block(def.platformBlock());
        int bed = Integer.MAX_VALUE;
        Set<BlockPos> columns = new LinkedHashSet<>();
        for (BlockPos at : under) {
            bed = Math.min(bed, at.getY());
            columns.add(new BlockPos(at.getX(), 0, at.getZ()));
        }
        if (bed == Integer.MAX_VALUE) { return; }
        for (BlockPos column : columns) { pad(level, new BlockPos(column.getX(), bed + 1, column.getZ()), floor); }
    }

    private void support(ServerLevel level, BlockPos portalPos) { pad(level, portalPos, block(def.platformBlock())); }

    private static void pad(ServerLevel level, BlockPos above, Block floor) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos at = above.offset(dx, -1, dz);
                hold(level, at);
                if (occupied(level, at)) { continue; }
                level.setBlock(at, floor.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static boolean occupied(ServerLevel level, BlockPos at) { return !level.isEmptyBlock(at) && !(level.getBlockState(at).getBlock() instanceof LiquidBlock); }

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

    private record Room(int minX, int maxX, int minZ, int maxZ, Set<BlockPos> edges) {}

    private static Block block(String name) {
        if (name.isEmpty()) { return Blocks.STONE; }
        Block found = Registered.block(name);
        if (found != null) { return found; }
        ContentLog.LOGGER.error("Portal platform block {} is not registered, using stone", name);
        return Blocks.STONE;
    }
}
