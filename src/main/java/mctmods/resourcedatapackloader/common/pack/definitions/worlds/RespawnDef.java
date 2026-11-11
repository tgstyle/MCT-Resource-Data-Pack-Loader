package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

public final class RespawnDef {
    public static final int MIN_AFTER = 10;
    public static final int MOST_DISTANCE = 256;
    public static final int DEFAULT_DISTANCE = 64;
    public static final int MOST_CAP = 64;
    public final int after;
    public final int playerDistance;
    public final Loot loot;
    public final boolean spawners;
    public final boolean mobs;
    public final int mobCap;
    public final Rebuild rebuild;

    public RespawnDef(int after, int playerDistance, Loot loot, boolean spawners, boolean mobs, int mobCap, Rebuild rebuild) {
        this.after = after;
        this.playerDistance = playerDistance;
        this.loot = loot;
        this.spawners = spawners;
        this.mobs = mobs;
        this.mobCap = mobCap;
        this.rebuild = rebuild;
    }

    public boolean rebuilds() { return rebuild != Rebuild.NONE; }

    public enum Loot { NONE, EMPTIED, ALL }

    public enum Rebuild { NONE, BROKEN, FULL }
}
