package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentPathIntersects {
    private static final Map<String, PathIntersectDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentPathIntersects() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.PATHINTERSECTS, "path intersect design", (key, contents) -> {
            PathIntersectDef def = parse(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("pathintersects", "Loaded " + DEFS.size() + " path intersect design(s) from packs"); }
    }

    @Nullable public static PathIntersectDef byName(String named) { return DEFS.get(named); }

    @Nullable public static PathIntersectDef forJunction(long seed, int x, int z) {
        List<String> wanted = ContentControl.list(ContentControl.VILLAGES, "villagePathIntersects", Config.worldgen.villagePathIntersects());
        if (wanted.isEmpty()) { return null; }
        List<PathIntersectDef> picks = new ArrayList<>();
        for (String named : wanted) {
            PathIntersectDef def = DEFS.get(named.trim());
            if (def == null) {
                ContentLog.LOGGER.errorOnce(WARNED, named, "villagePathIntersects names design '{}', which no pack provides, so junctions are left plain", named);
                continue;
            }
            picks.add(def);
        }
        return WeightedPicks.pick(picks, PathIntersectDef::weight, SeededRandom.at(seed, x, z));
    }

    @Nullable private static PathIntersectDef parse(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "path intersect design");
        if (json == null) { return null; }
        Map<Character, String> legend = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "legend", new JsonObject()).entrySet()) {
            String symbol = entry.getKey().trim();
            if (symbol.length() != 1) {
                ContentLog.LOGGER.error("Path intersect design {} legend symbol '{}' must be a single character, so it is left out", key, entry.getKey());
                continue;
            }
            if (PathIntersectDef.role(symbol.charAt(0))) {
                ContentLog.LOGGER.error("Path intersect design {} legend gives '{}' a block, but that character is a road role, so it is left out", key, symbol);
                continue;
            }
            legend.put(symbol.charAt(0), entry.getValue().getAsString());
        }
        List<String> mouth = Json.strings(json, "mouth");
        List<String> corner = Json.strings(json, "corner");
        roles(key, legend, mouth, "mouth");
        roles(key, legend, corner, "corner");
        if (mouth.isEmpty() && corner.isEmpty()) {
            ContentLog.LOGGER.error("Path intersect design {} draws neither a mouth nor a corner, so it is dropped", key);
            return null;
        }
        return new PathIntersectDef(key, Json.getString(json, "name", key.getPath()), Math.max(1, Json.getInt(json, "weight", 1)), Map.copyOf(legend), mouth, corner);
    }

    private static void roles(ResourceLocation key, Map<Character, String> legend, List<String> rows, String part) {
        for (String row : rows) {
            for (char held : row.toCharArray()) {
                if (!PathIntersectDef.role(held) && !legend.containsKey(held)) { ContentLog.LOGGER.error("Path intersect {} {} uses '{}', which is neither a role letter nor in the legend", key, part, held); }
            }
        }
    }
}
