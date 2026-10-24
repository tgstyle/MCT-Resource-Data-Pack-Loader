package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.math.MathHelper;

public final class PriceScaleDef {
    public final String objective;
    public final float perPoint;
    public final float min;
    public final float max;

    public PriceScaleDef(String objective, float perPoint, float min, float max) {
        this.objective = objective;
        this.perPoint = perPoint;
        this.min = min;
        this.max = max;
    }

    public int apply(int count, int score, int stackLimit) { return MathHelper.clamp(Math.round(count * MathHelper.clamp(1.0F + score * perPoint, min, max)), 1, stackLimit); }
}
