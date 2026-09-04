package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record GrowthDef(int maxHeight, int stages, List<String> soil, boolean needsWater, int waterRange, boolean needsSky, boolean damage, float damageAmount, boolean breaksNeighbors, int spread, String drop, int dropCount) {
    public static GrowthDef bush() { return new GrowthDef(1, 16, List.of(), false, 1, false, false, 1.0F, false, 0, "", 1); }
}
