package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ContentVent extends ContentShapeBase {
    public ContentVent(ContentPlacer placer, ShapeDef shape) { super(placer, shape); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        return discColumn(world, random, origin, (radius, level, height) -> radius, true);
    }
}
