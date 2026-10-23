package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record SeasonDef(int days, List<List<String>> order) {
    public int index(long day) { return (int) Math.floorMod(Math.floorDiv(day, (long) days), (long) order.size()); }

    public List<String> seasonal() { return order.stream().flatMap(List::stream).distinct().toList(); }
}
