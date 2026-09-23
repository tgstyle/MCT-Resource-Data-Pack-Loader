package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PieceLaid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import java.util.ArrayList;
import java.util.List;

public final class CityPlotSeams {
    private static final int SEAM_REACH = 2;
    private static final int SEAM_UNDER = 8;
    private static final int SEAM_OVER = 40;
    private static final int PIT_OVER = 16;
    private static final int PIT_UNDER = 8;
    private static final int PIT_DEPTH = 2;
    private static final int PIT_WALLS = 3;
    private static final int PIT_EDGE = 1;
    private CityPlotSeams() {}

    public static void pits(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        BoundingBox chunkBox = CityCrown.clip(level, chunk);
        BoundingBox clip = new BoundingBox(chunkBox.minX() - PIT_EDGE, chunkBox.minY(), chunkBox.minZ() - PIT_EDGE, chunkBox.maxX() + PIT_EDGE, chunkBox.maxY(), chunkBox.maxZ() + PIT_EDGE);
        for (StructureStart start : CityCrown.cities(manager, chunk)) {
            List<Integer> streets = new ArrayList<>();
            for (StructurePiece piece : start.getPieces()) {
                if (!CityCrown.street(piece)) { continue; }
                BoundingBox box = piece.getBoundingBox();
                streets.addAll(List.of(box.minX(), box.minZ(), box.maxX(), box.maxZ()));
            }
            int[] roadways = streets.stream().mapToInt(Integer::intValue).toArray();
            int[] filled = {0};
            CityBiome.within(level, clip, () -> {
                for (StructurePiece piece : start.getPieces()) {
                    if (CityCrown.settling(piece) && piece.getBoundingBox().intersects(clip)) { filled[0] += fillPits(level, piece.getBoundingBox(), clip, roadways); }
                }
            });
            if (filled[0] > 0) { ContentLog.LOGGER.debug("Filled {} block(s) of pit left open inside the plots of the city at {}, {}, where a plot kept a column of its box clear that the rings around it skip", filled[0], start.getBoundingBox().minX(), start.getBoundingBox().minZ()); }
        }
    }

    private static int fillPits(WorldGenLevel level, BoundingBox held, BoundingBox box, int[] roadways) {
        List<CityRails.Laid> bores = CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int filled = 0;
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                if (CityPlotGround.nearRoad(roadways, x, z, 0)) { continue; }
                filled += fillPit(level, held, box, bores, at, x, z);
            }
        }
        return filled;
    }

    private static int fillPit(WorldGenLevel level, BoundingBox held, BoundingBox box, List<CityRails.Laid> bores, BlockPos.MutableBlockPos at, int x, int z) {
        int from = held.maxY() + PIT_OVER;
        int floor = held.minY() - PIT_UNDER;
        int here = standingTop(level, at, x, z, from, floor);
        if (here == Integer.MIN_VALUE) { return 0; }
        BlockState footing = level.getBlockState(at.set(x, here, z));
        if (!CityPlotGround.terrain(footing) || CityPlotGround.liquid(level.getBlockState(at.set(x, here + 1, z)))) { return 0; }
        int low = Integer.MAX_VALUE;
        int walls = 0;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            int nx = x + side.getStepX();
            int nz = z + side.getStepZ();
            int top = standingTop(level, at, nx, nz, from, floor);
            if (top == Integer.MIN_VALUE) { return 0; }
            low = Math.min(low, top);
            if (CityPlotGround.solid(level.getBlockState(at.set(nx, here + 1, nz))) && CityPlotGround.solid(level.getBlockState(at.set(nx, here + 2, nz)))) { walls++; }
        }
        if (low - here < PIT_DEPTH || walls < PIT_WALLS) { return 0; }
        BlockState ground = CityPlotGround.groundFor(level, x, z);
        BlockState top = ground.is(Blocks.DIRT) ? Blocks.GRASS_BLOCK.defaultBlockState() : ground;
        int filled = 0;
        for (int y = here + 1; y <= low; y++) {
            at.set(x, y, z);
            if (!box.isInside(at) || doorBeside(level, x, y, z) || laid(level, x, y, z) || CityRails.insideBore(bores, x, y, z) || CityPlotGround.solid(level.getBlockState(at))) { break; }
            level.setBlock(at, y == low ? top : ground, 2);
            filled++;
        }
        return filled;
    }

    static boolean laid(WorldGenLevel level, int x, int y, int z) { return level instanceof PieceLaid pieces && pieces.rdpl$laid(x, y, z); }

    private static int standingTop(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z, int from, int floor) {
        for (int y = from; y >= floor; y--) {
            BlockState state = level.getBlockState(at.set(x, y, z));
            if (CityPlotGround.solid(state) && !state.is(BlockTags.LEAVES)) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean doorBeside(WorldGenLevel level, int x, int y, int z) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(at.set(x + side.getStepX(), y, z + side.getStepZ())).is(BlockTags.DOORS)) { return true; }
        }
        return false;
    }

    public static void seams(WorldGenLevel level, BoundingBox box, PiecesContainer pieces) {
        BoundingBox city = pieces.calculateBoundingBox();
        List<BoundingBox> covered = new ArrayList<>();
        for (StructurePiece piece : pieces.pieces()) {
            BoundingBox held = piece.getBoundingBox();
            if (buried(piece) || held.maxX() < box.minX() || held.minX() > box.maxX() || held.maxZ() < box.minZ() || held.minZ() > box.maxZ()) { continue; }
            covered.add(held);
        }
        List<CityRails.Laid> bores = CityRails.subways(CityGround.of(level), box.minX(), box.minZ(), box.maxX(), box.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int filled = 0;
        for (int x = Math.max(city.minX() - SEAM_REACH, box.minX()); x <= Math.min(city.maxX() + SEAM_REACH, box.maxX()); x++) {
            for (int z = Math.max(city.minZ() - SEAM_REACH, box.minZ()); z <= Math.min(city.maxZ() + SEAM_REACH, box.maxZ()); z++) {
                if (!coveredAt(covered, x, z) && seam(level, box, bores, at, x, z, city.minY())) { filled++; }
            }
        }
        if (filled > 0) { ContentLog.LOGGER.debug("Filled {} block(s) of groove left between the pieces of the city at {}, {} in the chunk at {}, {}", filled, city.minX(), city.minZ(), box.minX(), box.minZ()); }
    }

    private static boolean buried(StructurePiece piece) { return piece instanceof ContentCityStationPiece || (piece instanceof ContentCityRailPiece rail && rail.subway()); }

    private static boolean coveredAt(List<BoundingBox> covered, int x, int z) {
        for (BoundingBox held : covered) {
            if (CityPlotGround.inside(held, x, z)) { return true; }
        }
        return false;
    }

    private static boolean seam(WorldGenLevel level, BoundingBox box, List<CityRails.Laid> bores, BlockPos.MutableBlockPos at, int x, int z, int seat) {
        int here = surfaceOf(level, at, x, z, seat, true);
        if (here == Integer.MIN_VALUE) { return false; }
        int upTo = Integer.MIN_VALUE;
        int west = surfaceOf(level, at, x - 1, z, seat, false);
        int east = surfaceOf(level, at, x + 1, z, seat, false);
        int north = surfaceOf(level, at, x, z - 1, seat, false);
        int south = surfaceOf(level, at, x, z + 1, seat, false);
        if (west > here && east > here) { upTo = Math.min(west, east); }
        if (north > here && south > here) { upTo = upTo == Integer.MIN_VALUE ? Math.min(north, south) : Math.min(upTo, Math.min(north, south)); }
        if (upTo == Integer.MIN_VALUE || upTo - here != 1 || CityRails.insideBore(bores, x, upTo, z)) { return false; }
        at.set(x, upTo, z);
        if (!box.isInside(at) || CityPlotGround.solid(level.getBlockState(at))) { return false; }
        if (level.getBlockState(at.set(x - 1, upTo, z)).is(BlockTags.DOORS) || level.getBlockState(at.set(x + 1, upTo, z)).is(BlockTags.DOORS) || level.getBlockState(at.set(x, upTo, z - 1)).is(BlockTags.DOORS) || level.getBlockState(at.set(x, upTo, z + 1)).is(BlockTags.DOORS)) { return false; }
        boolean wet = !level.getBlockState(at.set(x, upTo - 1, z)).getFluidState().isEmpty();
        level.setBlock(at.set(x, upTo, z), CityPlotGround.verge(level, at, wet, x, upTo, z), 2);
        return true;
    }

    private static int surfaceOf(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z, int seat, boolean terrainOnly) {
        int ground = Integer.MIN_VALUE;
        for (int y = seat - SEAM_UNDER; y <= seat + SEAM_OVER; y++) {
            BlockState held = level.getBlockState(at.set(x, y, z));
            if (CityPlotGround.liquid(held)) {
                if (!terrainOnly) { return Integer.MIN_VALUE; }
                continue;
            }
            if (!CityPlotGround.solid(held) || (terrainOnly && !CityPlotGround.terrain(held))) { continue; }
            if (CityPlotGround.solid(level.getBlockState(at.set(x, y + 1, z)))) { continue; }
            ground = y;
        }
        return ground;
    }
}
