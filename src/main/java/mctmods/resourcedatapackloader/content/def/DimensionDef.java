package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;

public final class DimensionDef {
    public static final String OVERWORLD = "overworld";
    public static final String FLAT = "flat";
    public static final String VOID = "void";
    public static final String NETHER = "nether";
    public static final String END = "end";
    public static final String SINGLE = "single";
    public static final String INHERIT = "inherit";
    public final ResourceLocation registryName;
    public final int id;
    public final String suffix;
    public final boolean keepLoaded;
    public final String terrain;
    public final String generatorOptions;
    public final boolean structures;
    public final String biomeSource;
    public final String biome;
    public final boolean hasSkyLight;
    public final boolean surfaceWorld;
    public final boolean respawn;
    public final boolean spawning;
    public final int cloudHeight;
    public final int groundLevel;
    public final double movementFactor;
    public final int fogColor;
    public final int skyColor;
    public final int fixedTime;
    public final boolean sunriseColors;
    public final boolean nether;
    public final boolean beds;
    public final boolean waterVaporizes;
    public final boolean showFog;
    public final float ambientLight;
    public final float starBrightness;
    public final int cloudColor;
    public final int respawnDimension;
    public final Map<String, String> gameRules;
    public final List<String> requires;

    public DimensionDef(ResourceLocation registryName, int id, String suffix, boolean keepLoaded, String terrain, String generatorOptions, boolean structures, String biomeSource, String biome, boolean hasSkyLight, boolean surfaceWorld, boolean respawn, boolean spawning, int cloudHeight, int groundLevel, double movementFactor, int fogColor, int skyColor, int fixedTime, boolean sunriseColors, boolean nether, boolean beds, boolean waterVaporizes, boolean showFog, float ambientLight, float starBrightness, int cloudColor, int respawnDimension, Map<String, String> gameRules, List<String> requires) {
        this.registryName = registryName;
        this.id = id;
        this.suffix = suffix;
        this.keepLoaded = keepLoaded;
        this.terrain = terrain;
        this.generatorOptions = generatorOptions;
        this.structures = structures;
        this.biomeSource = biomeSource;
        this.biome = biome;
        this.hasSkyLight = hasSkyLight;
        this.surfaceWorld = surfaceWorld;
        this.respawn = respawn;
        this.spawning = spawning;
        this.cloudHeight = cloudHeight;
        this.groundLevel = groundLevel;
        this.movementFactor = movementFactor;
        this.fogColor = fogColor;
        this.skyColor = skyColor;
        this.fixedTime = fixedTime;
        this.sunriseColors = sunriseColors;
        this.nether = nether;
        this.beds = beds;
        this.waterVaporizes = waterVaporizes;
        this.showFog = showFog;
        this.ambientLight = ambientLight;
        this.starBrightness = starBrightness;
        this.cloudColor = cloudColor;
        this.respawnDimension = respawnDimension;
        this.gameRules = gameRules;
        this.requires = requires;
    }

    public String getName() { return registryName.getNamespace() + "_" + registryName.getPath(); }
}
