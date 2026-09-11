package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityStairsPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityStairsPiece::new;
    public static final int RUN = 7;
    public static final int WIDE = 5;
    public static final int CLIMB_LEAST = 3;
    private static final int HEADROOM = 2;
    private static final int PASS = 3;
    private static final int PASSAGE_MOST = 32;
    private static final String LEVEL = "Level";
    private static final String TOP = "Top";
    private static final String ROW = "Row";
    private static final String NEAR = "Near";
    private static final String WAY = "Way";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String BED_HALF = "Bed";
    private final int level;
    private final int top;
    private final int row;
    private final int near;
    private final int way;
    private final int middle;
    private final boolean alongX;
    private final int bedHalf;

    public ContentCityStairsPiece(int level, int top, int row, int near, int way, int middle, boolean alongX, int bedHalf) {
        super(TYPE, 0, box(level, top, row, near, way, middle, alongX));
        this.level = level;
        this.top = top;
        this.row = row;
        this.near = near;
        this.way = way;
        this.middle = middle;
        this.alongX = alongX;
        this.bedHalf = bedHalf;
    }

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
    }

    private static BoundingBox box(int level, int top, int row, int near, int way, int middle, boolean alongX) {
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
    }

    private int stepX(int along, int lane) { return alongX ? row + along : near + lane; }

    private int stepZ(int along, int lane) { return alongX ? near + lane : row + along; }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState lining = block(ContentCity.railTunnelBlock(true));
        if (lining == null || top - this.level < CLIMB_LEAST) { return; }
        BlockState step = stateOr(ContentCity.stairBlock(), lining);
        BlockState air = Blocks.AIR.defaultBlockState();
        BoundingBox held = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, held, box, this.level - 1, top, 2);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the subway stairwell at {}, {} was carved", felled, held.minX(), held.minZ()); }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int cut = 0;
        for (int along = -1; along <= RUN + 1; along++) {
            for (int lane = -1; lane <= WIDE; lane++) {
                boolean inside = along >= 0 && along <= RUN && lane >= 0 && lane < WIDE;
                int x = stepX(along, lane);
                int z = stepZ(along, lane);
                for (int y = this.level; y <= top; y++) {
                    if (!box.isInside(at.set(x, y, z))) { continue; }
                    boolean divider = inside && lane == WIDE / 2;
                    level.setBlock(at, y == this.level || !inside || divider ? lining : air, 2);
                    cut++;
                }
            }
        }
        int laid = 0;
        int along = 0;
        int flight = 0;
        boolean forward = true;
        for (int y = this.level + 1; y <= top; y++) {
            boolean turning = forward ? along == RUN : along == 0;
            int most = turning ? WIDE : WIDE / 2;
            for (int wide = 0; wide < most; wide++) {
                int lane = turning ? wide : flight == 0 ? wide : WIDE / 2 + 1 + wide;
                int x = stepX(along, lane);
                int z = stepZ(along, lane);
                if (box.isInside(at.set(x, y, z))) {
                    level.setBlock(at, step, 2);
                    laid++;
                }
                if (!turning || lane != WIDE / 2) { continue; }
                for (int head = 1; head <= HEADROOM; head++) {
                    if (box.isInside(at.set(x, y + head, z))) { level.setBlock(at, air, 2); }
                }
            }
            if (forward) {
                along++;
                if (along > RUN) { along = RUN; forward = false; flight = 1 - flight; }
            }
            else {
                along--;
                if (along < 0) { along = 0; forward = true; flight = 1 - flight; }
            }
        }
        laid += passage(level, box, lining, at);
        laid += railing(level, box, at);
        laid += bench(level, box, at);
        if (cut > 0) { ContentLog.LOGGER.debug("A subway station stairwell at {}, {} carved {} cell(s) from y {} to y {} and laid {} step(s)", stepX(0, 0), stepZ(0, 0), cut, this.level, top, laid); }
    }

    private int passage(WorldGenLevel level, BoundingBox box, BlockState lining, BlockPos.MutableBlockPos at) {
        BlockState platform = stateOr(ContentCity.platformBlock(), lining);
        BlockState air = Blocks.AIR.defaultBlockState();
        int wall = middle + way * (bedHalf + ContentCity.platformWidth() + 1);
        int from = way > 0 ? near - 1 : near + WIDE;
        if (Math.abs(from - wall) > PASSAGE_MOST) {
            ContentLog.LOGGER.warn("A subway station at {}, {} stands {} block(s) from its platform, further than a passage is dug, so it is left without one", stepX(0, 0), stepZ(0, 0), Math.abs(from - wall));
            return 0;
        }
        int floor = this.level + 1;
        int roof = floor + HEADROOM + 1;
        int laid = 0;
        for (int across = from; way > 0 ? across >= wall : across <= wall; across -= way) {
            for (int along = -1; along <= PASS; along++) {
                boolean inside = along >= 0 && along < PASS;
                int x = alongX ? row + along : across;
                int z = alongX ? across : row + along;
                for (int y = floor; y <= roof; y++) {
                    if (!box.isInside(at.set(x, y, z))) { continue; }
                    level.setBlock(at, !inside || y == roof ? lining : y == floor ? platform : air, 2);
                    laid++;
                }
            }
        }
        return laid;
    }

    private int bench(WorldGenLevel level, BoundingBox box, BlockPos.MutableBlockPos at) { return platformBench(level, box, alongX, row - 1, middle, this.level, bedHalf, way, at); }

    static int platformBench(WorldGenLevel level, BoundingBox box, boolean alongX, int from, int middle, int floor, int bedHalf, int way, BlockPos.MutableBlockPos at) {
        if (ContentCity.platformWidth() <= 0) { return 0; }
        return bench(level, box, alongX, from, middle + way * (bedHalf + (ContentCity.platformWidth() + 1) / 2), floor + 2, awayFrom(alongX, way), at);
    }

    static Direction awayFrom(boolean alongX, int way) { return alongX ? (way > 0 ? Direction.SOUTH : Direction.NORTH) : (way > 0 ? Direction.EAST : Direction.WEST); }

    static int bench(WorldGenLevel level, BoundingBox box, boolean alongX, int from, int across, int y, Direction facing, BlockPos.MutableBlockPos at) {
        int span = ContentCity.benchLength();
        BlockState seat = block(ContentCity.benchBlock());
        if (span < 2 || seat == null) { return 0; }
        if (seat.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) { seat = seat.setValue(BlockStateProperties.HORIZONTAL_FACING, facing); }
        BlockState arm = block(ContentCity.benchEndBlock());
        int laid = 0;
        for (int step = 0; step < span; step++) {
            int x = alongX ? from + step : across;
            int z = alongX ? across : from + step;
            BlockState put = step == 0 || step == span - 1 ? arm : seat;
            if (put == null) { continue; }
            if (!box.isInside(at.set(x, y, z))) { continue; }
            if (!level.getBlockState(at).isAir()) { continue; }
            if (!level.getBlockState(at.set(x, y - 1, z)).isFaceSturdy(level, at, Direction.UP)) { continue; }
            level.setBlock(at.set(x, y, z), put, 2);
            laid++;
        }
        return laid;
    }

    private int railing(WorldGenLevel level, BoundingBox box, BlockPos.MutableBlockPos at) {
        BlockState rail = block(ContentCity.railingBlock());
        if (rail == null) { return 0; }
        int laid = 0;
        for (int along = -1; along <= RUN + 1; along++) {
            for (int lane = -1; lane <= WIDE; lane++) {
                boolean inside = along >= 0 && along <= RUN && lane >= 0 && lane < WIDE;
                if (inside) { continue; }
                if (!box.isInside(at.set(stepX(along, lane), top + 1, stepZ(along, lane)))) { continue; }
                level.setBlock(at, rail, 2);
                level.getChunk(at).markPosForPostprocessing(at);
                laid++;
            }
        }
        return laid;
    }

    @Nullable private static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    private static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
