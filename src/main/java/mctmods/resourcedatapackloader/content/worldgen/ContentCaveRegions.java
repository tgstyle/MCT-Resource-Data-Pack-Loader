package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BiomeSpawnDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentCaveRegions {
    public static final String COVER_FEATURE = "cover";
    private static final ResourceLocation DRIPSTONE = ResourceLocation.fromNamespaceAndPath("minecraft", "dripstone_caves");
    private static final String OVERWORLD = "minecraft:overworld";
    private static final String NETHER = "minecraft:the_nether";
    private static final int SEA = 64;
    private static final float SHALLOWEST = 0.2F;
    private static final float DEEPEST = 1.0F;
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, CaveRegionDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Made> MADE = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentCaveRegions() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff()) { return; }
        Json.eachFile(PackManager.CAVEREGIONS, "cave region", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            CaveRegionDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("caveregions", "Loaded " + DEFS.size() + " cave region(s) from packs"); }
    }

    public static void generate() {
        MADE.clear();
        List<CaveRegionDef> active = new ArrayList<>();
        for (CaveRegionDef def : DEFS.values()) {
            if (def.weight() <= 0 || !ContentRegistry.available(def.requires(), def.key())) { continue; }
            JsonObject biome = build(def);
            if (biome == null) { continue; }
            GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), "worldgen/biome/" + def.key().getPath() + ".json", biome.toString());
            active.add(def);
        }
        int plain = Math.max(0, ContentControl.number(ContentControl.TERRAIN, "caveRegionPlainWeight", Config.worldgen.caveRegionPlainWeight()));
        int total = plain;
        for (CaveRegionDef def : active) { total += def.weight(); }
        float low = -1.0F;
        for (CaveRegionDef def : active) {
            float width = total <= 0 ? 0.0F : 2.0F * def.weight() / total;
            JsonObject point = point(def, low, low + width);
            low += width;
            Set<Block> replace = new LinkedHashSet<>();
            for (BlockMatchDef match : def.coverReplace()) {
                Block block = Registered.find(ForgeRegistries.BLOCKS, match.block());
                if (block == null) { ContentLog.LOGGER.error("Cave region {} names coverReplace block {}, which is not registered, leaving it out", def.key(), match.block()); }
                else { replace.add(block); }
            }
            BlockState floor = state(def, def.floorCover(), "floorCover");
            BlockState ceiling = state(def, def.ceilingCover(), "ceilingCover");
            ContentCover cover = floor == null && ceiling == null ? null : new ContentCover(def.key(), floor, def.floorChance(), ceiling, def.ceilingChance(), replace, def.minHeight() == CaveRegionDef.WORLD_FLOOR ? Integer.MIN_VALUE : def.minHeight(), def.maxHeight());
            List<BlockState> states = new ArrayList<>();
            if (floor != null) { states.add(floor); }
            if (ceiling != null) { states.add(ceiling); }
            ContentPalette palette = states.isEmpty() ? null : new ContentPalette(states, List.of(1, 1), Set.of(), Set.of(), Set.of(), Set.of(), Set.of());
            MADE.put(def.key(), new Made(def, point, cover, palette));
            if (cover != null) { writeCover(def); }
            ContentLog.LOGGER.debug("Cave region {} takes the underground at humidity {} and depth {}", def.key(), point.get("humidity"), point.get("depth"));
        }
        if (!MADE.isEmpty()) { Summary.info("caveregions.generated", "Generated " + MADE.size() + " cave biome(s) from cave regions, " + plain + " parts in " + total + " of the underground left plain"); }
    }

    public static boolean any() { return !MADE.isEmpty(); }

    @Nullable public static ContentCover cover(ResourceLocation region) {
        Made made = MADE.get(region);
        return made == null ? null : made.cover();
    }

    public static ContentPalette palette(ResourceLocation region) {
        Made made = MADE.get(region);
        return made == null || made.palette() == null ? new ContentPalette(List.of(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()), List.of(1), Set.of(), Set.of(), Set.of(), Set.of(), Set.of()) : made.palette();
    }

    public static void points(String dimension, JsonArray out) {
        for (Made made : MADE.values()) {
            if (!appliesTo(made.def(), dimension)) { continue; }
            JsonObject entry = new JsonObject();
            entry.addProperty("biome", made.def().key().toString());
            entry.add("parameters", made.point().deepCopy());
            out.add(entry);
        }
    }

    public static boolean placesIn(String dimension) {
        for (Made made : MADE.values()) {
            if (appliesTo(made.def(), dimension)) { return true; }
        }
        return false;
    }

    private static boolean appliesTo(CaveRegionDef def, String dimension) {
        if (!OVERWORLD.equals(dimension) && !NETHER.equals(dimension)) { return false; }
        if (def.dimensions().isEmpty()) { return true; }
        for (String named : def.dimensions()) {
            if (ContentFormats.dimensionId(named).equals(dimension)) { return true; }
        }
        return false;
    }

    @Nullable private static JsonObject build(CaveRegionDef def) {
        ResourceLocation base = def.biome() == null ? DRIPSTONE : def.biome();
        JsonObject held = GameData.json(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), "worldgen/biome/" + base.getPath() + ".json"));
        if (held == null) {
            ContentLog.LOGGER.error("Cave region {} is built on biome {}, which is not one the game or a mod ships, so it is skipped", def.key(), base);
            return null;
        }
        JsonObject biome = held.deepCopy();
        JsonObject spawners = GsonHelper.getAsJsonObject(biome, "spawners", new JsonObject()).deepCopy();
        if (!def.keepDefaultSpawns() && !def.spawns().isEmpty()) {
            for (String category : new ArrayList<>(spawners.keySet())) { spawners.add(category, new JsonArray()); }
        }
        for (BiomeSpawnDef spawn : def.spawns()) {
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(spawn.entity()) && !ContentEntities.defines(spawn.entity())) {
                ContentLog.LOGGER.error("Cave region {} spawns '{}', which is not a registered entity, leaving it out", def.key(), spawn.entity());
                continue;
            }
            JsonArray list = spawners.has(spawn.category()) ? GsonHelper.getAsJsonArray(spawners, spawn.category()) : new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("type", spawn.entity().toString());
            entry.addProperty("weight", spawn.weight());
            entry.addProperty("minCount", spawn.min());
            entry.addProperty("maxCount", spawn.max());
            list.add(entry);
            spawners.add(spawn.category(), list);
        }
        biome.add("spawners", spawners);
        return biome;
    }

    private static JsonObject point(CaveRegionDef def, float humidityLow, float humidityHigh) {
        float shallow = def.maxHeight() == Integer.MAX_VALUE ? SHALLOWEST : Mth.clamp((SEA - def.maxHeight()) / 128.0F, SHALLOWEST, DEEPEST);
        float deep = def.minHeight() == CaveRegionDef.WORLD_FLOOR ? DEEPEST : Mth.clamp((SEA - def.minHeight()) / 128.0F, SHALLOWEST, DEEPEST);
        if (deep < shallow) { deep = shallow; }
        JsonObject point = new JsonObject();
        point.add("temperature", WorldgenJson.range(-1.0F, 1.0F));
        point.add("humidity", WorldgenJson.range(humidityLow, humidityHigh));
        point.add("continentalness", WorldgenJson.range(-1.0F, 1.0F));
        point.add("erosion", WorldgenJson.range(-1.0F, 1.0F));
        point.add("depth", WorldgenJson.range(shallow, deep));
        point.add("weirdness", WorldgenJson.range(-1.0F, 1.0F));
        point.addProperty("offset", 0.0F);
        return point;
    }

    private static void writeCover(CaveRegionDef def) {
        String namespace = def.key().getNamespace();
        String path = def.key().getPath() + "_cover";
        JsonObject config = new JsonObject();
        config.addProperty("region", def.key().toString());
        JsonObject configured = new JsonObject();
        configured.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + COVER_FEATURE);
        configured.add("config", config);
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.CONFIGURED_FEATURES + "/" + path + ".json", configured.toString());
        JsonObject placed = new JsonObject();
        placed.addProperty("feature", namespace + ":" + path);
        placed.add("placement", new JsonArray());
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.PLACED_FEATURES + "/" + path + ".json", placed.toString());
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ContentFormats.ADD_FEATURES);
        modifier.addProperty("biomes", def.key().toString());
        modifier.addProperty("features", namespace + ":" + path);
        modifier.addProperty("step", "top_layer_modification");
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.BIOME_MODIFIERS + "/" + path + ".json", modifier.toString());
    }

    @Nullable private static BlockState state(CaveRegionDef def, String name, String key) {
        if (name.isEmpty()) { return null; }
        Block block = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(name.trim()));
        if (block == null) {
            ContentLog.LOGGER.error("Cave region {} names {} '{}', which is not a registered block, so that cover is left out", def.key(), key, name);
            return null;
        }
        return block.defaultBlockState();
    }

    @Nullable private static CaveRegionDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Cave region {} is empty, ignoring it", key);
            return null;
        }
        for (String rubic : List.of("skyStone", "skyIslands", "skyThickness")) {
            if (json.has(rubic)) { ContentLog.LOGGER.info("Cave region {} sets '{}', which was the rubic world's and is not read on this line", key, rubic); }
        }
        if (json.has("waterLevel")) { ContentLog.LOGGER.info("Cave region {} sets waterLevel, which this line does not read: aquifers are the noise settings' and cannot be pinned per biome", key); }
        if (json.has("structures")) { ContentLog.LOGGER.info("Cave region {} names structures, which wait for the structure layer", key); }
        int minHeight = json.has("minHeight") ? GsonHelper.getAsInt(json, "minHeight") : CaveRegionDef.WORLD_FLOOR;
        int maxHeight = GsonHelper.getAsInt(json, "maxHeight", 48);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Cave region {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        List<BlockMatchDef> replace = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "coverReplace", new JsonArray())) {
            BlockMatchDef match = ContentParser.match(key, element);
            if (match != null) { replace.add(match); }
        }
        List<PickDef> structures = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "structures", new JsonArray())) {
            if (element.isJsonPrimitive()) { structures.add(new PickDef(element.getAsString().trim().toLowerCase(Locale.ROOT), 1)); }
            else if (element.isJsonObject()) { structures.add(new PickDef(GsonHelper.getAsString(element.getAsJsonObject(), "structure", "").trim().toLowerCase(Locale.ROOT), Math.max(1, GsonHelper.getAsInt(element.getAsJsonObject(), "weight", 1)))); }
        }
        return new CaveRegionDef(key, Math.max(0, GsonHelper.getAsInt(json, "weight", 1)), minHeight, maxHeight, Json.strings(json, "dimensions"),
                GsonHelper.getAsString(json, "floorCover", "").trim(), Mth.clamp(GsonHelper.getAsFloat(json, "floorChance", 1.0F), 0.0F, 1.0F),
                GsonHelper.getAsString(json, "ceilingCover", "").trim(), Mth.clamp(GsonHelper.getAsFloat(json, "ceilingChance", 1.0F), 0.0F, 1.0F),
                List.copyOf(replace), ContentBiomeParser.spawns(key, json), GsonHelper.getAsBoolean(json, "keepDefaultSpawns", false),
                List.copyOf(structures), Mth.clamp(GsonHelper.getAsFloat(json, "structureChance", 1.0F), 0.0F, 1.0F),
                GsonHelper.getAsString(json, "structureLoot", "").trim(), ContentParser.location(GsonHelper.getAsString(json, "biome", "")), Json.strings(json, "requires"));
    }

    private record Made(CaveRegionDef def, JsonObject point, @Nullable ContentCover cover, @Nullable ContentPalette palette) {}
}
