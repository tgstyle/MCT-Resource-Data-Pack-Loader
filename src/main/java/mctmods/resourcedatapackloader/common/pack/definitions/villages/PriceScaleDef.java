package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import net.minecraft.util.Mth;

public record PriceScaleDef(String objective, float perPoint, float min, float max) {
    public int apply(int count, int score, int stackLimit) { return Mth.clamp(Math.round(count * Mth.clamp(1.0F + score * perPoint, min, max)), 1, stackLimit); }
}
