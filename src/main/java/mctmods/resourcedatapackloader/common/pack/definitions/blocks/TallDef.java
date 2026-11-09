package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

public record TallDef(int parts, int[] rise, String produceFrom, boolean regrow) {
    public static final int MIN_PARTS = 2;
    public static final int MAX_PARTS = 4;
    public static final int DEFAULT_PARTS = 3;
    public static final String FROM_BASE = "base";
    public static final String FROM_ALL = "all";

    public int riseAt(int part) { return rise[part - 1]; }

    public static int[] defaultRise(int parts, int maxAge) {
        int[] rise = new int[parts - 1];
        for (int n = 1; n < parts; n++) { rise[n - 1] = (maxAge * n + parts - 1) / parts; }
        return rise;
    }
}
