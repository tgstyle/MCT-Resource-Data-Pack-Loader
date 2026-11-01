package mctmods.resourcedatapackloader.common.dimension;

import mctmods.resourcedatapackloader.common.cards.CardEvents;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.MobSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RuleSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.TradeSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentDimensionParser;
import mctmods.resourcedatapackloader.common.rules.ContentGrowSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentLootSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentMobSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentRaidSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentSpawnSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentTradeSwitch;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.network.EnvironmentsMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public final class ContentEnvironments {
    public static final String DEFAULT = "default";
    private static final String ENVIRONMENTS = "environments";
    private static final List<String> BLOCKS = List.of("sky", "ambience");
    public static final List<String> FIXED = List.of("hasSkyLight", "surfaceWorld", "respawn", "respawnDimension", "spawning", "nether", "beds", "waterVaporizes", "groundLevel", "movementFactor", "fixedTime", "ambientLight");
    private static final String BIOMES = "biomes";
    private static final String SPAWNING = "spawning";
    private static final String GROWTH = "growth";
    private static final String MOBS = "mobs";
    private static final String LOOT = "loot";
    private static final String TRADES = "trades";
    private static final String RULES = "rules";
    private static final String RAIDS = "raids";
    private static final List<String> OWN_BLOCKS = List.of(BIOMES, SPAWNING, GROWTH, MOBS, LOOT, TRADES, RULES, RAIDS);
    private static final Map<ResourceLocation, Map<String, DimensionDef>> SETS = new HashMap<>();
    private static final Map<DimensionDef, List<String>> PLACES = new IdentityHashMap<>();
    private static final Map<DimensionDef, SpawnSwitchDef> SPAWNS = new IdentityHashMap<>();
    private static final Map<DimensionDef, GrowSwitchDef> GROWS = new IdentityHashMap<>();
    private static final Map<DimensionDef, MobSwitchDef> MOBLINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, RaidSwitchDef> RAIDLINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, Map<ResourceLocation, ResourceLocation>> LOOTS = new IdentityHashMap<>();
    private static final Map<DimensionDef, TradeSwitchDef> TRADELINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, RuleSwitchDef> RULESETS = new IdentityHashMap<>();
    private static final Map<ResourceLocation, List<String>> ACTIVE = new ConcurrentHashMap<>();
    @Nullable private static volatile Holder<Biome> viewed;

    private ContentEnvironments() {}

    static void parse(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "dimension");
        if (json == null) { return; }
        if (!json.has(ENVIRONMENTS)) {
            ContentSeasons.parse(key, json);
            return;
        }
        if (!json.get(ENVIRONMENTS).isJsonObject()) {
            ContentLog.LOGGER.error("Dimension {} gives environments that are not an object of named environments, ignoring them", key);
            ContentSeasons.parse(key, json);
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
            DimensionDef set = ContentDimensionParser.parse(key, layered);
            sets.put(name, set);
            List<String> biomes = Json.strings(entry.getValue().getAsJsonObject(), BIOMES);
            if (!biomes.isEmpty()) { PLACES.put(set, biomes); }
            JsonElement spawning = entry.getValue().getAsJsonObject().get(SPAWNING);
            SpawnSwitchDef switched = spawning == null ? null : ContentSpawnSwitch.parse(owner, spawning);
            if (switched != null) { SPAWNS.put(set, switched); }
            GrowSwitchDef grown = ContentGrowSwitch.parse(owner, entry.getValue().getAsJsonObject().get(GROWTH));
            if (grown != null) { GROWS.put(set, grown); }
            MobSwitchDef mobs = ContentMobSwitch.parse(owner, name, entry.getValue().getAsJsonObject().get(MOBS));
            if (mobs != null) { MOBLINES.put(set, mobs); }
            RaidSwitchDef raided = ContentRaidSwitch.parse(owner, entry.getValue().getAsJsonObject().get(RAIDS));
            if (raided != null) { RAIDLINES.put(set, raided); }
            Map<ResourceLocation, ResourceLocation> swaps = ContentLootSwitch.parse(owner, entry.getValue().getAsJsonObject().get(LOOT));
            if (swaps != null) { LOOTS.put(set, swaps); }
            TradeSwitchDef offered = ContentTradeSwitch.parse(key, owner, key + "/" + name, entry.getValue().getAsJsonObject().get(TRADES));
            if (offered != null) { TRADELINES.put(set, offered); }
            RuleSwitchDef ruled = ContentRuleSwitch.parse(owner, entry.getValue().getAsJsonObject().get(RULES), !biomes.isEmpty(), set.traits().precipitation());
            if (ruled != null) { RULESETS.put(set, ruled); }
        }
        if (!sets.isEmpty()) { SETS.put(key, Collections.unmodifiableMap(sets)); }
        ContentSeasons.parse(key, json);
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
                else {
                    put(held, entry.getKey(), entry.getValue());
                    VanillaClients.clientDrawn(owner, block.getKey() + "." + entry.getKey());
                }
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

    @Nullable static DimensionDef resolve(ResourceLocation dimension, @Nullable DimensionDef base) { return resolve(dimension, base, viewed != null && EffectiveSide.get().isClient() ? viewed : null); }

    @Nullable static DimensionDef resolve(ResourceLocation dimension, @Nullable DimensionDef base, @Nullable Holder<Biome> biome) {
        List<String> active = base == null ? null : ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        if (sets == null) { return base; }
        if (biome != null && active.size() > 1) {
            for (int index = active.size() - 1; index > 0; index--) {
                DimensionDef set = sets.get(active.get(index));
                if (set != null && PLACES.containsKey(set) && BiomeNames.matches(PLACES.get(set), biome)) { return set; }
            }
        }
        DimensionDef set = sets.get(active.get(0));
        return set == null ? base : set;
    }

    private static <T> T chosen(ServerLevel level, BlockPos pos, Map<DimensionDef, T> blocks, T none) {
        ResourceLocation dimension = level.dimension().location();
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        if (sets == null) { return none; }
        if (active.size() > 1) {
            Holder<Biome> biome = level.getBiome(pos);
            for (int index = active.size() - 1; index > 0; index--) {
                DimensionDef set = sets.get(active.get(index));
                if (set != null && PLACES.containsKey(set) && BiomeNames.matches(PLACES.get(set), biome)) { return blocks.getOrDefault(set, none); }
            }
        }
        DimensionDef set = whole(dimension);
        T held = set == null ? null : blocks.get(set);
        return held == null ? none : held;
    }

    @Nullable private static DimensionDef whole(ResourceLocation dimension) {
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        return sets == null ? null : sets.get(active.get(0));
    }

    public static RuleSwitchDef wide(ResourceLocation dimension) {
        DimensionDef set = whole(dimension);
        RuleSwitchDef held = set == null ? null : RULESETS.get(set);
        return held == null ? RuleSwitchDef.NONE : held;
    }

    public static RuleSwitchDef rules(ServerLevel level, BlockPos pos) { return chosen(level, pos, RULESETS, RuleSwitchDef.NONE); }

    public static SpawnSwitchDef spawning(ServerLevel level, BlockPos pos) { return chosen(level, pos, SPAWNS, SpawnSwitchDef.NONE); }

    public static GrowSwitchDef growth(ServerLevel level, BlockPos pos) { return chosen(level, pos, GROWS, GrowSwitchDef.NONE); }

    public static MobSwitchDef mobs(ServerLevel level, BlockPos pos) { return chosen(level, pos, MOBLINES, MobSwitchDef.NONE); }

    public static RaidSwitchDef raids(ServerLevel level, BlockPos pos) { return chosen(level, pos, RAIDLINES, RaidSwitchDef.NONE); }

    public static Map<ResourceLocation, ResourceLocation> loot(ServerLevel level, BlockPos pos) { return chosen(level, pos, LOOTS, Map.of()); }

    public static TradeSwitchDef trades(ServerLevel level, BlockPos pos) { return chosen(level, pos, TRADELINES, TradeSwitchDef.NONE); }

    static Collection<MobSwitchDef> mobDefs() { return MOBLINES.values(); }

    public static List<SpawnSwitchDef> spawnings(ResourceLocation dimension) {
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        if (sets == null) { return List.of(); }
        List<SpawnSwitchDef> regions = new ArrayList<>();
        for (String name : active) { regions.add(SPAWNS.getOrDefault(sets.get(name), SpawnSwitchDef.NONE)); }
        return regions;
    }

    public static Collection<SpawnSwitchDef> spawnDefs() { return SPAWNS.values(); }

    public static Set<ResourceLocation> dimensions() { return Collections.unmodifiableSet(SETS.keySet()); }

    public static Set<String> names(ResourceLocation dimension) { return SETS.getOrDefault(dimension, Map.of()).keySet(); }

    public static boolean local(ResourceLocation dimension, String name) {
        DimensionDef set = SETS.getOrDefault(dimension, Map.of()).get(name);
        return set != null && PLACES.containsKey(set);
    }

    public static boolean hasEffects(ResourceLocation dimension, DimensionDef base) {
        if (base.hasEffects()) { return true; }
        for (DimensionDef set : SETS.getOrDefault(dimension, Map.of()).values()) { if (set.hasEffects()) { return true; } }
        return false;
    }

    public static List<String> active(ResourceLocation dimension) { return ACTIVE.getOrDefault(dimension, List.of(DEFAULT)); }

    public static void set(MinecraftServer server, ResourceLocation dimension, String name, boolean on) {
        List<String> before = active(dimension);
        change(server, dimension, name, on);
        announce(server, dimension, before);
    }

    public static void announce(MinecraftServer server, ResourceLocation dimension, List<String> before) {
        List<String> entered = new ArrayList<>(active(dimension));
        entered.removeAll(before);
        if (!entered.isEmpty()) { CardEvents.environmentChanged(server, dimension, entered); }
    }

    public static void change(MinecraftServer server, ResourceLocation dimension, String name, boolean on) {
        List<String> now = new ArrayList<>(active(dimension));
        if (local(dimension, name)) {
            now.remove(name);
            if (on) { now.add(name); }
        }
        else if (on) { now.set(0, name); }
        else if (DEFAULT.equals(name) || now.get(0).equals(name)) { now.set(0, DEFAULT); }
        ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
        if (level != null) { ContentTradeSwitch.closeTrades(level); }
        put(dimension, now);
        EnvironmentStorage.of(server).keep(ACTIVE);
        if (level != null) {
            ContentMobSwitch.settleAll(level);
            ContentRuleSwitch.settle(level);
        }
        EnvironmentsMessage message = new EnvironmentsMessage(Map.of(dimension, active(dimension)), false);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { RDPLNetwork.sendEnvironments(player, message); }
    }

    private static void put(ResourceLocation dimension, List<String> names) {
        if (names.isEmpty() || names.size() == 1 && DEFAULT.equals(names.get(0))) { ACTIVE.remove(dimension); }
        else { ACTIVE.put(dimension, List.copyOf(names)); }
    }

    public static void onServerStarted(ServerStartedEvent event) {
        ACTIVE.clear();
        ContentSeasons.forget();
        ContentRaidSwitch.check(RAIDLINES.values());
        EnvironmentStorage storage = EnvironmentStorage.of(event.getServer());
        for (Map.Entry<ResourceLocation, List<String>> entry : storage.kept().entrySet()) {
            List<String> kept = new ArrayList<>();
            for (String name : entry.getValue()) {
                if (DEFAULT.equals(name) || names(entry.getKey()).contains(name)) { kept.add(name); }
                else {
                    ContentLog.LOGGER.error("Dimension {} was saved with the environment '{}', which no dimension file names any more, so it is dropped", entry.getKey(), name);
                    if (kept.isEmpty()) { kept.add(DEFAULT); }
                }
            }
            put(entry.getKey(), kept);
        }
        storage.keep(ACTIVE);
        ContentMobSwitch.start(event.getServer(), mobDefs());
        ContentLootSwitch.start(event.getServer(), LOOTS.values());
        ContentLootSwitch.startMaps(event.getServer(), ContentStructureMaps.defs());
        ContentTradeSwitch.start(TRADELINES.values());
        for (ServerLevel level : event.getServer().getAllLevels()) { ContentRuleSwitch.settle(level); }
    }

    public static void stopped() {
        clear();
        ContentSeasons.forget();
    }

    public static void clear() {
        ACTIVE.clear();
        viewed = null;
        ContentSpawnSwitch.forget();
        ContentGrowSwitch.forget();
        ContentMobSwitch.forget();
    }

    public static void view(Holder<Biome> biome) { viewed = biome; }

    public static List<SkyLookDef> looks() {
        List<SkyLookDef> found = new ArrayList<>();
        for (Map<String, DimensionDef> sets : SETS.values()) {
            for (DimensionDef set : sets.values()) {
                if (set.look() != null) { found.add(set.look()); }
            }
        }
        return found;
    }

    public static List<Integer> tints(ResourceLocation dimension) {
        Map<String, DimensionDef> sets = SETS.getOrDefault(dimension, Map.of());
        List<Integer> found = new ArrayList<>();
        for (String name : active(dimension)) {
            DimensionDef set = sets.get(name);
            SkyLookDef look = set == null ? null : set.look();
            if (look == null) { continue; }
            found.add(look.grassTint());
            found.add(look.foliageTint());
            found.add(look.snowColor());
        }
        return found;
    }

    public static void receive(Map<ResourceLocation, List<String>> active, boolean whole) {
        if (whole) { ACTIVE.clear(); }
        for (Map.Entry<ResourceLocation, List<String>> entry : active.entrySet()) { put(entry.getKey(), entry.getValue()); }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { RDPLNetwork.sendEnvironments(player, new EnvironmentsMessage(new LinkedHashMap<>(ACTIVE), true)); }
    }
}
