package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.common.Tags;
import java.util.ArrayList;
import java.util.List;

public final class CityPlotGround {
    static final int BRIDGE_GAP = 6;
    static final int RING = 2;
    private static final int FACING_GAP = 2;
    private static final int BRIDGE_STEP = 2;
    private static final int BRIDGE_HIGH = 12;
    private static final int FOOTING_RING = 4;
    private static final int FOOTING_DROP = 24;
    private static final int BANK_DEPTH = 5;
    private static final int CUT_HEIGHT = 6;
    private static final int ROAD_NEAR = 6;
    static final int ROAD_BESIDE = 1;
    private static final int ROOF_CLEAR = 4;
    private static final int VERGE_WET = 8;
    private static final int FOOTING_REACH = 8;
    static final int FILL_UNDER = 8;
    static final int PILING_REACH = 24;

    private CityPlotGround() {}

    public static boolean terrain(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) || state.is(Blocks.GRAVEL) || state.is(BlockTags.SAND) || state.is(Blocks.CLAY)
                || state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK) || state.is(BlockTags.ICE) || state.is(Tags.Blocks.ORES) || ContentBiomes.packGround(state);
    }

    static boolean opening(BlockState state) { return terrain(state) || state.is(BlockTags.TERRACOTTA) || state.is(Blocks.SANDSTONE) || state.is(Blocks.RED_SANDSTONE) || state.is(Blocks.MOSSY_COBBLESTONE); }

    static boolean clearable(BlockState state) { return !state.isAir() && (opening(state) || (state.canBeReplaced() && state.getFluidState().isEmpty())); }

    static boolean cuts(WorldGenLevel level, BlockPos at, BlockState over) {
        if (terrain(over) || over.is(Blocks.DIRT_PATH) || over.is(Blocks.SANDSTONE) || over.is(BlockTags.LOGS)) { return true; }
        return (over.getBlock() instanceof LeavesBlock || !solid(over)) && ContentCityTrees.clears(level, at, over);
    }

    @SuppressWarnings("deprecation") static boolean solid(BlockState state) { return state.isSolid(); }

    static boolean liquid(BlockState state) { return !state.getFluidState().isEmpty(); }

    private static boolean open(BlockState state) { return state.isAir() || liquid(state); }

    static int belowLoose(WorldGenLevel level, int x, int z, int from, int floor, BlockPos.MutableBlockPos at) {
        int y = from;
        while (y >= floor && level.getBlockState(at.set(x, y, z)).getBlock() instanceof FallingBlock) { y--; }
        return y;
    }

    @SuppressWarnings("deprecation") static boolean stoneStep(BlockState state) { return state.getBlock() instanceof StairBlock && !state.ignitedByLava(); }

    public static boolean kept(int[] keep, int x, int z) {
        for (int at = 0; at + 3 < keep.length; at += 4) {
            if (x >= keep[at] && x <= keep[at + 2] && z >= keep[at + 1] && z <= keep[at + 3]) { return true; }
        }
        return false;
    }

    public static int band(int[] well, int x, int z) {
        int bx = x < well[0] ? well[0] - x : x > well[2] ? x - well[2] : 0;
        int bz = z < well[1] ? well[1] - z : z > well[3] ? z - well[3] : 0;
        return Math.max(bx, bz);
    }

    public static BoundingBox layer(BoundingBox held, int y) { return new BoundingBox(held.minX(), y, held.minZ(), held.maxX(), y, held.maxZ()); }

    public static int[] within(BoundingBox held, int[] boxes) {
        List<Integer> found = new ArrayList<>();
        for (int at = 0; at + 3 < boxes.length; at += 4) {
            if (boxes[at + 2] < held.minX() || boxes[at] > held.maxX() || boxes[at + 3] < held.minZ() || boxes[at + 1] > held.maxZ()) { continue; }
            for (int part = 0; part < 4; part++) { found.add(boxes[at + part]); }
        }
        return found.stream().mapToInt(Integer::intValue).toArray();
    }

    static boolean inside(BoundingBox held, int x, int z) { return x >= held.minX() && x <= held.maxX() && z >= held.minZ() && z <= held.maxZ(); }

    static BlockState groundFor(WorldGenLevel level, int x, int z) {
        Holder<Biome> biome = CityBiome.surface(level, x, z);
        ContentBiomes.Soil soil = ContentBiomes.soil(biome);
        if (soil != null) { return groundFor(soil.top().defaultBlockState(), soil.filler().defaultBlockState()); }
        if (biome.is(BiomeTags.IS_BADLANDS)) { return Blocks.TERRACOTTA.defaultBlockState(); }
        if (biome.is(Tags.Biomes.IS_DESERT) || biome.is(BiomeTags.IS_BEACH)) { return Blocks.SAND.defaultBlockState(); }
        return Blocks.DIRT.defaultBlockState();
    }

    static BlockState groundFor(BlockState top, BlockState filler) {
        if (top.is(BlockTags.TERRACOTTA) || filler.is(BlockTags.TERRACOTTA)) { return Blocks.TERRACOTTA.defaultBlockState(); }
        if (top.is(BlockTags.SAND) || filler.is(BlockTags.SAND)) { return Blocks.SAND.defaultBlockState(); }
        if (top.is(Blocks.GRAVEL)) { return Blocks.GRAVEL.defaultBlockState(); }
        return Blocks.DIRT.defaultBlockState();
    }

    private static BlockState fieldGround(WorldGenLevel level, int x, int z) {
        BlockState ground = groundFor(level, x, z);
        if (!ground.is(Blocks.SAND)) { return ground; }
        Holder<Biome> biome = CityBiome.surface(level, x, z);
        ContentBiomes.Soil soil = ContentBiomes.soil(biome);
        boolean sandTop = soil == null ? biome.is(Tags.Biomes.IS_DESERT) || biome.is(BiomeTags.IS_BEACH) : soil.top().defaultBlockState().is(BlockTags.SAND);
        return sandTop ? ground : Blocks.DIRT.defaultBlockState();
    }

    static BlockState pathForGround(WorldGenLevel level, int x, int z, BlockState path, BlockState support, boolean earthy) {
        BlockState ground = groundFor(level, x, z);
        if (ground.is(Blocks.SAND)) { return ContentCityBlocks.swapped(Blocks.SANDSTONE.defaultBlockState()); }
        if (ground.is(Blocks.TERRACOTTA)) { return ContentCityBlocks.swapped(Blocks.TERRACOTTA.defaultBlockState()); }
        if (ground.is(Blocks.GRAVEL)) { return ContentCityBlocks.swapped(Blocks.GRAVEL.defaultBlockState()); }
        return earthy || support.is(Blocks.CLAY) ? path : support;
    }

    static boolean earthy(BlockState held) { return held.isAir() || held.is(Blocks.GRASS_BLOCK) || held.is(Blocks.DIRT) || held.is(Blocks.COARSE_DIRT) || held.is(Blocks.PODZOL) || held.is(Blocks.MYCELIUM) || held.is(Blocks.DIRT_PATH) || decoration(held) || !solid(held); }

    static boolean decoration(BlockState state) { return state.is(BlockTags.CRYSTAL_SOUND_BLOCKS) || state.is(Blocks.CALCITE) || state.is(Blocks.SMOOTH_BASALT) || state.is(Blocks.MOSSY_COBBLESTONE); }

    static boolean inTheWay(BlockState held, BlockState road, BlockState support, BlockState deck) {
        if (!solid(held) || held.instrument() == NoteBlockInstrument.BASS || held.is(BlockTags.LEAVES) || terrain(held) || decoration(held) || held.is(Blocks.DRIPSTONE_BLOCK) || held.is(Blocks.POINTED_DRIPSTONE)) { return false; }
        if (held.is(road.getBlock()) || held.is(support.getBlock()) || held.is(deck.getBlock())) { return false; }
        return !held.is(Blocks.DIRT_PATH) && !held.is(BlockTags.PLANKS) && !held.is(Blocks.SANDSTONE) && !held.is(Blocks.RED_SANDSTONE) && !held.is(BlockTags.TERRACOTTA);
    }

    static BlockState exposed(WorldGenLevel level, BlockPos.MutableBlockPos at, BlockState laid) {
        if (!laid.is(Blocks.DIRT)) { return laid; }
        BlockState over = level.getBlockState(at.above());
        return solid(over) ? laid : Blocks.GRASS_BLOCK.defaultBlockState();
    }

    static BlockState overWater(WorldGenLevel level, int x, int y, int z) {
        CityPalette wet = CityPalette.mixed(ContentCity.vergeWaterBlock());
        BlockState laid = wet == null ? null : wet.pick(level.getSeed(), x, y, z);
        return laid == null || laid.isAir() ? Blocks.OAK_PLANKS.defaultBlockState() : laid;
    }

    static BlockState verge(WorldGenLevel level, BlockPos.MutableBlockPos at, boolean wet, int x, int y, int z) {
        if (wet) { return overWater(level, x, y, z); }
        CityPalette asked = CityPalette.mixed(ContentCity.vergeBlock());
        return asked == null ? exposed(level, at, groundFor(level, x, z)) : asked.pick(level.getSeed(), x, y, z);
    }

    public static void fillDown(WorldGenLevel level, BoundingBox box, int x, int from, int z, BlockState ground) { fillDown(level, box, x, from, z, level.getMinBuildHeight() + 1, ground); }

    static void fillUnder(WorldGenLevel level, BoundingBox box, int x, int z, int from, int floor) { fillDown(level, box, x, from, z, floor, groundFor(level, x, z)); }

    static int piling(WorldGenLevel level, int x, int z, int from, BlockState support, int roof, BlockPos.MutableBlockPos at) {
        int laid = 0;
        for (int y = from; y >= from - PILING_REACH && y > level.getMinBuildHeight() && y > roof; y--) {
            at.set(x, y, z);
            if (solid(level.getBlockState(at))) { break; }
            level.setBlock(at, support, 2);
            laid++;
        }
        return laid;
    }

    private static void fillDown(WorldGenLevel level, BoundingBox box, int x, int from, int z, int floor, BlockState ground) { pier(level, box, x, from, z, floor, Integer.MAX_VALUE, ground); }

    static void pier(WorldGenLevel level, BoundingBox box, int x, int from, int z, int floor, int bed, BlockState ground) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int y = from; y >= floor; y--) {
            at.set(x, y, z);
            if (!box.isInside(at)) { break; }
            BlockState held = level.getBlockState(at);
            if (!held.isAir() && !liquid(held) && (y <= bed || !terrain(held))) { break; }
            level.setBlock(at, ground, 2);
        }
    }

    static int fillBank(WorldGenLevel level, BoundingBox box, int x, int z, int from, int floor) { return fillBank(level, box, x, z, from, floor, false); }

    private static int fillBank(WorldGenLevel level, BoundingBox box, int x, int z, int from, int floor, boolean field) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int footing = Integer.MIN_VALUE;
        for (int y = from; y >= Math.max(level.getMinBuildHeight() + 1, floor - FOOTING_REACH); y--) {
            at.set(x, y, z);
            BlockState held = level.getBlockState(at);
            if (liquid(held)) { return 0; }
            if (solid(held)) {
                footing = y;
                break;
            }
        }
        if (footing == Integer.MIN_VALUE) { return 0; }
        BlockState ground = field ? fieldGround(level, x, z) : groundFor(level, x, z);
        int filled = 0;
        for (int y = from; y > footing; y--) {
            at.set(x, y, z);
            if (!box.isInside(at)) { continue; }
            level.setBlock(at, exposed(level, at, ground), 2);
            filled++;
        }
        return filled;
    }

    private static int fillBank(WorldGenLevel level, BoundingBox box, List<CityRails.Laid> bores, int x, int z, int from, int floor, boolean field) {
        int roof = CityRails.boreRoof(bores, x, z);
        if (roof == Integer.MIN_VALUE) { return fillBank(level, box, x, z, from, floor, field); }
        if (from <= roof) { return 0; }
        return fillBank(level, box, x, z, from, Math.max(floor, roof + 1 + FOOTING_REACH), field);
    }

    static boolean covered(List<BoundingBox> others, int x, int z) {
        for (BoundingBox other : others) {
            if (inside(other, x, z)) { return true; }
        }
        return false;
    }

    static int bridge(WorldGenLevel level, BoundingBox held, int seat, BoundingBox near, int nearSeat, BoundingBox box, List<BoundingBox> others, List<BoundingBox> streets) {
        if (Math.abs(seat - nearSeat) > BRIDGE_STEP) { return 0; }
        int[] strip = facingStrip(held, near);
        if (strip == null) { return 0; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int bridged = 0;
        for (int x = Math.max(strip[0], box.minX()); x <= Math.min(strip[1], box.maxX()); x++) {
            for (int z = Math.max(strip[2], box.minZ()); z <= Math.min(strip[3], box.maxZ()); z++) {
                if (covered(streets, x, z)) { continue; }
                int base = away(held, x, z) <= away(near, x, z) ? seat : nearSeat;
                for (int y = base + 1; y <= base + BRIDGE_HIGH; y++) {
                    at.set(x, y, z);
                    if (!box.isInside(at) || insideAny(others, at)) { continue; }
                    BlockState state = level.getBlockState(at);
                    if (!opening(state) && !overhang(state)) { continue; }
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    bridged++;
                }
            }
        }
        return bridged;
    }

    @javax.annotation.Nullable private static int[] facingStrip(BoundingBox held, BoundingBox near) {
        int lowX = Math.max(held.minX(), near.minX());
        int highX = Math.min(held.maxX(), near.maxX());
        int lowZ = Math.max(held.minZ(), near.minZ());
        int highZ = Math.min(held.maxZ(), near.maxZ());
        if (lowX <= highX && (facing(near.minZ() - held.maxZ()) || facing(held.minZ() - near.maxZ()))) {
            boolean beyond = near.minZ() > held.maxZ();
            return new int[] { lowX, highX, beyond ? held.maxZ() + 1 : near.maxZ() + 1, beyond ? near.minZ() - 1 : held.minZ() - 1 };
        }
        if (lowZ <= highZ && (facing(near.minX() - held.maxX()) || facing(held.minX() - near.maxX()))) {
            boolean beyond = near.minX() > held.maxX();
            return new int[] { beyond ? held.maxX() + 1 : near.maxX() + 1, beyond ? near.minX() - 1 : held.minX() - 1, lowZ, highZ };
        }
        return null;
    }

    private static boolean facing(int gap) { return gap >= FACING_GAP && gap <= BRIDGE_GAP; }

    private static int away(BoundingBox held, int x, int z) { return Math.max(0, Math.max(held.minX() - x, x - held.maxX())) + Math.max(0, Math.max(held.minZ() - z, z - held.maxZ())); }

    private static boolean insideAny(List<BoundingBox> others, BlockPos at) {
        for (BoundingBox other : others) {
            if (other.isInside(at)) { return true; }
        }
        return false;
    }

    private static boolean overhang(BlockState state) { return state.is(BlockTags.LEAVES) && (!state.hasProperty(LeavesBlock.PERSISTENT) || !state.getValue(LeavesBlock.PERSISTENT)); }

    static void vergeFill(WorldGenLevel level, BoundingBox box, List<BoundingBox> others, int x, int z, int grade, BlockPos.MutableBlockPos at) {
        if (!box.isInside(at.set(x, grade, z)) || covered(others, x, z)) { return; }
        if (solid(level.getBlockState(at))) {
            BlockState below = level.getBlockState(at.set(x, grade - 1, z));
            if (!solid(below) && !liquid(below)) { fillBank(level, box, x, z, grade - 1, grade - 6); }
            return;
        }
        int bed = wetBed(level, at, x, z, grade);
        if (bed != Integer.MIN_VALUE) { fillDown(level, box, x, grade, z, groundFor(level, x, z)); }
        else { fillBank(level, box, x, z, grade, grade - 5); }
    }

    static int wetBed(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z, int grade) {
        boolean wet = false;
        for (int y = grade; y >= grade - VERGE_WET; y--) {
            BlockState stood = level.getBlockState(at.set(x, y, z));
            if (liquid(stood)) { wet = true; }
            else if (solid(stood)) { return wet ? y : Integer.MIN_VALUE; }
        }
        return Integer.MIN_VALUE;
    }

    static int cutBank(WorldGenLevel level, BoundingBox box, int x, int z, int from, int roof) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int cut = 0;
        for (int y = from; y <= roof; y++) {
            at.set(x, y, z);
            if (!box.isInside(at)) { continue; }
            BlockState held = level.getBlockState(at);
            if (held.isAir()) { continue; }
            if (!terrain(held) && solid(held)) { break; }
            level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
            cut++;
        }
        at.set(x, from - 1, z);
        if (box.isInside(at) && level.getBlockState(at).is(Blocks.DIRT) && !solid(level.getBlockState(at.above()))) { level.setBlock(at, Blocks.GRASS_BLOCK.defaultBlockState(), 2); }
        return cut;
    }

    public static void eaves(WorldGenLevel level, BoundingBox held, BoundingBox box, int seat) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int top = Math.min(held.maxY(), level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1);
                for (int y = seat + 1; y <= top; y++) {
                    at.set(x, y, z);
                    BlockState state = level.getBlockState(at);
                    if (terrain(state) || state.is(Blocks.VINE)) { level.setBlock(at, Blocks.AIR.defaultBlockState(), 2); }
                }
            }
        }
    }

    public static int footing(WorldGenLevel level, BoundingBox held, BoundingBox box, int seat, BlockState ground, List<CityRails.Laid> bores) {
        int least = level.getMinBuildHeight() + 1;
        int minX = Math.max(held.minX(), box.minX());
        int maxX = Math.min(held.maxX(), box.maxX());
        int minZ = Math.max(held.minZ(), box.minZ());
        int maxZ = Math.min(held.maxZ(), box.maxZ());
        if (minX > maxX || minZ > maxZ) { return 0; }
        int depth = maxZ - minZ + 1;
        int from = seat - 1;
        int[] tops = new int[(maxX - minX + 1) * depth];
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int spot = (x - minX) * depth + (z - minZ);
                tops[spot] = Integer.MIN_VALUE;
                for (int y = from; y > Math.max(least - 1, from - FOOTING_DROP); y--) {
                    BlockState state = level.getBlockState(at.set(x, y, z));
                    if (!solid(state)) { continue; }
                    tops[spot] = y;
                    break;
                }
            }
        }
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
        int stood = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int spot = (x - minX) * depth + (z - minZ);
                BlockState laid = ground;
                if (tops[spot] != Integer.MIN_VALUE) {
                    BlockState resting = level.getBlockState(at.set(x, tops[spot], z));
                    if (resting.isCollisionShapeFullBlock(level, at)) { laid = resting; }
                }
                int floor = Math.max(Math.max(least, CityRails.boreRoof(bores, x, z) + 1), restingFloor(tops, depth, spot, from));
                for (int y = from; y >= floor; y--) {
                    at.set(x, y, z);
                    if (!box.isInside(at)) { continue; }
                    if (solid(level.getBlockState(at))) { break; }
                    boolean exposed = !solid(level.getBlockState(above.set(x, y + 1, z)));
                    BlockState placed = laid;
                    if (exposed && laid.is(Blocks.DIRT)) { placed = Blocks.GRASS_BLOCK.defaultBlockState(); }
                    else if (!exposed && laid.is(Blocks.GRASS_BLOCK)) { placed = Blocks.DIRT.defaultBlockState(); }
                    level.setBlock(at, placed, 2);
                    stood++;
                }
            }
        }
        return stood;
    }

    private static int restingFloor(int[] tops, int depth, int spot, int from) {
        int own = tops[spot];
        int floor = own == Integer.MIN_VALUE ? from : own + 1;
        int best = own == Integer.MIN_VALUE ? Integer.MAX_VALUE : from - own;
        int spotX = spot / depth;
        int spotZ = spot % depth;
        for (int i = 0; i < tops.length; i++) {
            if (i == spot || tops[i] == Integer.MIN_VALUE) { continue; }
            int cost = (Math.abs(i / depth - spotX) + Math.abs(i % depth - spotZ)) * 4 + Math.max(0, from - tops[i]);
            if (cost >= best) { continue; }
            best = cost;
            floor = tops[i];
        }
        return floor;
    }

    public static int liftOffRoof(WorldGenLevel level, BoundingBox held, BoundingBox box) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int lifted = 0;
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int top = Math.min(held.maxY() + ROOF_CLEAR, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1);
                for (int y = held.maxY() + 1; y <= top; y++) {
                    at.set(x, y, z);
                    if (!terrain(level.getBlockState(at))) { continue; }
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    lifted++;
                }
            }
        }
        return lifted;
    }

    public static boolean ringMisses(BoundingBox held, BoundingBox box) { return held.intersects(box) || !held.inflatedBy(RING).intersects(box); }

    public static int bankRing(WorldGenLevel level, BoundingBox held, BoundingBox box, int bank, int[] keep, int[] roads, List<CityRails.Laid> bores, boolean field, String named) {
        int banked = 0;
        int cut = 0;
        int tapered = 0;
        int propped = 0;
        int wide = held.maxX() - held.minX() + 5;
        int deep = held.maxZ() - held.minZ() + 5;
        int[] filled = new int[wide * deep];
        int[] shorn = new int[wide * deep];
        boolean inWindow = held.intersects(box);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = held.minX() - RING; x <= held.maxX() + RING; x++) {
            for (int z = held.minZ() - RING; z <= held.maxZ() + RING; z++) {
                if (x > held.minX() && x < held.maxX() && z > held.minZ() && z < held.maxZ()) { continue; }
                if (kept(keep, x, z) || nearRoad(roads, x, z, ROAD_BESIDE)) { continue; }
                at.set(x, bank, z);
                if (!box.isInside(at)) { continue; }
                BlockState state = level.getBlockState(at);
                if (liquid(state)) { continue; }
                int index = (x - held.minX() + RING) * deep + (z - held.minZ() + RING);
                if (solid(state)) {
                    if (inside(held, x, z) || !inWindow || !nearRoad(roads, x, z, ROAD_NEAR)) { continue; }
                    shorn[index] = cutBank(level, box, x, z, bank + 1, bank + CUT_HEIGHT);
                    cut += shorn[index];
                    at.set(x, bank - 1, z);
                    BlockState under = level.getBlockState(at);
                    if (!solid(under) && !liquid(under)) { filled[index] = fillBank(level, box, bores, x, z, bank - 1, bank - BANK_DEPTH, field); }
                    banked += filled[index];
                    continue;
                }
                filled[index] = fillBank(level, box, bores, x, z, bank, bank - BANK_DEPTH, field);
                banked += filled[index];
            }
        }
        for (int x = held.minX() - RING - 1; inWindow && x <= held.maxX() + RING + 1; x++) {
            for (int z = held.minZ() - RING - 1; z <= held.maxZ() + RING + 1; z++) {
                if (x > held.minX() - RING - 1 && x < held.maxX() + RING + 1 && z > held.minZ() - RING - 1 && z < held.maxZ() + RING + 1) { continue; }
                if (kept(keep, x, z) || nearRoad(roads, x, z, ROAD_BESIDE)) { continue; }
                int inX = Math.clamp(x, held.minX() - RING, held.maxX() + RING);
                int inZ = Math.clamp(z, held.minZ() - RING, held.maxZ() + RING);
                int index = (inX - held.minX() + RING) * deep + (inZ - held.minZ() + RING);
                if (shorn[index] >= 2) {
                    at.set(x, bank + 1, z);
                    if (box.isInside(at) && solid(level.getBlockState(at))) { tapered += cutBank(level, box, x, z, bank + 2, bank + CUT_HEIGHT); }
                    continue;
                }
                if (filled[index] < 2) { continue; }
                at.set(x, bank - 1, z);
                if (!box.isInside(at)) { continue; }
                BlockState state = level.getBlockState(at);
                if (!solid(state) && !liquid(state)) { tapered += fillBank(level, box, bores, x, z, bank - 1, bank - BANK_DEPTH, false); }
            }
        }
        for (int x = held.minX() - RING - 2; x <= held.maxX() + RING + 2; x++) {
            for (int z = held.minZ() - RING - 2; z <= held.maxZ() + RING + 2; z++) {
                if (kept(keep, x, z)) { continue; }
                at.set(x, bank, z);
                if (!box.isInside(at)) { continue; }
                BlockState state = level.getBlockState(at);
                if (!solid(state) || !terrain(state)) { continue; }
                at.set(x, bank - 1, z);
                BlockState under = level.getBlockState(at);
                if (!solid(under) && !liquid(under)) { propped += fillBank(level, box, bores, x, z, bank - 1, bank - BANK_DEPTH, false); }
            }
        }
        if (propped > 0) { ContentLog.LOGGER.debug("Propped {} block(s) of earth under ground that {} at {}, {} left hovering at its bank of y {}", propped, named, held.minX(), held.minZ(), bank); }
        if (tapered > 0) { ContentLog.LOGGER.debug("Tapered {} block(s) a ring further out from {} at {}, {}, one below its bank at y {}", tapered, named, held.minX(), held.minZ(), bank); }
        if (cut > 0) { ContentLog.LOGGER.debug("Cut {} block(s) off the uphill ring of {} at {}, {}, down to its bank at y {}", cut, named, held.minX(), held.minZ(), bank); }
        return banked + cut;
    }

    static boolean nearRoad(int[] roads, int x, int z, int reach) {
        for (int at = 0; at + 3 < roads.length; at += 4) {
            if (x >= roads[at] - reach && x <= roads[at + 2] + reach && z >= roads[at + 1] - reach && z <= roads[at + 3] + reach) { return true; }
        }
        return false;
    }

    static boolean banded(WorldGenLevel level, CityGround ground, int x, int top, int z, int depth) { return top > ground.sea() + 3 + depth && CityBiome.surface(level, x, z).is(BiomeTags.IS_BADLANDS); }

    public static void raisedFooting(WorldGenLevel level, BoundingBox held, BoundingBox box, int seat, int[] keep, int[] roads, List<CityRails.Laid> bores) {
        CityGround ground = CityGround.of(level);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX() - FOOTING_RING, box.minX()); x <= Math.min(held.maxX() + FOOTING_RING, box.maxX()); x++) {
            for (int z = Math.max(held.minZ() - FOOTING_RING, box.minZ()); z <= Math.min(held.maxZ() + FOOTING_RING, box.maxZ()); z++) {
                if (!inside(held, x, z) && (kept(keep, x, z) || nearRoad(roads, x, z, 0))) { continue; }
                int floor = ground.floor(x, z);
                if (floor >= seat - 1) { continue; }
                BlockState resting = level.getBlockState(at.set(x, floor, z));
                if (resting.is(BlockTags.BASE_STONE_OVERWORLD) && floor >= ground.sea() - 1) { continue; }
                int crust = seat - 1;
                while (crust > floor && open(level.getBlockState(at.set(x, crust, z)))) { crust--; }
                if (crust == floor && inside(held, x, z) && solid(resting)) { crust = seat - 1; }
                int depth = ground.surfaceDepth(x, z);
                int filler = seat - 1 - depth;
                boolean banded = banded(level, ground, x, seat - 1, z, depth);
                BlockState rock = resting.is(BlockTags.BASE_STONE_OVERWORLD) || ContentBiomes.packStone(resting) ? resting : ground.stone();
                for (int y = Math.max(floor, CityRails.boreRoof(bores, x, z)) + 1; y <= crust; y++) {
                    at.set(x, y, z);
                    BlockState was = level.getBlockState(at);
                    boolean hollow = open(was);
                    if (y < filler) {
                        if (hollow) { level.setBlock(at, banded ? ground.band(x, y, z) : rock, 2); }
                        continue;
                    }
                    if (!hollow && !was.is(BlockTags.BASE_STONE_OVERWORLD)) { continue; }
                    BlockState under = level.getBlockState(at.below());
                    level.setBlock(at, solid(under) ? verge(level, at, false, x, y, z) : exposed(level, at, Blocks.DIRT.defaultBlockState()), 2);
                }
            }
        }
    }

    public static void soil(WorldGenLevel level, BoundingBox held, BoundingBox box, int seat, String named) {
        CityPalette verges = CityPalette.of(ContentCity.vergeBlock(), Blocks.DIRT.defaultBlockState());
        long seed = level.getSeed();
        int soiled = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                for (int y = seat - 8; y <= seat; y++) {
                    at.set(x, y, z);
                    if (!level.getBlockState(at).is(BlockTags.SAND)) { continue; }
                    level.setBlock(at, verges.pick(seed, x, y, z), 2);
                    soiled++;
                }
            }
        }
        if (soiled > 0) { ContentLog.LOGGER.debug("Turned {} sand block(s) to soil under {} at {}, {}", soiled, named, held.minX(), held.minZ()); }
    }
}
