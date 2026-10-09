package mctmods.resourcedatapackloader.util;

public final class Colors {
    private Colors() {}

    public static int red(int color) { return color >> 16 & 255; }

    public static int green(int color) { return color >> 8 & 255; }

    public static int blue(int color) { return color & 255; }
}
