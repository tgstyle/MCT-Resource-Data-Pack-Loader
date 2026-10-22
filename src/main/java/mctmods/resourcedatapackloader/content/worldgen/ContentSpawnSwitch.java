package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.SpawnSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentSpawnSwitch {
    private static final List<String> RATES = List.of("surfaceDayMonsterRate", "surfaceNightMonsterRate", "undergroundDayMonsterRate", "undergroundNightMonsterRate");
    private static final List<String> CAPS = List.of("monsterCap", "creatureCap", "ambientCap", "waterCreatureCap");
    private static final String WEIGHTS = "weights";
    private static final int CHUNK_DIVISOR = 289;
    @Nullable private static NaturalSpawner.SpawnState heldState;
    @Nullable private static ServerLevel heldLevel;
    private static int scope = -1;

    private ContentSpawnSwitch() {}

    @Nullable static SpawnSwitchDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives spawning that is not an object, ignoring it", owner);
            return null;
        }
        SpawnSwitchDef def = new SpawnSwitchDef(owner);
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            int rate = RATES.indexOf(key);
            int cap = CAPS.indexOf(key);
            if (rate >= 0) {
                Double value = number(entry.getValue());
                if (value == null || value < 0.0D) { ContentLog.LOGGER.error("{} gives spawning.{} that is not a number of 0 or more, ignoring it", owner, key); }
                else { def.rates[rate] = value.floatValue(); }
            }
            else if (cap >= 0) {
                Double value = number(entry.getValue());
                if (value == null || value < 0.0D || value != Math.rint(value) || value > Integer.MAX_VALUE) { ContentLog.LOGGER.error("{} gives spawning.{} that is not a whole number of 0 or more, ignoring it", owner, key); }
                else { def.caps[cap] = value.intValue(); }
            }
            else if (WEIGHTS.equals(key)) { weights(owner, def, entry.getValue()); }
            else { ContentLog.LOGGER.error("{} gives spawning.{}, which is not a spawning key, ignoring it", owner, key); }
        }
        return def;
    }

    private static void weights(String owner, SpawnSwitchDef def, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives spawning.weights that is not an object of entity id to multiplier, ignoring it", owner);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            Identifier id = Identifier.tryParse(entry.getKey().trim().toLowerCase(Locale.ROOT));
            Double value = number(entry.getValue());
            if (id == null || value == null || value < 0.0D) { ContentLog.LOGGER.error("{} gives spawning.weights '{}', which needs an entity id and a multiplier of 0 or more, ignoring it", owner, entry.getKey()); }
            else { def.weights.put(id, value.floatValue()); }
        }
    }

    @Nullable static Double number(JsonElement element) {
        if (!element.isJsonPrimitive()) { return null; }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isNumber()) { return null; }
        double value = primitive.getAsDouble();
        return Double.isFinite(value) ? value : null;
    }

    static void checkWeights(Collection<SpawnSwitchDef> defs) {
        for (SpawnSwitchDef def : defs) {
            for (Identifier id : def.weights.keySet()) {
                if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) { ContentLog.LOGGER.error("{} weighs the entity '{}', which does not exist", def.owner, id); }
            }
        }
    }

    private static int index(MobCategory category) {
        return switch (category) {
            case MONSTER -> 0;
            case CREATURE -> 1;
            case AMBIENT -> 2;
            case WATER_CREATURE -> 3;
            default -> -1;
        };
    }

    @Nullable private static SpawnSwitchDef at(ServerLevel level, BlockPos pos) {
        if (!ContentControl.packDecides(ContentControl.SPAWNING)) { return null; }
        return ContentEnvironments.spawning(level, pos);
    }

    public static float rate(ServerLevel level, BlockPos pos, boolean sky, boolean day) {
        SpawnSwitchDef def = at(level, pos);
        if (def == null) { return -1.0F; }
        return def.rates[sky ? (day ? 0 : 1) : (day ? 2 : 3)];
    }

    public static int ceiling(ServerLevel level, MobCategory category) {
        int normal = category.getMaxInstancesPerChunk();
        int slot = index(category);
        if (slot < 0 || !ContentControl.packDecides(ContentControl.SPAWNING)) { return normal; }
        int best = -1;
        for (SpawnSwitchDef def : ContentEnvironments.spawnings(level.dimension().identifier())) { best = Math.max(best, def.caps[slot] >= 0 ? def.caps[slot] : normal); }
        return best < 0 ? normal : best;
    }

    public static int ceiling(NaturalSpawner.SpawnState state, MobCategory category) {
        ServerLevel level = levelOf(state);
        return level == null ? category.getMaxInstancesPerChunk() : ceiling(level, category);
    }

    public static boolean local(LocalMobCapCalculator calculator, NaturalSpawner.SpawnState state, MobCategory category, ChunkPos pos) {
        scope = ceiling(state, category);
        try { return calculator.canSpawn(category, pos); }
        finally { scope = -1; }
    }

    public static int scoped(MobCategory category) { return scope >= 0 ? scope : category.getMaxInstancesPerChunk(); }

    @Nullable private static ServerLevel levelOf(NaturalSpawner.SpawnState state) {
        if (state == heldState) { return heldLevel; }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) { return null; }
        for (ServerLevel level : server.getAllLevels()) {
            if (level.getChunkSource().getLastSpawnState() == state) {
                heldState = state;
                heldLevel = level;
                return level;
            }
        }
        return null;
    }

    static int capAt(ServerLevel level, BlockPos pos, MobCategory category) {
        int slot = index(category);
        SpawnSwitchDef def = slot < 0 ? null : at(level, pos);
        return def != null && def.caps[slot] >= 0 ? def.caps[slot] : category.getMaxInstancesPerChunk();
    }

    public static boolean overCap(ServerLevel level, Mob mob, BlockPos pos) {
        MobCategory category = mob.getType().getCategory();
        NaturalSpawner.SpawnState state = level.getChunkSource().getLastSpawnState();
        if (state == null || index(category) < 0) { return false; }
        int cap = capAt(level, pos, category);
        if (cap >= ceiling(level, category)) { return false; }
        return state.getMobCategoryCounts().getInt(category) >= cap * state.getSpawnableChunkCount() / CHUNK_DIVISOR;
    }

    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        SpawnSwitchDef def = at(level, event.getPos());
        if (def == null || def.weights.isEmpty()) { return; }
        for (Weighted<MobSpawnSettings.SpawnerData> held : new ArrayList<>(event.getSpawnerDataList())) {
            Float multiplier = def.weights.get(BuiltInRegistries.ENTITY_TYPE.getKey(held.value().type()));
            if (multiplier == null) { continue; }
            event.removeSpawnerData(held);
            if (multiplier > 0.0F) { event.addSpawnerData(new Weighted<>(held.value(), Math.max(1, Math.round(held.weight() * multiplier)))); }
        }
    }

    static void forget() {
        heldState = null;
        heldLevel = null;
        scope = -1;
    }
}
