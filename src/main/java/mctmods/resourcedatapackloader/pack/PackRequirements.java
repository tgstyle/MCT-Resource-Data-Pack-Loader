package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class PackRequirements {
    public static final String REQUIRES = "requires";
    public static final String CONFIG_GATE = "config:";
    public static final String FILE_GATE = "file:";
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<String> FOLDERS = List.of(
            PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS, PackManager.BIOMES, PackManager.WORLDGEN,
            PackManager.DIMENSIONS, PackManager.WORLDTEMPLATES, PackManager.PATHINTERSECTS, PackManager.STRUCTUREMAPS, PackManager.PORTALFRAMES, PackManager.GATES, PackManager.POTIONS,
            PackManager.POTION_TYPES, PackManager.BREWING, PackManager.VILLAGERS, PackManager.TRADES, PackManager.VILLAGES, PackManager.ENTITIES);
    private static final Set<String> WANTED = new LinkedHashSet<>();
    private static boolean scanned;

    private PackRequirements() {}

    public static Set<String> required() {
        if (!scanned) { scan(); }
        return Collections.unmodifiableSet(WANTED);
    }

    public static boolean modLoaded(String modid) { return ModList.get().isLoaded(modid); }

    public static Path gameDirectory() { return FMLPaths.GAMEDIR.get(); }

    private static void scan() {
        scanned = true;
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        Set<String> found = new LinkedHashSet<>();
        for (String folder : FOLDERS) { manager.forEach(folder, PackManager.JSON, (namespace, path, contents) -> collect(contents, found)); }
        for (String modid : found) {
            if (modLoaded(modid) || manager.provides(modid)) { continue; }
            WANTED.add(modid);
        }
    }

    private static void collect(String contents, Set<String> found) {
        if (!contents.contains("\"" + REQUIRES + "\"")) { return; }
        try {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            if (json == null || !json.has(REQUIRES)) { return; }
            JsonElement element = json.get(REQUIRES);
            if (!element.isJsonArray()) { return; }
            for (JsonElement each : element.getAsJsonArray()) {
                if (!each.isJsonPrimitive()) { continue; }
                String modid = each.getAsString().trim().toLowerCase(Locale.ROOT);
                if (modid.isEmpty() || modid.startsWith(CONFIG_GATE) || modid.startsWith(FILE_GATE)) { continue; }
                found.add(modid);
            }
        }
        catch (RuntimeException malformed) { ContentLog.LOGGER.debug("Could not read requirements from a pack file: {}", malformed.getMessage()); }
    }
}
