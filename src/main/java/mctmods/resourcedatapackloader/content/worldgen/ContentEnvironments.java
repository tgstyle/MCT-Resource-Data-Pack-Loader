package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GrowSwitchDef;
import mctmods.resourcedatapackloader.content.def.MobSwitchDef;
import mctmods.resourcedatapackloader.content.def.RaidSwitchDef;
import mctmods.resourcedatapackloader.content.def.RuleSwitchDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.def.SpawnSwitchDef;
import mctmods.resourcedatapackloader.content.def.TradeSwitchDef;
import mctmods.resourcedatapackloader.network.MessageEnvironments;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
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

public final class ContentEnvironments {
    public static final String DEFAULT = "default";
    private static final String ENVIRONMENTS = "environments";
    private static final List<String> BLOCKS = List.of("sky", "ambience");
    private static final List<String> FIXED = List.of("hasSkyLight", "surfaceWorld", "respawn", "respawnDimension", "spawning", "nether", "beds", "waterVaporizes", "groundLevel", "movementFactor", "fixedTime", "ambientLight");
    private static final String BIOMES = "biomes";
    private static final String SPAWNING = "spawning";
    private static final String GROWTH = "growth";
    private static final String MOBS = "mobs";
    private static final String LOOT = "loot";
    private static final String TRADES = "trades";
    private static final String RULES = "rules";
    private static final String RAIDS = "raids";
    private static final List<String> OWN_BLOCKS = List.of(BIOMES, SPAWNING, GROWTH, MOBS, LOOT, TRADES, RULES, RAIDS);
    private static final Map<Identifier, Map<String, DimensionDef>> SETS = new HashMap<>();
    private static final Map<DimensionDef, List<String>> PLACES = new IdentityHashMap<>();
    private static final Map<DimensionDef, SpawnSwitchDef> SPAWNS = new IdentityHashMap<>();
    private static final Map<DimensionDef, GrowSwitchDef> GROWS = new IdentityHashMap<>();
    private static final Map<DimensionDef, MobSwitchDef> MOBLINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, Map<Identifier, Identifier>> LOOTS = new IdentityHashMap<>();
    private static final Map<DimensionDef, TradeSwitchDef> TRADELINES = new IdentityHashMap<>();
    private static final Map<DimensionDef, RuleSwitchDef> RULESETS = new IdentityHashMap<>();
    private static final Map<DimensionDef, RaidSwitchDef> RAIDLINES = new IdentityHashMap<>();
    private static final Map<Identifier, List<String>> ACTIVE = new ConcurrentHashMap<>();
    @Nullable private static volatile Holder<Biome> viewed;

    private ContentEnvironments() {}

    static void parse(Identifier key, String contents) {
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
            JsonElement growth = entry.getValue().getAsJsonObject().get(GROWTH);
            GrowSwitchDef grown = growth == null ? null : ContentGrowSwitch.parse(owner, growth);
            if (grown != null) { GROWS.put(set, grown); }
            JsonElement mobs = entry.getValue().getAsJsonObject().get(MOBS);
            MobSwitchDef moved = mobs == null ? null : ContentMobSwitch.parse(owner, name, mobs);
            if (moved != null) { MOBLINES.put(set, moved); }
            JsonElement loot = entry.getValue().getAsJsonObject().get(LOOT);
            Map<Identifier, Identifier> swapped = loot == null ? null : ContentLootSwitch.parse(owner, loot);
            if (swapped != null) { LOOTS.put(set, swapped); }
            JsonElement trades = entry.getValue().getAsJsonObject().get(TRADES);
            TradeSwitchDef traded = trades == null ? null : ContentTradeSwitch.parse(key, owner, key + "/" + name, trades);
            if (traded != null) { TRADELINES.put(set, traded); }
            JsonElement rules = entry.getValue().getAsJsonObject().get(RULES);
            RuleSwitchDef ruled = rules == null ? null : ContentRuleSwitch.parse(owner, rules, !biomes.isEmpty(), set.traits().precipitation());
            if (ruled != null) { RULESETS.put(set, ruled); }
            JsonElement raids = entry.getValue().getAsJsonObject().get(RAIDS);
            RaidSwitchDef raided = raids == null ? null : ContentRaidSwitch.parse(owner, raids);
            if (raided != null) { RAIDLINES.put(set, raided); }
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

    @Nullable static DimensionDef resolve(Identifier dimension, @Nullable DimensionDef base) { return resolve(dimension, base, viewed != null && EffectiveSide.get().isClient() ? viewed : null); }

    @Nullable static DimensionDef resolve(Identifier dimension, @Nullable DimensionDef base, @Nullable Holder<Biome> biome) {
        List<String> active = base == null ? null : ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        if (sets == null) { return base; }
        if (biome != null && active.size() > 1) {
            for (int index = active.size() - 1; index > 0; index--) {
                DimensionDef set = sets.get(active.get(index));
                if (set != null && PLACES.containsKey(set) && BiomeNames.matches(PLACES.get(set), biome)) { return set; }
            }
        }
        DimensionDef set = sets.get(active.getFirst());
        return set == null ? base : set;
    }

    private static <T> T chosen(ServerLevel level, BlockPos pos, Map<DimensionDef, T> blocks, T none) {
        Identifier dimension = level.dimension().identifier();
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
        DimensionDef whole = whole(dimension);
        return whole == null ? none : blocks.getOrDefault(whole, none);
    }

    @Nullable private static DimensionDef whole(Identifier dimension) {
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        return sets == null ? null : sets.get(active.getFirst());
    }

    static RuleSwitchDef wide(Identifier dimension) {
        DimensionDef whole = whole(dimension);
        return whole == null ? RuleSwitchDef.NONE : RULESETS.getOrDefault(whole, RuleSwitchDef.NONE);
    }

    static SpawnSwitchDef spawning(ServerLevel level, BlockPos pos) { return chosen(level, pos, SPAWNS, SpawnSwitchDef.NONE); }

    static GrowSwitchDef growth(ServerLevel level, BlockPos pos) { return chosen(level, pos, GROWS, GrowSwitchDef.NONE); }

    static MobSwitchDef mobs(ServerLevel level, BlockPos pos) { return chosen(level, pos, MOBLINES, MobSwitchDef.NONE); }

    static Map<Identifier, Identifier> loot(ServerLevel level, BlockPos pos) { return chosen(level, pos, LOOTS, Map.of()); }

    static TradeSwitchDef trades(ServerLevel level, BlockPos pos) { return chosen(level, pos, TRADELINES, TradeSwitchDef.NONE); }

    static RuleSwitchDef rules(ServerLevel level, BlockPos pos) { return chosen(level, pos, RULESETS, RuleSwitchDef.NONE); }

    static RaidSwitchDef raids(ServerLevel level, BlockPos pos) { return chosen(level, pos, RAIDLINES, RaidSwitchDef.NONE); }

    static List<SpawnSwitchDef> spawnings(Identifier dimension) {
        List<String> active = ACTIVE.get(dimension);
        Map<String, DimensionDef> sets = active == null ? null : SETS.get(dimension);
        if (sets == null) { return List.of(); }
        List<SpawnSwitchDef> found = new ArrayList<>();
        for (String name : active) {
            DimensionDef set = sets.get(name);
            found.add(set == null ? SpawnSwitchDef.NONE : SPAWNS.getOrDefault(set, SpawnSwitchDef.NONE));
        }
        return found;
    }

    static Collection<DimensionDef> sets(Identifier dimension) { return SETS.getOrDefault(dimension, Map.of()).values(); }

    public static Set<Identifier> dimensions() { return Collections.unmodifiableSet(SETS.keySet()); }

    public static Set<String> names(Identifier dimension) { return SETS.getOrDefault(dimension, Map.of()).keySet(); }

    static boolean local(Identifier dimension, String name) {
        DimensionDef set = SETS.getOrDefault(dimension, Map.of()).get(name);
        return set != null && PLACES.containsKey(set);
    }

    public static List<String> active(Identifier dimension) { return ACTIVE.getOrDefault(dimension, List.of(DEFAULT)); }

    public static void set(MinecraftServer server, Identifier dimension, String name, boolean on) {
        List<String> before = active(dimension);
        change(server, dimension, name, on);
        announce(server, dimension, before);
    }

    static void announce(MinecraftServer server, Identifier dimension, List<String> before) {
        List<String> entered = new ArrayList<>(active(dimension));
        entered.removeAll(before);
        if (!entered.isEmpty()) { CardEvents.environmentChanged(server, dimension, entered); }
    }

    static void change(MinecraftServer server, Identifier dimension, String name, boolean on) {
        List<String> now = new ArrayList<>(active(dimension));
        if (local(dimension, name)) {
            now.remove(name);
            if (on) { now.add(name); }
        }
        else if (on) { now.set(0, name); }
        else if (DEFAULT.equals(name) || now.getFirst().equals(name)) { now.set(0, DEFAULT); }
        ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
        if (level != null) { ContentTradeSwitch.closeTrades(level); }
        put(dimension, now);
        EnvironmentStorage.of(server).keep(ACTIVE);
        if (level != null) {
            ContentMobSwitch.settleAll(level);
            ContentRuleSwitch.settle(level);
        }
        MessageEnvironments message = new MessageEnvironments(Map.of(dimension, active(dimension)), false);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { RDPLNetwork.sendEnvironments(player, message); }
    }

    private static void put(Identifier dimension, List<String> names) {
        if (names.isEmpty() || names.size() == 1 && DEFAULT.equals(names.getFirst())) { ACTIVE.remove(dimension); }
        else { ACTIVE.put(dimension, List.copyOf(names)); }
    }

    public static void onServerStarted(ServerStartedEvent event) {
        ACTIVE.clear();
        ContentSeasons.forget();
        ContentRaidSwitch.check(RAIDLINES.values());
        EnvironmentStorage storage = EnvironmentStorage.of(event.getServer());
        for (Map.Entry<Identifier, List<String>> entry : storage.kept().entrySet()) {
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
        ContentSpawnSwitch.checkWeights(SPAWNS.values());
        ContentMobSwitch.start(event.getServer(), MOBLINES.values());
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
        ContentSpawnSwitch.forget();
        ContentGrowSwitch.forget();
        ContentMobSwitch.forget();
        ACTIVE.clear();
        viewed = null;
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

    public static List<Integer> tints(Identifier dimension) {
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

    public static void receive(Map<Identifier, List<String>> active, boolean whole) {
        if (whole) { ACTIVE.clear(); }
        for (Map.Entry<Identifier, List<String>> entry : active.entrySet()) { put(entry.getKey(), entry.getValue()); }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { RDPLNetwork.sendEnvironments(player, new MessageEnvironments(new LinkedHashMap<>(ACTIVE), true)); }
    }
}
