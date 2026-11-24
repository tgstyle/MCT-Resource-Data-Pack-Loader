package mctmods.resourcedatapackloader.common.pack.definitions.sky;

public final class SkyPoseDef {
    public static final SkyPoseDef NONE = new SkyPoseDef(0.0F, 0.0F, false, false);
    public final float roll;
    public final float spin;
    public final boolean flipU;
    public final boolean flipV;

    public SkyPoseDef(float roll, float spin, boolean flipU, boolean flipV) {
        this.roll = roll;
        this.spin = spin;
        this.flipU = flipU;
        this.flipV = flipV;
    }

    public boolean spins() { return spin != 0.0F; }

    public float u(float edge) { return flipU ? 1.0F - edge : edge; }

    public float v(float edge) { return flipV ? 1.0F - edge : edge; }

    public float degrees(double ticks) { return (float) ((roll + turn(spin, ticks)) % 360.0D); }

    public static float spun(float spin, double ticks) { return (float) (turn(spin, ticks) % 360.0D); }

    private static double turn(float spin, double ticks) { return spin * ticks / 20.0D; }
}
