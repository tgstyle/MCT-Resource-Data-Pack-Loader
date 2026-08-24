package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import java.util.function.Predicate;

public final class ContentFieldShape implements IContentShape {
    private static final int OFFSET = 8;
    private final ContentPlacer placer;
    private final ContentField field;
    private final float threshold;
    private final int minHeight;
    private final int maxHeight;
    private final int fade;
    private final int salt;

    public ContentFieldShape(ContentPlacer placer, ShapeDef shape, int minHeight, int maxHeight, ResourceLocation registryName) {
        this.placer = placer;
        this.field = shape.field;
        this.threshold = shape.threshold;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.fade = shape.fade;
        this.salt = registryName.toString().hashCode();
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) { return false; }

    private static float hash01(long seed, int x, int y, int z) {
        long h = seed ^ 0x9E3779B97F4A7C15L;
        h ^= x * 0x2545F4914F6CDD1DL;
        h ^= (long) y * 0x6C62272E07BB0142L;
        h ^= (long) z * 0xCBF29CE484222325L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return (h >>> 40) / (float) (1 << 24);
    }

    public void generateChunk(World world, int chunkX, int chunkZ, Predicate<BlockPos> valid) {
        int baseX = chunkX * 16 + OFFSET;
        int baseZ = chunkZ * 16 + OFFSET;
        int lowest = Math.max(ContentPlacer.floorY(world), minHeight);
        int highest = Math.min(ContentPlacer.ceilingY(world) - 1, maxHeight);
        if (highest < lowest) { return; }
        long seed = world.getSeed() ^ salt;
        Random random = new Random(seed ^ (chunkX * 341873128712L + chunkZ * 132897987541L));
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = baseX; x < baseX + 16; x++) {
            for (int z = baseZ; z < baseZ + 16; z++) {
                at.setPos(x, lowest, z);
                if (!valid.test(at)) { continue; }
                for (int y = lowest; y <= highest; y++) {
                    if (field.strength(seed, x, y, z) < threshold) { continue; }
                    if (fade > 0) {
                        int above = maxHeight - y;
                        if (above < fade && hash01(seed, x, y, z) >= (above + 1) / (float) fade) { continue; }
                    }
                    placer.place(world, random, x, y, z);
                }
            }
        }
    }
}
