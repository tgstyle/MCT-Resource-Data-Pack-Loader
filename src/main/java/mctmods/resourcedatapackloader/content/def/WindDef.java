package mctmods.resourcedatapackloader.content.def;

public final class WindDef {
    public final float gust;
    public final int everyMin;
    public final int everyMax;
    public final float swing;

    public WindDef(float gust, int[] every, float swing) {
        this.gust = gust;
        this.everyMin = every[0];
        this.everyMax = every[1];
        this.swing = swing;
    }
}
