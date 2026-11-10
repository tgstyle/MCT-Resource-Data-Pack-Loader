package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SeasonDef;

public record SeasonState(SeasonDef def, int index, long ticks) {
    public String name() { return def.order().get(index).getFirst(); }

    public int count() { return def.order().size(); }

    public int dayInSeason() { return def.dayInSeason(ticks); }

    public int daysPerSeason() { return def.days(); }

    public float yearProgress() { return def.progress(ticks); }
}
