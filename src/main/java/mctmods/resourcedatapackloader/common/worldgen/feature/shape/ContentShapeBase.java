package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.feature.IContentShape;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

abstract class ContentShapeBase implements IContentShape {
    final ContentPlacer placer;
    final ShapeDef shape;

    ContentShapeBase(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return placer.offers(wood); }

    final int radius(Random random) { return MathHelper.clamp(shape.radius.pick(random), 0, ShapeDef.MOST_REACH); }

    final int rise(Random random) { return Math.max(1, shape.height.pick(random)); }

    final int step() { return shape.hanging ? -1 : 1; }
}
