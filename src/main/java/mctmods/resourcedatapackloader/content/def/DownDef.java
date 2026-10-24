package mctmods.resourcedatapackloader.content.def;

public final class DownDef {
    public final int seconds;
    public final int reviveSeconds;
    public final String says;
    public final String revivedSays;

    public DownDef(int seconds, int reviveSeconds, String says, String revivedSays) {
        this.seconds = seconds;
        this.reviveSeconds = reviveSeconds;
        this.says = says;
        this.revivedSays = revivedSays;
    }
}
