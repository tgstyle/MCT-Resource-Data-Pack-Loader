package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DungeonSizeDef {
    public static final int MIN_FOOTPRINT = 48;
    public static final int MAX_FOOTPRINT = 176;
    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_FOOTPRINT = 112;
    public static final int[] DEFAULT_LEVELS = {4, 7};
    public static final int[] DEFAULT_ROOMS = {15, 30};
    public static final List<DungeonSizeDef> BUILT_IN = Collections.unmodifiableList(Arrays.asList(
            new DungeonSizeDef("small", 30, 80, new int[]{2, 5}, new int[]{10, 20}),
            new DungeonSizeDef("medium", 25, 112, new int[]{4, 7}, new int[]{15, 30}),
            new DungeonSizeDef("large", 25, 144, new int[]{6, 9}, new int[]{20, 40}),
            new DungeonSizeDef("vast", 20, 176, new int[]{8, 11}, new int[]{25, 50})));
    public final String name;
    public final int weight;
    public final int footprint;
    public final int[] levels;
    public final int[] rooms;

    public DungeonSizeDef(String name, int weight, int footprint, int[] levels, int[] rooms) {
        this.name = name;
        this.weight = weight;
        this.footprint = footprint;
        this.levels = levels;
        this.rooms = rooms;
    }
}
