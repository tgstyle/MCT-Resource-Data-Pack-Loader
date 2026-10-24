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
import mctmods.resourcedatapackloader.util.DimensionValues;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentStructureMaps {
    public static final String MAP_STRUCTURE = "map";
    public static final String MAP_PIECE = "map_piece";
    private static final Map<Identifier, StructureMapDef> DEFS = new LinkedHashMap<>();
    private static final Set<String> MISSING = new LinkedHashSet<>();
    private static final Set<String> OVERSIZE = new LinkedHashSet<>();
    private static final List<String> SETS = new ArrayList<>();
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
        SETS.clear();
        for (StructureMapDef def : DEFS.values()) {
            if (def.spacing() <= 0 && def.at() == null) {
                ContentLog.LOGGER.info("Structure map {} has neither spacing nor at, so it builds nowhere", def.key());
                continue;
            }
            String namespace = def.key().getNamespace();
            JsonObject structure = new JsonObject();
            structure.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + MAP_STRUCTURE);
            structure.addProperty("map", def.key().toString());
            structure.add("biomes", ContentFormats.anyBiomes());
            structure.addProperty("step", "surface_structures");
            structure.add("spawn_overrides", new JsonObject());
            structure.addProperty("terrain_adaptation", "none");
            GeneratedResources.put(PackType.SERVER_DATA, namespace, WorldgenFiles.file(WorldgenFiles.STRUCTURE, def.key().getPath()), structure.toString());
            GeneratedResources.put(PackType.SERVER_DATA, namespace, WorldgenFiles.file(WorldgenFiles.STRUCTURE_SET, def.key().getPath()), set(def).toString());
            SETS.add(def.key().toString());
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

    public static List<String> sets() { return List.copyOf(SETS); }

    @Nullable public static StructureMapDef def(Identifier key) { return DEFS.get(key); }

    public static void missing(StructureMapDef def, String named) { ContentLog.LOGGER.errorOnce(MISSING, named, "Structure map {} places structure '{}', which could not be loaded, so its cells stay empty", def.key(), named); }

    public static void oversize(StructureMapDef def, String named, int span) { ContentLog.LOGGER.errorOnce(OVERSIZE, named, "Structure map {} places structure '{}', which is {} block(s) across, where the game carries a map's pieces at most {} block(s) from the cell they start in, so the far side of it may be left out", def.key(), named, span, StructureMapDef.CELL_MOST); }

    public static boolean refuses(LevelAccessor level, Structure structure) {
        if (!(structure instanceof ContentMapStructure held) || !(level instanceof ServerLevel server)) { return false; }
        StructureMapDef def = DEFS.get(held.map());
        return def != null && !DimensionValues.listed(server.dimension().identifier().toString(), def.dimensions(), false);
    }

    @Nullable private static StructureMapDef parse(Identifier key, String contents) {
        JsonObject json = Json.GSON.fromJson(contents, JsonObject.class);
        if (json == null || !json.has("layers")) {
            ContentLog.LOGGER.error("Structure map {} has no layers, so it is dropped", key);
            return null;
        }
        JsonArray held = Json.getJsonArray(json, "layers");
        if (held.isEmpty() || held.size() > StructureMapDef.LIMIT) {
            ContentLog.LOGGER.error("Structure map {} has {} layer(s), the most being {}, so it is dropped", key, held.size(), StructureMapDef.LIMIT);
            return null;
        }
        List<StructureMapDef.Layer> layers = new ArrayList<>();
        int wide = 1;
        int deep = 1;
        for (int at = 0; at < held.size(); at++) {
            if (!held.get(at).isJsonObject()) {
                ContentLog.LOGGER.error("Structure map {} layer {} is not an object, so the map is dropped", key, at);
                return null;
            }
            JsonObject entry = held.get(at).getAsJsonObject();
            Map<Character, List<PickDef>> palette = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> mark : Json.getJsonObject(entry, "palette", new JsonObject()).entrySet()) {
                String symbol = mark.getKey().trim();
                if (symbol.length() != 1 || symbol.charAt(0) == '.') {
                    ContentLog.LOGGER.error("Structure map {} layer {} palette symbol '{}' must be a single character other than '.', so the map is dropped", key, at, mark.getKey());
                    return null;
                }
                List<PickDef> picks = new ArrayList<>();
                if (mark.getValue().isJsonArray()) {
                    for (String choice : Json.texts(mark.getValue().getAsJsonArray(), mark.getKey())) { picks.add(weighted(choice)); }
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
        int cell = Mth.clamp(Json.getInt(json, "cell", 32), 1, 48);
        int spacing = Math.max(0, Json.getInt(json, "spacing", 0));
        int[] pinned = null;
        if (json.has("at")) {
            JsonArray spot = Json.getJsonArray(json, "at");
            if (spot.size() == 2) { pinned = new int[] { Json.asInt(spot.get(0), "at", 0), Json.asInt(spot.get(1), "at", 0) }; }
            else { ContentLog.LOGGER.error("Structure map {} pins 'at' with {} number(s) instead of x and z, so the pin is ignored", key, spot.size()); }
        }
        StructureMapDef def = new StructureMapDef(key, cell, Mth.clamp(Json.getInt(json, "ground", 0), 0, layers.size() - 1), spacing, Mth.clamp(Json.getInt(json, "chance", 100), 1, 100), pinned,
                Json.strings(json, "dimensions"), List.copyOf(layers), wide, deep, depth(key, json), spawners(key, json), loot(key, json));
        if (spacing > 0 && spacing * 16 < def.widest()) {
            ContentLog.LOGGER.error("Structure map {} asks for a spacing of {} chunk(s) but spans {} block(s), so it is dropped rather than overlapping itself", key, spacing, def.widest());
            return null;
        }
        return def;
    }

    @Nullable private static int[] depth(Identifier key, JsonObject json) {
        JsonElement held = json.get("depth");
        if (held == null) { return null; }
        List<Integer> numbers = new ArrayList<>();
        if (held.isJsonArray()) {
            for (JsonElement number : held.getAsJsonArray()) { numbers.add(Json.asInt(number, "depth", -1)); }
        }
        else { numbers.add(Json.asInt(held, "depth", -1)); }
        boolean fine = numbers.size() == 1 || numbers.size() == 2;
        for (int number : numbers) { fine &= number >= 0; }
        if (!fine) {
            ContentLog.LOGGER.error("Structure map {} gives a depth that is not one or two numbers of 0 or more, so the depth is ignored", key);
            return null;
        }
        int least = numbers.getFirst();
        int most = numbers.getLast();
        return new int[] { Math.min(least, most), Math.max(least, most) };
    }

    private static List<PickDef> spawners(Identifier key, JsonObject json) {
        List<PickDef> picks = new ArrayList<>();
        JsonElement held = json.get("spawners");
        if (held == null) { return picks; }
        if (held.isJsonArray()) {
            for (String choice : Json.texts(held.getAsJsonArray(), "spawners")) { picks.add(weighted(choice)); }
        }
        else if (held.isJsonPrimitive()) { picks.add(weighted(held.getAsString())); }
        else { ContentLog.LOGGER.error("Structure map {} gives spawners that are not a mob id or a list of them, so they are ignored", key); }
        return List.copyOf(picks);
    }

    @Nullable private static Identifier loot(Identifier key, JsonObject json) {
        if (!json.has("loot")) { return null; }
        String text = Json.getString(json, "loot", "");
        Identifier loot = ContentLootSwitch.id(text);
        if (loot == null) { ContentLog.LOGGER.error("Structure map {} gives loot '{}', which is not a loot table id, so the loot is ignored", key, text); }
        return loot;
    }

    static Collection<StructureMapDef> defs() { return DEFS.values(); }

    static PickDef weighted(String written) {
        String text = Settings.lower(written);
        int at = text.lastIndexOf('=');
        if (at < 0) { return new PickDef(text, 1); }
        int weight;
        try { weight = Integer.parseInt(text.substring(at + 1).trim()); }
        catch (NumberFormatException notNumber) { weight = 1; }
        return new PickDef(text.substring(0, at).trim(), Math.max(1, weight));
    }
}
