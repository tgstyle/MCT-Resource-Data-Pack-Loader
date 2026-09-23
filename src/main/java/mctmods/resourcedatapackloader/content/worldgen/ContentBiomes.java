package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
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
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentBiomes {
    public static final String OVERWORLD_TAG = "minecraft:is_overworld";
    public static final String OVERWORLD = "minecraft:overworld";
    private static final float VANILLA_OFFSET = 0.001F;
    private static final float[][] BANDS = { { -1.0F, -0.45F }, { -0.45F, -0.15F }, { -0.15F, 0.2F }, { 0.2F, 0.55F }, { 0.55F, 1.0F } };
    private static final Map<String, Integer> CLIMATES = Map.of("icy", 0, "cool", 1, "medium", 2, "warm", 3, "desert", 4);
    private static final Map<String, String> VILLAGES = Map.of("", "plains", "oak", "plains", "sandstone", "desert", "acacia", "savanna", "spruce", "taiga");
    private static final String EXTRA_TREES = "extratreechance";
    private static final String FALLS = "falls";
    private static final String SPAWN_TARGET = "spawn_target";
    private static final String TREES = "trees";
    private static final String SAND = "minecraft:sand";
    private static final String MYCELIUM = "minecraft:mycelium";
    private static final String TERRACOTTA = "minecraft:terracotta";
    private static final List<Map.Entry<String, List<String>>> KINDS = List.of(
            Map.entry(TREES, List.of("tree", "dark_forest_vegetation", "bamboo_vegetation")),
            Map.entry("bigmushrooms", List.of("mushroom_island_vegetation")),
            Map.entry("mushrooms", List.of("brown_mushroom", "red_mushroom")),
            Map.entry("flowers", List.of("flower", "sunflower")),
            Map.entry("grass", List.of("patch_grass", "patch_tall_grass")),
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
    private static final Set<String> SWITCHES = Set.of(FALLS, "pumpkins", "desertwells", "ice", "fossils", "rocks");
    private static final Set<String> HEAD_MODIFIERS = Set.of("minecraft:count", "minecraft:rarity_filter", "minecraft:noise_threshold_count", "minecraft:noise_based_count");
    private static final Map<ResourceLocation, BiomeDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Made> MADE = new LinkedHashMap<>();
    private static Set<Block> packGround = Set.of();
    private static Set<Block> packStone = Set.of();
    private static Map<ResourceLocation, Soil> packSoil = Map.of();
    private static final Map<ResourceLocation, Set<String>> TAGS = new LinkedHashMap<>();
    private static final Map<BiomeSource, List<Band>> HEIGHT_BANDS = Collections.synchronizedMap(new WeakHashMap<>());
    private static boolean loaded;

    private ContentBiomes() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff() || !Config.content.biomes()) { return; }
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
        for (Made made : MADE.values()) {
            if (!made.def().replaces().isEmpty() && !made.def().banded()) { ContentLog.LOGGER.info("Biome {} names replaces but no minHeight or maxHeight; replaces only narrows a height band, so it does nothing here", made.def().key()); }
        }
        packGround = packBlocks(true);
        packStone = packBlocks(false);
        packSoil = packSoils();
        if (!MADE.isEmpty()) { Summary.info("biomes.generated", "Generated " + MADE.size() + " biome(s) from packs, placed in the overworld through the generated preset"); }
    }

    public static boolean any() { return !MADE.isEmpty(); }

    public static boolean packGround(BlockState state) { return packGround.contains(state.getBlock()); }

    public static boolean packStone(BlockState state) { return packStone.contains(state.getBlock()); }

    @Nullable public static Soil soil(Holder<Biome> biome) { return biome.unwrapKey().map(key -> packSoil.get(key.location())).orElse(null); }

    private static Map<ResourceLocation, Soil> packSoils() {
        Map<ResourceLocation, Soil> soils = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Made> made : MADE.entrySet()) { soils.put(made.getKey(), new Soil(soilBlock(made.getValue().top()), soilBlock(made.getValue().filler()))); }
        return Map.copyOf(soils);
    }

    private static Block soilBlock(@Nullable ResourceLocation id) {
        Block block = Registered.find(ForgeRegistries.BLOCKS, id);
        return block == null ? Blocks.AIR : block;
    }

    public record Soil(Block top, Block filler) {}

    private static Set<Block> packBlocks(boolean soil) {
        Set<Block> ground = new HashSet<>();
        for (Made made : MADE.values()) {
            if (soil) {
                addGround(ground, made.top());
                addGround(ground, made.filler());
            }
            addGround(ground, made.stone());
        }
        ground.remove(Blocks.AIR);
        return Set.copyOf(ground);
    }

    private static void addGround(Set<Block> ground, @Nullable ResourceLocation id) {
        Block block = Registered.find(ForgeRegistries.BLOCKS, id);
        if (block != null) { ground.add(block); }
    }

    public static Set<ResourceLocation> known() {
        Set<ResourceLocation> known = new LinkedHashSet<>(MADE.keySet());
        known.addAll(ContentCaveRegions.made());
        for (MultiNoiseBiomeSourceParameterList.Preset preset : List.of(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD, MultiNoiseBiomeSourceParameterList.Preset.NETHER)) {
            for (Pair<Climate.ParameterPoint, ResourceKey<Biome>> pair : MultiNoiseBiomeSourceParameterList.knownPresets().get(preset).values()) { known.add(pair.getSecond().location()); }
        }
        for (ResourceKey<Biome> end : List.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS, Biomes.THE_VOID)) { known.add(end.location()); }
        return known;
    }

    public static boolean shipped(ResourceLocation biome) {
        String path = "worldgen/biome/" + biome.getPath() + ".json";
        return GameData.has(ResourceLocation.fromNamespaceAndPath(biome.getNamespace(), path)) || GeneratedResources.has(PackType.SERVER_DATA, biome.getNamespace(), path) || PackManager.get().provides(PackType.SERVER_DATA, biome.getNamespace(), path);
    }

    public static boolean placesBiomes(String list, String scope) {
        if (OVERWORLD.equals(list) && any()) { return true; }
        if (ContentCaveRegions.placesIn(scope)) { return true; }
        return ContentBiomeControl.enabled() && ContentBiomeControl.appliesTo(scope);
    }

    @Nullable public static BiomeDef def(ResourceLocation key) {
        Made made = MADE.get(key);
        return made == null ? null : made.def();
    }

    @Nullable public static String villageKind(ResourceLocation key) {
        BiomeDef def = def(key);
        return def == null ? null : VILLAGES.getOrDefault(def.villageType(), VILLAGES.get(""));
    }

    public static JsonArray biomes(String list, String scope) {
        JsonArray out = new JsonArray();
        boolean blocking = ContentBiomeControl.enabled() && ContentBiomeControl.appliesTo(scope);
        ContentBiomeControl.reset();
        boolean overworld = OVERWORLD.equals(list);
        int placed = 0;
        for (Made made : overworld ? MADE.values() : List.<Made>of()) {
            if (made.point() != null) {
                out.add(entry(blocking ? ContentBiomeControl.place(made.def().key(), scope) : made.def().key().toString(), made.point()));
                placed++;
            }
            if (made.def().banded()) { out.add(entry(made.def().key().toString(), ContentCaveRegions.unreachable())); }
        }
        ContentCaveRegions.points(scope, out);
        for (Pair<Climate.ParameterPoint, ResourceKey<Biome>> pair : MultiNoiseBiomeSourceParameterList.knownPresets().get(overworld ? MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD : MultiNoiseBiomeSourceParameterList.Preset.NETHER).values()) {
            ResourceLocation id = pair.getSecond().location();
            JsonObject point = encode(pair.getFirst());
            point.addProperty("offset", VANILLA_OFFSET);
            out.add(entry(blocking ? ContentBiomeControl.place(id, scope) : id.toString(), point));
        }
        if (blocking) { ContentBiomeControl.report(scope); }
        ContentLog.LOGGER.debug("The {} biome list for {} holds {} entries, {} of them the pack's own climate boxes", list, scope, out.size(), placed);
        return out;
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        if (level.dimension() == Level.OVERWORLD) { ContentBiomeControl.reset(); }
        BiomeSource source = level.getChunkSource().getGenerator().getBiomeSource();
        HEIGHT_BANDS.remove(source);
        Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
        List<Band> bands = new ArrayList<>();
        for (Made made : MADE.values()) {
            if (!made.def().banded()) { continue; }
            Holder<Biome> held = registry.getHolder(ResourceKey.create(Registries.BIOME, made.def().key())).orElse(null);
            if (held == null || !source.possibleBiomes().contains(held)) { continue; }
            Set<String> replaces = new LinkedHashSet<>();
            for (String named : made.def().replaces()) { replaces.add(named.trim().toLowerCase(Locale.ROOT)); }
            bands.add(new Band(made.def().minHeight(), made.def().maxHeight(), Set.copyOf(replaces), held));
        }
        if (!bands.isEmpty()) { HEIGHT_BANDS.put(source, List.copyOf(bands)); }
    }

    @Nullable public static Holder<Biome> bandAt(BiomeSource source, int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        if (HEIGHT_BANDS.isEmpty()) { return null; }
        List<Band> bands = HEIGHT_BANDS.get(source);
        if (bands == null) { return null; }
        int y = QuartPos.toBlock(quartY) + 1;
        String under = null;
        for (Band band : bands) {
            if (y < band.minHeight() || y > band.maxHeight()) { continue; }
            if (!band.replaces().isEmpty()) {
                if (under == null) { under = surface(source, quartX, quartY, quartZ, sampler); }
                if (!band.replaces().contains(under)) { continue; }
            }
            return band.biome();
        }
        return null;
    }

    private static String surface(BiomeSource source, int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        if (!(source instanceof MultiNoiseBiomeSource noise)) { return ""; }
        Climate.TargetPoint at = sampler.sample(quartX, quartY, quartZ);
        return noise.getNoiseBiome(new Climate.TargetPoint(at.temperature(), at.humidity(), at.continentalness(), at.erosion(), 0L, at.weirdness())).unwrapKey().map(key -> key.location().toString()).orElse("");
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
            inner.asList().add(0, WorldgenJson.condition(WorldgenJson.biomeIs(self), WorldgenJson.sequence(ground)));
        }
    }

    public static void spawnTargets(JsonObject settings) {
        JsonArray targets = settings.has(SPAWN_TARGET) ? GsonHelper.getAsJsonArray(settings, SPAWN_TARGET) : new JsonArray();
        int added = 0;
        for (Made made : MADE.values()) {
            if (!made.def().playerSpawn() || made.point() == null) { continue; }
            targets.add(made.point().deepCopy());
            added++;
        }
        if (added == 0) { return; }
        settings.add(SPAWN_TARGET, targets);
        ContentLog.LOGGER.debug("The world spawn may also be looked for in the climate of {} biome(s) that ask for it, beside the game's own anywhere-inland target", added);
    }

    @Nullable private static JsonObject build(BiomeDef def) {
        JsonObject base = GameData.json(ResourceLocation.fromNamespaceAndPath(def.baseBiome().getNamespace(), "worldgen/biome/" + def.baseBiome().getPath() + ".json"));
        if (base == null && !ContentBiomeParser.PLAINS.equals(def.baseBiome())) {
            ContentLog.LOGGER.error("Biome {} is built on {}, which is not a biome the game or a mod ships, so it is built on {}", def.key(), def.baseBiome(), ContentBiomeParser.PLAINS);
            base = GameData.json(ResourceLocation.fromNamespaceAndPath(ContentBiomeParser.PLAINS.getNamespace(), "worldgen/biome/" + ContentBiomeParser.PLAINS.getPath() + ".json"));
        }
        if (base == null) { return null; }
        JsonObject biome = base.deepCopy();
        if (def.snow() && def.temperature() >= 0.15F) { ContentLog.LOGGER.debug("Biome {} asks for snow at temperature {}, and snow only falls below 0.15, so it rains there", def.key(), def.temperature()); }
        biome.addProperty("temperature", def.temperature());
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
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(spawn.entity()) && ContentEntities.undefined(spawn.entity())) {
                ContentLog.LOGGER.error("Biome {} spawns '{}', which is not a registered entity, leaving it out", def.key(), spawn.entity());
                continue;
            }
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
        boolean noExtra = def.decoration().containsKey(EXTRA_TREES) && extra <= 0;
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
                if (count == null) { step.add(TREES.equals(kind) && noExtra ? withoutExtra(def, id) : id); }
                else if (count <= 0) { ContentLog.LOGGER.debug("Biome {} turns '{}' off, dropping {}", def.key(), kind, id); }
                else if (SWITCHES.contains(kind)) { step.add(id); }
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

    private static String withoutExtra(BiomeDef def, String id) {
        ResourceLocation feature = ResourceLocation.tryParse(id);
        JsonObject placed = feature == null ? null : GameData.json(ResourceLocation.fromNamespaceAndPath(feature.getNamespace(), "worldgen/placed_feature/" + feature.getPath() + ".json"));
        if (placed == null) { return id; }
        for (JsonElement modifier : GsonHelper.getAsJsonArray(placed, "placement", new JsonArray())) {
            if (!modifier.isJsonObject() || !"minecraft:count".equals(GsonHelper.getAsString(modifier.getAsJsonObject(), "type", ""))) { continue; }
            JsonElement count = modifier.getAsJsonObject().get("count");
            if (count == null || !count.isJsonObject() || !"minecraft:weighted_list".equals(GsonHelper.getAsString(count.getAsJsonObject(), "type", ""))) { return id; }
            int least = Integer.MAX_VALUE;
            for (JsonElement entry : GsonHelper.getAsJsonArray(count.getAsJsonObject(), "distribution", new JsonArray())) {
                JsonElement data = entry.isJsonObject() ? entry.getAsJsonObject().get("data") : null;
                if (data == null || !data.isJsonPrimitive() || !data.getAsJsonPrimitive().isNumber()) { return id; }
                least = Math.min(least, data.getAsInt());
            }
            if (least == Integer.MAX_VALUE) { return id; }
            JsonObject head = new JsonObject();
            head.addProperty("type", "minecraft:count");
            head.addProperty("count", least);
            String made = rewritten(def, id, head, "no_extra");
            return made == null ? id : made;
        }
        return id;
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
        if (at < 0) { placement.asList().add(0, head); }
        else { placement.set(at, head); }
        out.add("placement", placement);
        String path = def.key().getPath() + "/" + feature.getPath() + "_" + suffix;
        GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), "worldgen/placed_feature/" + path + ".json", out.toString());
        return def.key().getNamespace() + ":" + path;
    }

    @Nullable private static JsonObject box(BiomeDef def) {
        if (def.climate().isEmpty() || def.weight() <= 0) { return null; }
        int band = CLIMATES.getOrDefault(def.climate(), -1);
        if (band < 0) {
            ContentLog.LOGGER.error("Biome {} asks for climate '{}', which is not one of icy, cool, medium, warm or desert, so it is registered but not placed", def.key(), def.climate());
            return null;
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
        List<String> types = def.types().isEmpty() ? guessed(def) : def.types();
        if (def.types().isEmpty()) { ContentLog.LOGGER.debug("Biome {} lists no types, guessing them from its properties: {}", def.key(), types); }
        for (String type : types) {
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

    private static List<String> guessed(BiomeDef def) {
        List<String> types = new ArrayList<>();
        int trees = def.decoration().getOrDefault(TREES, 0);
        boolean humid = def.rainfall() > 0.85F;
        float temperature = def.temperature();
        if (trees >= 3) {
            if (humid && temperature >= 0.9F) { types.add("jungle"); }
            else if (!humid) {
                types.add("forest");
                if (temperature <= 0.2F) { types.add("coniferous"); }
            }
        }
        else { types.add("plains"); }
        if (humid) { types.add("wet"); }
        if (def.rainfall() < 0.15F) { types.add("dry"); }
        if (temperature > 0.85F) { types.add("hot"); }
        if (temperature < 0.15F) { types.add("cold"); }
        if (trees > 0 && trees < 3) { types.add("sparse"); }
        else if (trees >= 10) { types.add("dense"); }
        if (def.snow()) { types.add("snowy"); }
        if (!SAND.equals(def.topBlock()) && temperature >= 1.0F && def.rainfall() < 0.2F) { types.add("savanna"); }
        if (SAND.equals(def.topBlock())) { types.add("sandy"); }
        else if (MYCELIUM.equals(def.topBlock())) { types.add("mushroom"); }
        if (TERRACOTTA.equals(def.fillerBlock())) { types.add("mesa"); }
        return types;
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
        if (id == null || Registered.find(ForgeRegistries.BLOCKS, id) == null) {
            ContentLog.LOGGER.error("Biome {} names {} '{}', which is not a registered block, so the ground keeps the base biome's", def.key(), key, name);
            return null;
        }
        return id;
    }

    private record Made(BiomeDef def, @Nullable ResourceLocation top, @Nullable ResourceLocation filler, @Nullable ResourceLocation stone, @Nullable JsonObject point) {}

    private record Band(int minHeight, int maxHeight, Set<String> replaces, Holder<Biome> biome) {}
}
