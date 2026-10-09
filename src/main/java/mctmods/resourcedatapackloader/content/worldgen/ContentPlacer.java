package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.world.BlockFlags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.List;

public final class ContentPlacer {
    public static final int WRITE_REACH = 1;
    public static final int CHUNK_ONLY = 0;
    private final WorldGenLevel level;
    private final ContentPalette palette;
    private final int centerX;
    private final int centerZ;
    private final int reach;
    private final List<BoundingBox> keepOut;
    private final BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos beside = new BlockPos.MutableBlockPos();

    public ContentPlacer(WorldGenLevel level, ContentPalette palette, ChunkPos center) { this(level, palette, center, WRITE_REACH); }

    public ContentPlacer(WorldGenLevel level, ContentPalette palette, ChunkPos center, int reach) { this(level, palette, center, reach, List.of()); }

    private ContentPlacer(WorldGenLevel level, ContentPalette palette, ChunkPos center, int reach, List<BoundingBox> keepOut) {
        this.keepOut = keepOut;
        this.level = level;
        this.palette = palette;
        this.centerX = center.x;
        this.centerZ = center.z;
        this.reach = reach;
    }

    public static int scatter(RandomSource random, int bound) {
        if (bound <= 0) { return 0; }
        return random.nextInt(bound) - random.nextInt(bound);
    }

    public WorldGenLevel level() { return level; }

    public ContentPlacer rebound(ContentPalette other) { return new ContentPlacer(level, other, new ChunkPos(centerX, centerZ), reach, keepOut); }

    public ContentPlacer keeping(List<BoundingBox> footprints) { return new ContentPlacer(level, palette, new ChunkPos(centerX, centerZ), reach, footprints); }

    public boolean keepsAny(int minX, int minZ, int maxX, int maxZ) {
        for (BoundingBox footprint : keepOut) {
            if (footprint.intersects(minX, minZ, maxX, maxZ)) { return true; }
        }
        return false;
    }

    public boolean keeps(int x, int z) { return keeps(keepOut, x, z); }

    public static boolean keeps(List<BoundingBox> footprints, int x, int z) {
        for (BoundingBox footprint : footprints) {
            if (footprint.intersects(x, z, x, z)) { return true; }
        }
        return false;
    }

    public int lowX() { return (centerX - reach) << 4; }

    public int highX() { return ((centerX + reach) << 4) + 15; }

    public int lowZ() { return (centerZ - reach) << 4; }

    public int highZ() { return ((centerZ + reach) << 4) + 15; }

    public ContentPalette palette() { return palette; }

    public int floorY() { return level.getMinBuildHeight() + 1; }

    public int ceilingY() { return level.getMaxBuildHeight(); }

    public boolean unwritable(int x, int z) { return Math.abs(SectionPos.blockToSectionCoord(x) - centerX) > reach || Math.abs(SectionPos.blockToSectionCoord(z) - centerZ) > reach; }

    public boolean unreadable(BlockPos pos) { return !loaded(level, pos); }

    @SuppressWarnings("deprecation") public static boolean loaded(LevelReader level, BlockPos pos) { return level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())); }

    public static boolean underCover(WorldGenLevel level, int x, int y, int z) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int above = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1; above >= y; above--) {
            BlockState state = level.getBlockState(at.set(x, above, z));
            if (state.getLightBlock(level, at) > 0 || !state.getFluidState().isEmpty()) { return true; }
        }
        return false;
    }

    public boolean place(RandomSource random, int x, int y, int z) {
        if (occupied(x, y, z)) { return false; }
        return placeExactly(palette.choose(random), x, y, z);
    }

    public boolean disc(RandomSource random, BlockPos origin, int radius, boolean round, int lowY, int highY) {
        int span = radius * radius;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
                if (round && offX * offX + offZ * offZ > span) { continue; }
                for (int y = lowY; y <= highY; y++) { placed |= place(random, origin.getX() + offX, y, origin.getZ() + offZ); }
            }
        }
        return placed;
    }

    public boolean placeExactly(BlockState state, int x, int y, int z) {
        if (y < floorY() || y >= ceilingY() || unwritable(x, z) || keeps(x, z)) { return false; }
        return level.setBlock(scratch.set(x, y, z), state, BlockFlags.UPDATE_CLIENTS_KNOWN_SHAPE);
    }

    public boolean occupied(int x, int y, int z) {
        if (keeps(x, z) || !replaces(x, y, z)) { return true; }
        return palette.wantsNearby() && !beside(x, y, z);
    }

    public boolean replaces(int x, int y, int z) {
        if (y < floorY() || y >= ceilingY() || unwritable(x, z)) { return false; }
        return palette.replaceable(level.getBlockState(scratch.set(x, y, z)));
    }

    private boolean beside(int x, int y, int z) {
        for (int offX = -1; offX <= 1; offX++) {
            for (int offY = -1; offY <= 1; offY++) {
                int nearY = y + offY;
                if (nearY < floorY() - 1 || nearY >= ceilingY()) { continue; }
                for (int offZ = -1; offZ <= 1; offZ++) {
                    if (offX == 0 && offY == 0 && offZ == 0) { continue; }
                    beside.set(x + offX, nearY, z + offZ);
                    if (!loaded(level, beside)) { continue; }
                    if (palette.isNearby(level.getBlockState(beside))) { return true; }
                }
            }
        }
        return false;
    }
}
