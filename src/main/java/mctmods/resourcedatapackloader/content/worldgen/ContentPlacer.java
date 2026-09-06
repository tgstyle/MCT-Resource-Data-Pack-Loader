package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentPlacer {
    private static final int FLAGS = 2 | 16;
    private static final int WRITE_REACH = 1;
    public static final int CHUNK_ONLY = 0;
    private final WorldGenLevel level;
    private final ContentPalette palette;
    private final int centerX;
    private final int centerZ;
    private final int reach;
    private final BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos beside = new BlockPos.MutableBlockPos();

    public ContentPlacer(WorldGenLevel level, ContentPalette palette, ChunkPos center) { this(level, palette, center, WRITE_REACH); }

    public ContentPlacer(WorldGenLevel level, ContentPalette palette, ChunkPos center, int reach) {
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

    public ContentPalette palette() { return palette; }

    public int floorY() { return level.getMinBuildHeight() + 1; }

    public int ceilingY() { return level.getMaxBuildHeight(); }

    public boolean writable(int x, int z) {
        return Math.abs(SectionPos.blockToSectionCoord(x) - centerX) <= reach && Math.abs(SectionPos.blockToSectionCoord(z) - centerZ) <= reach;
    }

    public boolean unreadable(BlockPos pos) { return !loaded(level, pos); }

    @SuppressWarnings("deprecation") public static boolean loaded(LevelReader level, BlockPos pos) { return level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())); }

    public boolean place(RandomSource random, int x, int y, int z) {
        if (occupied(x, y, z)) { return false; }
        return placeExactly(palette.choose(random), x, y, z);
    }

    public boolean placeExactly(BlockState state, int x, int y, int z) {
        if (y < floorY() || y >= ceilingY() || !writable(x, z)) { return false; }
        return level.setBlock(scratch.set(x, y, z), state, FLAGS);
    }

    public boolean occupied(int x, int y, int z) {
        if (y < floorY() || y >= ceilingY() || !writable(x, z)) { return true; }
        BlockPos pos = scratch.set(x, y, z);
        if (!palette.replaceable(level.getBlockState(pos))) { return true; }
        return palette.wantsNearby() && !beside(x, y, z);
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
