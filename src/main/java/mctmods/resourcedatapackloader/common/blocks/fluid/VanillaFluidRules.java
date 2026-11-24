package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidReactionDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserFluids;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.CreateFluidSourceEvent;

public final class VanillaFluidRules {
    private static final Identifier WATER_ID = Identifier.fromNamespaceAndPath("minecraft", "water");
    private static final Identifier LAVA_ID = Identifier.fromNamespaceAndPath("minecraft", "lava");
    private static final List<FluidReactionDef> WATER = new ArrayList<>();
    private static final List<FluidReactionDef> LAVA = new ArrayList<>();
    private static final List<Place> SOURCES = new ArrayList<>();
    private static final List<Rain> RAIN = new ArrayList<>();
    private static final String BIOMES = "biomes";
    private static final String DIMENSIONS = "dimensions";
    private static final String CHANCE = "chance";
    private static final String MAX_LEVEL = "maxLevel";
    private static final String MIN_HEIGHT = "minHeight";
    private static final String MAX_HEIGHT = "maxHeight";
    private static final String SEA_LEVEL = "seaLevel";
    private static final String IN_CLAIMS = "inClaims";
    private static final String INFINITE = "infinite";
    private static final String RAIN_KEY = "rain";
    private static final String FLUID = "fluid";
    private static final List<String> RULE_KEYS = List.of(FLUID, INFINITE, RAIN_KEY, "reactions");
    private static final Set<String> RAIN_MEMBERS = Set.of(BIOMES, DIMENSIONS, CHANCE, MAX_LEVEL, MIN_HEIGHT, MAX_HEIGHT, SEA_LEVEL, IN_CLAIMS);
    private static final Map<BlockState, Boolean> TOUCHED = new ConcurrentHashMap<>();

    private VanillaFluidRules() {}

    public static boolean wanted() { return !WATER.isEmpty() || !LAVA.isEmpty(); }

    public static void load() {
        WATER.clear();
        LAVA.clear();
        SOURCES.clear();
        RAIN.clear();
        if (Config.definitionsOff() || !Config.content.fluidRules()) { return; }
        Json.eachObject(PackManager.FLUID_RULES, "fluid rule", VanillaFluidRules::read);
    }

    public static void resolve() {
        TOUCHED.clear();
        WATER.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + WATER_ID));
        LAVA.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + LAVA_ID));
    }

    public static List<FluidReactionDef> rulesOf(Block block) {
        if (block == Blocks.WATER) { return WATER; }
        return block == Blocks.LAVA ? LAVA : Collections.emptyList();
    }

    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        BlockState state = event.getState();
        if (!rulesOf(state.getBlock()).isEmpty()) { FluidReactionQueue.watch(level, event.getPos(), 0, false); }
        if (!touched(state)) { return; }
        for (Direction side : event.getNotifiedSides()) {
            BlockPos beside = event.getPos().relative(side);
            if (level.isLoaded(beside) && !rulesOf(level.getBlockState(beside).getBlock()).isEmpty()) { FluidReactionQueue.watch(level, beside, 0, false); }
        }
    }

    public static void tagsUpdated() { TOUCHED.clear(); }

    private static boolean touched(BlockState state) { return TOUCHED.computeIfAbsent(state, key -> touches(WATER, key) || touches(LAVA, key)); }

    private static boolean touches(List<FluidReactionDef> rules, BlockState state) {
        for (FluidReactionDef rule : rules) {
            FluidTouch touch = rule.touch();
            if (touch != null && touch.matches(state)) { return true; }
        }
        return false;
    }

    private static void read(Identifier key, JsonObject json) {
        Json.unknownKeys(json, "the top level", RULE_KEYS);
        String named = Json.text(json, FLUID, "");
        Identifier fluid = named.isEmpty() ? null : Identifier.tryParse(named);
        List<FluidReactionDef> into = fluid == null ? null : WATER_ID.equals(fluid) ? WATER : LAVA_ID.equals(fluid) ? LAVA : null;
        if (into == null) {
            ContentLog.LOGGER.error("Fluid rule {} names the fluid '{}', but only minecraft:water and minecraft:lava take rules this way, so the file is skipped", key, named);
            return;
        }
        List<FluidReactionDef> rules = ContentParserFluids.reactions(key, json, true);
        for (FluidReactionDef rule : rules) {
            Identifier sound = rule.sound.isEmpty() ? null : Identifier.tryParse(rule.sound);
            if (sound != null && !"minecraft".equals(sound.getNamespace())) { VanillaClients.clientDrawn(key, "sound " + rule.sound); }
        }
        into.addAll(rules);
        if (json.has(INFINITE)) { readInfinite(key, json, into == WATER); }
        if (json.has(RAIN_KEY)) { readRain(key, json, into == WATER); }
    }

    private static void readInfinite(Identifier key, JsonObject json, boolean water) {
        if (!water) {
            ContentLog.LOGGER.error("Fluid rule {} has 'infinite' in a lava file, but only water takes infinite, so it is skipped", key);
            return;
        }
        if (!GsonHelper.isObjectNode(json, INFINITE)) {
            ContentLog.LOGGER.error("Fluid rule {} has an infinite that is not an object, so it is ignored", key);
            return;
        }
        JsonObject place = json.getAsJsonObject(INFINITE);
        for (String member : place.keySet()) {
            if (!BIOMES.equals(member) && !DIMENSIONS.equals(member)) {
                ContentLog.LOGGER.error("Fluid rule {} has the unknown member '{}' in 'infinite', so the entry is ignored", key, member);
                return;
            }
        }
        List<String> biomes = Json.names(place, BIOMES);
        List<String> dimensions = Json.names(place, DIMENSIONS);
        if (biomes.isEmpty() && dimensions.isEmpty()) {
            ContentLog.LOGGER.error("Fluid rule {} has an 'infinite' that names no biomes and no dimensions, so the entry is ignored", key);
            return;
        }
        SOURCES.add(new Place(Collections.unmodifiableList(biomes), Collections.unmodifiableList(dimensions)));
    }

    private static void readRain(Identifier key, JsonObject json, boolean water) {
        if (!water) {
            ContentLog.LOGGER.error("Fluid rule {} has 'rain' in a lava file, but only water takes rain, so it is skipped", key);
            return;
        }
        if (!GsonHelper.isObjectNode(json, RAIN_KEY)) {
            ContentLog.LOGGER.error("Fluid rule {} has a rain that is not an object, so it is ignored", key);
            return;
        }
        JsonObject rain = json.getAsJsonObject(RAIN_KEY);
        for (String member : rain.keySet()) {
            if (!RAIN_MEMBERS.contains(member)) {
                ContentLog.LOGGER.error("Fluid rule {} has the unknown member '{}' in 'rain', so the entry is ignored", key, member);
                return;
            }
        }
        int chance = Json.number(rain, CHANCE, 160);
        if (chance < 1) {
            ContentLog.LOGGER.error("Fluid rule {} sets rain chance to {}, which is below 1, so 1 is used", key, chance);
            chance = 1;
        }
        int maxLevel = Json.number(rain, MAX_LEVEL, 4);
        if (maxLevel < 1 || maxLevel > 7) {
            ContentLog.LOGGER.error("Fluid rule {} sets rain maxLevel to {}, which is outside 1 to 7, so it is clamped", key, maxLevel);
            maxLevel = Math.clamp(maxLevel, 1, 7);
        }
        int minHeight = rain.has(MIN_HEIGHT) ? Json.number(rain, MIN_HEIGHT, Integer.MIN_VALUE) : Integer.MIN_VALUE;
        int maxHeight = rain.has(MAX_HEIGHT) ? Json.number(rain, MAX_HEIGHT, Integer.MAX_VALUE) : Integer.MAX_VALUE;
        RAIN.add(new Rain(Collections.unmodifiableList(Json.names(rain, BIOMES)), Collections.unmodifiableList(Json.names(rain, DIMENSIONS)), chance, maxLevel, minHeight, maxHeight, Json.flag(rain, SEA_LEVEL), !Json.getBoolean(rain, IN_CLAIMS, true)));
    }

    public static void rained(Level level, BlockPos pos, BlockState state) {
        if (RAIN.isEmpty() || state.getBlock() != Blocks.WATER) { return; }
        int depth = state.getValue(LiquidBlock.LEVEL);
        Holder<Biome> biome = level.getBiome(pos);
        for (Rain rain : RAIN) {
            if (depth < 1 || depth > rain.maxLevel() || pos.getY() < rain.minHeight() || pos.getY() > rain.maxHeight()) { continue; }
            if (!BiomeNames.matches(rain.biomes(), biome) || !rain.dimensions().isEmpty() && !CardPlace.inAny(rain.dimensions(), level)) { continue; }
            if (rain.seaLevel() && pos.getY() > level.getSeaLevel() || rain.outsideClaims() && ClaimGuard.covers(level, pos) != 0) { continue; }
            if (level.getRandom().nextInt(rain.chance()) == 0) { level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3); }
            return;
        }
    }

    public static boolean sourcesWanted() { return !SOURCES.isEmpty(); }

    public static void onCreateSource(CreateFluidSourceEvent event) {
        if (SOURCES.isEmpty() || !event.getFluidState().getType().isSame(Fluids.WATER)) { return; }
        ServerLevel level = event.getLevel();
        Holder<Biome> biome = level.getBiome(event.getPos());
        for (Place place : SOURCES) {
            if (BiomeNames.matches(place.biomes(), biome) && (place.dimensions().isEmpty() || CardPlace.inAny(place.dimensions(), level))) { return; }
        }
        event.setCanConvert(false);
    }

    private record Place(List<String> biomes, List<String> dimensions) {}

    private record Rain(List<String> biomes, List<String> dimensions, int chance, int maxLevel, int minHeight, int maxHeight, boolean seaLevel, boolean outsideClaims) {}
}
