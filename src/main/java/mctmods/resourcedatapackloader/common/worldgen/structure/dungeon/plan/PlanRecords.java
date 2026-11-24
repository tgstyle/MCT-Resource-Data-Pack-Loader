package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import java.util.ArrayList;
import java.util.Collections;
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

    public static final class Shaft {
        public static final int SIDE = 3;
        public final int type;
        public final int x;
        public final int z;
        public final int topY;
        public final int bottomY;

        public Shaft(int type, int x, int z, int topY, int bottomY) {
            this.type = type;
            this.x = x;
            this.z = z;
            this.topY = topY;
            this.bottomY = bottomY;
        }

        public int minX(int pad) { return x - pad; }

        public int maxX(int pad) { return x + SIDE - 1 + pad; }

        public int minZ(int pad) { return z - pad; }

        public int maxZ(int pad) { return z + SIDE - 1 + pad; }

        public int centreX() { return x + SIDE / 2; }

        public int centreZ() { return z + SIDE / 2; }

        public boolean covers(int px, int pz, int pad) { return px >= minX(pad) && px <= maxX(pad) && pz >= minZ(pad) && pz <= maxZ(pad); }
    }

    public static final class Entrance {
        public final int type;
        public final int x;
        public final int z;
        public final int room;
        @Nullable
        public final String toproomTemplate;
        public final List<Entrance> extras;

        public Entrance(int type, int x, int z, int room, @Nullable String toproomTemplate, List<Entrance> extras) {
            this.type = type;
            this.x = x;
            this.z = z;
            this.room = room;
            this.toproomTemplate = toproomTemplate;
            this.extras = Collections.unmodifiableList(new ArrayList<>(extras));
        }
    }

    public static final class Feature {
        public final int type;
        public final int x0;
        public final int y0;
        public final int z0;
        public final int x1;
        public final int y1;
        public final int z1;
        public final int blockSlot;
        public final int seed;

        public Feature(int type, int x0, int y0, int z0, int x1, int y1, int z1, int blockSlot, int seed) {
            this.type = type;
            this.x0 = x0;
            this.y0 = y0;
            this.z0 = z0;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.blockSlot = blockSlot;
            this.seed = seed;
        }
    }

    public static final class Spawner {
        public final int x;
        public final int y;
        public final int z;
        public final int kind;
        public final int level;
        public final String mobId;
        public final long seed;

        public Spawner(int x, int y, int z, int kind, int level, String mobId, long seed) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.kind = kind;
            this.level = level;
            this.mobId = mobId;
            this.seed = seed;
        }
    }

    public static final class Chest {
        public final int x;
        public final int y;
        public final int z;
        public final int kind;
        public final int level;
        public final String lootTable;
        public final long seed;

        public Chest(int x, int y, int z, int kind, int level, String lootTable, long seed) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.kind = kind;
            this.level = level;
            this.lootTable = lootTable;
            this.seed = seed;
        }
    }
}
