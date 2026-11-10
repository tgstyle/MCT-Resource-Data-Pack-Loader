package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SeasonDef;

public final class SeasonState {
    private final SeasonDef def;
    private final int index;
    private final long ticks;

    SeasonState(SeasonDef def, int index, long ticks) {
        this.def = def;
        this.index = index;
        this.ticks = ticks;
    }

    public String name() { return def.order.get(index).get(0); }

    public int index() { return index; }

    public int count() { return def.order.size(); }

    public int dayInSeason() { return def.dayInSeason(ticks); }

    public int daysPerSeason() { return def.days; }

    public float yearProgress() { return def.progress(ticks); }
}
