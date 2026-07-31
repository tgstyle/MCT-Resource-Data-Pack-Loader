package mctmods.resourcedatapackloader.content.def;

import java.util.Collections;
import java.util.List;

public final class GrowthDef {
    public final int maxHeight;
    public final int stages;
    public final List<String> soil;
    public final boolean needsWater;
    public final int waterRange;
    public final boolean needsSky;
    public final boolean damage;
    public final float damageAmount;
    public final boolean breaksNeighbours;
    public final int spread;
    public final String drop;
    public final int dropCount;

    public static GrowthDef bush() { return new GrowthDef(1, 16, Collections.emptyList(), false, 1, false, false, 1.0F, false, 0, "", 1); }

    public GrowthDef(int maxHeight, int stages, List<String> soil, boolean needsWater, int waterRange, boolean needsSky, boolean damage, float damageAmount, boolean breaksNeighbours, int spread, String drop, int dropCount) {
        this.maxHeight = maxHeight;
        this.stages = stages;
        this.soil = soil;
        this.needsWater = needsWater;
        this.waterRange = waterRange;
        this.needsSky = needsSky;
        this.damage = damage;
        this.damageAmount = damageAmount;
        this.breaksNeighbours = breaksNeighbours;
        this.spread = spread;
        this.drop = drop;
        this.dropCount = dropCount;
    }
}
