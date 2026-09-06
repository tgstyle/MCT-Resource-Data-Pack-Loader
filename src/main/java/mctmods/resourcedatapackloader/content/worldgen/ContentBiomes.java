package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.BiomeSpawnDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBiomes {
    public static final String OVERWORLD_TAG = "minecraft:is_overworld";
    public static final String OVERWORLD = "minecraft:overworld";
    public static final String NETHER = "minecraft:the_nether";
    private static final float VANILLA_OFFSET = 0.001F;
    private static final float[][] BANDS = { { -1.0F, -0.45F }, { -0.45F, -0.15F }, { -0.15F, 0.2F }, { 0.2F, 0.55F }, { 0.55F, 1.0F } };
    private static final float[] BAND_STEPS = { 0.15F, 0.5F, 0.9F, 1.5F };
    private static final Map<String, Integer> CLIMATES = Map.of("icy", 0, "cool", 1, "medium", 2, "warm", 3, "desert", 4);
    private static final Map<String, String> VILLAGES = Map.of("", "plains", "oak", "plains", "sandstone", "desert", "acacia", "savanna", "spruce", "taiga");
    private static final String EXTRA_TREES = "extratreechance";
    private static final String FALLS = "falls";
    private static final String TREES = "trees";
    private static final List<Map.Entry<String, List<String>>> KINDS = List.of(
            Map.entry(TREES, List.of("tree", "dark_forest_vegetation", "bamboo_vegetation")),
            Map.entry("bigmushrooms", List.of("mushroom_island_vegetation")),
            Map.entry("mushrooms", List.of("brown_mushroom", "red_mushroom")),
            Map.entry("flowers", List.of("flower", "sunflower")),
            Map.entry("grass", List.of("grass")),
            Map.entry("deadbush", List.of("dead_bush")),
            Map.entry("reeds", List.of("sugar_cane")),
            Map.entry("cacti", List.of("cactus")),
            Map.entry("sand", List.of("disk_sand")),
            Map.entry("gravel", List.of("disk_gravel")),
            Map.entry("clay", List.of("disk_clay")),
            Map.entry("waterlily", List.of("waterlily")),
            Map.entry(FALLS, List.of("lake_lava", "spring_water", "spring_lava")),
            Map.entry("pumpkins", List.of("pumpkin")),
            Map.entry("desertwells", List.of("desert_well")),
            Map.entry("ice", List.of("ice_spike", "ice_patch")),
            Map.entry("fossils", List.of("fossil")),
            Map.entry("rocks", List.of("forest_rock")));
    private static final Set<String> HEAD_MODIFIERS = Set.of("minecraft:count", "minecraft:rarity_filter", "minecraft:noise_threshold_count", "minecraft:noise_based_count");
    private static final Map<ResourceLocation, BiomeDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Made> MADE = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Set<String>> TAGS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentBiomes() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff()) { return; }
        Json.eachFile(PackManager.BIOMES, "biome file", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            BiomeDef def = ContentBiomeParser.parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("biomes", "Loaded " + DEFS.size() + " biome definition(s) from packs"); }
    }

    public static void generate() {
        MADE.clear();
        TAGS.clear();
        for (BiomeDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            JsonObject biome = build(def);
            if (biome == null) { continue; }
            GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), "worldgen/biome/" + def.key().getPath() + ".json", biome.toString());
            MADE.put(def.key(), new Made(def, block(def, def.topBlock(), "topBlock"), block(def, def.fillerBlock(), "fillerBlock"), block(def, def.stoneBlock(), "stoneBlock"), box(def)));
            tags(def);
        }
        for (Map.Entry<ResourceLocation, Set<String>> tag : TAGS.entrySet()) {
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();
            for (String biome : tag.getValue()) { values.add(biome); }
            json.add("values", values);
            GeneratedResources.put(PackType.SERVER_DATA, tag.getKey().getNamespace(), ContentFormats.BIOME_TAGS + "/" + tag.getKey().getPath() + ".json", json.toString());
        }
        if (!MADE.isEmpty()) { Summary.info("biomes.generated", "Generated " + MADE.size() + " biome(s) from packs, placed in the overworld through the generated preset"); }
    }

    public static boolean any() { return !MADE.isEmpty(); }

    public static boolean placesBiomes(String dimension) {
        if (OVERWORLD.equals(dimension) && any()) { return true; }
        if (ContentCaveRegions.placesIn(dimension)) { return true; }
        return ContentBiomeControl.enabled() && ContentBiomeControl.appliesTo(dimension) && (OVERWORLD.equals(dimension) || NETHER.equals(dimension));
    }

    @Nullable public static BiomeDef def(ResourceLocation key) {
        Made made = MADE.get(key);
        return made == null ? null : made.def();
    }

    public static JsonArray biomes(String dimension) {
        JsonArray out = new JsonArray();
        Map<ResourceLocation, ResourceLocation> taken = new LinkedHashMap<>();
        boolean blocking = ContentBiomeControl.enabled() && ContentBiomeControl.appliesTo(dimension);
        ContentBiomeControl.reset();
        boolean overworld = OVERWORLD.equals(dimension);
        for (Made made : overworld ? MADE.values() : List.<Made>of()) {
            if (made.def().replaces().isEmpty()) {
                out.add(entry(blocking ? ContentBiomeControl.place(made.def().key()) : made.def().key().toString(), made.point()));
                continue;
            }
            for (String named : made.def().replaces()) {
                ResourceLocation replaced = ResourceLocation.tryParse(named.trim());
                if (replaced == null) { ContentLog.LOGGER.error("Biome {} replaces '{}', which is not a biome id, leaving it out", made.def().key(), named); }
                else { taken.put(replaced, made.def().key()); }
            }
        }
        ContentCaveRegions.points(dimension, out);
        Set<ResourceLocation> found = new LinkedHashSet<>();
        for (Pair<Climate.ParameterPoint, ResourceKey<Biome>> pair : MultiNoiseBiomeSourceParameterList.knownPresets().get(overworld ? MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD : MultiNoiseBiomeSourceParameterList.Preset.NETHER).values()) {
            ResourceLocation id = pair.getSecond().location();
            JsonObject point = encode(pair.getFirst());
            ResourceLocation by = taken.get(id);
            point.addProperty("offset", VANILLA_OFFSET);
            if (by != null) { found.add(id); }
            ResourceLocation placed = by == null ? id : by;
            out.add(entry(blocking ? ContentBiomeControl.place(placed) : placed.toString(), point));
        }
        if (blocking) { ContentBiomeControl.report(dimension); }
        for (Map.Entry<ResourceLocation, ResourceLocation> replaced : taken.entrySet()) {
            if (!found.contains(replaced.getKey())) { ContentLog.LOGGER.error("Biome {} replaces {}, which the overworld does not place, so that replacement does nothing", replaced.getValue(), replaced.getKey()); }
        }
        for (Made made : overworld ? MADE.values() : List.<Made>of()) {
            if (made.def().replaces().isEmpty()) { ContentLog.LOGGER.debug("Biome {} takes the overworld at temperature {}, humidity {} (inland, every erosion and weirdness)", made.def().key(), made.point().get("temperature"), made.point().get("humidity")); }
            else { ContentLog.LOGGER.debug("Biome {} takes over the overworld places of {}", made.def().key(), made.def().replaces()); }
        }
        ContentLog.LOGGER.debug("The {} biome list holds {} entries, {} of them the pack's own boxes and {} vanilla places re-pointed", dimension, out.size(), overworld ? MADE.size() - (int) taken.values().stream().distinct().count() : 0, found.size());
        return out;
    }

    public static void surface(JsonObject settings) {
        JsonArray outer = WorldgenJson.sequenceOf(settings);
        JsonArray inner = null;
        for (JsonElement element : outer) {
            if (!element.isJsonObject()) { continue; }
            JsonObject rule = element.getAsJsonObject();
            if (!"minecraft:above_preliminary_surface".equals(GsonHelper.getAsString(GsonHelper.getAsJsonObject(rule, "if_true", new JsonObject()), "type", ""))) { continue; }
            JsonObject then = GsonHelper.getAsJsonObject(rule, "then_run", new JsonObject());
            if (then.has("sequence")) { inner = GsonHelper.getAsJsonArray(then, "sequence"); }
        }
        for (Made made : MADE.values()) {
            List<String> self = List.of(made.def().key().toString());
            if (made.stone() != null) { outer.add(WorldgenJson.condition(WorldgenJson.biomeIs(self), WorldgenJson.block(made.stone().toString()))); }
            if (made.top() == null && made.filler() == null) { continue; }
            if (inner == null) {
                ContentLog.LOGGER.error("Biome {} names a topBlock or fillerBlock, but the overworld surface rule has no surface section to put them in, so the ground keeps the base biome's blocks", made.def().key());
                continue;
            }
            List<JsonObject> ground = new ArrayList<>();
            if (made.top() != null) { ground.add(WorldgenJson.condition(WorldgenJson.stoneDepth(true, false), WorldgenJson.block(made.top().toString()))); }
            if (made.filler() != null) { ground.add(WorldgenJson.condition(WorldgenJson.stoneDepth(true, true), WorldgenJson.block(made.filler().toString()))); }
            inner.asList().addFirst(WorldgenJson.condition(WorldgenJson.biomeIs(self), WorldgenJson.sequence(ground)));
        }
    }

    public static void spawnTargets(JsonObject settings) {
        JsonArray targets = new JsonArray();
        for (Made made : MADE.values()) {
            if (made.def().playerSpawn()) { targets.add(made.point().deepCopy()); }
        }
        if (targets.isEmpty()) { return; }
        settings.add("spawn_target", targets);
        ContentLog.LOGGER.debug("The world spawn is looked for in the climate of {} biome(s) that ask for it, in place of the game's own anywhere-inland target", targets.size());
    }

    @Nullable private static JsonObject build(BiomeDef def) {
        JsonObject base = GameData.json(ResourceLocation.fromNamespaceAndPath(def.baseBiome().getNamespace(), "worldgen/biome/" + def.baseBiome().getPath() + ".json"));
        if (base == null) {
            ContentLog.LOGGER.error("Biome {} is built on {}, which is not a biome the game or a mod ships, so it is skipped", def.key(), def.baseBiome());
            return null;
        }
        JsonObject biome = base.deepCopy();
        float temperature = def.temperature();
        if (def.snow() && temperature >= 0.15F) {
            ContentLog.LOGGER.debug("Biome {} asks for snow at temperature {}; this line snows below 0.15, so the biome is written at 0.1", def.key(), temperature);
            temperature = 0.1F;
        }
        biome.addProperty("temperature", temperature);
        biome.addProperty("downfall", def.rainfall());
        biome.addProperty("has_precipitation", def.rain());
        JsonObject effects = GsonHelper.getAsJsonObject(biome, "effects", new JsonObject());
        color(effects, "water_color", def.waterColor());
        color(effects, "grass_color", def.grassColor());
        color(effects, "foliage_color", def.foliageColor());
        biome.add("effects", effects);
        biome.add("spawners", spawners(def, GsonHelper.getAsJsonObject(biome, "spawners", new JsonObject())));
        biome.addProperty("creature_spawn_probability", def.spawnChance());
        biome.add("features", decorate(def, GsonHelper.getAsJsonArray(biome, "features", new JsonArray())));
        return biome;
    }

    private static void color(JsonObject effects, String key, int color) {
        if (color != BiomeDef.NO_COLOR) { effects.addProperty(key, color); }
    }

    private static JsonObject spawners(BiomeDef def, JsonObject base) {
        JsonObject out = base.deepCopy();
        if (!def.keepDefaultSpawns()) {
            for (String category : new ArrayList<>(out.keySet())) { out.add(category, new JsonArray()); }
        }
        for (BiomeSpawnDef spawn : def.spawns()) {
            JsonArray list = out.has(spawn.category()) ? GsonHelper.getAsJsonArray(out, spawn.category()) : new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("type", spawn.entity().toString());
            entry.addProperty("weight", spawn.weight());
            entry.addProperty("minCount", spawn.min());
            entry.addProperty("maxCount", spawn.max());
            list.add(entry);
            out.add(spawn.category(), list);
        }
        return out;
    }

    private static JsonArray decorate(BiomeDef def, JsonArray steps) {
        for (String key : def.decoration().keySet()) {
            if (!EXTRA_TREES.equals(key) && kindKnown(key)) { continue; }
            if (!EXTRA_TREES.equals(key)) { ContentLog.LOGGER.error("Biome {} sets decoration '{}', which is not a known setting", def.key(), key); }
        }
        JsonArray out = new JsonArray();
        int extra = def.decoration().getOrDefault(EXTRA_TREES, 0);
        for (JsonElement element : steps) {
            JsonArray step = new JsonArray();
            List<String> ids = new ArrayList<>();
            if (element.isJsonArray()) {
                for (JsonElement id : element.getAsJsonArray()) { ids.add(id.getAsString()); }
            }
            else { ids.add(element.getAsString()); }
            for (String id : ids) {
                String kind = kindOf(id);
                Integer count = kind == null ? null : def.decoration().get(kind);
                if (count == null) { step.add(id); }
                else if (count <= 0) { ContentLog.LOGGER.debug("Biome {} turns '{}' off, dropping {}", def.key(), kind, id); }
                else if (FALLS.equals(kind)) { step.add(id); }
                else { step.add(counted(def, id, count, kind)); }
                if (TREES.equals(kind) && extra > 0 && (count == null || count > 0)) {
                    String rarer = rarer(def, id, extra);
                    if (rarer != null) { step.add(rarer); }
                }
            }
            out.add(step);
        }
        return out;
    }

    private static boolean kindKnown(String key) {
        for (Map.Entry<String, List<String>> kind : KINDS) {
            if (kind.getKey().equals(key)) { return true; }
        }
        return false;
    }

    @Nullable private static String kindOf(String id) {
        String path = id.substring(id.indexOf(':') + 1);
        for (Map.Entry<String, List<String>> kind : KINDS) {
            for (String word : kind.getValue()) {
                if (path.contains(word)) { return kind.getKey(); }
            }
        }
        return null;
    }

    private static String counted(BiomeDef def, String id, int count, String kind) {
        JsonObject head = new JsonObject();
        head.addProperty("type", "minecraft:count");
        head.addProperty("count", count);
        String made = rewritten(def, id, head, kind);
        return made == null ? id : made;
    }

    @Nullable private static String rarer(BiomeDef def, String id, int percent) {
        JsonObject head = new JsonObject();
        head.addProperty("type", "minecraft:rarity_filter");
        head.addProperty("chance", Math.max(1, Math.round(100.0F / Mth.clamp(percent, 1, 100))));
        return rewritten(def, id, head, "extra");
    }

    @Nullable private static String rewritten(BiomeDef def, String id, JsonObject head, String suffix) {
        ResourceLocation feature = ResourceLocation.tryParse(id);
        if (feature == null) { return null; }
        JsonObject placed = GameData.json(ResourceLocation.fromNamespaceAndPath(feature.getNamespace(), "worldgen/placed_feature/" + feature.getPath() + ".json"));
        if (placed == null) {
            ContentLog.LOGGER.error("Biome {} sets a count for {}, whose placement could not be read, so it keeps the base biome's own", def.key(), id);
            return null;
        }
        JsonObject out = placed.deepCopy();
        JsonArray placement = out.has("placement") ? GsonHelper.getAsJsonArray(out, "placement") : new JsonArray();
        int at = -1;
        for (int index = 0; index < placement.size(); index++) {
            JsonElement modifier = placement.get(index);
            if (modifier.isJsonObject() && HEAD_MODIFIERS.contains(GsonHelper.getAsString(modifier.getAsJsonObject(), "type", ""))) {
                at = index;
                break;
            }
        }
        if (at < 0) { placement.asList().addFirst(head); }
        else { placement.set(at, head); }
        out.add("placement", placement);
        String path = def.key().getPath() + "/" + feature.getPath() + "_" + suffix;
        GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), "worldgen/placed_feature/" + path + ".json", out.toString());
        return def.key().getNamespace() + ":" + path;
    }

    private static JsonObject box(BiomeDef def) {
        int band = CLIMATES.getOrDefault(def.climate(), -1);
        if (band < 0) {
            if (!def.climate().isEmpty()) { ContentLog.LOGGER.error("Biome {} asks for climate '{}', which is not one of icy, cool, medium, warm or desert, so its own temperature places it", def.key(), def.climate()); }
            band = BANDS.length - 1;
            for (int step = 0; step < BAND_STEPS.length; step++) {
                if (def.temperature() < BAND_STEPS[step]) {
                    band = step;
                    break;
                }
            }
        }
        float width = Mth.clamp(def.weight() / 25.0F, 0.1F, 2.0F);
        float low = Mth.clamp(def.rainfall() * 2.0F - 1.0F - width / 2.0F, -1.0F, 1.0F - width);
        JsonObject point = new JsonObject();
        point.add("temperature", WorldgenJson.range(BANDS[band][0], BANDS[band][1]));
        point.add("humidity", WorldgenJson.range(low, low + width));
        point.add("continentalness", WorldgenJson.range(-0.11F, 1.0F));
        point.add("erosion", WorldgenJson.range(-1.0F, 1.0F));
        point.addProperty("depth", 0.0F);
        point.add("weirdness", WorldgenJson.range(-1.0F, 1.0F));
        point.addProperty("offset", 0.0F);
        return point;
    }

    private static JsonObject entry(String biome, JsonObject point) {
        JsonObject out = new JsonObject();
        out.addProperty("biome", biome);
        out.add("parameters", point);
        return out;
    }

    private static JsonObject encode(Climate.ParameterPoint point) {
        return Climate.ParameterPoint.CODEC.encodeStart(JsonOps.INSTANCE, point).result().map(JsonElement::getAsJsonObject).orElseGet(JsonObject::new);
    }

    private static void tags(BiomeDef def) {
        String id = def.key().toString();
        tag(OVERWORLD_TAG, id, def);
        for (String type : def.types()) {
            String named = ContentFormats.biomeTag(type);
            if (named == null) { ContentLog.LOGGER.error("Biome {} lists type '{}', which no biome tag on this line answers to", def.key(), type); }
            else { tag(named, id, def); }
        }
        if (def.villages()) {
            String kind = VILLAGES.get(def.villageType());
            if (kind == null) {
                ContentLog.LOGGER.error("Biome {} sets villageType '{}', which is not oak, sandstone, acacia or spruce, so villages here build as plains villages", def.key(), def.villageType());
                kind = VILLAGES.get("");
            }
            tag("minecraft:has_structure/village_" + kind, id, def);
        }
        if (def.strongholds()) { tag("minecraft:stronghold_biased_to", id, def); }
    }

    private static void tag(String named, String biome, BiomeDef def) {
        ResourceLocation tag = ResourceLocation.tryParse(named);
        if (tag == null) {
            ContentLog.LOGGER.error("Biome {} would join tag '{}', which is not a valid id", def.key(), named);
            return;
        }
        TAGS.computeIfAbsent(tag, key -> new LinkedHashSet<>()).add(biome);
    }

    @Nullable private static ResourceLocation block(BiomeDef def, String name, String key) {
        if (name.isEmpty()) { return null; }
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null || Registered.find(BuiltInRegistries.BLOCK, id) == null) {
            ContentLog.LOGGER.error("Biome {} names {} '{}', which is not a registered block, so the ground keeps the base biome's", def.key(), key, name);
            return null;
        }
        return id;
    }

    private record Made(BiomeDef def, @Nullable ResourceLocation top, @Nullable ResourceLocation filler, @Nullable ResourceLocation stone, JsonObject point) {}
}
