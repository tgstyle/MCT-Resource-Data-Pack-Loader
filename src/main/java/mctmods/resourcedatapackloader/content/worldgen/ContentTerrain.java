package mctmods.resourcedatapackloader.content.worldgen;

import com.google.gson.JsonObject;

import net.minecraft.world.Difficulty;

import java.util.HashSet;
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

    public static int worldMinHeight() { return number("worldMinHeight", Config.worldgen.worldMinHeight(), ContentWorldShape.VANILLA_MIN); }

    public static int worldMaxHeight() { return number("worldMaxHeight", Config.worldgen.worldMaxHeight(), ContentWorldShape.VANILLA_MAX); }

    public static String deepStone() { return text("deepStone", Config.worldgen.deepStone()); }

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
