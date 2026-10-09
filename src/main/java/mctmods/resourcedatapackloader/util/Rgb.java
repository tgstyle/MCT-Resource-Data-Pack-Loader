package mctmods.resourcedatapackloader.util;

import net.minecraft.util.FastColor;

public final class Rgb {
    private Rgb() {}

    public static float channel(int rgb, int shift) { return (rgb >> shift & 255) / 255.0F; }

    public static float red(int rgb) { return FastColor.ARGB32.red(rgb) / 255.0F; }

    public static float green(int rgb) { return FastColor.ARGB32.green(rgb) / 255.0F; }

    public static float blue(int rgb) { return FastColor.ARGB32.blue(rgb) / 255.0F; }
}
