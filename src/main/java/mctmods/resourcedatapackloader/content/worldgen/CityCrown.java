package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.common.Tags;
import java.util.ArrayList;
import java.util.List;

public final class CityCrown {
    private static final int REACH = 4;
    private static final int UNDER = 24;
    private static final int OVER = 16;
    private static final int MESA_CAP = 15;
    private static final int[] NONE = new int[0];
    private static final ThreadLocal<Born> BORN = new ThreadLocal<>();

    private CityCrown() {}

    private record Keep(List<BoundingBox> boxes, BoundingBox held, List<CityRails.Laid> bores) {}

    private static final class Born {
        private final int minX;
        private final int minZ;
        private final BlockState[] tops = new BlockState[256];
        private final int[] ys = new int[256];

        private Born(ChunkPos chunk) {
            minX = chunk.getMinBlockX();
            minZ = chunk.getMinBlockZ();
        }

        private int spot(int x, int z) { return (x - minX) * 16 + (z - minZ); }
    }

    static List<StructureStart> cities(StructureManager manager, ChunkPos chunk) { return manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure); }

    static boolean settling(StructurePiece piece) { return piece instanceof ContentCityPlotPiece plot ? plot.settled() : piece instanceof ContentCityFarmPiece || piece instanceof ContentMapPiece || piece instanceof ContentCityWellPiece well && well.crowned(); }

    static boolean street(StructurePiece piece) { return piece instanceof ContentCityPiece || piece instanceof ContentCityBulbPiece || piece instanceof ContentCityIntersectPiece; }

    private static boolean reaches(StructurePiece piece, BoundingBox clip) { return settling(piece) && piece.getBoundingBox().inflatedBy(REACH).intersects(clip); }

    private static boolean crowns(List<StructureStart> starts, BoundingBox clip) {
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                if (reaches(piece, clip)) { return true; }
            }
        }
        return false;
    }

    static BoundingBox clip(WorldGenLevel level, ChunkPos chunk) { return new BoundingBox(chunk.getMinBlockX(), level.getMinBuildHeight(), chunk.getMinBlockZ(), chunk.getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getMaxBlockZ()); }

    public static void born(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        BORN.remove();
        if (!crowns(cities(manager, chunk), clip(level, chunk))) { return; }
        Born born = new Born(chunk);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = born.minX; x < born.minX + 16; x++) {
            for (int z = born.minZ; z < born.minZ + 16; z++) {
                int top = bornTop(level, at, x, z);
                if (top == Integer.MIN_VALUE) { continue; }
                born.tops[born.spot(x, z)] = level.getBlockState(at.set(x, top, z));
                born.ys[born.spot(x, z)] = top;
            }
        }
        BORN.set(born);
    }

    public static void crowned(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        Born born = BORN.get();
        BORN.remove();
        if (born == null) { return; }
        BoundingBox clip = clip(level, chunk);
        for (StructureStart start : cities(manager, chunk)) {
            List<BoundingBox> boxes = new ArrayList<>();
            List<Integer> streets = new ArrayList<>();
            for (StructurePiece piece : start.getPieces()) {
                BoundingBox box = piece.getBoundingBox();
                boxes.add(box);
                if (street(piece)) { streets.addAll(List.of(box.minX(), box.minZ(), box.maxX(), box.maxZ())); }
            }
            int[] roadways = streets.stream().mapToInt(Integer::intValue).toArray();
            for (StructurePiece piece : start.getPieces()) {
                if (!reaches(piece, clip)) { continue; }
                ContentCityPlotPiece plot = piece instanceof ContentCityPlotPiece held ? held : null;
                crown(level, piece, clip, born, plot == null ? NONE : plot.roads(), roadways, boxes);
            }
        }
    }

    private static int bornTop(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z) {
        for (int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1; y > level.getMinBuildHeight(); y--) {
            BlockState held = level.getBlockState(at.set(x, y, z));
            if (!CityPlotGround.solid(held) || !CityPlotGround.terrain(held)) { continue; }
            return held.isCollisionShapeFullBlock(level, at) ? y : Integer.MIN_VALUE;
        }
        return Integer.MIN_VALUE;
    }

    private static boolean underSoil(BlockState held) { return held.is(BlockTags.BASE_STONE_OVERWORLD) || held.is(Tags.Blocks.ORES) || ContentBiomes.packStone(held); }

    private static boolean covers(BlockState held) { return CityPlotGround.solid(held) && !held.is(BlockTags.LEAVES); }

    private static boolean insideAnother(List<BoundingBox> boxes, BoundingBox own, BlockPos at) {
        for (BoundingBox box : boxes) {
            if (box != own && box.isInside(at)) { return true; }
        }
        return false;
    }

    private enum Crowned {
        WRITTEN("crowned"),
        BORN_UNKNOWN("whose born top was no ground of its own"),
        OFF_CLIP("outside the chunk being built"),
        UNDER_ROAD("under a road"),
        TOP_NOT_GROUND("standing under something that is not ground"),
        SHELTERED("surfaced under an overhang"),
        INSIDE_PIECE("inside another piece"),
        BORN_UNDER_SOIL("born under soil already"),
        TOP_NOT_UNDER_SOIL("wearing their own surface still"),
        HELD("held for a piece of the city"),
        BORED("inside a bore");
        private final String said;

        Crowned(String said) { this.said = said; }
    }

    private static final class Crowning {
        private final int[] tally = new int[Crowned.values().length];
        private final int[][] first = new int[Crowned.values().length][];

        private void took(Crowned why, int x, int y, int z) {
            tally[why.ordinal()]++;
            if (first[why.ordinal()] == null) { first[why.ordinal()] = new int[] {x, y, z}; }
        }

        private int crowned() { return tally[Crowned.WRITTEN.ordinal()]; }

        private int passed() {
            int passed = 0;
            for (Crowned why : Crowned.values()) {
                if (why != Crowned.WRITTEN && why != Crowned.OFF_CLIP && why != Crowned.SHELTERED) { passed += tally[why.ordinal()]; }
            }
            return passed;
        }

        private int sheltered() { return tally[Crowned.SHELTERED.ordinal()]; }

        private int[] shelteredAt() { return first[Crowned.SHELTERED.ordinal()]; }

        private String skips() {
            StringBuilder said = new StringBuilder();
            for (Crowned why : Crowned.values()) {
                if (why == Crowned.WRITTEN || why == Crowned.SHELTERED || tally[why.ordinal()] == 0) { continue; }
                if (!said.isEmpty()) { said.append(", "); }
                int[] spot = first[why.ordinal()];
                said.append(tally[why.ordinal()]).append(' ').append(why.said).append(" (first at ").append(spot[0]).append(", ").append(spot[1]).append(", ").append(spot[2]).append(')');
            }
            return said.isEmpty() ? "none" : said.toString();
        }
    }

    private static void crown(WorldGenLevel level, StructurePiece piece, BoundingBox clip, Born born, int[] roads, int[] roadways, List<BoundingBox> boxes) {
        BoundingBox held = piece.getBoundingBox();
        CityGround ground = CityGround.of(level);
        List<CityRails.Laid> bores = CityRails.subways(ground, held.minX() - REACH, held.minZ() - REACH, held.maxX() + REACH, held.maxZ() + REACH);
        Keep keep = new Keep(boxes, held, bores);
        int floor = held.minY() - UNDER;
        int roof = held.maxY() + OVER;
        Crowning crowning = new Crowning();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = held.minX() - REACH; x <= held.maxX() + REACH; x++) {
            for (int z = held.minZ() - REACH; z <= held.maxZ() + REACH; z++) {
                if (x < clip.minX() || x > clip.maxX() || z < clip.minZ() || z > clip.maxZ()) {
                    crowning.took(Crowned.OFF_CLIP, x, held.minY(), z);
                    continue;
                }
                BlockState was = born.tops[born.spot(x, z)];
                int wasY = born.ys[born.spot(x, z)];
                if (was == null || wasY < floor) {
                    crowning.took(Crowned.BORN_UNKNOWN, x, held.minY(), z);
                    continue;
                }
                if (CityPlotGround.nearRoad(roads, x, z, 0) || CityPlotGround.nearRoad(roadways, x, z, 0)) {
                    crowning.took(Crowned.UNDER_ROAD, x, held.minY(), z);
                    continue;
                }
                int top = groundTop(level, at, x, z, floor, roof);
                if (top == Integer.MIN_VALUE) {
                    int sheltered = CityPlotGround.inside(held, x, z) ? Integer.MIN_VALUE : surfaced(level, ground, at, keep, x, z, held.minY() - 1, roof);
                    if (sheltered != Integer.MIN_VALUE) {
                        crowning.took(Crowned.SHELTERED, x, sheltered, z);
                        continue;
                    }
                    crowning.took(Crowned.TOP_NOT_GROUND, x, held.minY(), z);
                    continue;
                }
                if (insideAnother(boxes, held, at.set(x, top, z))) {
                    crowning.took(Crowned.INSIDE_PIECE, x, top, z);
                    continue;
                }
                if (!underSoil(level.getBlockState(at))) {
                    crowning.took(Crowned.TOP_NOT_UNDER_SOIL, x, top, z);
                    continue;
                }
                if (CityPlotSeams.laid(level, x, top, z)) {
                    crowning.took(Crowned.HELD, x, top, z);
                    continue;
                }
                if (CityRails.insideBore(bores, x, top, z)) {
                    crowning.took(Crowned.BORED, x, top, z);
                    continue;
                }
                Holder<Biome> biome = CityBiome.surface(level, x, z);
                if (biome.is(BiomeTags.IS_BADLANDS) && mesa(level, ground, at, keep, biome, x, z, top, top) != Integer.MIN_VALUE) {
                    crowning.took(Crowned.WRITTEN, x, top, z);
                    continue;
                }
                at.set(x, top, z);
                if (wasY > roof) {
                    crowning.took(Crowned.BORN_UNDER_SOIL, x, top, z);
                    continue;
                }
                level.setBlock(at, underSoil(was) ? CityPlotGround.exposed(level, at, CityPlotGround.groundFor(level, x, z)) : was, 2);
                crowning.took(Crowned.WRITTEN, x, top, z);
            }
        }
        if (crowning.crowned() + crowning.passed() > 0) { ContentLog.LOGGER.debug("Crowned {} block(s) around {} at {}, {} back with the surface the land was born with, passed over {}", crowning.crowned(), piece.getClass().getSimpleName(), held.minX(), held.minZ(), crowning.skips()); }
        if (crowning.sheltered() > 0) { ContentLog.LOGGER.debug("Surfaced {} column(s) of stone under an overhang around {} at {}, {}, first at {}, {}, {}", crowning.sheltered(), piece.getClass().getSimpleName(), held.minX(), held.minZ(), crowning.shelteredAt()[0], crowning.shelteredAt()[1], crowning.shelteredAt()[2]); }
    }

    private static int groundTop(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z, int floor, int roof) {
        boolean eaves = false;
        for (int y = roof; y >= floor; y--) {
            BlockState held = level.getBlockState(at.set(x, y, z));
            if (!covers(held)) { continue; }
            if (CityPlotGround.terrain(held)) { return eaves && walledIn(level, at, x, y, z, roof) ? Integer.MIN_VALUE : y; }
            if (CityPlotGround.solid(level.getBlockState(at.set(x, y - 1, z)))) { return Integer.MIN_VALUE; }
            eaves = true;
        }
        return Integer.MIN_VALUE;
    }

    private static int surfaced(WorldGenLevel level, CityGround ground, BlockPos.MutableBlockPos at, Keep keep, int x, int z, int low, int high) {
        Holder<Biome> biome = CityBiome.surface(level, x, z);
        return biome.is(BiomeTags.IS_BADLANDS) ? mesa(level, ground, at, keep, biome, x, z, low, high) : soil(level, ground, at, keep, biome, x, z, low, high);
    }

    private static int mesa(WorldGenLevel level, CityGround ground, BlockPos.MutableBlockPos at, Keep keep, Holder<Biome> biome, int x, int z, int low, int high) {
        int depth = ground.surfaceDepth(x, z);
        int sea = ground.sea();
        boolean bryce = biome.is(Biomes.ERODED_BADLANDS);
        boolean wooded = biome.is(Biomes.WOODED_BADLANDS);
        boolean plain = Math.cos(ground.surfaceNoise(x, z) * 2.75 * Math.PI) > 0.0;
        int stone = 0;
        int left = -1;
        boolean sand = false;
        boolean laying = false;
        int first = Integer.MIN_VALUE;
        for (int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1; y > level.getMinBuildHeight() && (bryce || stone < MESA_CAP); y--) {
            if (y < low && (!laying || left <= 0)) { break; }
            BlockState held = level.getBlockState(at.set(x, y, z));
            if (!CityPlotGround.solid(held) && !CityPlotGround.liquid(held)) {
                left = -1;
                continue;
            }
            if (!CityPlotGround.opening(held)) { continue; }
            if (left == -1) {
                sand = y >= sea - 1 && y <= sea + 3 + depth && (!wooded || y <= 86 + depth * 2);
                left = depth + Math.max(0, y - sea);
                BlockState top = mesaTop(ground, x, y, z, sea, depth, wooded, plain, sand);
                laying = y >= low && y <= high && top != null && bare(level, at, keep, x, y, z);
                if (laying) {
                    level.setBlock(at, top, 2);
                    if (first == Integer.MIN_VALUE) { first = y; }
                }
            }
            else if (left > 0) {
                left--;
                if (laying && bare(level, at, keep, x, y, z)) { level.setBlock(at, sand ? Blocks.ORANGE_TERRACOTTA.defaultBlockState() : ground.band(x, y, z), 2); }
            }
            stone++;
        }
        return first;
    }

    private static BlockState mesaTop(CityGround ground, int x, int y, int z, int sea, int depth, boolean wooded, boolean plain, boolean sand) {
        if (y < sea - 1) { return depth > 0 ? Blocks.ORANGE_TERRACOTTA.defaultBlockState() : null; }
        if (wooded && y > 86 + depth * 2) { return plain ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState(); }
        if (sand) { return Blocks.RED_SAND.defaultBlockState(); }
        if (y < 64 || y > 127) { return Blocks.ORANGE_TERRACOTTA.defaultBlockState(); }
        return plain ? Blocks.TERRACOTTA.defaultBlockState() : ground.band(x, y, z);
    }

    private static int soil(WorldGenLevel level, CityGround ground, BlockPos.MutableBlockPos at, Keep keep, Holder<Biome> biome, int x, int z, int low, int high) {
        int depth = ground.surfaceDepth(x, z);
        int sea = ground.sea();
        BlockState earth = CityPlotGround.groundFor(level, x, z);
        BlockState filler = earth;
        BlockState lid = null;
        int left = -1;
        boolean laying = false;
        int first = Integer.MIN_VALUE;
        for (int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1; y > level.getMinBuildHeight(); y--) {
            if (y < low && (!laying || left <= 0)) { break; }
            BlockState held = level.getBlockState(at.set(x, y, z));
            if (!CityPlotGround.solid(held) && !CityPlotGround.liquid(held)) {
                left = -1;
                continue;
            }
            if (!CityPlotGround.opening(held)) { continue; }
            if (left == -1) {
                if (depth <= 0) {
                    lid = Blocks.AIR.defaultBlockState();
                    filler = Blocks.STONE.defaultBlockState();
                }
                else if (y >= sea - 4 && y <= sea + 1) {
                    lid = null;
                    filler = earth;
                }
                if (y < sea && lid != null && lid.isAir()) { lid = biome.value().coldEnoughToSnow(at) ? Blocks.ICE.defaultBlockState() : Blocks.WATER.defaultBlockState(); }
                left = depth;
                BlockState top;
                if (y >= sea - 1) { top = lid == null ? CityPlotGround.exposed(level, at, earth) : lid; }
                else if (y < sea - 7 - depth) {
                    lid = Blocks.AIR.defaultBlockState();
                    filler = Blocks.STONE.defaultBlockState();
                    top = Blocks.GRAVEL.defaultBlockState();
                }
                else { top = filler; }
                laying = y >= low && y <= high && bare(level, at, keep, x, y, z);
                if (laying) {
                    level.setBlock(at, top, 2);
                    if (first == Integer.MIN_VALUE) { first = y; }
                }
            }
            else if (left > 0) {
                left--;
                if (laying && !filler.is(Blocks.STONE) && bare(level, at, keep, x, y, z)) { level.setBlock(at, filler, 2); }
                if (left == 0 && filler.is(BlockTags.SAND) && depth > 1) {
                    left = RandomSource.create(Hashes.mix(ground.seed(), x, y, z)).nextInt(4) + Math.max(0, y - 63);
                    filler = filler.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
                }
            }
        }
        return first;
    }

    private static boolean bare(WorldGenLevel level, BlockPos.MutableBlockPos at, Keep keep, int x, int y, int z) {
        return underSoil(level.getBlockState(at.set(x, y, z))) && !insideAnother(keep.boxes(), keep.held(), at) && !CityRails.insideBore(keep.bores(), x, y, z) && !CityPlotSeams.laid(level, x, y, z);
    }

    private static boolean walledIn(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int y, int z, int roof) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx != 0 || dz != 0) && openAbove(level, at, x + dx, y + 2, z + dz, roof)) { return false; }
            }
        }
        return true;
    }

    private static boolean openAbove(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int from, int z, int roof) {
        for (int y = roof; y >= from; y--) {
            if (covers(level.getBlockState(at.set(x, y, z)))) { return false; }
        }
        return true;
    }
}
