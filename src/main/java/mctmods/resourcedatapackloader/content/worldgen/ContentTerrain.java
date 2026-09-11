package mctmods.resourcedatapackloader.content.worldgen;

import com.google.gson.JsonObject;

import net.minecraft.world.Difficulty;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

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

    public static String worldGameMode() { return text("worldGameMode", Config.worldgen.worldGameMode()); }

    public static String worldType() { return text("worldType", Config.worldgen.worldType()); }

    public static List<String> worldTypeExceptions() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return Config.worldgen.worldTypeExceptions(); }
        return ContentControl.list(ContentControl.TERRAIN, "worldTypeExceptions", Config.worldgen.worldTypeExceptions());
    }

    @Nullable public static JsonObject generatorOptions() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return ContentControl.object(ContentControl.TERRAIN, "generatorOptions", Config.worldgen.generatorOptions());
    }

    public record Flat(List<String> layers, List<String> structures, boolean decorated) {}

    private static final Map<String, String> FLAT_STRUCTURES = Map.of("village", "minecraft:villages", "mineshaft", "minecraft:mineshafts", "stronghold", "minecraft:strongholds", "oceanmonument", "minecraft:ocean_monuments", "mansion", "minecraft:woodland_mansions");

    public static Flat flat() {
        List<String> layers = new ArrayList<>();
        List<String> structures = new ArrayList<>();
        boolean decorated = false;
        List<String> written = ContentControl.off(ContentControl.TERRAIN) ? List.of(Config.worldgen.generatorOptions()) : ContentControl.list(ContentControl.TERRAIN, "generatorOptions", List.of(Config.worldgen.generatorOptions()));
        if (written.size() == 1 && written.get(0).indexOf(';') >= 0) {
            String[] parts = written.get(0).split(";");
            written = List.of(parts.length > 1 ? parts[1].split(",") : parts[0].split(","));
            if (parts.length > 3) {
                for (String named : parts[3].split(",")) {
                    String key = named.trim().toLowerCase(Locale.ROOT);
                    int at = key.indexOf('(');
                    if (at >= 0) { key = key.substring(0, at); }
                    String set = FLAT_STRUCTURES.get(key);
                    if ("decoration".equals(key)) { decorated = true; }
                    else if (set != null) { structures.add(set); }
                    else if (!key.isEmpty() && WARNED.add("flat." + key)) { ContentLog.LOGGER.info("generatorOptions names the flat structure '{}', which has no structure set on this version, so it is left out", key); }
                }
            }
        }
        for (String layer : written) {
            String trimmed = layer.trim();
            if (!trimmed.isEmpty()) { layers.add(trimmed); }
        }
        return new Flat(layers, structures, decorated);
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

    @Nullable public static Difficulty worldDifficulty() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        Difficulty found = null;
        for (String entry : ContentControl.list(ContentControl.TERRAIN, "worldDifficulty", Config.worldgen.worldDifficulty())) {
            String[] parts = entry.split("=", 2);
            String named = parts[parts.length - 1].trim();
            if (parts.length == 2 && !parts[0].trim().equals("minecraft:overworld") && !parts[0].trim().equals("0")) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.info("worldDifficulty entry '{}' names a dimension, but difficulty is save wide on this version, so only a bare entry or the overworld's is read", entry); }
                continue;
            }
            Difficulty difficulty = Difficulty.byName(named.toLowerCase(Locale.ROOT));
            if (difficulty == null) {
                if (WARNED.add(entry)) { ContentLog.LOGGER.error("worldDifficulty entry '{}' is not one of peaceful, easy, normal or hard, ignoring it", entry); }
                continue;
            }
            if (found == null || parts.length == 2) { found = difficulty; }
        }
        return found;
    }

    private static int number(String key, int fallback, int off) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return off; }
        return ContentControl.number(ContentControl.TERRAIN, key, fallback);
    }
}
