package mctmods.resourcedatapackloader.content.def;

public final class SpreadDef {
    public static final String EVEN = "even";
    public static final String CENTERED = "centered";
    public static final String SPRAWL = "sprawl";
    public static final String TERRAIN = "terrain";
    public static final String CAVERN = "cavern";
    public static final String SUBMERGED = "submerged";
    public final String type;
    public final int center;
    public final int range;
    public final int smoothness;
    public final int veinHeight;
    public final int veinDiameter;
    public final int verticalDensity;
    public final int horizontalDensity;
    public final int offsetMin;
    public final int offsetMax;
    public final boolean ceiling;

    public SpreadDef(String type, int center, int range, int smoothness, int veinHeight, int veinDiameter, int verticalDensity, int horizontalDensity, int offsetMin, int offsetMax, boolean ceiling) {
        this.type = type;
        this.center = center;
        this.range = range;
        this.smoothness = smoothness;
        this.veinHeight = veinHeight;
        this.veinDiameter = veinDiameter;
        this.verticalDensity = verticalDensity;
        this.horizontalDensity = horizontalDensity;
        this.offsetMin = offsetMin;
        this.offsetMax = offsetMax;
        this.ceiling = ceiling;
    }

    public static SpreadDef even() { return new SpreadDef(EVEN, 32, 16, 2, 32, 12, 16, 32, 0, 0, false); }

    public boolean isSprawl() { return SPRAWL.equals(type); }
}
