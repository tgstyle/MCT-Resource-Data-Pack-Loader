package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidReactionDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserFluids;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class VanillaFluidRules {
    private static final ResourceLocation WATER_ID = new ResourceLocation(IdNames.VANILLA, "water");
    private static final ResourceLocation LAVA_ID = new ResourceLocation(IdNames.VANILLA, "lava");
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
    private static final Set<String> INFINITE_MEMBERS = new HashSet<>(Arrays.asList(BIOMES, DIMENSIONS));
    private static final Set<String> RAIN_MEMBERS = new HashSet<>(Arrays.asList(BIOMES, DIMENSIONS, CHANCE, MAX_LEVEL, MIN_HEIGHT, MAX_HEIGHT, SEA_LEVEL, IN_CLAIMS));
    private static final List<String> RULE_KEYS = Collections.unmodifiableList(Arrays.asList(FLUID, INFINITE, RAIN_KEY, "reactions"));
    private static final Map<IBlockState, Boolean> TOUCHED = new ConcurrentHashMap<>();

    private VanillaFluidRules() {}

    public static boolean wanted() { return !WATER.isEmpty() || !LAVA.isEmpty(); }

    public static boolean sourcesWanted() { return !SOURCES.isEmpty(); }

    public static void load() {
        WATER.clear();
        LAVA.clear();
        SOURCES.clear();
        RAIN.clear();
        if (!Config.content.load || !Config.content.fluidRules) { return; }
        Json.eachObject(PackManager.FLUID_RULES, "fluid rule", VanillaFluidRules::read);
    }

    public static void resolve() {
        TOUCHED.clear();
        WATER.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + WATER_ID));
        LAVA.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + LAVA_ID));
    }

    public static List<FluidReactionDef> rulesOf(Block block) {
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) { return WATER; }
        return block == Blocks.LAVA || block == Blocks.FLOWING_LAVA ? LAVA : Collections.emptyList();
    }

    @SubscribeEvent public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        if (world.isRemote) { return; }
        IBlockState state = event.getState();
        if (!rulesOf(state.getBlock()).isEmpty()) { FluidReactionQueue.watch(world, event.getPos(), 0, false); }
        if (!touched(state)) { return; }
        for (EnumFacing side : event.getNotifiedSides()) {
            BlockPos beside = event.getPos().offset(side);
            if (world.isBlockLoaded(beside) && !rulesOf(world.getBlockState(beside).getBlock()).isEmpty()) { FluidReactionQueue.watch(world, beside, 0, false); }
        }
    }

    private static boolean touched(IBlockState state) { return TOUCHED.computeIfAbsent(state, key -> touches(WATER, key) || touches(LAVA, key)); }

    private static boolean touches(List<FluidReactionDef> rules, IBlockState state) {
        for (FluidReactionDef rule : rules) {
            if (rule.touch() != null && rule.touch().matches(state)) { return true; }
        }
        return false;
    }

    @SubscribeEvent public static void onCreateSource(BlockEvent.CreateFluidSourceEvent event) {
        World world = event.getWorld();
        if (SOURCES.isEmpty() || event.getState().getBlock() != Blocks.FLOWING_WATER || world.isRemote) { return; }
        Biome biome = world.getBiome(event.getPos());
        int dimension = world.provider.getDimension();
        for (Place place : SOURCES) {
            if (place.matches(biome, dimension)) { return; }
        }
        event.setResult(Event.Result.DENY);
    }

    @Nullable private static JsonObject member(ResourceLocation key, JsonObject json, String name, boolean water, Set<String> allowed) {
        if (!json.has(name)) { return null; }
        if (!water) {
            ContentLog.LOGGER.error("Fluid rule {} has '{}' in a lava file, but only water takes {}, so it is skipped", key, name, name);
            return null;
        }
        JsonObject object = Json.object(json, name);
        for (Map.Entry<String, JsonElement> member : object.entrySet()) {
            if (!allowed.contains(member.getKey())) {
                ContentLog.LOGGER.error("Fluid rule {} has the unknown member '{}' in '{}', so the entry is ignored", key, member.getKey(), name);
                return null;
            }
        }
        return object;
    }

    private static void readSources(ResourceLocation key, JsonObject json, boolean water) {
        JsonObject infinite = member(key, json, INFINITE, water, INFINITE_MEMBERS);
        if (infinite == null) { return; }
        List<String> biomes = Json.names(infinite, BIOMES);
        List<String> dimensions = Json.names(infinite, DIMENSIONS);
        if (biomes.isEmpty() && dimensions.isEmpty()) {
            ContentLog.LOGGER.error("Fluid rule {} has an 'infinite' that names no biomes and no dimensions, so the entry is ignored", key);
            return;
        }
        SOURCES.add(new Place(biomes, dimensions));
    }

    private static int clamped(ResourceLocation key, JsonObject rain, String member, int fallback, int high) {
        int value = Json.number(rain, member, fallback);
        int fitted = MathHelper.clamp(value, 1, high);
        if (fitted != value) { ContentLog.LOGGER.error("Fluid rule {} sets '{}' to {} in 'rain', which is outside 1 to {}, so it is clamped to {}", key, member, value, high, fitted); }
        return fitted;
    }

    private static void readRain(ResourceLocation key, JsonObject json, boolean water) {
        JsonObject rain = member(key, json, RAIN_KEY, water, RAIN_MEMBERS);
        if (rain == null) { return; }
        int chance = clamped(key, rain, CHANCE, 160, Integer.MAX_VALUE);
        int maxLevel = clamped(key, rain, MAX_LEVEL, 4, 7);
        int minHeight = Json.number(rain, MIN_HEIGHT, Integer.MIN_VALUE);
        int maxHeight = Json.number(rain, MAX_HEIGHT, Integer.MAX_VALUE);
        boolean outsideClaims = rain.has(IN_CLAIMS) && !Json.flag(rain, IN_CLAIMS);
        RAIN.add(new Rain(new Place(Json.names(rain, BIOMES), Json.names(rain, DIMENSIONS)), chance, maxLevel, minHeight, maxHeight, Json.flag(rain, SEA_LEVEL), outsideClaims));
    }

    public static void rained(World world, BlockPos pos) {
        if (RAIN.isEmpty() || world.isRemote) { return; }
        IBlockState state = world.getBlockState(pos);
        if ((state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.FLOWING_WATER) || !world.isRainingAt(pos.up())) { return; }
        int level = state.getValue(BlockLiquid.LEVEL);
        Biome biome = world.getBiome(pos);
        int dimension = world.provider.getDimension();
        for (Rain rule : RAIN) {
            if (!rule.accepts(world, pos, level, biome, dimension)) { continue; }
            if (world.rand.nextInt(rule.chance) == 0) { world.setBlockState(pos, Blocks.WATER.getDefaultState(), BlockFlags.UPDATE_ALL); }
            return;
        }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", RULE_KEYS);
        String named = Json.text(json, FLUID, "");
        ResourceLocation fluid = IdNames.optional(named);
        List<FluidReactionDef> into = WATER_ID.equals(fluid) ? WATER : LAVA_ID.equals(fluid) ? LAVA : null;
        if (into == null) {
            ContentLog.LOGGER.error("Fluid rule {} names the fluid '{}', but only minecraft:water and minecraft:lava take rules this way, so the file is skipped", key, named);
            return;
        }
        readSources(key, json, into == WATER);
        readRain(key, json, into == WATER);
        List<FluidReactionDef> rules = ContentParserFluids.reactions(key, json, true);
        for (FluidReactionDef rule : rules) {
            if (!rule.sound.isEmpty() && !IdNames.VANILLA.equals(new ResourceLocation(rule.sound).getNamespace())) { VanillaClients.clientDrawn(key, "sound " + rule.sound); }
        }
        into.addAll(rules);
    }

    private static final class Place {
        private final List<String> biomes;
        private final List<String> dimensions;

        private Place(List<String> biomes, List<String> dimensions) {
            this.biomes = biomes;
            this.dimensions = dimensions;
        }

        private boolean matches(Biome biome, int dimension) { return BiomeNames.matches(biomes, biome) && (dimensions.isEmpty() || CardPlace.inAny(dimensions, dimension)); }
    }

    private static final class Rain {
        private final Place place;
        private final int chance;
        private final int maxLevel;
        private final int minHeight;
        private final int maxHeight;
        private final boolean seaLevel;
        private final boolean outsideClaims;

        private Rain(Place place, int chance, int maxLevel, int minHeight, int maxHeight, boolean seaLevel, boolean outsideClaims) {
            this.place = place;
            this.chance = chance;
            this.maxLevel = maxLevel;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.seaLevel = seaLevel;
            this.outsideClaims = outsideClaims;
        }

        private boolean accepts(World world, BlockPos pos, int level, Biome biome, int dimension) {
            if (level < 1 || level > maxLevel || pos.getY() < minHeight || pos.getY() > maxHeight || !place.matches(biome, dimension)) { return false; }
            if (seaLevel && pos.getY() > world.getSeaLevel()) { return false; }
            return !outsideClaims || ClaimGuard.covers(world, pos) == 0;
        }
    }
}
