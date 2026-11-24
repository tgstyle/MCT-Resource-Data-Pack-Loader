package mctmods.resourcedatapackloader.core.util.math;

import net.minecraft.util.Mth;

public final class Colors {
    public static final int WHITE_TEXT = 0xFFFFFF;
    public static final int GREY_TEXT = 0x808080;
    public static final int GREEN_TEXT = 0x55FF55;

    private Colors() {}

    public static int red(int color) { return color >> 16 & 255; }

    public static int green(int color) { return color >> 8 & 255; }

    public static int blue(int color) { return color & 255; }

    public static int overlay(int color, int alpha) { return alpha << 24 | color; }

    public static int withAlpha(int rgb, int alpha) { return alpha << 24 | rgb & 0xFFFFFF; }

    public static int withAlpha(int rgb, float alpha) { return withAlpha(rgb, Math.round(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F)); }
}
