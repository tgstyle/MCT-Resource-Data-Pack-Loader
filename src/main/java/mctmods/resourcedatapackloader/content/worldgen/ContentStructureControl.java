package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentStructureControl {
    private static final String MINECRAFT = "minecraft";
    private static final String SETS = "worldgen/structure_set";
    private static final String STRUCTURES = "worldgen/structure";
    private static final String RANDOM_SPREAD = "minecraft:random_spread";
    private static final String CONCENTRIC_RINGS = "minecraft:concentric_rings";
    private static final Map<String, List<String>> LEGACY_NAMES = Map.of(
            "temples", List.of("desert_pyramids", "jungle_temples", "swamp_huts", "igloos"), "monuments", List.of("ocean_monuments"),
            "mansions", List.of("woodland_mansions"), "mineshafts", List.of("mineshafts"), "strongholds", List.of("strongholds"),
            "netherbridges", List.of("nether_complexes"), "endcities", List.of("end_cities"), "villages", List.of("villages"));
    private static final List<String> VANILLA_SETS = List.of("ancient_cities", "buried_treasures", "desert_pyramids", "end_cities", "igloos", "jungle_temples", "mineshafts",
            "nether_complexes", "nether_fossils", "ocean_monuments", "ocean_ruins", "pillager_outposts", "ruined_portals", "shipwrecks", "strongholds", "swamp_huts",
            "trail_ruins", "villages", "woodland_mansions");
    private static final Map<String, String> SET_DIMENSIONS = Map.of("nether_complexes", "minecraft:the_nether", "nether_fossils", "minecraft:the_nether", "end_cities", "minecraft:the_end");
    private static final Set<String> ADAPTATIONS = Set.of("none", "bury", "beard_thin", "beard_box", "encapsulate");
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static final Map<ResourceLocation, JsonObject> SET_JSON = new LinkedHashMap<>();
    private static final Map<ResourceLocation, JsonObject> STRUCTURE_JSON = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Set<ResourceLocation>> SET_MEMBERS = new LinkedHashMap<>();

    private ContentStructureControl() {}

    public static boolean enabled() { return !ContentControl.off(ContentControl.STRUCTURES); }

    public static void generate() {
        SET_JSON.clear();
        STRUCTURE_JSON.clear();
        SET_MEMBERS.clear();
        if (!enabled()) { return; }
        List<String> touched = new ArrayList<>();
        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template != null) {
            for (Map.Entry<String, Boolean> entry : template.structures().entrySet()) {
                if (entry.getValue()) { continue; }
                for (ResourceLocation set : sets(entry.getKey())) {
                    if (!appliesTo(set, template.dimensions())) { continue; }
                    JsonObject json = set(set);
                    if (json == null) { continue; }
                    json.add("structures", new JsonArray());
                    touched.add(entry.getKey() + " off");
                }
            }
        }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSpacing", Config.worldgen.structureSpacing())) { numbers(entry, "structureSpacing", touched, true); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSeparation", Config.worldgen.structureSeparation())) { numbers(entry, "structureSeparation", touched, false); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureMinDistanceFromSpawn", Config.worldgen.structureMinDistanceFromSpawn())) { distance(entry, touched); }
        pins(touched);
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureBiomes", Config.worldgen.structureBiomes())) { biomes(entry, touched); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSpawns", Config.worldgen.structureSpawns())) { spawns(entry, touched); }
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation())) { adaptation(entry, touched); }
        ContentStructureMost.load(ContentControl.list(ContentControl.STRUCTURES, "structureMost", Config.worldgen.structureMost()), touched);
        ContentStructureSpawners.load(ContentControl.list(ContentControl.STRUCTURES, "structureSpawners", Config.worldgen.structureSpawners()), touched);
        for (Map.Entry<ResourceLocation, JsonObject> set : SET_JSON.entrySet()) { GeneratedResources.put(PackType.SERVER_DATA, set.getKey().getNamespace(), SETS + "/" + set.getKey().getPath() + ".json", set.getValue().toString()); }
        for (Map.Entry<ResourceLocation, JsonObject> structure : STRUCTURE_JSON.entrySet()) { GeneratedResources.put(PackType.SERVER_DATA, structure.getKey().getNamespace(), STRUCTURES + "/" + structure.getKey().getPath() + ".json", structure.getValue().toString()); }
        if (!touched.isEmpty()) { Summary.info("structures", "Controlling vanilla structures: " + String.join(", ", touched) + " (" + SET_JSON.size() + " structure set(s) and " + STRUCTURE_JSON.size() + " structure(s) rewritten)"); }
    }

    private static void numbers(String entry, String key, List<String> touched, boolean spacing) {
        String[] parts = split(entry, key);
        if (parts == null) { return; }
        int value;
        try { value = Integer.parseInt(parts[1].trim()); }
        catch (NumberFormatException notNumber) {
            ContentLog.LOGGER.error("{} entry '{}' does not end in a number", key, entry);
            return;
        }
        if (value <= 0) { return; }
        for (ResourceLocation set : sets(parts[0])) {
            JsonObject json = set(set);
            if (json == null) { continue; }
            JsonObject placement = GsonHelper.getAsJsonObject(json, "placement");
            String type = GsonHelper.getAsString(placement, "type", "");
            if (CONCENTRIC_RINGS.equals(type)) { placement.addProperty(spacing ? "distance" : "spread", value); }
            else if ("mineshafts".equals(set.getPath()) && spacing) { placement.addProperty("frequency", 1.0F / value); }
            else if (spacing) {
                placement.addProperty("spacing", value);
                if (GsonHelper.getAsInt(placement, "separation", 0) >= value) { placement.addProperty("separation", Math.max(0, value - 1)); }
            }
            else {
                placement.addProperty("separation", value);
                if (GsonHelper.getAsInt(placement, "spacing", 1) <= value) { placement.addProperty("spacing", value + 1); }
            }
            touched.add(parts[0] + (spacing ? " spacing " : " separation ") + value);
        }
    }

    private static void distance(String entry, List<String> touched) {
        String[] parts = split(entry, "structureMinDistanceFromSpawn");
        if (parts == null) { return; }
        int value;
        try { value = Integer.parseInt(parts[1].trim()); }
        catch (NumberFormatException notNumber) {
            ContentLog.LOGGER.error("structureMinDistanceFromSpawn entry '{}' does not end in a number", entry);
            return;
        }
        if (value <= 0) { return; }
        for (ResourceLocation set : sets(parts[0])) {
            JsonObject placement = spread(set, "structureMinDistanceFromSpawn");
            if (placement == null) { continue; }
            placement.addProperty("min_distance_from_spawn", value);
            placement.add("spawn", spawn());
            touched.add(parts[0] + " " + value + " blocks from spawn");
        }
    }

    private static void pins(List<String> touched) {
        Map<String, JsonArray> byName = new LinkedHashMap<>();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAt", Config.worldgen.structureAt())) {
            String[] parts = split(entry, "structureAt");
            if (parts == null) { continue; }
            String[] xz = parts[1].split(",");
            if (xz.length != 2) {
                ContentLog.LOGGER.error("structureAt entry '{}' is not written as structure=x,z", entry);
                continue;
            }
            try {
                JsonArray pin = new JsonArray();
                pin.add(Integer.parseInt(xz[0].trim()));
                pin.add(Integer.parseInt(xz[1].trim()));
                byName.computeIfAbsent(parts[0], key -> new JsonArray()).add(pin);
            }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.error("structureAt entry '{}' does not name two whole numbers", entry); }
        }
        for (Map.Entry<String, JsonArray> pinned : byName.entrySet()) {
            for (ResourceLocation set : sets(pinned.getKey())) {
                JsonObject placement = spread(set, "structureAt");
                if (placement == null) { continue; }
                placement.add("pins", pinned.getValue().deepCopy());
                for (ResourceLocation structure : members(set)) {
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
        JsonArray values = new JsonArray();
        for (String named : parts[1].split(",")) {
            String biome = named.trim();
            if (biome.isEmpty()) { continue; }
            if (biome.contains(":")) { values.add(biome); }
            else {
                String tag = ContentFormats.biomeTag(biome);
                if (tag == null) { ContentLog.LOGGER.error("structureBiomes entry '{}' names '{}', which is neither a biome id nor a biome type", entry, biome); }
                else { values.add("#" + tag); }
            }
        }
        if (values.isEmpty()) { return; }
        JsonElement set = holderSet(values);
        if (ContentControl.flag(ContentControl.STRUCTURES, "structureBiomesAreBlacklist", Config.worldgen.structureBiomesAreBlacklist())) {
            JsonObject not = new JsonObject();
            not.addProperty("type", ContentFormats.CONVENTION_HOLDER_SETS + ":not");
            not.add("value", set);
            set = not;
        }
        for (ResourceLocation structure : structures(parts[0])) {
            JsonObject json = structure(structure);
            if (json == null) { continue; }
            json.add("biomes", set.deepCopy());
            touched.add(structure.getPath() + " biomes");
        }
    }

    private static void spawns(String entry, List<String> touched) {
        String[] parts = split(entry, "structureSpawns");
        if (parts == null) { return; }
        Map<String, JsonArray> byCategory = new LinkedHashMap<>();
        for (String named : parts[1].split(",")) {
            String spawn = named.trim();
            if (spawn.isEmpty()) { continue; }
            String[] fields = spawn.split(":");
            if (fields.length != 5) {
                ContentLog.LOGGER.error("structureSpawns entry '{}' holds '{}', which is not written as namespace:entity:weight:least:most", entry, spawn);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(fields[0].trim() + ":" + fields[1].trim());
            EntityType<?> type = id == null ? null : Registered.find(ForgeRegistries.ENTITY_TYPES, id);
            if (type == null && (id == null || !ContentEntities.defines(id))) {
                ContentLog.LOGGER.error("structureSpawns entry '{}' names entity {}, which is not registered", entry, fields[0] + ":" + fields[1]);
                continue;
            }
            try {
                JsonObject spawner = new JsonObject();
                spawner.addProperty("type", id.toString());
                spawner.addProperty("weight", Math.max(1, Integer.parseInt(fields[2].trim())));
                int least = Math.max(1, Integer.parseInt(fields[3].trim()));
                spawner.addProperty("minCount", least);
                spawner.addProperty("maxCount", Math.max(least, Integer.parseInt(fields[4].trim())));
                byCategory.computeIfAbsent(type == null ? ContentEntities.categoryOf(id) : type.getCategory().getName(), key -> new JsonArray()).add(spawner);
            }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.error("structureSpawns entry '{}' holds '{}', whose weight or counts are not numbers", entry, spawn); }
        }
        JsonObject overrides = new JsonObject();
        for (Map.Entry<String, JsonArray> category : byCategory.entrySet()) {
            JsonObject held = new JsonObject();
            held.addProperty("bounding_box", "piece");
            held.add("spawns", category.getValue());
            overrides.add(category.getKey(), held);
        }
        if (byCategory.isEmpty()) {
            JsonObject none = new JsonObject();
            none.addProperty("bounding_box", "piece");
            none.add("spawns", new JsonArray());
            overrides.add("monster", none);
        }
        for (ResourceLocation structure : structures(parts[0])) {
            JsonObject json = structure(structure);
            if (json == null) { continue; }
            json.add("spawn_overrides", overrides.deepCopy());
            touched.add(structure.getPath() + " spawns " + byCategory.values().stream().mapToInt(JsonArray::size).sum() + " kind(s)");
        }
    }

    private static void adaptation(String entry, List<String> touched) {
        String[] parts = split(entry, "structureAdaptation");
        if (parts == null) { return; }
        String asked = parts[1].trim().toLowerCase(Locale.ROOT);
        if (!ADAPTATIONS.contains(asked)) {
            ContentLog.LOGGER.error("structureAdaptation entry '{}' asks for '{}', which is not one of {}", entry, asked, ADAPTATIONS);
            return;
        }
        String mode = ContentFormats.adaptation(asked);
        if (!mode.equals(asked) && WARNED.add(entry)) { ContentLog.LOGGER.warn("structureAdaptation entry '{}' asks for '{}', which this line does not carry, so '{}' stands in", entry, asked, mode); }
        for (ResourceLocation structure : structures(parts[0])) {
            JsonObject json = structure(structure);
            if (json == null) { continue; }
            json.addProperty("terrain_adaptation", mode);
            touched.add(structure.getPath() + " " + mode);
        }
    }

    @Nullable private static JsonObject spread(ResourceLocation set, String key) {
        JsonObject json = set(set);
        if (json == null) { return null; }
        JsonObject placement = GsonHelper.getAsJsonObject(json, "placement");
        String type = GsonHelper.getAsString(placement, "type", "");
        if (!RANDOM_SPREAD.equals(type) && !(ResourceDataPackLoader.MOD_ID + ":spread").equals(type)) {
            if (WARNED.add(key + set)) { ContentLog.LOGGER.error("{} names {}, which is placed by {} rather than on a grid, so it cannot be pinned or held off spawn", key, set, type); }
            return null;
        }
        placement.addProperty("type", ResourceDataPackLoader.MOD_ID + ":spread");
        return placement;
    }

    private static JsonArray spawn() {
        JsonArray out = new JsonArray();
        String written = ContentTerrain.worldSpawn().trim();
        String[] parts = written.split(",");
        if (parts.length == 2 || parts.length == 3) {
            try {
                out.add(Integer.parseInt(parts[0].trim()));
                out.add(Integer.parseInt(parts[parts.length - 1].trim()));
                return out;
            }
            catch (NumberFormatException ignored) { out = new JsonArray(); }
        }
        out.add(0);
        out.add(0);
        return out;
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

    private static boolean appliesTo(ResourceLocation set, List<String> dimensions) {
        if (dimensions.isEmpty()) { return true; }
        String home = SET_DIMENSIONS.getOrDefault(set.getPath(), "minecraft:overworld");
        for (String named : dimensions) {
            if (ContentFormats.dimensionId(named).equals(home)) { return true; }
        }
        return false;
    }

    @Nullable static String[] split(String entry, String key) {
        int at = entry.indexOf('=');
        if (at <= 0) {
            ContentLog.LOGGER.error("{} entry '{}' is not written as structure=value", key, entry);
            return null;
        }
        return new String[] { entry.substring(0, at).trim().toLowerCase(Locale.ROOT), entry.substring(at + 1) };
    }

    private static List<ResourceLocation> sets(String name) {
        String wanted = name.trim().toLowerCase(Locale.ROOT);
        List<String> legacy = LEGACY_NAMES.get(wanted);
        List<ResourceLocation> out = new ArrayList<>();
        if (legacy != null) {
            for (String set : legacy) { out.add(ResourceLocation.fromNamespaceAndPath(MINECRAFT, set)); }
            return out;
        }
        ResourceLocation id = wanted.contains(":") ? ResourceLocation.tryParse(wanted) : ResourceLocation.fromNamespaceAndPath(MINECRAFT, wanted);
        if (id == null) {
            ContentLog.LOGGER.error("'{}' is not a structure name this line knows nor a valid id", name);
            return out;
        }
        if (GameData.has(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), SETS + "/" + id.getPath() + ".json"))) {
            out.add(id);
            return out;
        }
        for (String set : VANILLA_SETS) {
            ResourceLocation setId = ResourceLocation.fromNamespaceAndPath(MINECRAFT, set);
            if (members(setId).contains(id)) {
                out.add(setId);
                return out;
            }
        }
        if (WARNED.add(wanted)) { ContentLog.LOGGER.error("'{}' is neither a 1.12.2 structure name, a structure set nor a structure the game ships, so its settings do nothing", name); }
        return out;
    }

    static List<ResourceLocation> structures(String name) {
        String wanted = name.trim().toLowerCase(Locale.ROOT);
        List<ResourceLocation> out = new ArrayList<>();
        ResourceLocation id = wanted.contains(":") ? ResourceLocation.tryParse(wanted) : ResourceLocation.fromNamespaceAndPath(MINECRAFT, wanted);
        if (id != null && !LEGACY_NAMES.containsKey(wanted) && GameData.has(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), STRUCTURES + "/" + id.getPath() + ".json"))) {
            out.add(id);
            return out;
        }
        for (ResourceLocation set : sets(name)) { out.addAll(members(set)); }
        return out;
    }

    private static Set<ResourceLocation> members(ResourceLocation set) {
        Set<ResourceLocation> known = SET_MEMBERS.get(set);
        if (known != null) { return known; }
        Set<ResourceLocation> found = new LinkedHashSet<>();
        SET_MEMBERS.put(set, found);
        JsonObject json = GameData.json(ResourceLocation.fromNamespaceAndPath(set.getNamespace(), SETS + "/" + set.getPath() + ".json"));
        if (json == null) { return found; }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "structures", new JsonArray())) {
            if (!element.isJsonObject()) { continue; }
            ResourceLocation structure = ResourceLocation.tryParse(GsonHelper.getAsString(element.getAsJsonObject(), "structure", ""));
            if (structure != null) { found.add(structure); }
        }
        return found;
    }

    @Nullable private static JsonObject set(ResourceLocation set) {
        JsonObject held = SET_JSON.get(set);
        if (held != null) { return held; }
        JsonObject json = GameData.json(ResourceLocation.fromNamespaceAndPath(set.getNamespace(), SETS + "/" + set.getPath() + ".json"));
        if (json == null) { return null; }
        JsonObject copy = json.deepCopy();
        SET_JSON.put(set, copy);
        return copy;
    }

    @Nullable private static JsonObject structure(ResourceLocation structure) {
        JsonObject held = STRUCTURE_JSON.get(structure);
        if (held != null) { return held; }
        JsonObject json = GameData.json(ResourceLocation.fromNamespaceAndPath(structure.getNamespace(), STRUCTURES + "/" + structure.getPath() + ".json"));
        if (json == null) { return null; }
        JsonObject copy = json.deepCopy();
        STRUCTURE_JSON.put(structure, copy);
        return copy;
    }
}
