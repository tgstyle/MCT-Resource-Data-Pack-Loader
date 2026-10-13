package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.SkyStoneDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentSkyLand {
    public static final String FUNCTION = "sky_land";
    public static final double USUAL_ISLANDS = 0.5D;
    public static final double USUAL_THICKNESS = 2.0D;
    private static final String ISLANDS = "islands";
    private static final String CAVES = "caves";
    private static final int HEADROOM = 8;
    private static final int MEMO_LIMIT = 4096;
    private static final double MASK_XZ = 2.0D;
    private static final double MASK_Y = 4.0D / 3.0D;
    private static final double MASK_SPREAD = 0.85D;
    private static final double FAR = 1000000.0D;
    private static final String MASK_NOISE = "minecraft:spaghetti_3d_thickness";
    private static final String CHEESE_NOISE = "minecraft:cave_cheese";
    private static final String LAYER_NOISE = "minecraft:cave_layer";
    private static final String ENTRANCES = "minecraft:overworld/caves/entrances";
    private static final String SPAGHETTI = "minecraft:overworld/caves/spaghetti_2d";
    private static final String ROUGHNESS = "minecraft:overworld/caves/spaghetti_roughness_function";
    private static final String PILLARS = "minecraft:overworld/caves/pillars";
    private static final String MIN = "minecraft:min";
    private static final String MAX = "minecraft:max";
    private static final String ADD = "minecraft:add";
    private static final String MUL = "minecraft:mul";
    private static final Map<ResourceLocation, Land> LAND = new ConcurrentHashMap<>();
    private static final Map<ChunkGenerator, Optional<Land>> HELD = new MapMaker().weakKeys().makeMap();
    private static final Map<String, Binding> BOUND = new ConcurrentHashMap<>();
    private static volatile boolean shaped;

    public record Land(ResourceLocation stone, int floor, int ceiling) {}

    public record Shape(double islands, double thickness) {}

    private record Binding(BiomeSource source, Climate.Sampler sampler, ThreadLocal<Long2ObjectOpenHashMap<Shape>> memo) {}

    private ContentSkyLand() {}

    public static void forget() {
        LAND.clear();
        HELD.clear();
    }

    public static void remember(ResourceLocation settings, Land land) { LAND.put(settings, land); }

    @Nullable public static Land landOf(ChunkGenerator generator) {
        if (LAND.isEmpty()) { return null; }
        return HELD.computeIfAbsent(generator, held -> held instanceof NoiseBasedChunkGenerator noise ? noise.generatorSettings().unwrapKey().map(key -> LAND.get(key.location())) : Optional.empty()).orElse(null);
    }

    @Nullable public static Land raise(JsonObject settings, int maxY, String dimension, String where) {
        String named = ContentTerrain.skyStone(dimension);
        if (named.isEmpty()) { return null; }
        Block block = ContentWorldShape.block(named, "skyStone");
        if (block == null) { return null; }
        ResourceLocation stone = BuiltInRegistries.BLOCK.getKey(block);
        if (maxY <= ContentWorldShape.VANILLA_MAX) {
            ContentLog.LOGGER.info("skyStone names {}, but {} ends at {}, which leaves no sky above the vanilla terrain for it, so it does nothing", named, where, maxY);
            return null;
        }
        JsonObject router = GsonHelper.getAsJsonObject(settings, "noise_router", null);
        if (router == null || !router.has("final_density")) {
            ContentLog.LOGGER.error("The game's overworld noise could not be read for {}, so it gets no sky land", where);
            return null;
        }
        boolean caves = caves(dimension);
        int[] heights = heights();
        int floor = Math.max(heights[0], ContentWorldShape.VANILLA_MAX);
        int ceiling = (int) Math.min(heights[1], (long) maxY - 2 - (caves ? 0 : HEADROOM));
        if (ceiling <= floor) {
            ContentLog.LOGGER.info("skyHeights {} to {} and the top of {} at {} leave no room for sky land above {}, so it does nothing", heights[0], heights[1], where, maxY, ContentWorldShape.VANILLA_MAX);
            return null;
        }
        double islands = islands();
        double thickness = thickness();
        JsonElement body = caves ? interpolated(cave()) : land(dimension, floor, ceiling, islands, thickness);
        router.add("final_density", pair(MAX, router.get("final_density"), range(floor, ceiling + 1.0D, body)));
        Land land = new Land(stone, floor, ceiling);
        stone(settings, land);
        ContentLog.LOGGER.debug("{} gets {} of {} between y {} and {}, islands {} thickness {}", where, caves ? CAVES : ISLANDS, stone, floor, ceiling, islands, thickness);
        return land;
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        shaped = ContentBiomes.shapesSky() || ContentCaveRegions.shapesSky();
        if (!shaped || landOf(level.getChunkSource().getGenerator()) == null) { return; }
        BOUND.put(level.dimension().location().toString(), new Binding(level.getChunkSource().getGenerator().getBiomeSource(), level.getChunkSource().randomState().sampler(), ThreadLocal.withInitial(Long2ObjectOpenHashMap::new)));
    }

    @Nullable public static Shape shapeAt(String dimension, int x, int y, int z) {
        if (!shaped) { return null; }
        Binding bound = BOUND.get(dimension);
        if (bound == null) { return null; }
        int quartX = x >> 2;
        int quartY = y >> 2;
        int quartZ = z >> 2;
        long key = ((long) (quartX & 0xFFFFFF) << 40) | ((long) (quartZ & 0xFFFFFF) << 16) | (quartY & 0xFFFF);
        Long2ObjectOpenHashMap<Shape> memo = bound.memo().get();
        Shape held = memo.get(key);
        if (held != null) { return held; }
        CaveRegionDef region = ContentCaveRegions.regionAt(bound.source(), quartX, quartY, quartZ);
        Holder<Biome> biome = bound.source().getNoiseBiome(quartX, quartY, quartZ, bound.sampler());
        BiomeDef def = biome.unwrapKey().map(found -> ContentBiomes.def(found.location())).orElse(null);
        SkyStoneDef own = region == null ? null : region.sky();
        SkyStoneDef ground = def == null ? null : def.sky();
        Shape found = new Shape(pick(own, ground, true), pick(own, ground, false));
        if (memo.size() > MEMO_LIMIT) { memo.clear(); }
        memo.put(key, found);
        return found;
    }

    private static double pick(@Nullable SkyStoneDef region, @Nullable SkyStoneDef biome, boolean islands) {
        float fromRegion = region == null ? Float.NaN : islands ? region.islands() : region.thickness();
        if (!Float.isNaN(fromRegion)) { return fromRegion; }
        return biome == null ? Double.NaN : islands ? biome.islands() : biome.thickness();
    }

    private static void stone(JsonObject settings, Land land) {
        List<JsonElement> steps = WorldgenJson.sequenceOf(settings).asList();
        int at = 0;
        for (int i = 0; i < steps.size(); i++) {
            JsonElement rule = steps.get(i);
            if (rule.isJsonObject() && "minecraft:above_preliminary_surface".equals(GsonHelper.getAsString(GsonHelper.getAsJsonObject(rule.getAsJsonObject(), "if_true", new JsonObject()), "type", ""))) { at = i + 1; }
        }
        JsonObject above = WorldgenJson.yAbove(WorldgenJson.anchor("absolute", ContentWorldShape.VANILLA_MAX));
        for (Map.Entry<ResourceLocation, ResourceLocation> biome : ContentBiomes.skyStones().entrySet()) {
            JsonObject inside = WorldgenJson.condition(above.deepCopy(), WorldgenJson.block(biome.getValue().toString()));
            steps.add(at++, WorldgenJson.condition(WorldgenJson.biomeIs(List.of(biome.getKey().toString())), inside));
        }
        steps.add(at, WorldgenJson.condition(above, WorldgenJson.block(land.stone().toString())));
    }

    private static boolean caves(String dimension) {
        String asked = ContentTerrain.skyShape(dimension);
        if (asked.isEmpty() || ISLANDS.equals(asked)) { return false; }
        if (CAVES.equals(asked)) { return true; }
        ContentLog.LOGGER.error("skyShape is '{}', not islands or caves, so the sky is shaped into islands", asked);
        return false;
    }

    private static double islands() {
        float value = ContentTerrain.skyIslands();
        if (value >= -1.0F && value <= 1.0F) { return value; }
        ContentLog.LOGGER.error("skyIslands is {}, which is outside -1 to 1, so the islands keep their usual reach", value);
        return USUAL_ISLANDS;
    }

    private static double thickness() {
        float value = ContentTerrain.skyThickness();
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("skyThickness is {}, which is below zero, so the islands keep their usual thickness", value);
        return USUAL_THICKNESS;
    }

    private static int[] heights() {
        int[] written = ContentTerrain.skyHeights();
        if (written.length == 0) { return new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE}; }
        if (written.length == 2 && written[0] < written[1]) { return written; }
        ContentLog.LOGGER.error("skyHeights is {}, which is not a lowest and a highest with the lowest below it, so the sky land fills the whole world above the vanilla terrain", Arrays.toString(written));
        return new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE};
    }

    private static JsonObject land(String dimension, int floor, int ceiling, double islands, double thickness) {
        JsonObject mask = new JsonObject();
        mask.addProperty("type", "minecraft:clamp");
        mask.add("input", pair(MUL, noise(MASK_NOISE, MASK_XZ, MASK_Y), new JsonPrimitive(MASK_SPREAD)));
        mask.addProperty("min", -1.0D);
        mask.addProperty("max", 1.0D);
        JsonObject out = new JsonObject();
        out.addProperty("type", ResourceDataPackLoader.id(FUNCTION).toString());
        out.add("mask", interpolated(mask));
        out.add("cave", interpolated(cave()));
        out.addProperty("dimension", dimension);
        out.addProperty("floor", floor);
        out.addProperty("ceiling", ceiling);
        out.addProperty("islands", islands);
        out.addProperty("thickness", thickness);
        return out;
    }

    private static JsonElement cave() {
        JsonElement layer = pair(MUL, new JsonPrimitive(4.0D), one("minecraft:square", noise(LAYER_NOISE, 1.0D, 8.0D)));
        JsonObject cheese = new JsonObject();
        cheese.addProperty("type", "minecraft:clamp");
        cheese.add("input", pair(ADD, new JsonPrimitive(0.27D), noise(CHEESE_NOISE, 1.0D, 2.0D / 3.0D)));
        cheese.addProperty("min", -1.0D);
        cheese.addProperty("max", 1.0D);
        JsonElement rooms = pair(ADD, layer, pair(ADD, cheese, new JsonPrimitive(0.5D)));
        JsonElement tunnels = pair(ADD, new JsonPrimitive(SPAGHETTI), new JsonPrimitive(ROUGHNESS));
        JsonElement open = pair(MIN, pair(MIN, rooms, new JsonPrimitive(ENTRANCES)), tunnels);
        JsonObject pillars = new JsonObject();
        pillars.addProperty("type", "minecraft:range_choice");
        pillars.addProperty("input", PILLARS);
        pillars.addProperty("min_inclusive", -FAR);
        pillars.addProperty("max_exclusive", 0.03D);
        pillars.addProperty("when_in_range", -FAR);
        pillars.addProperty("when_out_of_range", PILLARS);
        return pair(MAX, open, pillars);
    }

    private static JsonObject range(double least, double most, JsonElement inside) {
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:range_choice");
        out.addProperty("input", "minecraft:y");
        out.addProperty("min_inclusive", least);
        out.addProperty("max_exclusive", most);
        out.add("when_in_range", inside);
        out.addProperty("when_out_of_range", -1.0D);
        return out;
    }

    private static JsonObject pair(String type, JsonElement first, JsonElement second) {
        JsonObject out = new JsonObject();
        out.addProperty("type", type);
        out.add("argument1", first);
        out.add("argument2", second);
        return out;
    }

    private static JsonObject one(String type, JsonElement input) {
        JsonObject out = new JsonObject();
        out.addProperty("type", type);
        out.add("argument", input);
        return out;
    }

    private static JsonObject interpolated(JsonElement input) { return one("minecraft:interpolated", input); }

    private static JsonObject noise(String name, double xz, double y) {
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:noise");
        out.addProperty("noise", name);
        out.addProperty("xz_scale", xz);
        out.addProperty("y_scale", y);
        return out;
    }
}
