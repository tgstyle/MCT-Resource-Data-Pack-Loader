package mctmods.resourcedatapackloader.content.def;

public final class SkyPoseDef {
    public static final SkyPoseDef NONE = new SkyPoseDef(0.0F, 0.0F);
    public final float roll;
    public final float spin;

    public SkyPoseDef(float roll, float spin) {
        this.roll = roll;
        this.spin = spin;
    }

    public boolean spins() { return spin != 0.0F; }

    public float degrees(double ticks) { return (float) ((roll + spin * ticks / 20.0D) % 360.0D); }

    public static float spun(float spin, double ticks) { return (float) ((spin * ticks / 20.0D) % 360.0D); }
}
