package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.blastplaster.util.BlastPlasterUtil;
import mctmods.blastplaster.util.TreeCollector;
import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class ContentCityTrees {
    private static final int UP = 20;
    private static final int REACH = 2;
    private static final int SUSTAIN = 6;
    private static final int CROWN = 4;
    private static final int LOOSE = 5;

    private static final int PLANTED_REACH = 2;
    private static final int TRESTLE = 64;
    private static final int AROUND = 16;
    private static final int SWEEP_OUT = 8;
    private static final int SWEEP_UNDER = 4;
    private static final int SWEEP_OVER = 44;
    private static final int WRITTEN = 16;
    private static final byte UNREACHED = 127;

    private ContentCityTrees() {}

    public interface Felling {
        @Nullable BoundingBox stood();

        int fellFloor();

        @Nullable default BoundingBox owned() { return stood(); }

        @Nullable default BoundingBox felled() {
            BoundingBox stood = stood();
            return stood == null ? null : new BoundingBox(stood.minX(), fellFloor(), stood.minZ(), stood.maxX(), stood.maxY() + UP, stood.maxZ());
        }

        @Nullable default BoundingBox crowned() { return null; }
    }

    private record Stood(Felling piece, BoundingBox box) {}

    public static BoundingBox bridge(BoundingBox held, int level, int frame) { return new BoundingBox(held.minX(), level - TRESTLE, held.minZ(), held.maxX(), Math.max(held.maxY(), level + frame + 1), held.maxZ()); }

    public static int fellAround(WorldGenLevel level, StructureManager manager, ChunkPos chunk, Felling piece, BoundingBox box) {
        BoundingBox felled = piece.felled();
        if (felled == null) { return 0; }
        List<BoundingBox> kept = new ArrayList<>();
        for (Stood other : standing(manager, chunk, box)) {
            if (other.piece() != piece) { keep(kept, other); }
        }
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int cut = gather(level, felled, piece.crowned(), piece.stood() != null, box, kept, seeds, canopy);
        return cut + fell(level, seeds, canopy, at -> box.isInside(at) && free(level, kept, at), kept);
    }

    public static int fellOver(WorldGenLevel level, StructureManager manager, ChunkPos chunk, BoundingBox box, List<BlockPos> columns, int floor, int top) {
        List<BoundingBox> kept = new ArrayList<>();
        for (Stood other : standing(manager, chunk, box)) { keep(kept, other); }
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (BlockPos column : columns) {
            for (int y = floor; y <= top + UP; y++) {
                at.set(column.getX(), y, column.getZ());
                if (box.isInside(at)) { felled += sorted(level, at, inside(kept, at), true, seeds, canopy); }
            }
        }
        return felled + fell(level, seeds, canopy, spot -> box.isInside(spot) && free(level, kept, spot), kept);
    }

    public static void dressed(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        fellGrown(level, chunk, manager);
        sweep(level, chunk, manager);
    }

    private static void fellGrown(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        BoundingBox scan = new BoundingBox(chunk.getMinBlockX(), level.getMinBuildHeight(), chunk.getMinBlockZ(), chunk.getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getMaxBlockZ());
        BoundingBox area = scan.inflatedBy(AROUND);
        List<Stood> pieces = standing(manager, chunk, area);
        if (pieces.isEmpty()) { return; }
        List<BoundingBox> kept = new ArrayList<>();
        for (Stood piece : pieces) { keep(kept, piece); }
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        CityBiome.enter(level, chunk.getMiddleBlockX(), chunk.getMiddleBlockZ());
        try {
            for (Stood piece : pieces) { felled += gather(level, piece.box(), piece.piece().crowned(), piece.piece().stood() != null, scan, kept, seeds, canopy); }
            felled += fell(level, seeds, canopy, at -> area.isInside(at) && free(level, kept, at), kept);
        }
        finally { CityBiome.leave(); }
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) that grew around the city's pieces in chunk {}, {} after they were laid", felled, chunk.x, chunk.z); }
    }

    private static void sweep(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        for (StructureStart start : manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure)) {
            BoundingBox city = start.getBoundingBox();
            int minX = Math.max(city.minX() - SWEEP_OUT, chunk.getMinBlockX() - WRITTEN);
            int maxX = Math.min(city.maxX() + SWEEP_OUT, chunk.getMaxBlockX() + WRITTEN);
            int minZ = Math.max(city.minZ() - SWEEP_OUT, chunk.getMinBlockZ() - WRITTEN);
            int maxZ = Math.min(city.maxZ() + SWEEP_OUT, chunk.getMaxBlockZ() + WRITTEN);
            int minY = Math.max(level.getMinBuildHeight() + 1, city.minY() - SWEEP_UNDER);
            int maxY = Math.min(city.minY() + SWEEP_OVER, level.getMaxBuildHeight() - 1);
            if (minX > maxX || minZ > maxZ || minY > maxY) { continue; }
            int swept = sweep(level, new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
            if (swept > 0) { ContentLog.LOGGER.debug("Swept {} orphaned leaf block(s) no trunk sustains around the city at {}, {} in chunk {}, {}, left behind where a felled tree crossed a chunk edge", swept, city.minX(), city.minZ(), chunk.x, chunk.z); }
        }
    }

    private static int sweep(WorldGenLevel level, BoundingBox zone) {
        BoundingBox read = new BoundingBox(zone.minX() - SUSTAIN, Math.max(level.getMinBuildHeight(), zone.minY() - SUSTAIN), zone.minZ() - SUSTAIN, zone.maxX() + SUSTAIN, Math.min(level.getMaxBuildHeight() - 1, zone.maxY() + SUSTAIN), zone.maxZ() + SUSTAIN);
        int spanX = read.getXSpan();
        int spanY = read.getYSpan();
        int spanZ = read.getZSpan();
        byte[] reach = new byte[spanX * spanY * spanZ];
        IntArrayList frontier = new IntArrayList();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = 0; x < spanX; x++) {
            for (int z = 0; z < spanZ; z++) {
                for (int y = 0; y < spanY; y++) {
                    int cell = (x * spanZ + z) * spanY + y;
                    BlockState state = level.getBlockState(at.set(read.minX() + x, read.minY() + y, read.minZ() + z));
                    if (BlastPlasterUtil.isTreeWood(state)) { frontier.add(cell); }
                    else if (state.getBlock() instanceof LeavesBlock) { reach[cell] = UNREACHED; }
                }
            }
        }
        int[] steps = {spanZ * spanY, -spanZ * spanY, spanY, -spanY, 1, -1};
        for (int step = 1; step <= SUSTAIN && !frontier.isEmpty(); step++) {
            IntArrayList next = new IntArrayList();
            for (int i = 0; i < frontier.size(); i++) {
                int cell = frontier.getInt(i);
                int x = cell / (spanZ * spanY);
                int z = cell / spanY % spanZ;
                int y = cell % spanY;
                for (int side = 0; side < steps.length; side++) {
                    if (side == 0 && x == spanX - 1 || side == 1 && x == 0 || side == 2 && z == spanZ - 1 || side == 3 && z == 0 || side == 4 && y == spanY - 1 || side == 5 && y == 0) { continue; }
                    int near = cell + steps[side];
                    if (reach[near] != UNREACHED) { continue; }
                    reach[near] = (byte) step;
                    next.add(near);
                }
            }
            frontier = next;
        }
        int swept = 0;
        for (int x = zone.minX(); x <= zone.maxX(); x++) {
            for (int z = zone.minZ(); z <= zone.maxZ(); z++) {
                for (int y = zone.minY(); y <= zone.maxY(); y++) {
                    if (reach[((x - read.minX()) * spanZ + z - read.minZ()) * spanY + y - read.minY()] == UNREACHED) { swept += orphan(level, at.set(x, y, z)); }
                }
            }
        }
        return swept;
    }

    private static int orphan(WorldGenLevel level, BlockPos.MutableBlockPos at) {
        BlockState held = level.getBlockState(at);
        if (!(held.getBlock() instanceof LeavesBlock) || held.hasProperty(LeavesBlock.PERSISTENT) && held.getValue(LeavesBlock.PERSISTENT)) { return 0; }
        int top = at.getY();
        int swept = clear(level, at);
        for (int under = top - 1; under > level.getMinBuildHeight(); under--) {
            at.setY(under);
            if (!level.getBlockState(at).is(Blocks.VINE)) { break; }
            swept += clear(level, at);
        }
        at.setY(top);
        return swept;
    }

    public static List<BoundingBox> footprints(StructureManager manager, ChunkPos chunk, StructurePiece but, BoundingBox near) {
        List<BoundingBox> found = new ArrayList<>();
        for (StructureStart start : manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure)) {
            for (StructurePiece piece : start.getPieces()) {
                if (piece == but) { continue; }
                BoundingBox stood = piece instanceof Felling felling ? felling.stood() : piece instanceof ContentCityPlazaPiece plaza ? plaza.reached() : null;
                if (stood != null && stood.intersects(near)) { found.add(stood); }
            }
        }
        return found;
    }

    public static List<BoundingBox> foreign(StructureManager manager, ChunkPos chunk, StructurePiece mine, BoundingBox near, Predicate<StructurePiece> kind) { return matching(manager, chunk, mine, near, kind, false); }

    public static List<BoundingBox> kin(StructureManager manager, ChunkPos chunk, StructurePiece mine, BoundingBox near, Predicate<StructurePiece> kind) { return matching(manager, chunk, mine, near, kind, true); }

    public static List<StructurePiece> kinPieces(StructureManager manager, ChunkPos chunk, StructurePiece mine, BoundingBox near, Predicate<StructurePiece> kind) { return pieces(manager, chunk, mine, near, kind, true); }

    public static List<StructurePiece> everyPiece(StructureManager manager, ChunkPos chunk, StructurePiece mine, BoundingBox near, Predicate<StructurePiece> kind) {
        List<StructurePiece> found = pieces(manager, chunk, mine, near, kind, true);
        found.addAll(pieces(manager, chunk, mine, near, kind, false));
        return found;
    }

    private static List<BoundingBox> matching(StructureManager manager, ChunkPos chunk, StructurePiece mine, BoundingBox near, Predicate<StructurePiece> kind, boolean own) {
        List<BoundingBox> found = new ArrayList<>();
        for (StructurePiece piece : pieces(manager, chunk, mine, near, kind, own)) { found.add(stood(piece)); }
        return found;
    }

    private static List<StructurePiece> pieces(StructureManager manager, ChunkPos chunk, StructurePiece mine, BoundingBox near, Predicate<StructurePiece> kind, boolean own) {
        List<StructurePiece> found = new ArrayList<>();
        for (StructureStart start : manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure)) {
            if (start.getPieces().contains(mine) != own) { continue; }
            for (StructurePiece piece : start.getPieces()) {
                if (!kind.test(piece)) { continue; }
                BoundingBox stood = stood(piece);
                if (stood != null && stood.intersects(near.minX(), near.minZ(), near.maxX(), near.maxZ())) { found.add(piece); }
            }
        }
        return found;
    }

    private static BoundingBox stood(StructurePiece piece) { return piece instanceof Felling felling ? felling.stood() : piece.getBoundingBox(); }

    private static List<Stood> standing(StructureManager manager, ChunkPos chunk, BoundingBox near) {
        List<Stood> found = new ArrayList<>();
        for (StructureStart start : manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure)) {
            for (StructurePiece piece : start.getPieces()) {
                if (!(piece instanceof Felling felling)) { continue; }
                BoundingBox felled = felling.felled();
                if (felled != null && felled.inflatedBy(REACH).intersects(near)) { found.add(new Stood(felling, felled)); }
            }
        }
        return found;
    }

    private static int gather(WorldGenLevel level, BoundingBox felling, @Nullable BoundingBox crowned, boolean vines, BoundingBox scan, List<BoundingBox> kept, List<BlockPos> seeds, List<BlockPos> canopy) {
        int felled = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(felling.minX() - REACH, scan.minX()); x <= Math.min(felling.maxX() + REACH, scan.maxX()); x++) {
            for (int z = Math.max(felling.minZ() - REACH, scan.minZ()); z <= Math.min(felling.maxZ() + REACH, scan.maxZ()); z++) {
                for (int y = Math.max(felling.minY(), scan.minY()); y <= Math.min(felling.maxY(), scan.maxY()); y++) {
                    felled += sorted(level, at.set(x, y, z), inside(kept, at), vines, seeds, canopy);
                    if (crowned != null && crowned.isInside(at)) { crown(level, at, kept, seeds); }
                }
            }
        }
        return felled;
    }

    private static void crown(WorldGenLevel level, BlockPos at, List<BoundingBox> kept, List<BlockPos> seeds) {
        if (!(level.getBlockState(at).getBlock() instanceof LeavesBlock)) { return; }
        BlockPos.MutableBlockPos near = new BlockPos.MutableBlockPos();
        for (int dx = -CROWN; dx <= CROWN; dx++) {
            for (int dy = -CROWN; dy <= CROWN; dy++) {
                for (int dz = -CROWN; dz <= CROWN; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > CROWN) { continue; }
                    near.set(at.getX() + dx, at.getY() + dy, at.getZ() + dz);
                    if (inside(kept, near) || !BlastPlasterUtil.isTreeWood(level.getBlockState(near))) { continue; }
                    if (!planted(level, near)) { seeds.add(near.immutable()); }
                    return;
                }
            }
        }
    }

    private static int sorted(WorldGenLevel level, BlockPos.MutableBlockPos at, boolean held, boolean vines, List<BlockPos> seeds, List<BlockPos> canopy) {
        BlockState state = level.getBlockState(at);
        if (state.isAir()) { return 0; }
        if (state.getBlock() instanceof LeavesBlock) { canopy.add(at.immutable()); }
        else if (held) { return 0; }
        else if (BlastPlasterUtil.isTreeWood(state)) { if (!planted(level, at)) { seeds.add(at.immutable()); } }
        else if (vines && state.is(Blocks.VINE)) { return clear(level, at); }
        return 0;
    }

    private static void keep(List<BoundingBox> kept, Stood piece) {
        BoundingBox owned = piece.piece().owned();
        if (owned != null) { kept.add(owned); }
    }

    private static boolean free(WorldGenLevel level, List<BoundingBox> kept, BlockPos at) { return !inside(kept, at) || !BlastPlasterUtil.isTreeWood(level.getBlockState(at)); }

    private static boolean inside(List<BoundingBox> kept, BlockPos at) {
        for (BoundingBox box : kept) {
            if (box.isInside(at)) { return true; }
        }
        return false;
    }

    private static int fell(WorldGenLevel level, List<BlockPos> seeds, List<BlockPos> canopy, Predicate<BlockPos> within, List<BoundingBox> kept) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int felled = fellTrees(level, seeds, within, kept);
        for (BlockPos leaf : canopy) {
            BlockState state = level.getBlockState(leaf);
            if (!(state.getBlock() instanceof LeavesBlock)) { continue; }
            if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) { continue; }
            if (sustained(level, leaf, kept)) { continue; }
            at.set(leaf.getX(), leaf.getY(), leaf.getZ());
            felled += clear(level, at);
        }
        return felled;
    }

    private static int fellTrees(WorldGenLevel level, List<BlockPos> seeds, Predicate<BlockPos> within, List<BoundingBox> kept) {
        int felled = 0;
        int most = mctmods.blastplaster.Config.view(level).getMaxTreeSize();
        Set<BlockPos> done = new HashSet<>();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (BlockPos seed : seeds) {
            if (done.contains(seed)) { continue; }
            TreeCollector.Tree tree = TreeCollector.collect(level, seed, most, within);
            for (BlockPos log : tree.logs) {
                done.add(log);
                at.set(log.getX(), log.getY(), log.getZ());
                felled += clear(level, at);
            }
            for (BlockPos leaf : tree.leaves) {
                at.set(leaf.getX(), leaf.getY(), leaf.getZ());
                felled += clear(level, at);
            }
            for (BlockPos log : tree.logs) { felled += loose(level, log, within, kept, at); }
        }
        return felled;
    }

    private static int loose(WorldGenLevel level, BlockPos log, Predicate<BlockPos> within, List<BoundingBox> kept, BlockPos.MutableBlockPos at) {
        int cleared = 0;
        for (int dx = -LOOSE; dx <= LOOSE; dx++) {
            for (int dy = -LOOSE; dy <= LOOSE; dy++) {
                for (int dz = -LOOSE; dz <= LOOSE; dz++) {
                    at.set(log.getX() + dx, log.getY() + dy, log.getZ() + dz);
                    BlockState state = level.getBlockState(at);
                    if (!(state.getBlock() instanceof LeavesBlock) || state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT) || !within.test(at) || sustained(level, at, kept)) { continue; }
                    cleared += clear(level, at);
                }
            }
        }
        return cleared;
    }

    private static boolean sustained(WorldGenLevel level, BlockPos leaf, List<BoundingBox> kept) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int dx = -SUSTAIN; dx <= SUSTAIN; dx++) {
            for (int dy = -SUSTAIN; dy <= SUSTAIN; dy++) {
                for (int dz = -SUSTAIN; dz <= SUSTAIN; dz++) {
                    probe.set(leaf.getX() + dx, leaf.getY() + dy, leaf.getZ() + dz);
                    if (BlastPlasterUtil.isTreeWood(level.getBlockState(probe)) && !inside(kept, probe)) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean planted(WorldGenLevel level, BlockPos wood) { return ContentCityStructure.plantedAt(CityGround.of(level), wood.getX(), wood.getZ()); }

    public static boolean clears(WorldGenLevel level, BlockPos at, BlockState state) {
        if (state.isAir()) { return false; }
        if (!(state.getBlock() instanceof LeavesBlock)) { return true; }
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int dx = -PLANTED_REACH; dx <= PLANTED_REACH; dx++) {
            for (int dz = -PLANTED_REACH; dz <= PLANTED_REACH; dz++) {
                for (int dy = -SUSTAIN; dy <= 0; dy++) {
                    probe.set(at.getX() + dx, at.getY() + dy, at.getZ() + dz);
                    if (BlastPlasterUtil.isTreeWood(level.getBlockState(probe)) && planted(level, probe)) { return false; }
                }
            }
        }
        return true;
    }

    private static int clear(WorldGenLevel level, BlockPos.MutableBlockPos at) {
        BlockState held = level.getBlockState(at);
        if (!clears(level, at, held)) { return 0; }
        if (BlastPlasterUtil.isTreeWood(held) && planted(level, at)) { return 0; }
        level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
        BlockPos over = at.above();
        if (!level.getBlockState(over).is(Blocks.SNOW)) { return 1; }
        level.setBlock(over, Blocks.AIR.defaultBlockState(), 2);
        return 2;
    }
}
