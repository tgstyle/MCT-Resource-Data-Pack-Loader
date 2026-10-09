package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.util.Settings;

public final class ContentTints {
    public static final int WHITE = 0xFFFFFF;
    public static final String GRASS = "grass";
    public static final String WATER = "water";
    private static final String BIOME = "biome";
    private static final String FOLIAGE = "foliage";
    private static final String NONE = "none";

    private ContentTints() {}

    public static String mode(String tint) { return tint == null ? "" : Settings.lower(tint); }

    public static int fixed(String tint, Object context) {
        return switch (tint) {
            case BIOME, FOLIAGE, GRASS, WATER -> -1;
            case NONE -> WHITE;
            default -> ContentParser.color(tint, context);
        };
    }
}
