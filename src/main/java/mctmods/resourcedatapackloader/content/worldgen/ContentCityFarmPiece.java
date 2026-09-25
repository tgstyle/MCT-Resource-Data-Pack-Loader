package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.util.Hashes;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.List;
import javax.annotation.Nonnull;

public final class ContentCityFarmPiece extends StructurePiece implements PieceBeardifierModifier, ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityFarmPiece::new;
    private static final long CROP_SALT = 0x6A7F11L;
    private static final String LEVEL = "Level";
    private static final String EDGE = "Edge";
    private static final String SOIL = "Soil";
    private static final String GROUND = "Ground";
    private static final String CROPS = "Crops";
    private static final String WATER = "Water";
    private static final String ROW = "Row";
    private static final String PLOT = "Plot";
    private static final String HEIGHT = "Height";
    private static final String TURN = "Turn";
    private static final String KEEP = "Keep";
    private static final String ROADS = "Roads";
    private static final int BANK_REACH = 4;
    private final int level;
    private final String edge;
    private final String soil;
    private final String ground;
    private final String crops;
    private final boolean water;
    private final int row;
    private final String plot;
    private final int height;
    private final Rotation turn;
    private final int[] keep;
    private final int[] roads;

    public ContentCityFarmPiece(int fromX, int fromZ, int toX, int toZ, int level, int height, String edge, String soil, String ground, String crops, boolean water, int row, String plot, Rotation turn, int[] keep, int[] roads) {
        super(TYPE, 0, new BoundingBox(fromX, level, fromZ, toX, level + height, toZ));
        this.level = level;
        this.edge = edge;
        this.soil = soil;
        this.ground = ground;
        this.crops = crops;
        this.water = water;
        this.row = row;
        this.plot = plot;
        this.height = height;
        this.turn = turn;
        this.keep = keep;
        this.roads = roads;
    }

    public ContentCityFarmPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.edge = tag.getString(EDGE);
        this.soil = tag.getString(SOIL);
        this.ground = tag.getString(GROUND);
        this.crops = tag.getString(CROPS);
        this.water = tag.getBoolean(WATER);
        this.row = tag.getInt(ROW);
        this.plot = tag.getString(PLOT);
        this.height = tag.contains(HEIGHT) ? tag.getInt(HEIGHT) : boundingBox.maxY() - tag.getInt(LEVEL);
        this.turn = tag.contains(TURN) ? Rotation.valueOf(tag.getString(TURN)) : Rotation.NONE;
        this.keep = tag.getIntArray(KEEP);
        this.roads = tag.getIntArray(ROADS);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putString(EDGE, edge);
        tag.putString(SOIL, soil);
        tag.putString(GROUND, ground);
        tag.putString(CROPS, crops);
        tag.putBoolean(WATER, water);
        tag.putInt(ROW, row);
        tag.putString(PLOT, plot);
        tag.putInt(HEIGHT, height);
        tag.putString(TURN, turn.name());
        tag.putIntArray(KEEP, keep);
        tag.putIntArray(ROADS, roads);
    }

    private boolean crosswise() { return turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90; }

    private int wide() { return (crosswise() ? boundingBox.maxZ() - boundingBox.minZ() : boundingBox.maxX() - boundingBox.minX()) + 1; }

    private int deep() { return (crosswise() ? boundingBox.maxX() - boundingBox.minX() : boundingBox.maxZ() - boundingBox.minZ()) + 1; }

    private BlockPos world(int x, int y, int z) {
        int worldX = switch (turn) {
            case CLOCKWISE_90 -> boundingBox.maxX() - z;
            case COUNTERCLOCKWISE_90 -> boundingBox.minX() + z;
            default -> boundingBox.minX() + x;
        };
        int worldZ = switch (turn) {
            case CLOCKWISE_180 -> boundingBox.maxZ() - z;
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> boundingBox.minZ() + x;
            default -> boundingBox.minZ() + z;
        };
        return new BlockPos(worldX, level + y, worldZ);
    }

    private void put(WorldGenLevel world, BoundingBox box, BlockPos at, BlockState state) {
        if (box.isInside(at)) { world.setBlock(at, ContentCityBlocks.ruled(world, world.getSeed(), at, state), 2); }
    }

    private void plant(WorldGenLevel world, BoundingBox box, List<CityRails.Laid> bores, BlockPos at, BlockState state) {
        if (CityRails.insideBore(bores, at.getX(), at.getY(), at.getZ())) { return; }
        put(world, box, at, state);
    }

    @Override @Nonnull public BoundingBox stood() { return getBoundingBox(); }

    @Override public int fellFloor() { return level - 1; }

    @Override @Nonnull public BoundingBox owned() { return getBeardifierBox(); }

    private void laid(@Nonnull WorldGenLevel world, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull BoundingBox box) {
        BlockState fence = plotState(edge, Blocks.OAK_LOG.defaultBlockState());
        BlockState tilled = plotState(soil, Blocks.FARMLAND.defaultBlockState());
        BlockState under = plotState(ground, Blocks.DIRT.defaultBlockState());
        BlockState pond = Blocks.WATER.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BoundingBox held = getBoundingBox();
        List<CityRails.Laid> bores = CityRails.subways(CityGround.of(world), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        ContentCityTrees.fellAround(world, manager, chunk, this, box);
        CityPlotGround.soil(world, held, box, level, plot);
        int lastX = wide() - 1;
        int lastZ = deep() - 1;
        int step = water ? row + 1 : row;
        for (int x = 0; x <= lastX; x++) {
            for (int z = 0; z <= lastZ; z++) {
                for (int y = 1; y <= height; y++) { plant(world, box, bores, world(x, y, z), air); }
            }
        }
        for (int x = 0; x <= lastX; x++) {
            boolean rimRow = x == 0 || x == lastX;
            boolean channel = !rimRow && water && (x - 1) % step == row;
            for (int z = 0; z <= lastZ; z++) {
                boolean rim = rimRow || z == 0 || z == lastZ;
                plant(world, box, bores, world(x, 0, z), rim ? fence : channel ? pond : tilled);
                if (!rim && !channel) {
                    BlockPos at = world(x, 1, z);
                    plant(world, box, bores, at, crop(world.getSeed(), at));
                }
            }
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int z = 0; z <= lastZ; z++) {
            for (int x = 0; x <= lastX; x++) {
                BlockPos top = world(x, height, z);
                for (int y = top.getY(); y < world.getMaxBuildHeight(); y++) {
                    at.set(top.getX(), y, top.getZ());
                    if (!box.isInside(at) || world.getBlockState(at).isAir()) { break; }
                    world.setBlock(at, air, 2);
                }
                BlockPos foot = world(x, -1, z);
                for (int y = foot.getY(); y > world.getMinBuildHeight(); y--) {
                    at.set(foot.getX(), y, foot.getZ());
                    if (!box.isInside(at)) { break; }
                    BlockState below = world.getBlockState(at);
                    if (!below.isAir() && below.getFluidState().isEmpty()) { break; }
                    put(world, box, at.immutable(), under);
                }
            }
        }
        CityPlotGround.footing(world, held, box, level, under, bores);
        CityPlotGround.liftOffRoof(world, held, box);
        CityPlotGround.bankRing(world, held, box, level, keep, roads, bankBores(world), true, plot);
        VillageDef def = ContentVillages.byKey(plot);
        if (def == null) { return; }
        ContentCity.residents(world, def, box, index -> world(def.villagerX() + index, def.villagerY(), def.villagerZ()));
    }

    private List<CityRails.Laid> bankBores(WorldGenLevel world) {
        BoundingBox held = getBoundingBox();
        return CityRails.subways(CityGround.of(world), held.minX() - BANK_REACH, held.minZ() - BANK_REACH, held.maxX() + BANK_REACH, held.maxZ() + BANK_REACH);
    }

    public void ringBeyond(@Nonnull WorldGenLevel world, @Nonnull BoundingBox box) {
        BoundingBox held = getBoundingBox();
        if (CityPlotGround.ringMisses(held, box)) { return; }
        CityBiome.within(world, box, () -> CityPlotGround.bankRing(world, held, box, level, keep, roads, bankBores(world), true, plot));
    }

    private BlockState crop(long seed, BlockPos at) {
        String[] names = crops.isEmpty() ? new String[0] : crops.split(",");
        RandomSource roll = RandomSource.create(Hashes.mix(seed ^ CROP_SALT, at.getX(), at.getY(), at.getZ()));
        BlockState chosen = names.length == 0 ? Blocks.WHEAT.defaultBlockState() : plotState(names[roll.nextInt(names.length)].trim(), Blocks.WHEAT.defaultBlockState());
        if (!(chosen.getBlock() instanceof CropBlock grown)) { return chosen; }
        return grown.getStateForAge(roll.nextInt(grown.getMaxAge() + 1));
    }

    private static BlockState plotState(String named, BlockState fallback) {
        if (named.isEmpty()) { return fallback; }
        BlockState found = ContentStates.known(named, "a village plot");
        return found == null ? fallback : found;
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(getBoundingBox(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel world, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(world, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(world, manager, chunk, box); }
        finally { CityBiome.leave(); }
    }
}
