package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.DimensionPortalDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.util.worldgen.LegacyTerrainShape;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record DimensionDef(Identifier key, String terrain, List<String> flatOptions, boolean structures, String biomeSource, String biome, List<PickDef> biomeList, int minHeight, int maxHeight, int seaLevel, boolean lavaOceans, String oceanFluid,
                           boolean hasSkyLight, boolean surfaceWorld, boolean spawning, int cloudHeight, double movementFactor, int fogColor, int skyColor, int cloudColor, long fixedTime, boolean sunriseColors,
                           boolean nether, boolean beds, boolean waterVaporizes, boolean showFog, float ambientLight, float starBrightness, boolean renderSky, boolean renderClouds, boolean renderWeather,
                           @Nullable Identifier respawnDimension, boolean respawn, int groundLevel, Map<String, String> gameRules, List<String> requires, @Nullable DimensionPortalDef portal, JsonObject options,
                           DimensionTraitsDef traits, @Nullable SkyLookDef look, List<TimeWindowDef> windows, Set<String> given) {
    public static final String OVERWORLD = "overworld";
    public static final String FLAT = "flat";
    public static final String VOID = "void";
    public static final String NETHER = "nether";
    public static final String END = "end";
    public static final String SINGLE = "single";
    public static final String INHERIT = "inherit";
    public static final String LIST = "list";
    public static final int UNSET = Integer.MIN_VALUE;

    public boolean gives(String option) { return given.contains(option); }

    public boolean shapesHeight() { return minHeight != UNSET && maxHeight != UNSET; }

    public boolean shapesNoise() { return shapesHeight() || seaLevel >= 0 || lavaOceans || !oceanFluid.isEmpty() || LegacyTerrainShape.of(options).any(); }

    public boolean hasEffects() { return cloudHeight >= 0 || fogColor >= 0 || showFog || !sunriseColors || !renderSky || !renderClouds || !renderWeather || !surfaceWorld || traits.sky() != null || traits.rain() != null; }

    public String base() {
        return switch (terrain) {
            case NETHER -> "the_nether";
            case END -> "the_end";
            default -> "overworld";
        };
    }
}
