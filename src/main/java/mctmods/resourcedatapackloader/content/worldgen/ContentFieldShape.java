package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import java.util.function.Predicate;

public final class ContentFieldShape extends ContentChunkShape {
    private final ContentField field;
    private final float threshold;
    private final int fade;
    private final int salt;

    public ContentFieldShape(ContentPlacer placer, ShapeDef shape, int minHeight, int maxHeight, ResourceLocation registryName) {
        super(placer, minHeight, maxHeight);
        this.field = shape.field;
        this.threshold = shape.threshold;
        this.fade = shape.fade;
        this.salt = registryName.toString().hashCode();
    }

    private static float hash01(long seed, int x, int y, int z) { return (MathUtil.scramble(seed ^ MathUtil.GOLDEN_GAMMA, x, y, z) >>> 40) / (float) (1 << 24); }

    public void generateChunk(World world, int chunkX, int chunkZ, Predicate<BlockPos> valid) {
        int baseX = chunkX * 16 + OFFSET;
        int baseZ = chunkZ * 16 + OFFSET;
        int lowest = lowest(world);
        int highest = highest(world);
        if (highest < lowest) { return; }
        long seed = world.getSeed() ^ salt;
        Random random = SeededRandom.at(seed, chunkX, chunkZ);
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
