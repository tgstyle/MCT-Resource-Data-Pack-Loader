package mctmods.resourcedatapackloader.content.worldgen;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.pack.port.Ids;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

public final class ContentTerrain {
    public static final String HARDCORE = "hardcore";
    private static final Set<String> WARNED = new HashSet<>();

    private ContentTerrain() {}

    private static String text(String key, String fallback) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, key, fallback).trim();
    }

    public static String worldSeed() { return text("worldSeed", Config.worldgen.worldSeed()); }

    public static String worldName() { return text("worldName", Config.worldgen.worldName()); }

    public static LevelSettings newWorld(LevelSettings settings) {
        String mode = ContentServer.worldGameMode().toLowerCase(Locale.ROOT);
        if (mode.isEmpty()) { return settings; }
        boolean hardcore = HARDCORE.equals(mode);
        GameType asked = hardcore ? GameType.SURVIVAL : GameType.byName(mode, null);
        if (asked == null) {
            if (WARNED.add("gamemode." + mode)) { ContentLog.LOGGER.error("A pack asks for the game mode '{}', which is not one of survival, hardcore, creative, adventure or spectator, so '{}' is played the way it was chosen", mode, settings.levelName()); }
            return settings;
        }
        Summary.info("terrain.gamemode", "Starting every new world in " + (hardcore ? HARDCORE : asked.getName()) + ", which is what a pack asks for");
        return new LevelSettings(settings.levelName(), asked, settings.hardcore() || hardcore, settings.difficulty(), settings.allowCommands() || asked == GameType.CREATIVE, settings.gameRules(), settings.getDataConfiguration());
    }

    public static WorldOptions newWorld(WorldOptions options) {
        String seed = worldSeed();
        Boolean structures = ContentServer.structures();
        if (seed.isEmpty() && structures == null) { return options; }
        if (!seed.isEmpty()) { Summary.info("terrain.seed", "Making every new world with the seed " + seed + ", which is what a pack asks for"); }
        if (structures != null) { Summary.info("server.structures", "Making every new world " + (structures ? "with" : "without") + " structures, which is what a pack asks for"); }
        long made = seed.isEmpty() ? options.seed() : WorldOptions.parseSeed(seed).orElse(options.seed());
        return new WorldOptions(made, structures == null ? options.generateStructures() : structures, options.generateBonusChest());
    }

    public static String worldType() { return text("worldType", Config.worldgen.worldType()); }

    public static List<String> worldTypeExceptions() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return Config.worldgen.worldTypeExceptions(); }
        return ContentControl.list(ContentControl.TERRAIN, "worldTypeExceptions", Config.worldgen.worldTypeExceptions());
    }

    @Nullable public static JsonObject generatorOptions() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return ContentControl.object(ContentControl.TERRAIN, "generatorOptions", Config.worldgen.generatorOptions());
    }

    @Nullable public static JsonObject customizedOptions() { return FLAT_TYPES.contains(worldType().toLowerCase(Locale.ROOT)) ? null : generatorOptions(); }

    public static void properties(Map<String, String> asked, String levelType) {
        String seed = worldSeed();
        if (!seed.isEmpty()) { asked.put("level-seed", seed); }
        if (ContentWorldShape.kept(levelType)) { return; }
        String type = ContentWorldShape.levelType();
        if (type != null) { asked.put("level-type", type); }
        JsonObject settings = generatorSettings();
        if (settings != null) { asked.put("generator-settings", settings.toString()); }
    }

    @Nullable private static JsonObject generatorSettings() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        JsonObject flat = ContentWorldShape.flatSettings();
        if (flat == null) { return customizedOptions(); }
        return String.join("", ContentControl.lines(ContentControl.TERRAIN, "generatorOptions", List.of(Config.worldgen.generatorOptions()))).isBlank() ? null : flat;
    }

    public record Flat(List<String> layers, List<String> structures, boolean decorated, boolean waterLakes, boolean lakes, String biome) {}

    private static final Map<String, List<String>> FLAT_STRUCTURES = Map.of("village", List.of("minecraft:villages"), "mineshaft", List.of("minecraft:mineshafts"), "stronghold", List.of("minecraft:strongholds"),
            "oceanmonument", List.of("minecraft:ocean_monuments"), "mansion", List.of("minecraft:woodland_mansions"), "biome_1", List.of("minecraft:desert_pyramids", "minecraft:jungle_temples", "minecraft:swamp_huts", "minecraft:igloos"));
    private static final List<String> LEGACY_BIOMES = List.of("ocean", "plains", "desert", "extreme_hills", "forest", "taiga", "swampland", "river", "hell", "sky", "frozen_ocean", "frozen_river", "ice_flats",
            "ice_mountains", "mushroom_island", "mushroom_island_shore", "beaches", "desert_hills", "forest_hills", "taiga_hills", "smaller_extreme_hills", "jungle", "jungle_hills", "jungle_edge", "deep_ocean",
            "stone_beach", "cold_beach", "birch_forest", "birch_forest_hills", "roofed_forest", "taiga_cold", "taiga_cold_hills", "redwood_taiga", "redwood_taiga_hills", "extreme_hills_with_trees", "savanna",
            "savanna_rock", "mesa", "mesa_rock", "mesa_clear_rock");
    private static final String PLAINS = "minecraft:plains";
    private static final List<String> FLAT_TYPES = List.of("flat", "superflat");

    public static Flat flat() { return flat(ContentControl.off(ContentControl.TERRAIN) ? List.of(Config.worldgen.generatorOptions()) : ContentControl.lines(ContentControl.TERRAIN, "generatorOptions", List.of(Config.worldgen.generatorOptions()))); }

    public static Flat flat(List<String> written) {
        List<String> layers = new ArrayList<>();
        List<String> structures = new ArrayList<>();
        boolean decorated = false;
        boolean waterLakes = false;
        boolean lakes = false;
        String biome = PLAINS;
        if (written.size() == 1 && written.getFirst().indexOf(';') >= 0) {
            String[] parts = written.getFirst().split(";");
            written = List.of(parts.length > 1 ? parts[1].split(",") : parts[0].split(","));
            if (parts.length > 2 && !parts[2].trim().isEmpty()) { biome = flatBiome(parts[2].trim()); }
            if (parts.length > 3) {
                for (String named : parts[3].split(",")) {
                    String key = named.trim().toLowerCase(Locale.ROOT);
                    int at = key.indexOf('(');
                    if (at >= 0) { key = key.substring(0, at); }
                    List<String> sets = FLAT_STRUCTURES.get(key);
                    if ("decoration".equals(key)) { decorated = true; }
                    else if ("lake".equals(key)) { waterLakes = true; }
                    else if ("lava_lake".equals(key)) { lakes = true; }
                    else if (sets != null) { structures.addAll(sets); }
                    else if (!key.isEmpty() && WARNED.add("flat." + key)) { ContentLog.LOGGER.info("generatorOptions names the flat feature '{}', which this version has no switch for, so it is left out{}", key, "dungeon".equals(key) ? "; dungeons come with decoration" : ""); }
                }
            }
        }
        for (String layer : written) {
            String trimmed = layer.trim();
            if (!trimmed.isEmpty()) { layers.add(trimmed); }
        }
        return new Flat(layers, structures, decorated, waterLakes, lakes, biome);
    }

    public static void leavesWaterLakes(Flat asked) {
        if (asked.waterLakes() && WARNED.add("flat.lake")) { ContentLog.LOGGER.info("generatorOptions names the flat feature '{}', which this version has no switch for, so it is left out{}", "lake", "; water lakes come only on a flat dimension that keeps the overworld biomes"); }
    }

    private static String flatBiome(String written) {
        String found = shippedBiome(written);
        return found == null ? PLAINS : found;
    }

    @Nullable public static String shippedBiome(String written) {
        String found = legacyBiome(written);
        ResourceLocation id = found == null ? null : ResourceLocation.tryParse(found);
        if (id != null && ContentBiomes.shipped(id)) { return id.toString(); }
        if (found != null && WARNED.add("biome." + found)) { ContentLog.LOGGER.error("generatorOptions names biome '{}', which is not a biome the game, a mod or a pack ships, so it is left to the game", written); }
        return null;
    }

    @Nullable public static String legacyBiome(String written) {
        try {
            int id = Integer.parseInt(written);
            if (id < 0) { return null; }
            if (id == 127) { return "minecraft:the_void"; }
            if (id < LEGACY_BIOMES.size()) { return Ids.biome("minecraft:" + LEGACY_BIOMES.get(id)); }
            if (WARNED.add("biome." + written)) { ContentLog.LOGGER.error("generatorOptions names biome number {}, which is not one this version can map, so it is left to the game", written); }
            return null;
        }
        catch (NumberFormatException named) { return Ids.biome(written); }
    }

    public static int worldMinHeight() { return number("worldMinHeight", Config.worldgen.worldMinHeight(), ContentWorldShape.VANILLA_MIN); }

    public static int worldMaxHeight() { return number("worldMaxHeight", Config.worldgen.worldMaxHeight(), ContentWorldShape.VANILLA_MAX); }

    public static String deepStone() { return text("deepStone", Config.worldgen.deepStone()); }

    public static String noiseCaves() { return text("noiseCaves", Config.worldgen.noiseCaves()); }

    public static String worldSpawn() { return text("worldSpawn", Config.worldgen.worldSpawn()); }

    public static int worldBorder() {
        int asked = number("worldBorder", Config.worldgen.worldBorder(), 0);
        int limit = Config.worldgen.worldBorderLimit();
        if (asked > limit) {
            ContentLog.LOGGER.error("worldBorder asks for {} blocks, more than the {} worldBorderLimit allows, so the border is left where the game puts it", asked, limit);
            return 0;
        }
        return Math.max(0, asked);
    }

    public static int worldTime() { return number("worldTime", Config.worldgen.worldTime(), -1); }

    public static long lockedTime(Level level) { return level.dimension() == Level.OVERWORLD ? worldTime() : -1L; }

    private static int number(String key, int fallback, int off) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return off; }
        return ContentControl.number(ContentControl.TERRAIN, key, fallback);
    }
}
