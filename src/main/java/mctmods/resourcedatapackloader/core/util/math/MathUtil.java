package mctmods.resourcedatapackloader.core.util.math;

import net.minecraft.util.Mth;

public final class MathUtil {
    private MathUtil() {}

    public static float ramp(float left, float span) { return left < span ? left / span : 1.0F; }

    public static float drain(float elapsed, float span) { return elapsed < span ? 1.0F - elapsed / span : 0.0F; }

    public static float unlerp(float v, float min, float max) { return (v - min) / (max - min); }

    public static double lerp(double a, double min, double max) { return min + a * (max - min); }

    public static double away(int block, double center, double radius, double cellOffset) {
        double offset = (block + cellOffset - center) / radius;
        return offset * offset;
    }

    public static double asinClamped(double value) { return Math.asin(Mth.clamp(value, -1.0D, 1.0D)); }

    public static float wave(float angle, float middle, float amplitude) { return middle + amplitude * Mth.sin(angle); }

    public static int percent(long hash) { return (int) Math.floorMod(hash, 100L); }

    public static int gap(int v, int lo, int hi) { return Math.max(0, Math.max(lo - v, v - hi)); }

    public static long dist2(int ax, int az, int bx, int bz) {
        long dx = ax - bx;
        long dz = az - bz;
        return dx * dx + dz * dz;
    }

    public static float smoothstep(float t) {
        float clamped = Mth.clamp(t, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }
}
