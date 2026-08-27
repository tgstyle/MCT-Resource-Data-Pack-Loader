package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentControl {
    public static final String ORES = "ores";
    public static final String BIOMES = "biomes";
    public static final String GENERATORS = "generators";
    public static final String STRUCTURES = "structures";
    public static final String SPAWNING = "spawning";
    public static final String BEDROCK = "bedrock";
    public static final String VOID = "void";
    public static final String RECIPES = "recipes";
    public static final String TERRAIN = "terrain";
    public static final String REPLACEMENTS = "replacements";
    public static final String VILLAGES = "villages";
    public static final String ENTITIES = "entities";
    public static final String CHUNKS = "chunks";
    public static final String BLAST_PLASTER = "blastPlaster";
    public static final String COMMANDS = "commands";
    private static final String DEFAULT = "default";
    private static final String GLOBAL = "global";
    private static final String OFF = "off";
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Set<String> KNOWN = new LinkedHashSet<>(Arrays.asList(
            "ambientCap", "bedrockLayers", "biomeNames", "biomeNamesAreBlacklist", "biomeWhitelist",
            "biomeTemperatureCenterY", "biomeTemperatureHeightFactor", "biomeTemperatureScaleMaxY",
            "blockBiomeDimensions", "blockBiomeDimensionsAreBlacklist", "blockBiomes", "blockFurnaceRecipes",
            "blockGeneratorDimensions", "blockGeneratorDimensionsAreBlacklist", "blockOres", "blockRecipes",
            "blockReplacementDimensions", "blockReplacementDimensionsAreBlacklist", "blockReplacementKey",
            "blockReplacementMaxHeight", "blockReplacementMinHeight", "blockReplacements", "blockWorldGenerators",
            "blockedFurnaceMods", "blockedGenerators", "blockedRecipeMods", "creatureCap", "dragonFight", "flatBedrock",
            "flatBedrockBiomeTypes", "flatBedrockBiomes", "flatBedrockBiomesAreBlacklist", "flatBedrockDimensions",
            "flatBedrockDimensionsAreBlacklist", "flatBedrockFiller", "flatBedrockFillers", "flatBedrockRoof", "flatBedrockRetrogen", "retrogen", "adoptExistingChunks",
            "furnaceWhitelist", "generatorOptions", "generatorTypeMap", "generatorTypes", "generatorTypesAreBlacklist",
            "generatorWhitelist", "hurryWritesAbove", "logBlockReplacements", "logBlockedBiomes", "logBlockedGenerators", "logBlockedOres",
            "logBlockedRecipes", "monsterCap", "neverSlowed", "oreTypes", "oreTypesAreBlacklist", "blockOreDimensions", "blockOreDimensionsAreBlacklist", "oreWhitelist",
            "pregenAllDimensions", "pregenDimensions", "pregenDimensionsWhenEntered", "pregenKeepLoaded", "pregenMillisPerRound", "pregenOnNewWorld", "pregenPauseAbove",
            "pregenFinishedSays", "pregenRelightSays", "pregenResume", "pregenRunningSays", "pregenSpectatingSays", "pregenStoppedSays", "pregenToBorder", "welcomeSays",
            "recipeMatch", "recipeWhitelist", "slowDistance", "slowDistantEntities", "slowRate", "slowRecheck",
            "slowedKinds", "spawnChunkRadii", "spawnChunkRadius", "structureBiomes", "structureBiomesAreBlacklist",
            "structureMinDistanceFromSpawn", "structureSeparation", "structureSpacing", "structureSpawners", "structureAdaptation", "terrainAdaptation",
            "gotoLevel", "gotoNextLevel", "gotoBackLevel", "gotoPlaceLevels",
            "structureSpawns", "surfaceDayMonsterRate", "monsterSpawnLight", "skyAnimals", "deepStone", "skyStone", "skyShape", "skyIslands", "skyThickness", "skyHeights", "noiseCaves", "deepRavines", "oreVeins",
            "caveRegionCells", "caveRegionCellsY", "caveRegionPlainWeight",
            "verticalCubeLoadDistance", "cubesSentPerTick", "cubeGenMillisPerRound", "cubeGCInterval",
            "surfaceNightMonsterRate", "terrainWorldTypes", "terrainWorldTypesAreBlacklist", "undergroundDayMonsterRate",
            "undergroundNightMonsterRate", "villageBlocks", "villageDecor", "villagePieces", "villagePiecesAreBlacklist",
            "villagePathBlock", "villagePathSupportBlock", "villagePathBridgeBlock", "villagePathExtraWidth",
            "villagePathCenterBlock", "villagePathCenterDash", "villagePathLineBlock", "villagePathSidewalkBlock",
            "villagePathSidewalkWidth", "villagePathMinimumWidth", "villagePathIntersects", "villagePathFlatRun",
            "villagePathBridgeSidewalkBlock", "villagePathBridgeBarrierBlock", "villagePathBridgeBarrierHeight", "voidPlatformBlock",
            "voidPlatformHeight", "voidPlatformSize", "voidWorld", "voidWorldDimensions",
            "voidWorldDimensionsAreBlacklist", "waterCreatureCap", "weatherCeiling", "structureAt", "rubicWorld", "rubicWorldDimensions", "rubicWorldDimensionsAreBlacklist", "terrainOffset", "worldBorder", "worldBelow", "worldAbove", "worldSeamEntities", "worldSeamBedrock", "worldDifficulty", "worldFallDamage", "worldGameMode", "worldGravity", "worldJumpStrength", "worldTerminalVelocity", "worldMaxHeight", "worldMinHeight", "worldName", "worldSeed", "worldSpawn", "worldTime", "worldType", "worldTypeExceptions"));

    private ContentControl() {}

    public static void check(@Nullable WorldTemplateDef template) {
        if (template == null || template.settings == null) { return; }
        for (Map.Entry<String, JsonElement> entry : template.settings.entrySet()) {
            if (KNOWN.contains(entry.getKey())) { continue; }
            ContentLog.LOGGER.error("World template {} sets '{}', which is not a setting anything reads, so it does nothing. Check the spelling against the list of settings in HOWTO.md", template.getKey(), entry.getKey());
        }
    }

    public static boolean off(String group) { return OFF.equals(mode(group)); }

    public static boolean packDecides(String group) { return DEFAULT.equals(mode(group)); }

    public static boolean flag(String group, String key, boolean fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (!value.isJsonPrimitive()) { return rejected(key, "true or false", fallback); }
        return value.getAsBoolean();
    }

    public static int number(String group, String key, int fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) { return rejected(key, "a number", fallback); }
        return value.getAsInt();
    }

    public static float decimal(String group, String key, float fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) { return rejected(key, "a number", fallback); }
        return value.getAsFloat();
    }

    public static String text(String group, String key, String fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (!value.isJsonPrimitive()) { return rejected(key, "a text value", fallback); }
        return value.getAsString();
    }

    public static String[] list(String group, String key, String[] fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (!value.isJsonArray()) { return rejected(key, "a list of text values", fallback); }
        List<String> found = new ArrayList<>();
        for (JsonElement entry : value.getAsJsonArray()) {
            if (!entry.isJsonPrimitive()) { return rejected(key, "a list of text values", fallback); }
            found.add(entry.getAsString());
        }
        return found.toArray(new String[0]);
    }

    public static String[] lines(String group, String key, String[] fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (value.isJsonPrimitive()) { return new String[] {value.getAsString()}; }
        if (!value.isJsonArray()) { return rejected(key, "a text value or a list of them", fallback); }
        List<String> found = new ArrayList<>();
        for (JsonElement entry : value.getAsJsonArray()) {
            if (!entry.isJsonPrimitive()) { return rejected(key, "a text value or a list of them", fallback); }
            found.add(entry.getAsString());
        }
        return found.toArray(new String[0]);
    }

    public static int[] numbers(String group, String key, int[] fallback) {
        JsonElement value = setting(group, key);
        if (value == null) { return fallback; }
        if (!value.isJsonArray()) { return rejected(key, "a list of numbers", fallback); }
        JsonArray array = value.getAsJsonArray();
        int[] found = new int[array.size()];
        for (int i = 0; i < found.length; i++) {
            JsonElement entry = array.get(i);
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isNumber()) { return rejected(key, "a list of numbers", fallback); }
            found[i] = entry.getAsInt();
        }
        return found;
    }

    private static <T> T rejected(String key, String wanted, T fallback) {
        if (WARNED.add(key)) { ContentLog.LOGGER.error("The active world template sets '{}' to something that is not {}, using the global value instead", key, wanted); }
        return fallback;
    }

    @Nullable private static JsonElement setting(String group, String key) {
        if (!packDecides(group)) { return null; }
        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template == null || template.settings == null) { return null; }
        JsonObject settings = template.settings;
        return settings.has(key) ? settings.get(key) : null;
    }

    private static String mode(String group) {
        String held = raw(group);
        String[] known = MODES.get(group);
        if (known != null && Objects.equals(known[0], held)) { return known[1]; }
        String value = held.trim().toLowerCase(Locale.ROOT);
        if (!DEFAULT.equals(value) && !GLOBAL.equals(value) && !OFF.equals(value)) {
            ContentLog.LOGGER.error("control.{} is '{}', which is not default, global or off, using default", group, value);
            value = DEFAULT;
        }
        MODES.put(group, new String[] {held, value});
        return value;
    }

    private static final Map<String, String[]> MODES = new ConcurrentHashMap<>();

    private static String raw(String group) {
        switch (group) {
            case ORES: return Config.control.ores;
            case BIOMES: return Config.control.biomes;
            case GENERATORS: return Config.control.generators;
            case STRUCTURES: return Config.control.structures;
            case SPAWNING: return Config.control.spawning;
            case BEDROCK: return Config.control.bedrock;
            case VOID: return Config.control.voidWorld;
            case RECIPES: return Config.control.recipes;
            case TERRAIN: return Config.control.terrain;
            case REPLACEMENTS: return Config.control.replacements;
            case VILLAGES: return Config.control.villages;
            case ENTITIES: return Config.control.entities;
            case CHUNKS: return Config.control.chunks;
            case BLAST_PLASTER: return Config.control.blastPlaster;
            case COMMANDS: return Config.control.commands;
            default: return DEFAULT;
        }
    }
}
