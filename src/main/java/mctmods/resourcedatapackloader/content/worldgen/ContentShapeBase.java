package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

abstract class ContentShapeBase implements IContentShape {
    final ShapeDef shape;

    ContentShapeBase(ShapeDef shape) { this.shape = shape; }

    final int radius(RandomSource random) { return Mth.clamp(shape.radius().pick(random), 0, ShapeDef.MOST_REACH); }

    final int rise(RandomSource random) { return Math.max(1, shape.height().pick(random)); }

    final int step() { return shape.hanging() ? -1 : 1; }
}
