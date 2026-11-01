package mctmods.resourcedatapackloader.common.pack.definitions.sky;

public record SkyPoseDef(float roll, float spin) {
    public static final SkyPoseDef NONE = new SkyPoseDef(0.0F, 0.0F);

    public boolean spins() { return spin != 0.0F; }

    public float degrees(double ticks) { return (float) ((roll + spin * ticks / 20.0D) % 360.0D); }

    public static float spun(float spin, double ticks) { return (float) ((spin * ticks / 20.0D) % 360.0D); }
}
