package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.feature.IContentShape;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentPalette;

import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

abstract class ContentShapeBase implements IContentShape {
    @Override public boolean places(WorldGenLevel level, ContentPalette palette, BlockState wood, List<BlockState> canopies) { return palette.offers(wood); }

    final ShapeDef shape;

    ContentShapeBase(ShapeDef shape) { this.shape = shape; }

    final int radius(RandomSource random) { return Mth.clamp(shape.radius().pick(random), 0, ShapeDef.MOST_REACH); }

    final int rise(RandomSource random) { return Math.max(1, shape.height().pick(random)); }

    final int step() { return shape.hanging() ? -1 : 1; }
}
