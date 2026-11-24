package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ContentSpire extends ContentShapeBase {
    public ContentSpire(ContentPlacer placer, ShapeDef shape) { super(placer, shape); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        return discColumn(world, random, origin, (radius, level, height) -> reach(radius, level / (double) height), false);
    }

    private int reach(int radius, double climbed) {
        if (ShapeDef.BELL.equals(shape.taper)) { return (int) Math.round(radius * Math.sqrt(Math.max(0.0D, 1.0D - climbed * climbed))); }
        if (ShapeDef.NEEDLE.equals(shape.taper)) { return (int) Math.round(radius * (1.0D - climbed) * (1.0D - climbed)); }
        return (int) Math.round(radius * (1.0D - climbed));
    }
}
