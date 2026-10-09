package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.MathHelper;
import java.util.Random;

abstract class ContentShapeBase implements IContentShape {
    final ContentPlacer placer;
    final ShapeDef shape;

    ContentShapeBase(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    final int radius(Random random) { return MathHelper.clamp(shape.radius.pick(random), 0, ShapeDef.MOST_REACH); }

    final int rise(Random random) { return Math.max(1, shape.height.pick(random)); }

    final int step() { return shape.hanging ? -1 : 1; }
}
