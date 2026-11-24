package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import java.util.List;
import net.minecraft.block.Block;

public final class BaseDef {
    public static final double BED_WEIGHT = 20;
    public static final double SPAWN_WEIGHT = 10;
    public static final double CHEST_WEIGHT = 3;
    public static final double PLACED_WEIGHT = 0.05;
    public static final double MINUTES_WEIGHT = 0.5;
    public static final double CLAIM_WEIGHT = 10;
    public static final int MINUTE_CAP = 60;
    public static final int DEFAULT_CELL_CAP = 20000;
    public static final int MIN_CELL_CAP = 100;
    public final String claims;
    public final double minScore;
    public final double fullScore;
    public final int radius;
    public final Weights weights;
    public final double decayDays;
    public final double cellFloor;
    public final int cellCap;
    public final boolean creative;
    public final double chance;
    public final int rollEvery;
    public final int cooldown;
    public final boolean onlineOnly;
    public final String loss;
    public final int loseGrace;
    public final boolean rewardAll;
    public final String onLogoff;
    public final int logoffGrace;
    public final boolean follow;
    public final boolean hostileToAll;
    public final boolean insideSpawn;
    public final String breakClaimed;
    public final List<Block> breakable;
    public final boolean share;

    public BaseDef(String claims, double minScore, double fullScore, int radius, Weights weights, double decayDays, double cellFloor, int cellCap, boolean creative, double chance, int rollEvery, int cooldown, boolean onlineOnly, String loss, int loseGrace, boolean rewardAll, String onLogoff, int logoffGrace, boolean follow, boolean hostileToAll, boolean insideSpawn, String breakClaimed, List<Block> breakable, boolean share) {
        this.claims = claims;
        this.minScore = minScore;
        this.fullScore = fullScore;
        this.radius = radius;
        this.weights = weights;
        this.decayDays = decayDays;
        this.cellFloor = cellFloor;
        this.cellCap = cellCap;
        this.creative = creative;
        this.chance = chance;
        this.rollEvery = rollEvery;
        this.cooldown = cooldown;
        this.onlineOnly = onlineOnly;
        this.loss = loss;
        this.loseGrace = loseGrace;
        this.rewardAll = rewardAll;
        this.onLogoff = onLogoff;
        this.logoffGrace = logoffGrace;
        this.follow = follow;
        this.hostileToAll = hostileToAll;
        this.insideSpawn = insideSpawn;
        this.breakClaimed = breakClaimed;
        this.breakable = breakable;
        this.share = share;
    }

    public static final class Weights {
        public final double bed;
        public final double spawn;
        public final double chest;
        public final double placed;
        public final double minutes;
        public final double claim;

        public Weights(double bed, double spawn, double chest, double placed, double minutes, double claim) {
            this.bed = bed;
            this.spawn = spawn;
            this.chest = chest;
            this.placed = placed;
            this.minutes = minutes;
            this.claim = claim;
        }
    }
}
