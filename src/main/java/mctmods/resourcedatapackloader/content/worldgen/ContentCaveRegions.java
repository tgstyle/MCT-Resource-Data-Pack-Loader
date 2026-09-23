package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.BiomeSpawnDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Hashes;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentCaveRegions {
    public static final String COVER_FEATURE = "cover";
    public static final String STRUCTURE_FEATURE = "cave_structures";
    private static final ResourceLocation DRIPSTONE = ResourceLocation.fromNamespaceAndPath("minecraft", "dripstone_caves");
    private static final float UNREACHABLE = 2.0F;
    private static final int MEMO_LIMIT = 4096;
    private static final Object NONE = new Object();
    private static final ThreadLocal<Map<Long, Object>> MEMO = ThreadLocal.withInitial(HashMap::new);
    private static final Map<BiomeSource, Bound> BOUND = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, CaveRegionDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Made> MADE = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Map<String, List<MobSpawnSettings.SpawnerData>>> SPAWNERS = new LinkedHashMap<>();
    private static boolean loaded;
    private static boolean ambient;

    private record Bound(long seed, List<CaveRegionDef> regions, Map<ResourceLocation, Holder<Biome>> biomes) {}

    private ContentCaveRegions() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.CAVEREGIONS, "cave region", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            CaveRegionDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        for (CaveRegionDef def : DEFS.values()) { ambient |= def.weight() > 0 && def.hasAmbience(); }
        if (!DEFS.isEmpty()) { Summary.info("caveregions", "Loaded " + DEFS.size() + " cave region(s) from packs"); }
    }

    public static void generate() {
        MADE.clear();
        SPAWNERS.clear();
        List<CaveRegionDef> active = new ArrayList<>();
        for (CaveRegionDef def : DEFS.values()) {
            if (def.weight() <= 0 || !ContentRegistry.available(def.requires(), def.key())) { continue; }
            if (def.biome() != null) {
                JsonObject biome = build(def);
                if (biome == null) { continue; }
                GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), "worldgen/biome/" + def.key().getPath() + ".json", biome.toString());
            }
            active.add(def);
        }
        int plain = Math.max(0, ContentControl.number(ContentControl.TERRAIN, "caveRegionPlainWeight", Config.worldgen.caveRegionPlainWeight()));
        int total = plain;
        for (CaveRegionDef def : active) { total += def.weight(); }
        int structured = 0;
        for (CaveRegionDef def : active) {
            JsonObject point = unreachable();
            Set<Block> replace = new LinkedHashSet<>();
            for (BlockMatchDef match : def.coverReplace()) {
                Block block = Registered.find(ForgeRegistries.BLOCKS, match.block());
                if (block == null) { ContentLog.LOGGER.error("Cave region {} names coverReplace block {}, which is not registered, leaving it out", def.key(), match.block()); }
                else { replace.add(block); }
            }
            BlockState floor = state(def, def.floorCover(), "floorCover");
            BlockState ceiling = state(def, def.ceilingCover(), "ceilingCover");
            boolean unnamed = !def.coverReplace().isEmpty() && replace.isEmpty() && (floor != null || ceiling != null);
            if (unnamed) { ContentLog.LOGGER.error("Cave region {} names only coverReplace blocks that are not registered, so it takes no cover", def.key()); }
            ContentCover cover = unnamed || floor == null && ceiling == null ? null : new ContentCover(floor, def.floorChance(), ceiling, def.ceilingChance(), replace, def.minHeight() == CaveRegionDef.WORLD_FLOOR ? Integer.MIN_VALUE : def.minHeight(), def.maxHeight());
            List<BlockState> states = new ArrayList<>();
            if (floor != null) { states.add(floor); }
            if (ceiling != null) { states.add(ceiling); }
            ContentPalette palette = states.isEmpty() ? null : new ContentPalette(states, List.of(1, 1), Set.of(), Set.of(), Set.of(), Set.of(), Set.of());
            MADE.put(def.key(), new Made(def, point, cover, palette));
            if (cover != null) { writeFeature(def, COVER_FEATURE, "_cover", "top_layer_modification"); }
            if (def.hasStructures()) {
                writeFeature(def, STRUCTURE_FEATURE, "_structures", "underground_structures");
                structured++;
            }
            ContentLog.LOGGER.debug("Cave region {} is rolled in cells from y {} to {} at weight {} against the plain weight {}", def.key(), def.minHeight() == CaveRegionDef.WORLD_FLOOR ? "the floor" : def.minHeight(), def.maxHeight(), def.weight(), plain);
        }
        if (!MADE.isEmpty()) { Summary.info("caveregions.generated", "Generated " + MADE.size() + " cave biome(s) from cave regions, " + plain + " parts in " + total + " of each height band left plain" + (structured > 0 ? ", " + structured + " placing structures" : "")); }
    }

    public static Set<ResourceLocation> made() {
        Set<ResourceLocation> out = new LinkedHashSet<>();
        for (Made made : MADE.values()) {
            if (made.def().biome() != null) { out.add(made.def().key()); }
        }
        return out;
    }

    @Nullable public static CaveRegionDef def(ResourceLocation region) {
        Made made = MADE.get(region);
        return made == null ? null : made.def();
    }

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
            if (made.def().biome() == null || !appliesTo(made.def(), dimension)) { continue; }
            JsonObject entry = new JsonObject();
            entry.addProperty("biome", made.def().key().toString());
            entry.add("parameters", made.point().deepCopy());
            out.add(entry);
        }
    }

    public static boolean placesIn(String dimension) {
        for (Made made : MADE.values()) {
            if (made.def().biome() != null && appliesTo(made.def(), dimension)) { return true; }
        }
        return false;
    }

    private static boolean appliesTo(CaveRegionDef def, String dimension) {
        if (def.dimensions().isEmpty()) { return true; }
        for (String named : def.dimensions()) {
            if (ContentFormats.dimensionId(named).equals(dimension)) { return true; }
        }
        return false;
    }

    @Nullable private static JsonObject build(CaveRegionDef def) {
        ResourceLocation base = def.biome();
        if (base == null) { return null; }
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
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(spawn.entity()) && ContentEntities.undefined(spawn.entity())) {
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

    public static void onLevelLoad(LevelEvent.Load event) {
        if (MADE.isEmpty() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        String dimension = level.dimension().location().toString();
        Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
        List<CaveRegionDef> regions = new ArrayList<>();
        Map<ResourceLocation, Holder<Biome>> biomes = new HashMap<>();
        for (Made made : MADE.values()) {
            if (!appliesTo(made.def(), dimension)) { continue; }
            if (made.def().biome() == null) {
                regions.add(made.def());
                continue;
            }
            Holder<Biome> held = registry.getHolder(ResourceKey.create(Registries.BIOME, made.def().key())).orElse(null);
            if (held == null) { continue; }
            regions.add(made.def());
            biomes.put(made.def().key(), held);
        }
        if (regions.isEmpty()) { return; }
        BOUND.put(level.getChunkSource().getGenerator().getBiomeSource(), new Bound(level.getSeed(), List.copyOf(regions), Map.copyOf(biomes)));
    }

    @Nullable public static Holder<Biome> biomeAt(BiomeSource source, int quartX, int quartY, int quartZ) {
        if (BOUND.isEmpty()) { return null; }
        Bound bound = BOUND.get(source);
        if (bound == null) { return null; }
        CaveRegionDef def = regionAt(bound, quartX, quartY, quartZ);
        return def == null || def.biome() == null ? null : bound.biomes().get(def.key());
    }

    public static boolean ambient() { return ambient; }

    @Nullable public static CaveRegionDef regionAt(ServerLevel level, BlockPos pos) {
        Bound bound = BOUND.get(level.getChunkSource().getGenerator().getBiomeSource());
        return bound == null ? null : regionAt(bound, QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
    }

    public static boolean pinsWater(BiomeSource source) {
        Bound bound = BOUND.get(source);
        return bound != null && bound.regions().stream().anyMatch(CaveRegionDef::hasWater);
    }

    public static int waterLevel(BiomeSource source, int x, int y, int z) {
        Bound bound = BOUND.get(source);
        CaveRegionDef def = bound == null ? null : regionAt(bound, QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z));
        return def == null ? CaveRegionDef.NO_WATER : def.waterLevel();
    }

    public static boolean holds(WorldGenLevel level, CaveRegionDef def, BlockPos pos) {
        if (def.biome() != null) { return level.getBiome(pos).is(def.key()); }
        Bound bound = BOUND.get(level.getLevel().getChunkSource().getGenerator().getBiomeSource());
        return bound != null && regionAt(bound, QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ())) == def;
    }

    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        if (MADE.isEmpty() || !(event.getLevel() instanceof ServerLevel level) || level.canSeeSky(event.getPos())) { return; }
        Bound bound = BOUND.get(level.getChunkSource().getGenerator().getBiomeSource());
        if (bound == null) { return; }
        BlockPos pos = event.getPos();
        CaveRegionDef def = regionAt(bound, QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
        if (def == null || def.biome() != null || def.spawns().isEmpty()) { return; }
        if (!def.keepDefaultSpawns()) {
            for (MobSpawnSettings.SpawnerData held : new ArrayList<>(event.getSpawnerDataList())) { event.removeSpawnerData(held); }
        }
        for (MobSpawnSettings.SpawnerData spawn : spawnersFor(def, event.getMobCategory().getName())) { event.addSpawnerData(spawn); }
    }

    private static List<MobSpawnSettings.SpawnerData> spawnersFor(CaveRegionDef def, String category) {
        Map<String, List<MobSpawnSettings.SpawnerData>> made = SPAWNERS.get(def.key());
        if (made == null) {
            made = new LinkedHashMap<>();
            for (BiomeSpawnDef spawn : def.spawns()) {
                EntityType<?> type = Registered.find(ForgeRegistries.ENTITY_TYPES, spawn.entity());
                if (type == null) { continue; }
                made.computeIfAbsent(spawn.category(), key -> new ArrayList<>()).add(new MobSpawnSettings.SpawnerData(type, spawn.weight(), spawn.min(), spawn.max()));
            }
            SPAWNERS.put(def.key(), made);
        }
        return made.getOrDefault(category, List.of());
    }

    @Nullable private static CaveRegionDef regionAt(Bound bound, int qx, int qy, int qz) {
        long memoKey = bound.seed() * 0x9E3779B97F4A7C15L ^ (((long) System.identityHashCode(bound) & 0xFF) << 54) ^ (((long) qx & 0x3FFFF) << 36) ^ (((long) qy & 0x3FFFF) << 18) ^ ((long) qz & 0x3FFFF);
        Map<Long, Object> memo = MEMO.get();
        Object held = memo.get(memoKey);
        if (held != null) { return clampBand(held == NONE ? null : (CaveRegionDef) held, qy << 2); }
        int cellsXZ = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCells", Config.worldgen.caveRegionCells()));
        int cellsY = Math.max(16, ContentControl.number(ContentControl.TERRAIN, "caveRegionCellsY", Config.worldgen.caveRegionCellsY()));
        CaveRegionDef found = resolve(bound.seed(), bound.regions(), qx, qy, qz, cellsXZ >> 2, cellsY >> 2);
        if (memo.size() > MEMO_LIMIT) { memo.clear(); }
        memo.put(memoKey, found == null ? NONE : found);
        return clampBand(found, qy << 2);
    }

    @Nullable private static CaveRegionDef clampBand(@Nullable CaveRegionDef def, int y) {
        if (def == null || y < def.minHeight() || y > def.maxHeight()) { return null; }
        return def;
    }

    @Nullable private static CaveRegionDef resolve(long seed, List<CaveRegionDef> defs, int qx, int qy, int qz, int spanXZ, int spanY) {
        int cellX = Math.floorDiv(qx, spanXZ);
        int cellY = Math.floorDiv(qy, spanY);
        int cellZ = Math.floorDiv(qz, spanXZ);
        long bestDistance = Long.MAX_VALUE;
        long bestHash = 0;
        int bestCenterY = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int cx = cellX + dx;
                    int cy = cellY + dy;
                    int cz = cellZ + dz;
                    long cellHash = Hashes.mix(seed, cx, cy, cz);
                    long jx = cx * (long) spanXZ + Math.floorMod(cellHash, spanXZ);
                    long jy = cy * (long) spanY + Math.floorMod(cellHash >>> 20, spanY);
                    long jz = cz * (long) spanXZ + Math.floorMod(cellHash >>> 40, spanXZ);
                    long offX = jx - qx;
                    long offY = (jy - qy) * spanXZ / Math.max(1, spanY);
                    long offZ = jz - qz;
                    long distance = offX * offX + offY * offY + offZ * offZ;
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestHash = cellHash;
                        bestCenterY = (int) (jy << 2);
                    }
                }
            }
        }
        return regionForCell(bestHash, bestCenterY, defs);
    }

    @Nullable private static CaveRegionDef regionForCell(long cellHash, int centerY, List<CaveRegionDef> defs) {
        int plain = Math.max(0, ContentControl.number(ContentControl.TERRAIN, "caveRegionPlainWeight", Config.worldgen.caveRegionPlainWeight()));
        int total = plain;
        for (CaveRegionDef def : defs) {
            if (centerY >= def.minHeight() && centerY <= def.maxHeight()) { total += def.weight(); }
        }
        if (total <= 0) { return null; }
        long roll = Math.floorMod(cellHash >>> 13, total);
        if (roll < plain) { return null; }
        roll -= plain;
        for (CaveRegionDef def : defs) {
            if (centerY < def.minHeight() || centerY > def.maxHeight()) { continue; }
            if (roll < def.weight()) { return def; }
            roll -= def.weight();
        }
        return null;
    }

    static JsonObject unreachable() {
        JsonObject point = new JsonObject();
        for (String parameter : new String[] {"temperature", "humidity", "continentalness", "erosion", "depth", "weirdness"}) { point.add(parameter, WorldgenJson.range(UNREACHABLE, UNREACHABLE)); }
        point.addProperty("offset", 1.0F);
        return point;
    }

    private static void writeFeature(CaveRegionDef def, String feature, String suffix, String step) {
        String namespace = def.key().getNamespace();
        String path = def.key().getPath() + suffix;
        JsonObject config = new JsonObject();
        config.addProperty("region", def.key().toString());
        JsonObject configured = new JsonObject();
        configured.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + feature);
        configured.add("config", config);
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.CONFIGURED_FEATURES + "/" + path + ".json", configured.toString());
        JsonObject placed = new JsonObject();
        placed.addProperty("feature", namespace + ":" + path);
        placed.add("placement", new JsonArray());
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.PLACED_FEATURES + "/" + path + ".json", placed.toString());
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ContentFormats.ADD_FEATURES);
        if (def.biome() == null) { modifier.add("biomes", ContentFormats.anyBiomes()); }
        else { modifier.addProperty("biomes", def.key().toString()); }
        modifier.addProperty("features", namespace + ":" + path);
        modifier.addProperty("step", step);
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.BIOME_MODIFIERS + "/" + path + ".json", modifier.toString());
    }

    @Nullable private static BlockState state(CaveRegionDef def, @Nullable BlockMatchDef cover, String key) {
        if (cover == null) { return null; }
        Block block = Registered.find(ForgeRegistries.BLOCKS, cover.block());
        if (block == null) {
            ContentLog.LOGGER.error("Cave region {} names {} '{}', which is not a registered block, so that cover is left out", def.key(), key, cover.block());
            return null;
        }
        return ContentStates.state(block, cover.properties(), "cave region " + def.key() + " " + key);
    }

    @Nullable private static BlockMatchDef cover(ResourceLocation key, JsonObject json, String name) { return json.has(name) ? ContentParser.match(key, json.get(name)) : null; }

    @Nullable private static CaveRegionDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Cave region {} is empty, ignoring it", key);
            return null;
        }
        for (String rubic : List.of("skyStone", "skyIslands", "skyThickness")) {
            if (json.has(rubic)) { ContentLog.LOGGER.info("Cave region {} sets '{}', which was the rubic world's and is not read on this line", key, rubic); }
        }
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
                cover(key, json, "floorCover"), Mth.clamp(GsonHelper.getAsFloat(json, "floorChance", 1.0F), 0.0F, 1.0F),
                cover(key, json, "ceilingCover"), Mth.clamp(GsonHelper.getAsFloat(json, "ceilingChance", 1.0F), 0.0F, 1.0F),
                List.copyOf(replace), ContentBiomeParser.spawns(key, json, 8, 4), GsonHelper.getAsBoolean(json, "keepDefaultSpawns", false),
                List.copyOf(structures), Mth.clamp(GsonHelper.getAsFloat(json, "structureChance", 1.0F), 0.0F, 1.0F),
                GsonHelper.getAsString(json, "structureLoot", "").trim(), json.has("biome") ? biome(key, GsonHelper.getAsString(json, "biome", "")) : null, Json.strings(json, "requires"),
                GsonHelper.getAsString(json, "ambientSound", "").trim(), Mth.clamp(GsonHelper.getAsFloat(json, "soundChance", 0.0111F), 0.0F, 1.0F),
                GsonHelper.getAsString(json, "particle", "").trim(), Mth.clamp(GsonHelper.getAsFloat(json, "particleChance", 0.00625F), 0.0F, 1.0F),
                json.has("waterLevel") ? GsonHelper.getAsInt(json, "waterLevel") : CaveRegionDef.NO_WATER);
    }

    @Nullable private static ResourceLocation biome(ResourceLocation key, String written) {
        if (written.trim().isEmpty()) { return null; }
        ResourceLocation named = ContentParser.location(written);
        if (named != null) { return named; }
        ContentLog.LOGGER.error("Cave region {} names biome '{}', which is not a biome id, so it is built on {}", key, written, DRIPSTONE);
        return DRIPSTONE;
    }

    private record Made(CaveRegionDef def, JsonObject point, @Nullable ContentCover cover, @Nullable ContentPalette palette) {}
}
