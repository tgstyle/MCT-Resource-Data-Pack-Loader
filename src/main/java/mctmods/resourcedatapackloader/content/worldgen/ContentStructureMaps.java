package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentStructureMaps {
    public static final String MAP_STRUCTURE = "map";
    public static final String MAP_PIECE = "map_piece";
    private static final Map<String, String> DIMENSION_TAGS = Map.of("minecraft:overworld", "#minecraft:is_overworld", "minecraft:the_nether", "#minecraft:is_nether", "minecraft:the_end", "#minecraft:is_end");
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, StructureMapDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> MISSING = new LinkedHashSet<>();
    private static final Set<String> OVERSIZE = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentStructureMaps() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.STRUCTUREMAPS, "structure map", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            StructureMapDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("structuremaps", "Loaded " + DEFS.size() + " structure map(s) from packs"); }
    }

    public static void generate() {
        int generated = 0;
        for (StructureMapDef def : DEFS.values()) {
            if (def.spacing() <= 0 && def.at() == null) {
                ContentLog.LOGGER.info("Structure map {} has neither spacing nor at, so it builds nowhere", def.key());
                continue;
            }
            String namespace = def.key().getNamespace();
            JsonObject structure = new JsonObject();
            structure.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + MAP_STRUCTURE);
            structure.addProperty("map", def.key().toString());
            structure.add("biomes", biomes(def));
            structure.addProperty("step", "surface_structures");
            structure.add("spawn_overrides", new JsonObject());
            structure.addProperty("terrain_adaptation", "none");
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "worldgen/structure/" + def.key().getPath() + ".json", structure.toString());
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "worldgen/structure_set/" + def.key().getPath() + ".json", set(def).toString());
            generated++;
        }
        if (generated > 0) { Summary.info("structuremaps.generated", "Generated " + generated + " structure(s) with their sets from structure maps"); }
    }

    private static JsonObject set(StructureMapDef def) {
        JsonObject entry = new JsonObject();
        entry.addProperty("structure", def.key().toString());
        entry.addProperty("weight", 1);
        JsonArray structures = new JsonArray();
        structures.add(entry);
        JsonObject set = new JsonObject();
        set.add("placement", placement(def));
        set.add("structures", structures);
        return set;
    }

    private static JsonObject placement(StructureMapDef def) {
        JsonObject placement = new JsonObject();
        placement.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + ContentWorldgen.SPREAD_PLACEMENT);
        placement.addProperty("salt", Math.abs(def.key().toString().hashCode()));
        int lattice = StructureMapDef.window() / 16;
        placement.addProperty("spacing", lattice);
        placement.addProperty("separation", lattice - 1);
        return placement;
    }

    @Nullable public static StructureMapDef def(ResourceLocation key) { return DEFS.get(key); }

    public static void missing(StructureMapDef def, String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("Structure map {} places structure '{}', which could not be loaded, so its cells stay empty", def.key(), named); }
    }

    public static void oversize(StructureMapDef def, String named, int span) {
        if (OVERSIZE.add(named)) { ContentLog.LOGGER.error("Structure map {} places structure '{}', which is {} block(s) across, where the game carries a map's pieces at most {} block(s) from the cell they start in, so the far side of it may be left out", def.key(), named, span, StructureMapDef.CELL_MOST); }
    }

    private static JsonElement biomes(StructureMapDef def) {
        if (def.dimensions().isEmpty()) { return ContentFormats.anyBiomes(); }
        JsonArray values = new JsonArray();
        for (String named : def.dimensions()) {
            String tag = DIMENSION_TAGS.get(ContentFormats.dimensionId(named));
            if (tag == null) { ContentLog.LOGGER.error("Structure map {} names dimension '{}', which is not one of the three with a biome tag, so it is left out", def.key(), named); }
            else { values.add(tag); }
        }
        if (values.isEmpty()) { return ContentFormats.anyBiomes(); }
        if (values.size() == 1) { return values.get(0); }
        JsonObject or = new JsonObject();
        or.addProperty("type", ContentFormats.CONVENTION_HOLDER_SETS + ":or");
        or.add("values", values);
        return or;
    }

    @Nullable private static StructureMapDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null || !json.has("layers")) {
            ContentLog.LOGGER.error("Structure map {} has no layers, so it is dropped", key);
            return null;
        }
        JsonArray held = GsonHelper.getAsJsonArray(json, "layers");
        if (held.isEmpty() || held.size() > StructureMapDef.LIMIT) {
            ContentLog.LOGGER.error("Structure map {} has {} layer(s), the most being {}, so it is dropped", key, held.size(), StructureMapDef.LIMIT);
            return null;
        }
        List<StructureMapDef.Layer> layers = new ArrayList<>();
        int wide = 1;
        int deep = 1;
        for (int at = 0; at < held.size(); at++) {
            JsonObject entry = held.get(at).getAsJsonObject();
            Map<Character, List<PickDef>> palette = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> mark : GsonHelper.getAsJsonObject(entry, "palette", new JsonObject()).entrySet()) {
                String symbol = mark.getKey().trim();
                if (symbol.length() != 1 || symbol.charAt(0) == '.') {
                    ContentLog.LOGGER.error("Structure map {} layer {} palette symbol '{}' must be a single character other than '.', so the map is dropped", key, at, mark.getKey());
                    return null;
                }
                List<PickDef> picks = new ArrayList<>();
                if (mark.getValue().isJsonArray()) {
                    for (JsonElement choice : mark.getValue().getAsJsonArray()) { picks.add(weighted(choice.getAsString())); }
                }
                else { picks.add(weighted(mark.getValue().getAsString())); }
                palette.put(symbol.charAt(0), List.copyOf(picks));
            }
            List<String> rows = Json.strings(entry, "map");
            if (rows.isEmpty() || rows.size() > StructureMapDef.LIMIT) {
                ContentLog.LOGGER.error("Structure map {} layer {} holds {} map row(s), the most being {}, so the map is dropped", key, at, rows.size(), StructureMapDef.LIMIT);
                return null;
            }
            for (String row : rows) {
                if (row.length() > StructureMapDef.LIMIT) {
                    ContentLog.LOGGER.error("Structure map {} layer {} row '{}' is {} cell(s) long, the most being {}, so the map is dropped", key, at, row, row.length(), StructureMapDef.LIMIT);
                    return null;
                }
                for (char cell : row.toCharArray()) {
                    if (cell != '.' && !palette.containsKey(cell)) {
                        ContentLog.LOGGER.error("Structure map {} layer {} uses '{}', which is not in that layer's palette, so the map is dropped", key, at, cell);
                        return null;
                    }
                }
                wide = Math.max(wide, row.length());
            }
            deep = Math.max(deep, rows.size());
            layers.add(new StructureMapDef.Layer(Map.copyOf(palette), rows));
        }
        int cell = Mth.clamp(GsonHelper.getAsInt(json, "cell", 32), 1, 48);
        int spacing = Math.max(0, GsonHelper.getAsInt(json, "spacing", 0));
        int[] pinned = null;
        if (json.has("at")) {
            JsonArray spot = GsonHelper.getAsJsonArray(json, "at");
            if (spot.size() == 2) { pinned = new int[] { spot.get(0).getAsInt(), spot.get(1).getAsInt() }; }
            else { ContentLog.LOGGER.error("Structure map {} pins 'at' with {} number(s) instead of x and z, so the pin is ignored", key, spot.size()); }
        }
        StructureMapDef def = new StructureMapDef(key, cell, Mth.clamp(GsonHelper.getAsInt(json, "ground", 0), 0, layers.size() - 1), spacing, Mth.clamp(GsonHelper.getAsInt(json, "chance", 100), 1, 100), pinned,
                Json.strings(json, "dimensions"), List.copyOf(layers), wide, deep);
        if (spacing > 0 && spacing * 16 < def.widest()) {
            ContentLog.LOGGER.error("Structure map {} asks for a spacing of {} chunk(s) but spans {} block(s), so it is dropped rather than overlapping itself", key, spacing, def.widest());
            return null;
        }
        return def;
    }

    private static PickDef weighted(String written) {
        String text = written.trim().toLowerCase(Locale.ROOT);
        int at = text.lastIndexOf('=');
        if (at < 0) { return new PickDef(text, 1); }
        try { return new PickDef(text.substring(0, at).trim(), Math.max(1, Integer.parseInt(text.substring(at + 1).trim()))); }
        catch (NumberFormatException notNumber) { return new PickDef(text, 1); }
    }
}
