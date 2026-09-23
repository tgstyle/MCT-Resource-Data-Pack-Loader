package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityStairsPiece extends StructurePiece implements ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityStairsPiece::new;
    public static final int RUN = 7;
    public static final int WIDE = 5;
    public static final int CLIMB_LEAST = 3;
    private static final int HEADROOM = 2;
    private static final int LANDING_HEADROOM = HEADROOM + 1;
    private static final int APPROACH_FILL = 8;
    private static final String LEVEL = "Level";
    private static final String TOP = "Top";
    private static final String ROW = "Row";
    private static final String NEAR = "Near";
    private static final String WAY = "Way";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String BED_HALF = "Bed";
    private static final String EDGE = "Edge";
    private final int level;
    private final int top;
    private final int row;
    private final int near;
    private final int way;
    private final int middle;
    private final boolean alongX;
    private final int bedHalf;
    private final int edge;

    public ContentCityStairsPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.top = tag.getInt(TOP);
        this.row = tag.getInt(ROW);
        this.near = tag.getInt(NEAR);
        this.way = tag.getInt(WAY);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.bedHalf = tag.getInt(BED_HALF);
        this.edge = tag.contains(EDGE) ? tag.getInt(EDGE) : wall(near, way);
    }

    static BoundingBox box(int level, int top, int row, int near, int way, int middle, boolean alongX) {
        int wall = middle + way * 64;
        int leastAcross = Math.min(Math.min(near - 1, near + WIDE), Math.min(middle, wall));
        int mostAcross = Math.max(Math.max(near - 1, near + WIDE), Math.max(middle, wall));
        int leastAlong = row - 1;
        int mostAlong = row + RUN + 1;
        int leastX = alongX ? leastAlong : leastAcross;
        int mostX = alongX ? mostAlong : mostAcross;
        int leastZ = alongX ? leastAcross : leastAlong;
        int mostZ = alongX ? mostAcross : mostAlong;
        return new BoundingBox(leastX, level, leastZ, mostX, top + HEADROOM + 1, mostZ);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(TOP, top);
        tag.putInt(ROW, row);
        tag.putInt(NEAR, near);
        tag.putInt(WAY, way);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(BED_HALF, bedHalf);
        tag.putInt(EDGE, edge);
    }

    private static int stepX(boolean alongX, int row, int along, int near, int lane) { return alongX ? row + along : near + lane; }

    private static int stepZ(boolean alongX, int row, int along, int near, int lane) { return alongX ? near + lane : row + along; }

    static BoundingBox well(int level, int top, int row, int near, boolean alongX) {
        int leastAlong = row - 1;
        int mostAlong = row + RUN + 1;
        int leastAcross = near - 1;
        int mostAcross = near + WIDE;
        return new BoundingBox(alongX ? leastAlong : leastAcross, level, alongX ? leastAcross : leastAlong, alongX ? mostAlong : mostAcross, top + HEADROOM + 1, alongX ? mostAcross : mostAlong);
    }

    @Override @Nullable public BoundingBox stood() { return null; }

    @Override public int fellFloor() { return top - 1; }

    static int wall(int near, int way) { return way > 0 ? near - 1 : near + WIDE; }

    static int approach(WorldGenLevel level, BoundingBox box, int top, int row, int near, int way, int edge, int deck, boolean alongX) {
        CityPalette linings = CityPalette.of(ContentCity.railTunnelBlock(true), Blocks.AIR.defaultBlockState());
        long seed = level.getSeed();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int wall = wall(near, way);
        int graded = 0;
        for (int along = 0; along <= RUN; along++) {
            for (int lane = 0; lane < WIDE; lane++) {
                int x = stepX(alongX, row, along, near, lane);
                int z = stepZ(alongX, row, along, near, lane);
                graded += CityPlotGround.cutBank(level, box, x, z, top + 1, top + LANDING_HEADROOM);
            }
        }
        for (int along = 0; along <= RUN; along++) {
            for (int across = edge; way > 0 ? across < wall : across > wall; across += way) {
                int x = alongX ? row + along : across;
                int z = alongX ? across : row + along;
                if (!box.isInside(at.set(x, top, z))) { continue; }
                graded += CityPlotGround.cutBank(level, box, x, z, top + 1, top + LANDING_HEADROOM);
                if (CityPlotGround.solid(level.getBlockState(at.set(x, top, z)))) { continue; }
                int filled = (deck >> along & 1) != 0 ? 0 : CityPlotGround.fillBank(level, box, x, z, top, top - APPROACH_FILL);
                if (filled == 0) {
                    level.setBlock(at.set(x, top, z), linings.pick(seed, x, top, z), 2);
                    filled = 1;
                }
                graded += filled;
            }
        }
        return graded;
    }

    static int platformBench(WorldGenLevel level, BoundingBox box, boolean alongX, int from, int middle, int floor, int bedHalf, int way, BlockPos.MutableBlockPos at) {
        if (ContentCity.platformWidth() <= 0) { return 0; }
        return bench(level, box, alongX, from, middle + way * (bedHalf + (ContentCity.platformWidth() + 1) / 2), floor + 2, awayFrom(alongX, way), at);
    }

    static Direction awayFrom(boolean alongX, int way) { return alongX ? (way > 0 ? Direction.SOUTH : Direction.NORTH) : (way > 0 ? Direction.EAST : Direction.WEST); }

    static int bench(WorldGenLevel level, BoundingBox box, boolean alongX, int from, int across, int y, Direction facing, BlockPos.MutableBlockPos at) {
        int span = ContentCity.benchLength();
        BlockState seat = CityPalette.state(ContentCity.benchBlock());
        if (span < 2 || seat == null) { return 0; }
        if (seat.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) { seat = seat.setValue(BlockStateProperties.HORIZONTAL_FACING, facing); }
        CityPalette arms = CityPalette.mixed(ContentCity.benchEndBlock());
        long seed = level.getSeed();
        int laid = 0;
        for (int step = 0; step < span; step++) {
            int x = alongX ? from + step : across;
            int z = alongX ? across : from + step;
            BlockState put = step == 0 || step == span - 1 ? arms == null ? null : arms.pick(seed, x, y, z) : seat;
            if (put == null) { continue; }
            if (!box.isInside(at.set(x, y, z))) { continue; }
            if (CityPlotGround.solid(level.getBlockState(at))) { continue; }
            if (!CityPlotGround.solid(level.getBlockState(at.set(x, y - 1, z)))) { continue; }
            level.setBlock(at.set(x, y, z), put, 2);
            laid++;
        }
        return laid;
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) { }
}
