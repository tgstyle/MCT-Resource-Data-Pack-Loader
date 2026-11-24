package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class RoomKindDef {
    public static final String HALL = "hall";
    public static final String BOX = "box";
    public static final String DEFAULT_MOBS = "common";
    public static final String DEFAULT_LOOT = "basic";
    public static final int DEFAULT_WEIGHT = 1;
    public static final int ANY = -1;
    public static final int NO_CAP = -1;
    public final String kind;
    public final int weight;
    public final int minLevel;
    public final int maxLevel;
    public final int maxPerDungeon;
    public final int maxPerLevel;
    @Nullable public final int[] minSize;
    @Nullable public final int[] height;
    public final List<PickDef> shapes;
    public final List<PickDef> templates;
    public final int yOffset;
    public final List<PickDef> turns;
    public final List<String> decor;
    public final String mobs;
    public final String loot;
    @Nullable public final Amount spawners;
    @Nullable public final Amount chests;
    public final Map<String, DungeonDef.Degree> rolls;
    public final int decay;
    public final boolean decayTemplate;
    public final boolean node;

    public RoomKindDef(String kind, int weight, int minLevel, int maxLevel, int maxPerDungeon, int maxPerLevel, @Nullable int[] minSize, @Nullable int[] height, List<PickDef> shapes, List<PickDef> templates, int yOffset, List<PickDef> turns, List<String> decor, String mobs, String loot, @Nullable Amount spawners, @Nullable Amount chests, Map<String, DungeonDef.Degree> rolls, int decay, boolean decayTemplate, boolean node) {
        this.kind = kind;
        this.weight = weight;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.maxPerDungeon = maxPerDungeon;
        this.maxPerLevel = maxPerLevel;
        this.minSize = minSize;
        this.height = height;
        this.shapes = shapes;
        this.templates = templates;
        this.yOffset = yOffset;
        this.turns = turns;
        this.decor = decor;
        this.mobs = mobs;
        this.loot = loot;
        this.spawners = spawners;
        this.chests = chests;
        this.rolls = rolls;
        this.decay = decay;
        this.decayTemplate = decayTemplate;
        this.node = node;
    }

    public static RoomKindDef hall() {
        return new RoomKindDef(HALL, DEFAULT_WEIGHT, ANY, ANY, NO_CAP, NO_CAP, null, null, Collections.singletonList(new PickDef(BOX, 1)), Collections.emptyList(), 0, Collections.emptyList(), Collections.emptyList(), DEFAULT_MOBS, DEFAULT_LOOT, null, null, Collections.emptyMap(), 0, false, false);
    }

    public boolean templated() { return !templates.isEmpty(); }

    public boolean allowsLevel(int level) { return (minLevel == ANY || level >= minLevel) && (maxLevel == ANY || level <= maxLevel); }

    public static final class Amount {
        public final boolean percent;
        public final int min;
        public final int max;

        public Amount(boolean percent, int min, int max) {
            this.percent = percent;
            this.min = min;
            this.max = max;
        }
    }
}
