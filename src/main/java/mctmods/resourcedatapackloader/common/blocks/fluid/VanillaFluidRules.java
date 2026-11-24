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
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

public final class VanillaFluidRules {
    private static final ResourceLocation WATER_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "water");
    private static final ResourceLocation LAVA_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "lava");
    private static final List<FluidReactionDef> WATER = new ArrayList<>();
    private static final List<FluidReactionDef> LAVA = new ArrayList<>();
    private static final List<Source> SOURCES = new ArrayList<>();
    private static final Map<BlockState, Boolean> TOUCHED = new ConcurrentHashMap<>();

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
    private static final List<String> INFINITE_MEMBERS = List.of(BIOMES, DIMENSIONS);
    private static final List<String> RAIN_MEMBERS = List.of(BIOMES, DIMENSIONS, CHANCE, MAX_LEVEL, MIN_HEIGHT, MAX_HEIGHT, SEA_LEVEL, IN_CLAIMS);

    private record Source(List<String> biomes, List<String> dimensions) {}

    private record Rain(List<String> biomes, List<String> dimensions, int chance, int maxLevel, int minHeight, int maxHeight, boolean seaLevel, boolean outsideClaims) {}

    private VanillaFluidRules() {}

    public static boolean wanted() { return !WATER.isEmpty() || !LAVA.isEmpty(); }

    public static boolean sourcesWanted() { return !SOURCES.isEmpty(); }

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

    public static void onCreateSource(BlockEvent.CreateFluidSourceEvent event) {
        if (SOURCES.isEmpty() || !event.getState().getFluidState().getType().isSame(Fluids.WATER)) { return; }
        Level level = event.getLevel();
        Holder<Biome> biome = level.getBiome(event.getPos());
        for (Source source : SOURCES) {
            if (BiomeNames.matches(source.biomes(), biome) && (source.dimensions().isEmpty() || CardPlace.inAny(source.dimensions(), level))) { return; }
        }
        event.setResult(Event.Result.DENY);
    }

    public static void rained(Level level, BlockPos pos, BlockState state) {
        if (RAIN.isEmpty() || state.getBlock() != Blocks.WATER) { return; }
        int depth = state.getValue(LiquidBlock.LEVEL);
        Holder<Biome> biome = level.getBiome(pos);
        for (Rain rain : RAIN) {
            if (depth < 1 || depth > rain.maxLevel() || pos.getY() < rain.minHeight() || pos.getY() > rain.maxHeight()) { continue; }
            if (!BiomeNames.matches(rain.biomes(), biome) || !(rain.dimensions().isEmpty() || CardPlace.inAny(rain.dimensions(), level))) { continue; }
            if (rain.seaLevel() && !(level instanceof ServerLevel server && pos.getY() <= server.getChunkSource().getGenerator().getSeaLevel())) { continue; }
            if (rain.outsideClaims() && ClaimGuard.covers(level, pos) != 0) { continue; }
            if (level.random.nextInt(rain.chance()) == 0) { level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3); }
            return;
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

    @Nullable private static JsonObject member(ResourceLocation key, JsonObject json, String name, boolean water, List<String> allowed) {
        if (!json.has(name)) { return null; }
        if (!water) {
            ContentLog.LOGGER.error("Fluid rule {} has '{}' in a lava file, but only water takes {}, so it is skipped", key, name, name);
            return null;
        }
        JsonObject place = Json.object(json, name);
        for (String member : place.keySet()) {
            if (!allowed.contains(member)) {
                ContentLog.LOGGER.error("Fluid rule {} has the unknown member '{}' in '{}', so the entry is ignored", key, member, name);
                return null;
            }
        }
        return place;
    }

    private static void readInfinite(ResourceLocation key, JsonObject json, boolean water) {
        JsonObject place = member(key, json, INFINITE, water, INFINITE_MEMBERS);
        if (place == null) { return; }
        List<String> biomes = Json.names(place, BIOMES);
        List<String> dimensions = Json.names(place, DIMENSIONS);
        if (biomes.isEmpty() && dimensions.isEmpty()) {
            ContentLog.LOGGER.error("Fluid rule {} has an 'infinite' that names no biomes and no dimensions, so the entry is ignored", key);
            return;
        }
        SOURCES.add(new Source(biomes, dimensions));
    }

    private static int clamped(ResourceLocation key, JsonObject place, String member, int fallback, int high) {
        int value = Json.number(place, member, fallback);
        if (value >= 1 && value <= high) { return value; }
        int fixed = Math.max(1, Math.min(high, value));
        ContentLog.LOGGER.error("Fluid rule {} sets '{}' in 'rain' to {}, which is outside 1 to {}, so {} is used instead", key, member, value, high, fixed);
        return fixed;
    }

    private static void readRain(ResourceLocation key, JsonObject json, boolean water) {
        JsonObject place = member(key, json, RAIN_KEY, water, RAIN_MEMBERS);
        if (place == null) { return; }
        RAIN.add(new Rain(Json.names(place, BIOMES), Json.names(place, DIMENSIONS), clamped(key, place, CHANCE, 160, Integer.MAX_VALUE), clamped(key, place, MAX_LEVEL, 4, 7), Json.number(place, MIN_HEIGHT, Integer.MIN_VALUE), Json.number(place, MAX_HEIGHT, Integer.MAX_VALUE), Json.flag(place, SEA_LEVEL), !Json.getBoolean(place, IN_CLAIMS, true)));
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", RULE_KEYS);
        String named = Json.text(json, FLUID, "");
        ResourceLocation fluid = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        List<FluidReactionDef> into = WATER_ID.equals(fluid) ? WATER : LAVA_ID.equals(fluid) ? LAVA : null;
        if (into == null) {
            ContentLog.LOGGER.error("Fluid rule {} names the fluid '{}', but only minecraft:water and minecraft:lava take rules this way, so the file is skipped", key, named);
            return;
        }
        readInfinite(key, json, into == WATER);
        readRain(key, json, into == WATER);
        List<FluidReactionDef> rules = ContentParserFluids.reactions(key, json, true);
        for (FluidReactionDef rule : rules) {
            ResourceLocation sound = rule.sound.isEmpty() ? null : ResourceLocation.tryParse(rule.sound);
            if (sound != null && !"minecraft".equals(sound.getNamespace())) { VanillaClients.clientDrawn(key, "sound " + rule.sound); }
        }
        into.addAll(rules);
    }
}
