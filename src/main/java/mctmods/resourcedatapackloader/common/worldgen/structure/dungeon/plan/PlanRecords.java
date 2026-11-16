package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import java.util.List;
import javax.annotation.Nullable;

public final class PlanRecords {
    private static final int FEATURE_SHELL = 1;
    public static final int SLAB_GAP = DungeonSlots.MAX_SUNK + DungeonFeatures.PIT_DEPTH + FEATURE_SHELL;
    public static final int SHAFT_LADDER = 0;
    public static final int SHAFT_SPIRAL = 1;
    public static final int ENTRANCE_SPIRAL = 1;
    public static final int ENTRANCE_TOPROOM = 2;
    public static final int ENTRANCE_REACH = 8;
    public static final int ENTRANCE_UP = 24;
    public static final int ENTRANCE_DOWN = 16;
    public static final int FEATURE_PILLARS = 0;
    public static final int FEATURE_POOL = 1;
    public static final int FEATURE_PARTITION = 2;
    public static final int FEATURE_ISLAND = 4;
    public static final int FEATURE_CAVE = 6;
    public static final int FEATURE_SINKHOLE = 7;
    public static final int FEATURE_FENCE = 8;

    public static final int SPAWNER_FLOOR = 0;
    public static final int SPAWNER_CEILING = 1;
    public static final int CHEST_BASIC = 0;
    public static final int CHEST_WEAK = 1;
    public static final int CHEST_TREASURE = 2;
    public static final int CHEST_BOSS = 3;

    private PlanRecords() {}

    public record Shaft(int type, int x, int z, int topY, int bottomY) {}

    public record Entrance(int type, int x, int z, int room, @Nullable String toproomTemplate, List<Entrance> extras) {
        public Entrance {
            extras = List.copyOf(extras);
        }
    }

    public record Feature(int type, int x0, int y0, int z0, int x1, int y1, int z1, int blockSlot, int seed) {}

    public record Spawner(int x, int y, int z, int kind, int level, String mobId, long seed) {}

    public record Chest(int x, int y, int z, int kind, int level, String lootTable, long seed) {}
}
