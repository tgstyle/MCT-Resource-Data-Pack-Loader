package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;

import java.util.List;

public record SeasonDef(int days, List<List<String>> order) {
    public int index(long day) { return (int) Math.floorMod(Math.floorDiv(day, (long) days), (long) order.size()); }

    public float progress(long ticks) {
        long year = (long) days * DimensionTraitsDef.VANILLA_DAY * order.size();
        return Math.floorMod(ticks, year) / (float) year;
    }

    public int dayInSeason(long ticks) { return (int) Math.floorMod(Math.floorDiv(ticks, DimensionTraitsDef.VANILLA_DAY), (long) days); }

    public List<String> seasonal() { return order.stream().flatMap(List::stream).distinct().toList(); }
}
