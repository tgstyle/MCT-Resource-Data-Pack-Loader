package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record RoomKindDef(String kind, int weight, int minLevel, int maxLevel, int maxPerDungeon, int maxPerLevel, @Nullable int[] minSize, @Nullable int[] height, List<PickDef> shapes, List<PickDef> templates, int yOffset, List<PickDef> turns, List<String> decor, String mobs, String loot, @Nullable Amount spawners, @Nullable Amount chests, Map<String, DungeonDef.Degree> rolls, int decay, boolean decayTemplate, boolean node) {
    public static final String HALL = "hall";
    public static final String DEFAULT_MOBS = "common";
    public static final String DEFAULT_LOOT = "basic";
    public static final int DEFAULT_WEIGHT = 1;
    public static final int ANY = -1;
    public static final int NO_CAP = -1;

    public static RoomKindDef hall() {
        return new RoomKindDef(HALL, DEFAULT_WEIGHT, ANY, ANY, NO_CAP, NO_CAP, null, null, List.of(new PickDef("box", 1)), List.of(), 0, List.of(), List.of(), DEFAULT_MOBS, DEFAULT_LOOT, null, null, Map.of(), 0, false, false);
    }

    public boolean templated() { return !templates.isEmpty(); }

    public boolean allowsLevel(int level) { return (minLevel == ANY || level >= minLevel) && (maxLevel == ANY || level <= maxLevel); }

    public record Amount(boolean percent, int min, int max) {}
}
