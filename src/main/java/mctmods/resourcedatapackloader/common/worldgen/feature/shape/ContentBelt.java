package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.util.worldgen.ChunkOrigin;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class ContentBelt extends ContentChunkShape {
    private final int radius;
    private final int rarity;
    private final boolean perChunk;
    private final long seedXor;

    public ContentBelt(ContentPlacer placer, ShapeDef shape, int minHeight, int maxHeight, ResourceLocation name) {
        super(placer, minHeight, maxHeight);
        this.radius = Math.max(1, shape.radius.most);
        this.rarity = shape.rarity > 0 ? shape.rarity : 400;
        this.perChunk = shape.perChunk;
        this.seedXor = name.toString().hashCode();
    }

    public void generateChunk(World world, int chunkX, int chunkZ, Predicate<BlockPos> valid) {
        int reach = (int) Math.ceil(radius / 16.0);
        long worldSeed = world.getSeed() ^ seedXor;
        Random worldRandom = new Random(worldSeed);
        long xSeed = worldRandom.nextLong() >> 3;
        long zSeed = worldRandom.nextLong() >> 3;
        for (int x = chunkX - reach; x <= chunkX + reach; x++) {
            for (int z = chunkZ - reach; z <= chunkZ + reach; z++) {
                Random chunkRandom = new Random(xSeed * x + zSeed * z ^ worldSeed);
                int count = perChunk ? rarity : 1;
                if (!perChunk && chunkRandom.nextInt(rarity) != 0) { continue; }
                for (int index = 0; index < count; index++) {
                    BlockPos source = source(chunkRandom, x, z);
                    if (source == null || !withinReach(source, chunkX, chunkZ)) { continue; }
                    if (!valid.test(source)) { continue; }
                    fill(world, chunkRandom, chunkX, chunkZ, source);
                }
            }
        }
    }

    private BlockPos source(Random random, int chunkX, int chunkZ) {
        int span = Math.abs(maxHeight - minHeight);
        if (span <= 0) { return null; }
        return new BlockPos(ChunkOrigin.base(chunkX) + random.nextInt(16), minHeight + random.nextInt(span), ChunkOrigin.base(chunkZ) + random.nextInt(16));
    }

    private boolean withinReach(BlockPos source, int chunkX, int chunkZ) {
        int nearX = MathHelper.clamp(source.getX(), ChunkOrigin.base(chunkX), ChunkOrigin.base(chunkX) + 15);
        int nearZ = MathHelper.clamp(source.getZ(), ChunkOrigin.base(chunkZ), ChunkOrigin.base(chunkZ) + 15);
        int offX = source.getX() - nearX;
        int offZ = source.getZ() - nearZ;
        return offX * offX + offZ * offZ < radius * radius;
    }

    private void fill(World world, Random random, int chunkX, int chunkZ, BlockPos source) {
        int span = radius * radius;
        int lowest = lowest(world, Math.max(minHeight - radius, source.getY() - radius));
        int highest = highest(world, Math.min(maxHeight + radius, source.getY() + radius));
        int baseX = ChunkOrigin.base(chunkX);
        int baseZ = ChunkOrigin.base(chunkZ);
        for (int x = baseX; x < baseX + 16; x++) {
            int offX = x - source.getX();
            int alongX = offX * offX;
            if (alongX >= span) { continue; }
            for (int z = baseZ; z < baseZ + 16; z++) {
                int offZ = z - source.getZ();
                int alongXZ = alongX + offZ * offZ;
                if (alongXZ >= span) { continue; }
                for (int y = lowest; y <= highest; y++) {
                    int offY = y - source.getY();
                    if (alongXZ + offY * offY >= span) { continue; }
                    placer.place(world, random, x, y, z);
                }
            }
        }
    }
}
