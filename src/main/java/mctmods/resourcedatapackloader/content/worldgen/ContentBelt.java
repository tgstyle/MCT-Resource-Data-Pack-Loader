package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import java.util.function.Predicate;

public final class ContentBelt implements IContentShape {
    private static final int OFFSET = 8;
    private final ContentPlacer placer;
    private final int radius;
    private final int rarity;
    private final boolean perChunk;
    private final int minHeight;
    private final int maxHeight;
    private final long seedXor;

    public ContentBelt(ContentPlacer placer, ShapeDef shape, int minHeight, int maxHeight, ResourceLocation name) {
        this.placer = placer;
        this.radius = Math.max(1, shape.radius.most);
        this.rarity = shape.rarity > 0 ? shape.rarity : 400;
        this.perChunk = shape.perChunk;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.seedXor = name.toString().hashCode();
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) { return false; }

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
        return new BlockPos(chunkX * 16 + random.nextInt(16) + OFFSET, minHeight + random.nextInt(span), chunkZ * 16 + random.nextInt(16) + OFFSET);
    }

    private boolean withinReach(BlockPos source, int chunkX, int chunkZ) {
        int nearX = clamp(source.getX(), chunkX * 16 + OFFSET, chunkX * 16 + OFFSET + 15);
        int nearZ = clamp(source.getZ(), chunkZ * 16 + OFFSET, chunkZ * 16 + OFFSET + 15);
        int offX = source.getX() - nearX;
        int offZ = source.getZ() - nearZ;
        return offX * offX + offZ * offZ < radius * radius;
    }

    private void fill(World world, Random random, int chunkX, int chunkZ, BlockPos source) {
        int span = radius * radius;
        int lowest = Math.max(ContentPlacer.floorY(world), Math.max(minHeight - radius, source.getY() - radius));
        int highest = Math.min(ContentPlacer.ceilingY(world) - 1, Math.min(maxHeight + radius, source.getY() + radius));
        int baseX = chunkX * 16 + OFFSET;
        int baseZ = chunkZ * 16 + OFFSET;
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

    private static int clamp(int value, int least, int most) { return value < least ? least : Math.min(value, most); }
}
