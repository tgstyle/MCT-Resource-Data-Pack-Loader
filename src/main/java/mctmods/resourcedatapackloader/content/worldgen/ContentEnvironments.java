package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParserWorlds;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GrowSwitchDef;
import mctmods.resourcedatapackloader.content.def.MobSwitchDef;
import mctmods.resourcedatapackloader.content.def.RaidSwitchDef;
import mctmods.resourcedatapackloader.content.def.RuleSwitchDef;
import mctmods.resourcedatapackloader.content.def.SpawnSwitchDef;
import mctmods.resourcedatapackloader.content.def.TradeSwitchDef;
import mctmods.resourcedatapackloader.network.MessageEnvironments;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.world.BiomeNames;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentEnvironments {
    public static final String DEFAULT = "default";
    private static final String ENVIRONMENTS = "environments";
    private static final String BIOMES = "biomes";
    private static final String SPAWNING = "spawning";
    private static final String GROWTH = "growth";
    private static final String MOBS = "mobs";
    private static final String LOOT = "loot";
    private static final String TRADES = "trades";
    private static final String RULES = "rules";
    private static final String RAIDS = "raids";
    private static final List<String> OWN_BLOCKS = Arrays.asList(BIOMES, SPAWNING, GROWTH, MOBS, LOOT, TRADES, RULES, RAIDS);
    private static final List<String> BLOCKS = Arrays.asList("sky", "ambience");
    private static final List<String> FIXED = Arrays.asList("hasSkyLight", "surfaceWorld", "respawn", "respawnDimension", "spawning", "nether", "beds", "waterVaporizes", "groundLevel", "movementFactor", "fixedTime", "ambientLight");
    private static final Map<DimensionDef, Map<String, DimensionDef>> SETS = new IdentityHashMap<>();
    private static final Map<DimensionDef, DimensionDef> BASES = new IdentityHashMap<>();
    private static final Map<DimensionDef, List<String>> PLACES = new IdentityHashMap<>();
    private static final Map<DimensionDef, SpawnSwitchDef> SPAWNS = new IdentityHashMap<>();
    private static final Map<DimensionDef, GrowSwitchDef> GROWS = new IdentityHashMap<>();
    private static final Map<DimensionDef, MobSwitchDef> MOBLINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, Map<ResourceLocation, ResourceLocation>> LOOTS = new IdentityHashMap<>();
    private static final Map<DimensionDef, TradeSwitchDef> TRADELINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, RuleSwitchDef> RULESETS = new IdentityHashMap<>();
    private static final Map<DimensionDef, RaidSwitchDef> RAIDLINES = new IdentityHashMap<>();
    private static final Map<Integer, List<String>> ACTIVE = new ConcurrentHashMap<>();
    @Nullable private static Biome viewed;
    @Nullable private static DimensionDef shown;

    private ContentEnvironments() {}

    static void parse(ResourceLocation key, String contents, DimensionDef base) {
        JsonObject json = Json.object(key, contents, "dimension");
        if (json == null || !json.has(ENVIRONMENTS)) { return; }
        if (!json.get(ENVIRONMENTS).isJsonObject()) {
            ContentLog.LOGGER.error("Dimension {} gives environments that are not an object of named environments, ignoring them", key);
            return;
        }
        Map<String, DimensionDef> sets = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject(ENVIRONMENTS).entrySet()) {
            String name = Settings.lower(entry.getKey());
            String owner = "Dimension " + key + " environment '" + entry.getKey() + "'";
            if (name.isEmpty() || DEFAULT.equals(name) || name.contains(" ")) {
                ContentLog.LOGGER.error("{} needs a name of one word other than '{}', ignoring it", owner, DEFAULT);
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object of sky, ambience, spawning, growth, mobs, loot, trades, rules and raids keys, ignoring it", owner);
                continue;
            }
            JsonObject layered = Json.object(key, contents, "dimension");
            if (layered == null) { continue; }
            layered.remove(ENVIRONMENTS);
            layer(owner, layered, entry.getValue().getAsJsonObject());
            DimensionDef set = ContentParserWorlds.dimension(key, layered);
            sets.put(name, set);
            BASES.put(set, base);
            List<String> biomes = Json.strings(entry.getValue().getAsJsonObject(), BIOMES);
            if (!biomes.isEmpty()) { PLACES.put(set, biomes); }
            SpawnSwitchDef spawning = ContentSpawnSwitch.parse(owner, entry.getValue().getAsJsonObject().get(SPAWNING));
            if (spawning != null) { SPAWNS.put(set, spawning); }
            GrowSwitchDef growth = ContentGrowSwitch.parse(owner, entry.getValue().getAsJsonObject().get(GROWTH));
            if (growth != null) { GROWS.put(set, growth); }
            MobSwitchDef mobs = ContentMobSwitch.parse(owner, name, entry.getValue().getAsJsonObject().get(MOBS));
            if (mobs != null) { MOBLINES.put(set, mobs); }
            Map<ResourceLocation, ResourceLocation> loot = ContentLootSwitch.parse(owner, entry.getValue().getAsJsonObject().get(LOOT));
            if (loot != null) { LOOTS.put(set, loot); }
            TradeSwitchDef trades = ContentTradeSwitch.parse(key, owner, base.id + "/" + name, entry.getValue().getAsJsonObject().get(TRADES));
            if (trades != null) { TRADELINES.put(set, trades); }
            RuleSwitchDef rules = ContentRuleSwitch.parse(owner, entry.getValue().getAsJsonObject().get(RULES), !biomes.isEmpty(), set.traits.precipitation);
            if (rules != null) { RULESETS.put(set, rules); }
            RaidSwitchDef raided = ContentRaidSwitch.parse(owner, entry.getValue().getAsJsonObject().get(RAIDS));
            if (raided != null) { RAIDLINES.put(set, raided); }
        }
        if (!sets.isEmpty()) { SETS.put(base, Collections.unmodifiableMap(sets)); }
    }

    private static void layer(String owner, JsonObject into, JsonObject environment) {
        for (Map.Entry<String, JsonElement> block : environment.entrySet()) {
            if (OWN_BLOCKS.contains(block.getKey())) { continue; }
            if (!BLOCKS.contains(block.getKey()) || !block.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("{} gives '{}', which an environment cannot change, ignoring it", owner, block.getKey());
                continue;
            }
            if (!into.has(block.getKey()) || !into.get(block.getKey()).isJsonObject()) { into.add(block.getKey(), new JsonObject()); }
            JsonObject held = into.getAsJsonObject(block.getKey());
            for (Map.Entry<String, JsonElement> entry : block.getValue().getAsJsonObject().entrySet()) {
                if ("sky".equals(block.getKey()) && FIXED.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives sky.{}, which is set once when the world loads, ignoring it", owner, entry.getKey()); }
                else { put(held, entry.getKey(), entry.getValue()); }
            }
        }
    }

    private static void put(JsonObject into, String key, JsonElement value) {
        JsonElement held = into.get(key);
        if (held == null || !held.isJsonObject() || !value.isJsonObject()) {
            into.add(key, value);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) { put(held.getAsJsonObject(), entry.getKey(), entry.getValue()); }
    }

    static DimensionDef resolve(DimensionDef base) {
        Map<String, DimensionDef> sets = SETS.get(base);
        List<String> active = sets == null ? null : ACTIVE.get(base.id);
        if (active == null) { return base; }
        Biome biome = viewed;
        if (biome != null && active.size() > 1 && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            for (int index = active.size() - 1; index > 0; index--) {
                DimensionDef set = sets.get(active.get(index));
                if (set != null && PLACES.containsKey(set) && BiomeNames.matches(PLACES.get(set), biome)) { return set; }
            }
        }
        DimensionDef set = sets.get(active.get(0));
        return set == null ? base : set;
    }

    @Nullable private static Map<String, DimensionDef> setsOf(int dimension) {
        DimensionDef def = ContentDimensions.byId(dimension);
        return def == null ? null : SETS.get(BASES.getOrDefault(def, def));
    }

    private static <T> T chosen(World world, BlockPos pos, Map<DimensionDef, T> blocks, T none) {
        int dimension = world.provider.getDimension();
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : setsOf(dimension);
        if (sets == null) { return none; }
        if (active.size() > 1) {
            Biome biome = world.getBiome(pos);
            for (int index = active.size() - 1; index > 0; index--) {
                DimensionDef set = sets.get(active.get(index));
                if (set != null && PLACES.containsKey(set) && BiomeNames.matches(PLACES.get(set), biome)) { return blocks.getOrDefault(set, none); }
            }
        }
        DimensionDef set = whole(dimension);
        return set == null ? none : blocks.getOrDefault(set, none);
    }

    @Nullable private static DimensionDef whole(int dimension) {
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : setsOf(dimension);
        return sets == null ? null : sets.get(active.get(0));
    }

    static RuleSwitchDef wide(int dimension) {
        DimensionDef set = whole(dimension);
        return set == null ? RuleSwitchDef.NONE : RULESETS.getOrDefault(set, RuleSwitchDef.NONE);
    }

    static RuleSwitchDef rules(World world, BlockPos pos) { return chosen(world, pos, RULESETS, RuleSwitchDef.NONE); }

    static SpawnSwitchDef spawning(World world, BlockPos pos) { return chosen(world, pos, SPAWNS, SpawnSwitchDef.NONE); }

    static GrowSwitchDef growth(World world, BlockPos pos) { return chosen(world, pos, GROWS, GrowSwitchDef.NONE); }

    static RaidSwitchDef raids(World world, BlockPos pos) { return chosen(world, pos, RAIDLINES, RaidSwitchDef.NONE); }

    static MobSwitchDef mobs(World world, BlockPos pos) { return chosen(world, pos, MOBLINES, MobSwitchDef.NONE); }

    static Collection<MobSwitchDef> allMobs() { return MOBLINES.values(); }

    static Map<ResourceLocation, ResourceLocation> loot(World world, BlockPos pos) { return chosen(world, pos, LOOTS, Collections.emptyMap()); }

    static TradeSwitchDef trades(World world, BlockPos pos) { return chosen(world, pos, TRADELINES, TradeSwitchDef.NONE); }

    static Collection<Map<ResourceLocation, ResourceLocation>> allLoot() { return LOOTS.values(); }

    static Collection<TradeSwitchDef> allTrades() { return TRADELINES.values(); }

    static List<SpawnSwitchDef> spawnings(int dimension) {
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : setsOf(dimension);
        if (sets == null) { return Collections.emptyList(); }
        List<SpawnSwitchDef> found = new ArrayList<>(active.size());
        for (String name : active) {
            DimensionDef set = sets.get(name);
            found.add(set == null ? SpawnSwitchDef.NONE : SPAWNS.getOrDefault(set, SpawnSwitchDef.NONE));
        }
        return found;
    }

    static Collection<SpawnSwitchDef> allSpawning() { return SPAWNS.values(); }

    public static Set<String> names(@Nullable DimensionDef def) {
        Map<String, DimensionDef> sets = def == null ? null : SETS.get(BASES.getOrDefault(def, def));
        return sets == null ? Collections.emptySet() : sets.keySet();
    }

    public static boolean local(@Nullable DimensionDef def, String name) {
        Map<String, DimensionDef> sets = def == null ? null : SETS.get(BASES.getOrDefault(def, def));
        DimensionDef set = sets == null ? null : sets.get(name);
        return set != null && PLACES.containsKey(set);
    }

    public static List<String> active(int dimension) { return ACTIVE.getOrDefault(dimension, Collections.singletonList(DEFAULT)); }

    public static void set(MinecraftServer server, int dimension, String name, boolean on) {
        List<String> now = new ArrayList<>(active(dimension));
        if (local(ContentDimensions.byId(dimension), name)) {
            now.remove(name);
            if (on) { now.add(name); }
        }
        else if (on) { now.set(0, name); }
        else if (DEFAULT.equals(name) || now.get(0).equals(name)) { now.set(0, DEFAULT); }
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world != null) { ContentTradeSwitch.closeTrades(world); }
        activate(dimension, now);
        EnvironmentStorage storage = EnvironmentStorage.get(server);
        if (storage != null) { storage.keep(ACTIVE); }
        if (world != null) {
            refresh(world);
            ContentMobSwitch.settleAll(world);
        }
        MessageEnvironments message = new MessageEnvironments(Collections.singletonMap(dimension, active(dimension)), false);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { RDPLNetwork.sendTo(message, player); }
    }

    private static void activate(int dimension, List<String> names) {
        if (names.size() == 1 && DEFAULT.equals(names.get(0))) { ACTIVE.remove(dimension); }
        else { ACTIVE.put(dimension, Collections.unmodifiableList(new ArrayList<>(names))); }
    }

    public static void serverStarted(MinecraftServer server) {
        ACTIVE.clear();
        ContentRaidSwitch.check(RAIDLINES.values());
        EnvironmentStorage storage = EnvironmentStorage.get(server);
        if (storage == null) { return; }
        for (Map.Entry<Integer, List<String>> entry : storage.kept().entrySet()) {
            Set<String> known = names(ContentDimensions.byId(entry.getKey()));
            List<String> kept = new ArrayList<>();
            for (String name : entry.getValue()) {
                if (DEFAULT.equals(name) || known.contains(name)) { kept.add(name); }
                else {
                    ContentLog.LOGGER.error("Dimension {} was saved with the environment '{}', which no dimension file names any more, so it is dropped", entry.getKey(), name);
                    if (kept.isEmpty()) { kept.add(DEFAULT); }
                }
            }
            if (!kept.isEmpty()) { activate(entry.getKey(), kept); }
        }
        storage.keep(ACTIVE);
        for (WorldServer world : server.worlds) { refresh(world); }
    }

    public static void serverStopped() { ACTIVE.clear(); }

    public static void receive(Map<Integer, List<String>> active, boolean whole) {
        if (whole) { ACTIVE.clear(); }
        for (Map.Entry<Integer, List<String>> entry : active.entrySet()) {
            if (entry.getValue().isEmpty()) { ACTIVE.remove(entry.getKey()); }
            else { activate(entry.getKey(), entry.getValue()); }
        }
        shown = null;
    }

    public static void view(World world, Biome biome) {
        viewed = biome;
        List<String> active = ACTIVE.get(world.provider.getDimension());
        if (active == null || active.size() < 2) { return; }
        DimensionDef now = world.provider instanceof ContentWorldProvider ? ContentDimensions.of(world) : ContentDimensions.overlay(world);
        if (now == shown) { return; }
        shown = now;
        refresh(world);
    }

    public static void refresh(World world) {
        if (world.provider instanceof ContentWorldProvider) {
            ((ContentWorldProvider) world.provider).refresh();
            return;
        }
        DimensionDef def = ContentDimensions.overlay(world);
        if (def != null) { ContentDimensionRules.renderers(world, world.provider, def); }
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) { RDPLNetwork.sendTo(new MessageEnvironments(new LinkedHashMap<>(ACTIVE), true), (EntityPlayerMP) event.player); }
    }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ACTIVE.clear();
        viewed = null;
        shown = null;
    }
}
