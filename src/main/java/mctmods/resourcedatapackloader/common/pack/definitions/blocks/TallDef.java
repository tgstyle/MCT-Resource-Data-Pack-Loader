package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

public record TallDef(int parts, int[] rise, String produceFrom, boolean regrow) {
    public static final int MIN_PARTS = 2;
    public static final int MAX_PARTS = 4;
    public static final int DEFAULT_PARTS = 3;
    public static final String BASE = "base";
    public static final String ALL = "all";

    public static int[] defaultRise(int parts, int maxAge) {
        int[] rise = new int[parts - 1];
        for (int part = 1; part < parts; part++) { rise[part - 1] = (maxAge * part + parts - 1) / parts; }
        return rise;
    }

    public int riseAt(int part) { return rise[part - 1]; }
}
