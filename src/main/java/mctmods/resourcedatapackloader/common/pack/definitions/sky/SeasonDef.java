package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import java.util.List;
import net.minecraft.SharedConstants;

public record SeasonDef(int days, List<List<String>> order) {
    public int index(long day) { return (int) Math.floorMod(Math.floorDiv(day, (long) days), (long) order.size()); }

    public List<String> seasonal() { return order.stream().flatMap(List::stream).distinct().toList(); }

    public float progress(long ticks) {
        long year = (long) days * SharedConstants.TICKS_PER_GAME_DAY * order.size();
        return Math.floorMod(ticks, year) / (float) year;
    }

    public int dayInSeason(long ticks) { return (int) Math.floorMod(Math.floorDiv(ticks, (long) SharedConstants.TICKS_PER_GAME_DAY), (long) days); }
}
