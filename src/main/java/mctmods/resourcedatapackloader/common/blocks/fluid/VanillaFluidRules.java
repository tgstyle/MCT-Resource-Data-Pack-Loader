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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
    private static final ResourceLocation WATER_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "water");
    private static final ResourceLocation LAVA_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "lava");
    private static final List<FluidReactionDef> WATER = new ArrayList<>();
    private static final List<FluidReactionDef> LAVA = new ArrayList<>();
    private static final List<Place> SOURCES = new ArrayList<>();
    private static final List<Rain> RAIN = new ArrayList<>();
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

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", RULE_KEYS);
        String named = Json.text(json, "fluid", "");
        ResourceLocation fluid = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        List<FluidReactionDef> into = WATER_ID.equals(fluid) ? WATER : LAVA_ID.equals(fluid) ? LAVA : null;
        if (into == null) {
            ContentLog.LOGGER.error("Fluid rule {} names the fluid '{}', but only minecraft:water and minecraft:lava take rules this way, so the file is skipped", key, named);
            return;
        }
        List<FluidReactionDef> rules = ContentParserFluids.reactions(key, json, true);
        for (FluidReactionDef rule : rules) {
            ResourceLocation sound = rule.sound.isEmpty() ? null : ResourceLocation.tryParse(rule.sound);
            if (sound != null && !"minecraft".equals(sound.getNamespace())) { VanillaClients.clientDrawn(key, "sound " + rule.sound); }
        }
        into.addAll(rules);
        if (json.has("infinite")) { readInfinite(key, json, into == WATER); }
        if (json.has("rain")) { readRain(key, json, into == WATER); }
    }

    private static void readInfinite(ResourceLocation key, JsonObject json, boolean water) {
        if (!water) {
            ContentLog.LOGGER.error("Fluid rule {} has 'infinite' in a lava file, but only water takes infinite, so it is skipped", key);
            return;
        }
        JsonObject infinite = Json.getJsonObject(json, "infinite", new JsonObject());
        if (infinite.isEmpty()) {
            ContentLog.LOGGER.error("Fluid rule {} has an 'infinite' that names no biomes and no dimensions, so the entry is ignored", key);
            return;
        }
        for (String member : infinite.keySet()) {
            if (!"biomes".equals(member) && !"dimensions".equals(member)) {
                ContentLog.LOGGER.error("Fluid rule {} has the unknown member '{}' in 'infinite', so the entry is ignored", key, member);
                return;
            }
        }
        SOURCES.add(new Place(Json.names(infinite, "biomes"), Json.names(infinite, "dimensions")));
    }

    private static final List<String> RULE_KEYS = List.of("fluid", "infinite", "rain", "reactions");
    private static final List<String> RAIN_MEMBERS = List.of("biomes", "dimensions", "chance", "maxLevel", "minHeight", "maxHeight", "seaLevel", "inClaims");

    private static void readRain(ResourceLocation key, JsonObject json, boolean water) {
        if (!water) {
            ContentLog.LOGGER.error("Fluid rule {} has 'rain' in a lava file, but only water takes rain, so it is skipped", key);
            return;
        }
        JsonObject rain = Json.getJsonObject(json, "rain", new JsonObject());
        for (String member : rain.keySet()) {
            if (!RAIN_MEMBERS.contains(member)) {
                ContentLog.LOGGER.error("Fluid rule {} has the unknown member '{}' in 'rain', so the entry is ignored", key, member);
                return;
            }
        }
        int chance = Json.number(rain, "chance", 160);
        if (chance < 1) {
            ContentLog.LOGGER.error("Fluid rule {} sets chance in 'rain' to {}, which is outside 1 and up, so 1 is used", key, chance);
            chance = 1;
        }
        int maxLevel = Json.number(rain, "maxLevel", 4);
        if (maxLevel < 1 || maxLevel > 7) {
            ContentLog.LOGGER.error("Fluid rule {} sets maxLevel in 'rain' to {}, which is outside 1 to 7, so {} is used", key, maxLevel, Mth.clamp(maxLevel, 1, 7));
            maxLevel = Mth.clamp(maxLevel, 1, 7);
        }
        int minHeight = rain.has("minHeight") ? Json.number(rain, "minHeight", Integer.MIN_VALUE) : Integer.MIN_VALUE;
        int maxHeight = rain.has("maxHeight") ? Json.number(rain, "maxHeight", Integer.MAX_VALUE) : Integer.MAX_VALUE;
        boolean outsideClaims = !Json.getBoolean(rain, "inClaims", true);
        RAIN.add(new Rain(new Place(Json.names(rain, "biomes"), Json.names(rain, "dimensions")), chance, maxLevel, minHeight, maxHeight, Json.flag(rain, "seaLevel"), outsideClaims));
    }

    public static void rained(Level level, BlockPos pos, BlockState state) {
        if (RAIN.isEmpty() || state.getBlock() != Blocks.WATER) { return; }
        int depth = state.getValue(LiquidBlock.LEVEL);
        if (depth < 1 || !(level instanceof ServerLevel server)) { return; }
        Holder<Biome> biome = level.getBiome(pos);
        for (Rain rain : RAIN) {
            if (depth > rain.maxLevel() || !BiomeNames.matches(rain.place().biomes(), biome) || !(rain.place().dimensions().isEmpty() || CardPlace.inAny(rain.place().dimensions(), level))) { continue; }
            if (pos.getY() < rain.minHeight() || pos.getY() > rain.maxHeight() || rain.seaLevel() && pos.getY() > server.getChunkSource().getGenerator().getSeaLevel()) { continue; }
            if (rain.outsideClaims() && ClaimGuard.covers(level, pos) != 0) { continue; }
            if (level.random.nextInt(rain.chance()) == 0) { level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3); }
            return;
        }
    }

    public static boolean sourcesWanted() { return !SOURCES.isEmpty(); }

    public static void onCreateSource(CreateFluidSourceEvent event) {
        if (SOURCES.isEmpty() || !event.getFluidState().getType().isSame(Fluids.WATER)) { return; }
        Level level = event.getLevel();
        Holder<Biome> biome = level.getBiome(event.getPos());
        for (Place place : SOURCES) {
            if (BiomeNames.matches(place.biomes(), biome) && (place.dimensions().isEmpty() || CardPlace.inAny(place.dimensions(), level))) { return; }
        }
        event.setCanConvert(false);
    }

    private record Place(List<String> biomes, List<String> dimensions) {}

    private record Rain(Place place, int chance, int maxLevel, int minHeight, int maxHeight, boolean seaLevel, boolean outsideClaims) {}
}
