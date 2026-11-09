package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

public final class TallDef {
    public static final int MIN_PARTS = 2;
    public static final int MAX_PARTS = 4;
    public static final int DEFAULT_PARTS = 3;
    public static final String FROM_BASE = "base";
    public static final String FROM_ALL = "all";
    public final int parts;
    public final int[] rise;
    public final String produceFrom;
    public final boolean regrow;

    public TallDef(int parts, int[] rise, String produceFrom, boolean regrow) {
        this.parts = parts;
        this.rise = rise;
        this.produceFrom = produceFrom;
        this.regrow = regrow;
    }

    public static int[] defaultRise(int parts, int maxAge) {
        int[] rise = new int[parts - 1];
        for (int index = 0; index < rise.length; index++) { rise[index] = (maxAge * (index + 1) + parts - 1) / parts; }
        return rise;
    }

    public int riseAt(int part) { return rise[part - 1]; }
}
