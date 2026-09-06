package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import java.util.function.Predicate;

public final class ContentFieldShape implements IContentChunkShape {
    private final ContentField field;
    private final float threshold;
    private final int minHeight;
    private final int maxHeight;
    private final int fade;
    private final int salt;

    public ContentFieldShape(ContentField field, ShapeDef shape, int minHeight, int maxHeight, ResourceLocation key) {
        this.field = field;
        this.threshold = shape.threshold();
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.fade = shape.fade();
        this.salt = key.toString().hashCode();
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) { return false; }

    @Override public void generateChunk(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid) {
        int lowest = Math.max(placer.floorY(), minHeight);
        int highest = Math.min(placer.ceilingY() - 1, maxHeight);
        if (highest < lowest) { return; }
        long seed = placer.level().getSeed() ^ salt;
        RandomSource random = RandomSource.create(seed ^ (chunk.x * 341873128712L + chunk.z * 132897987541L));
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
            for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
                at.set(x, lowest, z);
                if (!valid.test(at)) { continue; }
                for (int y = lowest; y <= highest; y++) {
                    if (field.strength(seed, x, y, z) < threshold) { continue; }
                    if (fade > 0) {
                        int above = maxHeight - y;
                        if (above < fade && hash01(seed, x, y, z) >= (above + 1) / (float) fade) { continue; }
                    }
                    placer.place(random, x, y, z);
                }
            }
        }
    }

    private static float hash01(long seed, int x, int y, int z) {
        long value = seed ^ 0x9E3779B97F4A7C15L;
        value ^= x * 0x2545F4914F6CDD1DL;
        value ^= (long) y * 0x6C62272E07BB0142L;
        value ^= (long) z * 0xCBF29CE484222325L;
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        return (value >>> 40) / (float) (1 << 24);
    }
}
