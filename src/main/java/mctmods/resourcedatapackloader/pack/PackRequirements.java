package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PackRequirements {
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<String> FOLDERS = Collections.unmodifiableList(Arrays.asList(
            PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS, PackManager.BIOMES, PackManager.WORLDGEN,
            PackManager.DIMENSIONS, PackManager.WORLDTEMPLATES, PackManager.PATHINTERSECTS, PackManager.GATES, PackManager.POTIONS,
            PackManager.POTION_TYPES, PackManager.BREWING, PackManager.VILLAGERS, PackManager.TRADES, PackManager.VILLAGES, PackManager.ENTITIES));
    private static final Set<String> WANTED = new LinkedHashSet<>();
    private static boolean scanned;

    private PackRequirements() {}

    public static Set<String> required() {
        if (!scanned) { scan(); }
        return Collections.unmodifiableSet(WANTED);
    }

    private static void scan() {
        scanned = true;
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        Set<String> found = new LinkedHashSet<>();
        for (String folder : FOLDERS) { manager.forEach(folder, PackManager.JSON, (namespace, path, contents) -> collect(contents, found)); }
        for (String modid : found) {
            if (manager.provides(modid)) { continue; }
            WANTED.add(modid);
        }
    }

    private static void collect(String contents, Set<String> found) {
        if (contents == null || !contents.contains("\"requires\"")) { return; }
        try {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            if (json == null || !json.has("requires")) { return; }
            JsonElement element = json.get("requires");
            if (!element.isJsonArray()) { return; }
            for (JsonElement each : element.getAsJsonArray()) {
                if (!each.isJsonPrimitive()) { continue; }
                String modid = each.getAsString().trim().toLowerCase(java.util.Locale.ROOT);
                if (!modid.isEmpty()) { found.add(modid); }
            }
        }
        catch (RuntimeException malformed) { ContentLog.LOGGER.debug("Could not read requirements from a pack file: {}", malformed.getMessage()); }
    }
}
