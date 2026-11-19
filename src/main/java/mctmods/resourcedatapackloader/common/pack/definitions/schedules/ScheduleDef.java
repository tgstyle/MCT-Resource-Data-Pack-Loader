package mctmods.resourcedatapackloader.common.pack.definitions.schedules;

public final class ScheduleDef {
    public final long after;
    public final long every;
    public final String runs;

    public ScheduleDef(long after, long every, String runs) {
        this.after = after;
        this.every = every;
        this.runs = runs;
    }
}
