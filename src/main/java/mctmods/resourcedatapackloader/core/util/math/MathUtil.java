package mctmods.resourcedatapackloader.core.util.math;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class MathUtil {
    public static final long GOLDEN_GAMMA = 0x9E3779B97F4A7C15L;
    public static final long NANOS_PER_MS = 1000000L;
    public static final float DEG_TO_RAD = 0.017453292F;
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MINUTE = 1200;

    public static float unlerp(final long v, final long min, final long max) { return (v - min) / (float) (max - min); }

    public static float unlerp(final float v, final float min, final float max) { return (v - min) / (max - min); }

    public static float lerp(final float a, final float min, final float max) { return min + a * (max - min); }

    public static float smoothstep(float t) {
        float clamped = MathHelper.clamp(t, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    public static boolean within(int v, int start, int length) { return v >= start && v < start + length; }

    public static boolean inside(int x, int y, int left, int top, int width, int height) { return within(x, left, width) && within(y, top, height); }

    public static float ramp(float left, float span) { return left < span ? left / span : 1.0F; }

    public static float drain(float elapsed, float span) { return elapsed < span ? 1.0F - elapsed / span : 0.0F; }

    public static double lerp(final double a, final double min, final double max) { return min + a * (max - min); }

    public static double away(int block, double center, double radius, double cellOffset) {
        double offset = (block + cellOffset - center) / radius;
        return offset * offset;
    }

    public static float unitFloat(long hash) { return (hash >>> 40) / (float) (1 << 24); }

    public static int percent(long hash) { return (int) Math.floorMod(hash, 100L); }

    public static double asinClamped(final double value) { return Math.asin(MathHelper.clamp(value, -1.0D, 1.0D)); }

    public static float wave(final float angle, final float middle, final float amplitude) { return middle + amplitude * MathHelper.sin(angle); }

    public static int roundRandom(final float value, final Random rand) {
        int floor = (int) Math.floor(value);
        return floor + (rand.nextFloat() < value - floor ? 1 : 0);
    }

    public static int randomIn(final Random random, final int low, final int high) { return low + random.nextInt(high - low + 1); }

    public static float turn(final float fraction) { return fraction * (float) Math.PI * 2.0F; }

    public static long mix(long seed, int x, int y, int z) { return scramble(seed ^ 0x28B7BD766A05068BL, x, y, z); }

    public static long scramble(long h, int x, int y, int z) {
        h ^= x * 0x2545F4914F6CDD1DL;
        h ^= (long) y * 0x6C62272E07BB0142L;
        h ^= (long) z * 0xCBF29CE484222325L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return h;
    }

    public static int mid(int lo, int hi) { return (lo + hi) / 2; }

    public static long dist2(int ax, int az, int bx, int bz) {
        long dx = ax - bx;
        long dz = az - bz;
        return dx * dx + dz * dz;
    }

    public static int gap(int v, int lo, int hi) { return Math.max(0, Math.max(lo - v, v - hi)); }

    public static int max(int a, int b, int c, int d) { return Math.max(Math.max(a, b), Math.max(c, d)); }
}
