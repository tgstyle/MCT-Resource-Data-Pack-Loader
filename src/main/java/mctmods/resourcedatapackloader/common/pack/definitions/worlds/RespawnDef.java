package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

public record RespawnDef(int after, int playerDistance, Loot loot, boolean spawners, boolean mobs, int mobCap, Rebuild rebuild) {
    public static final int MIN_AFTER = 10;
    public static final int MOST_DISTANCE = 256;
    public static final int DEFAULT_DISTANCE = 64;
    public static final int MOST_CAP = 64;

    public boolean rebuilds() { return rebuild != Rebuild.NONE; }

    public enum Loot { NONE, EMPTIED, ALL }

    public enum Rebuild { NONE, BROKEN, FULL }
}
