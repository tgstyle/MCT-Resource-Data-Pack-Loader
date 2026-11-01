package mctmods.resourcedatapackloader.content.worldgen;

public final class CityRuns {
    private CityRuns() {}

    public static int end(boolean[] flags, int from) {
        int end = from;
        while (end + 1 < flags.length && flags[end + 1]) { end++; }
        return end;
    }
}
