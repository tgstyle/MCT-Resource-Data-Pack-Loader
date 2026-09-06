package mctmods.resourcedatapackloader.content.def;

public record SpreadDef(String type, int center, int range, int smoothness, int veinHeight, int veinDiameter, int verticalDensity, int horizontalDensity, int offsetMin, int offsetMax, boolean ceiling) {
    public static final String EVEN = "even";
    public static final String CENTERED = "centered";
    public static final String SPRAWL = "sprawl";
    public static final String TERRAIN = "terrain";
    public static final String CAVERN = "cavern";
    public static final String SUBMERGED = "submerged";

    public static SpreadDef even() { return new SpreadDef(EVEN, 32, 16, 2, 32, 12, 16, 32, 0, 0, false); }

    public boolean isSprawl() { return SPRAWL.equals(type); }
}
