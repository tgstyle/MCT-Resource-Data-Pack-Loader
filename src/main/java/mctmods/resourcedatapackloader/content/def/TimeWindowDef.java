package mctmods.resourcedatapackloader.content.def;

import java.util.Set;

public record TimeWindowDef(int from, int to, float fogDensity, int fogColor, boolean sampleFog, int sunColor, int sunAuraColor, boolean precipitation, boolean alwaysRain, Set<String> given) {
    public static final String FOG_DENSITY = "sky.fogDensity";
    public static final String FOG_COLOR = "sky.fogColor";
    public static final String SUN_COLOR = "sky.sun.color";
    public static final String SUN_AURA = "sky.sun.auraColor";
    public static final String PRECIPITATION = "weather.precipitation";
    public static final String ALWAYS_RAIN = "weather.alwaysRain";

    public boolean gives(String key) { return given.contains(key); }

    public boolean contains(long tick) { return from <= to ? tick >= from && tick < to : tick >= from || tick < to; }

    public boolean holdsDry() { return gives(PRECIPITATION) && !precipitation; }
}
