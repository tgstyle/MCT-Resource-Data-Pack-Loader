package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import java.util.List;
import net.minecraft.world.level.block.Block;

public record BaseDef(String claims, double minScore, double fullScore, int radius, Weights weights, double decayDays, double cellFloor, int cellCap, boolean creative, double chance, int rollEvery, int cooldown,
        boolean onlineOnly, String loss, int loseGrace, boolean rewardAll, String onLogoff, int logoffGrace, boolean follow, boolean hostileToAll, boolean insideSpawn, String breakClaimed, List<Block> breakable, boolean share) {
    public static final double BED_WEIGHT = 20;
    public static final double SPAWN_WEIGHT = 10;
    public static final double CHEST_WEIGHT = 3;
    public static final double PLACED_WEIGHT = 0.05;
    public static final double MINUTES_WEIGHT = 0.5;
    public static final double CLAIM_WEIGHT = 10;
    public static final int DEFAULT_CELL_CAP = 20000;
    public static final int MIN_CELL_CAP = 100;

    public record Weights(double bed, double spawn, double chest, double placed, double minutes, double claim) {}
}
