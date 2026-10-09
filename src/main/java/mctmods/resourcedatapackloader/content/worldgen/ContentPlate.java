package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentPlate extends ContentShapeBase {
    public ContentPlate(ContentPlacer placer, ShapeDef shape) { super(placer, shape); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = radius(random);
        int thickness = Math.max(0, shape.height.pick(random));
        int centerY = origin.getY() + 1;
        boolean round = shape.isRound();
        return placer.disc(world, random, origin, radius, round, centerY - thickness, shape.slim ? centerY + thickness - 1 : centerY + thickness);
    }
}
