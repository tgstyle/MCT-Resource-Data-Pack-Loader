package mctmods.resourcedatapackloader.core.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Colors {
    public static final int WHITE_TEXT = 0xFFFFFF;
    public static final int GREY_TEXT = 0x808080;
    public static final int GREEN_TEXT = 0x55FF55;

    private Colors() {}

    public static int red(int color) { return color >> 16 & 255; }

    public static int green(int color) { return color >> 8 & 255; }

    public static int blue(int color) { return color & 255; }

    public static int rgb(int red, int green, int blue) { return red << 16 | green << 8 | blue; }

    public static float redF(int color) { return red(color) / 255.0F; }

    public static float greenF(int color) { return green(color) / 255.0F; }

    public static float blueF(int color) { return blue(color) / 255.0F; }

    public static double redD(int color) { return red(color) / 255.0D; }

    public static double greenD(int color) { return green(color) / 255.0D; }

    public static double blueD(int color) { return blue(color) / 255.0D; }

    public static int withAlpha(int rgb, int alpha) { return alpha << 24 | rgb & 0xFFFFFF; }

    public static int withAlpha(int rgb, float alpha) { return withAlpha(rgb, Math.round(MathHelper.clamp(alpha, 0.0F, 1.0F) * 255.0F)); }

    public static Vec3d vec(int color) { return new Vec3d(redD(color), greenD(color), blueD(color)); }

    public static float[] unit(int color, float alpha) { return new float[] {redF(color), greenF(color), blueF(color), alpha}; }
}
