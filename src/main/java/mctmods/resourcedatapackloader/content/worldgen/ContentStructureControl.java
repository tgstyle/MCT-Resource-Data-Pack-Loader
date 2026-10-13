package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.Compat;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentStructureControl {
    private static final String RANDOM_SPREAD = "minecraft:random_spread";
    private static final String CONCENTRIC_RINGS = "minecraft:concentric_rings";
    private static final String RINGS = ResourceDataPackLoader.MOD_ID + ":" + ContentWorldgen.RINGS_PLACEMENT;
    private static final Map<String, List<String>> LEGACY_NAMES = Map.of(
            "temples", List.of("desert_pyramids", "jungle_temples", "swamp_huts", "igloos"), "monuments", List.of("ocean_monuments"),
            "mansions", List.of("woodland_mansions"), "mineshafts", List.of("mineshafts"), "strongholds", List.of("strongholds"),
            "netherbridges", List.of("nether_complexes"), "endcities", List.of("end_cities"), "villages", List.of("villages"));
    private static final List<String> VANILLA_SETS = List.of("ancient_cities", "buried_treasures", "desert_pyramids", "end_cities", "igloos", "jungle_temples", "mineshafts",
            "nether_complexes", "nether_fossils", "ocean_monuments", "ocean_ruins", "pillager_outposts", "ruined_portals", "shipwrecks", "strongholds", "swamp_huts",
            "trail_ruins", "villages", "woodland_mansions");
    private static final Set<String> SPACING_FIXED = Set.of("nether_complexes");
    private static final Set<String> SEPARATION_FIXED = Set.of("desert_pyramids", "jungle_temples", "swamp_huts", "igloos", "mineshafts", "nether_complexes");
    private static final Set<String> ADAPTATIONS = Set.of("none", "bury", "beard_thin", "beard_box", "encapsulate");
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Map<Identifier, JsonObject> SET_JSON = new LinkedHashMap<>();
    private static final Map<Identifier, JsonObject> STRUCTURE_JSON = new LinkedHashMap<>();
    private static final Map<Identifier, Set<Identifier>> SET_MEMBERS = new LinkedHashMap<>();
    private static final Map<String, List<Identifier>> BIOME_FILTERED = new LinkedHashMap<>();

    private ContentStructureControl() {}

    public static boolean disabled() { return ContentControl.off(ContentControl.STRUCTURES); }

    public static void generate() {
        SET_JSON.clear();
        STRUCTURE_JSON.clear();
        SET_MEMBERS.clear();
        BIOME_FILTERED.clear();
        beardMansions();
        if (disabled()) {
            ContentPopulateControl.load(null, List.of(), new ArrayList<>());
            ContentStructureMost.load(List.of(), new ArrayList<>());
            ContentStructureSpawners.load(List.of(), new ArrayList<>());
            written();
            return;
        }
        List<String> touched = new ArrayList<>();
        WorldTemplateDef template = ContentWorldTemplates.active();
        List<String> off = new ArrayList<>();
        if (template != null) {
            for (Map.Entry<String, Boolean> entry : template.structures().entrySet()) {
                if (ContentPopulateControl.populates(entry.getKey())) { continue; }
                if (sets(entry.getKey(), false).isEmpty()) {
                    ContentLog.LOGGER.error("World template {} names structure '{}', which is not one of {}, ignoring it", template.key(), entry.getKey(), known());
                    continue;
                }
                if (!entry.getValue()) { off.add(entry.getKey()); }
            }
        }
        ContentPopulateControl.load(template, off, touched);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSpacing", Config.worldgen.structureSpacing())) { numbers(entry, "structureSpacing", touched, true); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSeparation", Config.worldgen.structureSeparation())) { numbers(entry, "structureSeparation", touched, false); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureMinDistanceFromSpawn", Config.worldgen.structureMinDistanceFromSpawn())) { distance(entry, touched); }
        pins(touched);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureBiomes", Config.worldgen.structureBiomes())) { biomes(entry, touched); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSpawns", Config.worldgen.structureSpawns())) { spawns(entry, touched); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation())) { adaptation(entry, touched); }
        ContentStructureMost.load(ContentControl.list(ContentControl.STRUCTURES, "structureMost", Config.worldgen.structureMost()), touched);
        ContentStructureSpawners.load(ContentControl.list(ContentControl.STRUCTURES, "structureSpawners", Config.worldgen.structureSpawners()), touched);
        written();
        if (!touched.isEmpty()) { Summary.info("structures", "Controlling vanilla structures: " + String.join(", ", touched) + " (" + SET_JSON.size() + " structure set(s) and " + STRUCTURE_JSON.size() + " structure(s) rewritten)"); }
    }

    private static void beardMansions() {
        for (Identifier mansion : structures("mansions")) {
            JsonObject json = structure(mansion);
            if (json != null) { json.addProperty("terrain_adaptation", "beard_thin"); }
        }
    }

    private static void written() {
        for (Map.Entry<Identifier, JsonObject> set : SET_JSON.entrySet()) { GeneratedResources.put(PackType.SERVER_DATA, set.getKey().getNamespace(), WorldgenFiles.file(WorldgenFiles.STRUCTURE_SET, set.getKey().getPath()), set.getValue().toString()); }
        for (Map.Entry<Identifier, JsonObject> structure : STRUCTURE_JSON.entrySet()) { GeneratedResources.put(PackType.SERVER_DATA, structure.getKey().getNamespace(), WorldgenFiles.file(WorldgenFiles.STRUCTURE, structure.getKey().getPath()), structure.getValue().toString()); }
    }

    private static void numbers(String entry, String key, List<String> touched, boolean spacing) {
        String[] parts = split(entry, key);
        if (parts == null) { return; }
        int value = positive(parts[1], entry, key);
        if (value == 0) { return; }
        for (Identifier set : sets(parts[0])) {
            if ((spacing ? SPACING_FIXED : SEPARATION_FIXED).contains(set.getPath())) { continue; }
            JsonObject json = set(set);
            if (json == null) { continue; }
            JsonObject placement = Json.getJsonObject(json, "placement");
            String type = Json.getString(placement, "type", "");
            if (CONCENTRIC_RINGS.equals(type)) { placement.addProperty(spacing ? "distance" : "spread", value); }
            else if ("mineshafts".equals(set.getPath()) && spacing) { placement.addProperty("frequency", 1.0F / value); }
            else if (spacing) {
                placement.addProperty("spacing", value);
                if (Json.getInt(placement, "separation", 0) >= value) { placement.addProperty("separation", Math.max(0, value - 1)); }
            }
            else {
                int apart = Json.getInt(placement, "spacing", 1);
                if (apart > value) { placement.addProperty("separation", value); }
                else if ("ocean_monuments".equals(set.getPath())) {
                    ContentLog.LOGGER.warn("structureSpacing asks for monuments every {} chunk(s) while structureSeparation keeps them {} apart, which leaves the game no room to place one. Separation is brought down to {}", apart, value, apart - 1);
                    placement.addProperty("separation", Math.max(0, apart - 1));
                }
                else {
                    placement.addProperty("separation", value);
                    placement.addProperty("spacing", value + 1);
                }
            }
            touched.add(parts[0] + (spacing ? " spacing " : " separation ") + value);
        }
    }

    private static int positive(String written, String entry, String key) {
        try { return Math.max(0, Integer.parseInt(written.trim())); }
        catch (NumberFormatException notNumber) {
            ContentLog.LOGGER.error("{} entry '{}' does not end in a number", key, entry);
            return 0;
        }
    }

    private static void distance(String entry, List<String> touched) {
        String[] parts = split(entry, "structureMinDistanceFromSpawn");
        if (parts == null) { return; }
        int value = positive(parts[1], entry, "structureMinDistanceFromSpawn");
        if (value == 0) { return; }
        for (Identifier set : sets(parts[0])) {
            JsonObject placement = spread(set, "structureMinDistanceFromSpawn");
            if (placement == null) { continue; }
            placement.addProperty("min_distance_from_spawn", value);
            placement.add("spawn", spawn());
            touched.add(parts[0] + " " + value + " blocks from spawn");
        }
    }

    private static void pins(List<String> touched) {
        Map<String, JsonArray> byName = new LinkedHashMap<>();
        for (Map.Entry<String, List<int[]>> pinned : StructureAtPins.all().entrySet()) {
            if (ContentCity.STRUCTURE.equals(pinned.getKey())) {
                for (int[] at : pinned.getValue()) { touched.add("the city center district pinned at " + at[0] + "," + at[1]); }
                continue;
            }
            JsonArray pins = byName.computeIfAbsent(pinned.getKey(), _ -> new JsonArray());
            for (int[] at : pinned.getValue()) {
                JsonArray pin = new JsonArray();
                pin.add(at[0]);
                pin.add(at[1]);
                pins.add(pin);
            }
        }
        for (Map.Entry<String, JsonArray> pinned : byName.entrySet()) {
            for (Identifier set : sets(pinned.getKey())) {
                JsonObject placement = rings(set);
                if (placement != null) { placement.addProperty("type", RINGS); }
                else { placement = spread(set, "structureAt"); }
                if (placement == null) { continue; }
                placement.add("pins", pinned.getValue().deepCopy());
                for (Identifier structure : members(set)) {
                    JsonObject json = structure(structure);
                    if (json != null) { json.add("biomes", ContentFormats.anyBiomes()); }
                }
                touched.add(pinned.getKey() + " pinned " + pinned.getValue().size() + " time(s), any biome");
            }
        }
    }

    private static void biomes(String entry, List<String> touched) {
        String[] parts = split(entry, "structureBiomes");
        if (parts == null) { return; }
        Set<String> found = new LinkedHashSet<>();
        Set<String> ids = new LinkedHashSet<>();
        for (String named : parts[1].split(",")) {
            String biome = Settings.lower(named);
            if (biome.isEmpty()) { continue; }
            if (biome.contains(":")) {
                found.add(biome);
                ids.add(biome);
                continue;
            }
            int before = found.size();
            String tag = ContentFormats.biomeTag(biome);
            if (tag != null) { found.add("#" + tag); }
            Identifier vanilla = Identifier.tryParse("minecraft:" + biome);
            if (vanilla != null && ContentBiomes.known().contains(vanilla)) {
                found.add(vanilla.toString());
                ids.add(vanilla.toString());
            }
            found.addAll(BiomeNames.ids(List.of(biome)));
            if (found.size() == before) { ContentLog.LOGGER.error("structureBiomes entry '{}' names '{}', which is neither a biome id, a biome's name nor a biome type", entry, biome); }
        }
        if (found.isEmpty()) { return; }
        JsonElement listed = holderSet(array(found));
        boolean blacklist = blacklisted(parts[0]);
        if (blacklist) {
            JsonObject not = new JsonObject();
            not.addProperty("type", ContentFormats.CONVENTION_HOLDER_SETS + ":not");
            not.add("value", listed);
            listed = not;
        }
        Set<Identifier> steered = new LinkedHashSet<>();
        for (Identifier ringed : sets(parts[0], false)) {
            JsonObject placement = rings(ringed);
            if (placement == null) { continue; }
            if (!placement.has("vanilla_preferred_biomes")) {
                placement.add("vanilla_preferred_biomes", placement.get("preferred_biomes").deepCopy());
                placement.addProperty("filtered_for", parts[0]);
                placement.addProperty("type", RINGS);
            }
            placement.add("preferred_biomes", filtered(placement.get("preferred_biomes"), listed, blacklist, ids));
            steered.addAll(members(ringed));
            touched.add(ringed.getPath() + " steered by biomes");
        }
        for (Identifier structure : structures(parts[0])) {
            if (steered.contains(structure)) { continue; }
            JsonObject json = structure(structure);
            if (json == null) { continue; }
            json.add("biomes", filtered(json.get("biomes"), listed, blacklist, ids));
            BIOME_FILTERED.computeIfAbsent(parts[0], _ -> new ArrayList<>()).add(structure);
            touched.add(structure.getPath() + " biomes");
        }
    }

    public static void checkBiomes(ServerLevel level) {
        Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        for (Map.Entry<String, List<Identifier>> entry : BIOME_FILTERED.entrySet()) {
            boolean present = false;
            boolean empty = true;
            for (Identifier id : entry.getValue()) {
                Structure structure = registry.getValue(id);
                if (structure == null) { continue; }
                present = true;
                if (structure.biomes().size() > 0) { empty = false; }
            }
            if (present && empty) { warnNoBiome(entry.getKey()); }
        }
    }

    static void warnNoBiome(Object name) { ContentLog.LOGGER.warn("Biome settings for {} leave no biome at all, so the vanilla list is left alone", name); }

    private static JsonElement filtered(JsonElement original, JsonElement listed, boolean blacklist, Set<String> ids) {
        JsonObject kept = composite("and", original.deepCopy(), listed.deepCopy());
        return blacklist || ids.isEmpty() ? kept : composite("or", kept, holderSet(array(ids)));
    }

    private static JsonArray array(Set<String> values) {
        JsonArray out = new JsonArray();
        for (String value : values) { out.add(value); }
        return out;
    }

    private static JsonObject composite(String type, JsonElement first, JsonElement second) {
        JsonArray values = new JsonArray();
        values.add(first);
        values.add(second);
        JsonObject out = new JsonObject();
        out.addProperty("type", ContentFormats.CONVENTION_HOLDER_SETS + ":" + type);
        out.add("values", values);
        return out;
    }

    private static void spawns(String entry, List<String> touched) {
        String[] parts = split(entry, "structureSpawns");
        if (parts == null) { return; }
        JsonArray listed = new JsonArray();
        for (String named : parts[1].split(",")) {
            String spawn = named.trim();
            if (spawn.isEmpty()) { continue; }
            String[] fields = spawn.split(":");
            if (fields.length != 5) {
                ContentLog.LOGGER.error("structureSpawns entry '{}' holds '{}', which is not written as namespace:entity:weight:least:most", entry, spawn);
                continue;
            }
            Identifier id = Identifier.tryParse(fields[0].trim() + ":" + fields[1].trim());
            EntityType<?> type = id == null ? null : Registered.find(BuiltInRegistries.ENTITY_TYPE, id);
            if (type == null && (id == null || ContentEntities.undefined(id))) {
                ContentLog.LOGGER.error("structureSpawns entry '{}' names entity {}, which is not registered", entry, fields[0] + ":" + fields[1]);
                continue;
            }
            if (type != null && (!DefaultAttributes.hasSupplier(type) || type == Compat.armorStand() || type == Compat.player())) {
                ContentLog.LOGGER.error("structureSpawns entry '{}' for {} names an entity that is not a living one, ignoring it", spawn, parts[0]);
                continue;
            }
            try {
                JsonObject spawner = new JsonObject();
                spawner.addProperty("type", id.toString());
                spawner.addProperty("weight", Math.max(1, Integer.parseInt(fields[2].trim())));
                int least = Math.max(1, Integer.parseInt(fields[3].trim()));
                spawner.addProperty("minCount", least);
                spawner.addProperty("maxCount", Math.max(least, Integer.parseInt(fields[4].trim())));
                listed.add(spawner);
            }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.error("structureSpawns entry '{}' holds '{}', whose weight or counts are not numbers", entry, spawn); }
        }
        for (Identifier structure : structures("temples".equals(parts[0]) ? "swamp_huts" : parts[0])) {
            JsonObject json = structure(structure);
            if (json == null) { continue; }
            JsonObject own = Json.getJsonObject(json, "spawn_overrides", new JsonObject());
            JsonObject overrides = own.deepCopy();
            overrides.add("monster", override(own, listed));
            json.add("spawn_overrides", overrides);
            touched.add(structure.getPath() + " spawns " + listed.size() + " kind(s)");
        }
    }

    private static JsonObject override(JsonObject own, JsonArray spawns) {
        JsonObject held = new JsonObject();
        held.addProperty("bounding_box", Json.getString(Json.getJsonObject(own, "monster", new JsonObject()), "bounding_box", "piece"));
        held.add("spawns", spawns.deepCopy());
        return held;
    }

    private static void adaptation(String entry, List<String> touched) {
        String[] parts = split(entry, "structureAdaptation");
        if (parts == null) { return; }
        String asked = Settings.lower(parts[1]);
        if (("temples".equals(parts[0]) || "mansions".equals(parts[0])) && !"none".equals(asked) && !"beard_thin".equals(asked)) {
            ContentLog.LOGGER.error("structureAdaptation asks for {}={}, but that structure settles itself only as it is built, so the terrain cannot be shaped for it beforehand. Only beard_thin is offered there, which banks the ground around it once it stands", parts[0], parts[1].trim());
            return;
        }
        if (!ADAPTATIONS.contains(asked)) {
            ContentLog.LOGGER.error("structureAdaptation entry '{}' asks for '{}', which is not one of {}", entry, asked, ADAPTATIONS);
            return;
        }
        for (Identifier structure : structures(parts[0])) {
            JsonObject json = structure(structure);
            if (json == null) { continue; }
            json.addProperty("terrain_adaptation", asked);
            touched.add(structure.getPath() + " " + asked);
        }
    }

    @Nullable private static JsonObject spread(Identifier set, String key) {
        JsonObject json = set(set);
        if (json == null) { return null; }
        JsonObject placement = Json.getJsonObject(json, "placement");
        String type = Json.getString(placement, "type", "");
        if (!RANDOM_SPREAD.equals(type) && !(ResourceDataPackLoader.MOD_ID + ":spread").equals(type)) {
            ContentLog.LOGGER.errorOnce(WARNED, key + set, "{} names {}, which is placed by {} rather than on a grid, so it cannot be pinned or held off spawn", key, set, type);
            return null;
        }
        placement.addProperty("type", ResourceDataPackLoader.MOD_ID + ":spread");
        return placement;
    }

    @Nullable private static JsonObject rings(Identifier set) {
        JsonObject json = set(set);
        if (json == null) { return null; }
        JsonObject placement = Json.getJsonObject(json, "placement");
        String type = Json.getString(placement, "type", "");
        return CONCENTRIC_RINGS.equals(type) || RINGS.equals(type) ? placement : null;
    }

    private static JsonArray spawn() {
        int[] at = spawnAt();
        JsonArray out = new JsonArray();
        out.add(at[0]);
        out.add(at[1]);
        return out;
    }

    public static int[] spawnAt() {
        String[] parts = ContentTerrain.worldSpawn().trim().split(",");
        if (parts.length == 2 || parts.length == 3) {
            try { return new int[] {Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[parts.length - 1].trim())}; }
            catch (NumberFormatException ignored) { return new int[] {0, 0}; }
        }
        return new int[] {0, 0};
    }

    private static JsonElement holderSet(JsonArray values) {
        if (values.size() == 1) { return values.get(0); }
        boolean tags = false;
        boolean ids = false;
        for (JsonElement value : values) {
            if (value.getAsString().startsWith("#")) { tags = true; }
            else { ids = true; }
        }
        if (ids && !tags) { return values; }
        JsonObject or = new JsonObject();
        or.addProperty("type", ContentFormats.CONVENTION_HOLDER_SETS + ":or");
        or.add("values", values);
        return or;
    }

    static boolean blacklisted(String structure) {
        String wanted = Settings.lower(structure);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureBiomesAreBlacklist", Config.worldgen.structureBiomesAreBlacklist())) {
            String[] parts = split(entry, "structureBiomesAreBlacklist");
            if (parts == null) { continue; }
            if (parts[0].equals(wanted)) { return Boolean.parseBoolean(parts[1].trim()); }
        }
        return false;
    }

    @Nullable static String[] split(String entry, String key) {
        String[] sides = Settings.filled(entry);
        if (sides == null) {
            ContentLog.LOGGER.error("{} entry '{}' is not written as structure=value", key, entry);
            return null;
        }
        return new String[] { Settings.lower(sides[0]), sides[1] };
    }

    private static String known() {
        List<String> names = new ArrayList<>(LEGACY_NAMES.keySet());
        Collections.sort(names);
        names.addAll(ContentPopulateControl.names());
        return String.join(", ", names) + " or a structure set or structure id";
    }

    private static List<Identifier> sets(String name) { return sets(name, true); }

    private static List<Identifier> sets(String name, boolean told) {
        String wanted = Settings.lower(name);
        List<String> legacy = LEGACY_NAMES.get(wanted);
        List<Identifier> out = new ArrayList<>();
        if (legacy != null) {
            for (String set : legacy) { out.add(Identifier.withDefaultNamespace(set)); }
            return out;
        }
        Identifier id = wanted.contains(":") ? Identifier.tryParse(wanted) : Identifier.withDefaultNamespace(wanted);
        if (id == null) {
            if (told) { ContentLog.LOGGER.error("'{}' is not a structure name this line knows nor a valid id", name); }
            return out;
        }
        if (GameData.has(WorldgenFiles.id(WorldgenFiles.STRUCTURE_SET, id))) {
            out.add(id);
            return out;
        }
        for (String set : VANILLA_SETS) {
            Identifier setId = Identifier.withDefaultNamespace(set);
            if (members(setId).contains(id)) {
                out.add(setId);
                return out;
            }
        }
        if (told) { ContentLog.LOGGER.errorOnce(WARNED, wanted, "'{}' is neither a 1.12.2 structure name, a structure set nor a structure the game ships, so its settings do nothing", name); }
        return out;
    }

    static List<Identifier> structures(String name) {
        String wanted = Settings.lower(name);
        List<Identifier> out = new ArrayList<>();
        Identifier id = wanted.contains(":") ? Identifier.tryParse(wanted) : Identifier.withDefaultNamespace(wanted);
        if (id != null && !LEGACY_NAMES.containsKey(wanted) && GameData.has(WorldgenFiles.id(WorldgenFiles.STRUCTURE, id))) {
            out.add(id);
            return out;
        }
        for (Identifier set : sets(name)) { out.addAll(members(set)); }
        return out;
    }

    private static Set<Identifier> members(Identifier set) {
        Set<Identifier> known = SET_MEMBERS.get(set);
        if (known != null) { return known; }
        Set<Identifier> found = new LinkedHashSet<>();
        SET_MEMBERS.put(set, found);
        JsonObject json = GameData.json(WorldgenFiles.id(WorldgenFiles.STRUCTURE_SET, set));
        if (json == null) { return found; }
        for (JsonElement element : Json.getJsonArray(json, "structures", new JsonArray())) {
            if (!element.isJsonObject()) { continue; }
            Identifier structure = Identifier.tryParse(Json.getString(element.getAsJsonObject(), "structure", ""));
            if (structure != null) { found.add(structure); }
        }
        return found;
    }

    @Nullable private static JsonObject set(Identifier set) { return copyOf(SET_JSON, WorldgenFiles.STRUCTURE_SET, set); }

    @Nullable private static JsonObject structure(Identifier structure) { return copyOf(STRUCTURE_JSON, WorldgenFiles.STRUCTURE, structure); }

    @Nullable private static JsonObject copyOf(Map<Identifier, JsonObject> held, String folder, Identifier id) {
        JsonObject known = held.get(id);
        if (known != null) { return known; }
        JsonObject json = GameData.json(WorldgenFiles.id(folder, id));
        if (json == null) { return null; }
        JsonObject copy = json.deepCopy();
        held.put(id, copy);
        return copy;
    }
}
