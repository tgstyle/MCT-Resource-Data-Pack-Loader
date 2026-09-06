package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentBelt implements IContentChunkShape {
    private final int radius;
    private final int rarity;
    private final boolean perChunk;
    private final int minHeight;
    private final int maxHeight;
    private final long seedXor;

    public ContentBelt(ShapeDef shape, int minHeight, int maxHeight, ResourceLocation key) {
        this.radius = Math.max(1, shape.radius().most());
        this.rarity = shape.rarity() > 0 ? shape.rarity() : ShapeDef.BELT_RARITY;
        this.perChunk = shape.perChunk();
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.seedXor = key.toString().hashCode();
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) { return false; }

    @Override public void generateChunk(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid) {
        int reach = (int) Math.ceil(radius / 16.0D);
        long worldSeed = placer.level().getSeed() ^ seedXor;
        RandomSource worldRandom = RandomSource.create(worldSeed);
        long xSeed = worldRandom.nextLong() >> 3;
        long zSeed = worldRandom.nextLong() >> 3;
        for (int x = chunk.x - reach; x <= chunk.x + reach; x++) {
            for (int z = chunk.z - reach; z <= chunk.z + reach; z++) {
                RandomSource chunkRandom = RandomSource.create(xSeed * x + zSeed * z ^ worldSeed);
                int count = perChunk ? rarity : 1;
                if (!perChunk && chunkRandom.nextInt(rarity) != 0) { continue; }
                for (int index = 0; index < count; index++) {
                    BlockPos source = source(chunkRandom, x, z);
                    if (source == null || !withinReach(source, chunk)) { continue; }
                    if (!valid.test(source)) { continue; }
                    fill(placer, chunkRandom, chunk, source);
                }
            }
        }
    }

    @Nullable private BlockPos source(RandomSource random, int chunkX, int chunkZ) {
        int span = Math.abs(maxHeight - minHeight);
        if (span <= 0) { return null; }
        return new BlockPos(chunkX * 16 + random.nextInt(16), minHeight + random.nextInt(span), chunkZ * 16 + random.nextInt(16));
    }

    private boolean withinReach(BlockPos source, ChunkPos chunk) {
        int nearX = Mth.clamp(source.getX(), chunk.getMinBlockX(), chunk.getMaxBlockX());
        int nearZ = Mth.clamp(source.getZ(), chunk.getMinBlockZ(), chunk.getMaxBlockZ());
        int offX = source.getX() - nearX;
        int offZ = source.getZ() - nearZ;
        return offX * offX + offZ * offZ < radius * radius;
    }

    private void fill(ContentPlacer placer, RandomSource random, ChunkPos chunk, BlockPos source) {
        int span = radius * radius;
        int lowest = Math.max(placer.floorY(), Math.max(minHeight - radius, source.getY() - radius));
        int highest = Math.min(placer.ceilingY() - 1, Math.min(maxHeight + radius, source.getY() + radius));
        for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
            int offX = x - source.getX();
            int alongX = offX * offX;
            if (alongX >= span) { continue; }
            for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
                int offZ = z - source.getZ();
                int alongXZ = alongX + offZ * offZ;
                if (alongXZ >= span) { continue; }
                for (int y = lowest; y <= highest; y++) {
                    int offY = y - source.getY();
                    if (alongXZ + offY * offY >= span) { continue; }
                    placer.place(random, x, y, z);
                }
            }
        }
    }
}
