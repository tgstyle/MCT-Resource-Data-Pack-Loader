package mctmods.resourcedatapackloader.content.def;

import java.util.Set;

public final class TimeWindowDef {
    public static final String FOG_DENSITY = "sky.fogDensity";
    public static final String FOG_COLOR = "sky.fogColor";
    public static final String SUN_COLOR = "sky.sun.color";
    public static final String SUN_AURA = "sky.sun.auraColor";
    public static final String PRECIPITATION = "weather.precipitation";
    public static final String ALWAYS_RAIN = "weather.alwaysRain";
    public final int from;
    public final int to;
    public final float fogDensity;
    public final int fogColor;
    public final boolean sampleFog;
    public final int sunColor;
    public final int sunAuraColor;
    public final boolean precipitation;
    public final boolean alwaysRain;
    private final Set<String> given;

    public TimeWindowDef(int from, int to, float fogDensity, int fogColor, boolean sampleFog, int sunColor, int sunAuraColor, boolean precipitation, boolean alwaysRain, Set<String> given) {
        this.from = from;
        this.to = to;
        this.fogDensity = fogDensity;
        this.fogColor = fogColor;
        this.sampleFog = sampleFog;
        this.sunColor = sunColor;
        this.sunAuraColor = sunAuraColor;
        this.precipitation = precipitation;
        this.alwaysRain = alwaysRain;
        this.given = given;
    }

    public boolean gives(String key) { return given.contains(key); }

    public boolean contains(long tick) { return from <= to ? tick >= from && tick < to : tick >= from || tick < to; }

    public boolean holdsDry() { return gives(PRECIPITATION) && !precipitation; }
}
