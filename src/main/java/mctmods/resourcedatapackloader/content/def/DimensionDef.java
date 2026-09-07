package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record DimensionDef(ResourceLocation key, String terrain, List<String> flatLayers, boolean structures, String biomeSource, String biome, int minHeight, int maxHeight, int seaLevel, boolean lavaOceans,
                           boolean hasSkyLight, boolean surfaceWorld, boolean spawning, int cloudHeight, double movementFactor, int fogColor, int skyColor, int cloudColor, long fixedTime, boolean sunriseColors,
                           boolean nether, boolean beds, boolean waterVaporizes, boolean showFog, float ambientLight, float starBrightness, boolean renderSky, boolean renderClouds, boolean renderWeather,
                           @Nullable ResourceLocation respawnDimension, Map<String, String> gameRules, List<String> requires, @Nullable DimensionPortalDef portal) {
    public static final String OVERWORLD = "overworld";
    public static final String FLAT = "flat";
    public static final String VOID = "void";
    public static final String NETHER = "nether";
    public static final String END = "end";
    public static final String SINGLE = "single";
    public static final String INHERIT = "inherit";
    public static final int UNSET = Integer.MIN_VALUE;

    public boolean shapesHeight() { return minHeight != UNSET && maxHeight != UNSET; }

    public boolean shapesNoise() { return shapesHeight() || seaLevel >= 0 || lavaOceans; }

    public boolean hasEffects() { return cloudHeight >= 0 || fogColor >= 0 || showFog || !sunriseColors || !renderSky || !renderClouds || !renderWeather; }

    public String base() {
        return switch (terrain) {
            case NETHER -> "the_nether";
            case END -> "the_end";
            default -> "overworld";
        };
    }
}
