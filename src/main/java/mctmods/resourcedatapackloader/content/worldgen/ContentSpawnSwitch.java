package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.SpawnSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentSpawnSwitch {
    private static final int CHUNKS = 289;
    private static final String WEIGHTS = "weights";
    private static final List<String> RATE_KEYS = List.of("surfaceDayMonsterRate", "surfaceNightMonsterRate", "undergroundDayMonsterRate", "undergroundNightMonsterRate");
    private static final List<String> CAP_KEYS = List.of("monsterCap", "creatureCap", "ambientCap", "waterCreatureCap");
    @Nullable private static NaturalSpawner.SpawnState seen;
    @Nullable private static ResourceLocation seenDimension;
    private static int scope = -1;

    private ContentSpawnSwitch() {}

    @Nullable static SpawnSwitchDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives spawning that is not an object, ignoring it", owner);
            return null;
        }
        float[] rates = new float[RATE_KEYS.size()];
        int[] caps = new int[CAP_KEYS.size()];
        Arrays.fill(rates, -1.0F);
        Arrays.fill(caps, -1);
        Map<String, Float> weights = new LinkedHashMap<>();
        boolean given = false;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            int rate = RATE_KEYS.indexOf(key);
            int cap = CAP_KEYS.indexOf(key);
            if (rate >= 0) {
                float amount = amount(owner, "spawning." + key, entry.getValue());
                if (amount >= 0.0F) {
                    rates[rate] = amount;
                    given = true;
                }
            }
            else if (cap >= 0) {
                float amount = amount(owner, "spawning." + key, entry.getValue());
                if (amount >= 0.0F && (amount == Float.POSITIVE_INFINITY || amount != Math.floor(amount))) { ContentLog.LOGGER.error("{} gives spawning.{} as {}, which is not a whole number of 0 or more, ignoring it", owner, key, amount); }
                else if (amount >= 0.0F) {
                    caps[cap] = (int) amount;
                    given = true;
                }
            }
            else if (WEIGHTS.equals(key)) { given |= weights(owner, entry.getValue(), weights); }
            else { ContentLog.LOGGER.error("{} gives spawning.{}, which is not a spawning key, ignoring it", owner, key); }
        }
        return given ? new SpawnSwitchDef(owner, rates, caps, weights) : null;
    }

    private static boolean weights(String owner, JsonElement element, Map<String, Float> into) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives spawning.weights that is not an object of entity ids, ignoring it", owner);
            return false;
        }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            float amount = amount(owner, "spawning.weights." + entry.getKey(), entry.getValue());
            if (amount >= 0.0F) {
                String id = Settings.lower(entry.getKey());
                into.put(id.indexOf(':') < 0 ? "minecraft:" + id : id, amount);
            }
        }
        return !into.isEmpty();
    }

    static float amount(String owner, String key, JsonElement value) {
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            ContentLog.LOGGER.error("{} gives {} that is not a number, ignoring it", owner, key);
            return -1.0F;
        }
        float amount = value.getAsFloat();
        if (!(amount >= 0.0F)) {
            ContentLog.LOGGER.error("{} gives {} below 0, ignoring it", owner, key);
            return -1.0F;
        }
        return amount;
    }

    private static int kind(MobCategory category) {
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

    static float rate(ServerLevel level, BlockPos pos, boolean sky, boolean day) {
        SpawnSwitchDef def = at(level, pos);
        return def == null ? -1.0F : def.rate((sky ? 0 : 2) + (day ? 0 : 1));
    }

    static int ceiling(ResourceLocation dimension, MobCategory category) {
        int normal = category.getMaxInstancesPerChunk();
        int kind = kind(category);
        if (kind < 0 || !ContentControl.packDecides(ContentControl.SPAWNING)) { return normal; }
        List<SpawnSwitchDef> regions = ContentEnvironments.spawnings(dimension);
        if (regions.isEmpty()) { return normal; }
        int top = 0;
        for (SpawnSwitchDef region : regions) {
            int cap = region.cap(kind);
            top = Math.max(top, cap < 0 ? normal : cap);
        }
        return top;
    }

    public static int ceiling(NaturalSpawner.SpawnState state, MobCategory category) {
        ResourceLocation dimension = dimensionOf(state);
        return dimension == null ? category.getMaxInstancesPerChunk() : ceiling(dimension, category);
    }

    @Nullable private static ResourceLocation dimensionOf(NaturalSpawner.SpawnState state) {
        if (state == seen) { return seenDimension; }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) { return null; }
        for (ServerLevel level : server.getAllLevels()) {
            if (level.getChunkSource().getLastSpawnState() == state) {
                seen = state;
                seenDimension = level.dimension().location();
                return seenDimension;
            }
        }
        return null;
    }

    private static int capAt(ServerLevel level, BlockPos pos, MobCategory category) {
        SpawnSwitchDef def = at(level, pos);
        int cap = def == null ? -1 : def.cap(kind(category));
        return cap < 0 ? category.getMaxInstancesPerChunk() : cap;
    }

    static boolean overCap(ServerLevel level, Mob mob, BlockPos pos) {
        MobCategory category = mob.getType().getCategory();
        if (kind(category) < 0) { return false; }
        int cap = capAt(level, pos, category);
        if (cap >= ceiling(level.dimension().location(), category)) { return false; }
        NaturalSpawner.SpawnState state = level.getChunkSource().getLastSpawnState();
        return state != null && state.getMobCategoryCounts().getInt(category) >= cap * state.getSpawnableChunkCount() / CHUNKS;
    }

    public static void scope(int ceiling) { scope = ceiling; }

    public static void unscope() { scope = -1; }

    public static int scoped(MobCategory category) { return scope >= 0 ? scope : category.getMaxInstancesPerChunk(); }

    public static void forget() {
        seen = null;
        seenDimension = null;
        scope = -1;
    }

    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        SpawnSwitchDef def = at(level, event.getPos());
        if (def == null || def.weights().isEmpty()) { return; }
        for (MobSpawnSettings.SpawnerData held : new ArrayList<>(event.getSpawnerDataList())) {
            Float scale = def.weights().get(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(held.type)));
            if (scale == null || scale == 1.0F) { continue; }
            event.removeSpawnerData(held);
            if (scale > 0.0F) { event.addSpawnerData(def.reweighted(held, Math.max(1, Math.round(held.getWeight().asInt() * scale)))); }
        }
    }

    public static void onServerStarted(ServerStartedEvent event) {
        for (SpawnSwitchDef def : ContentEnvironments.spawnDefs()) {
            for (String id : def.weights().keySet()) {
                ResourceLocation entity = ResourceLocation.tryParse(id);
                if (entity == null || !event.getServer().registryAccess().registryOrThrow(Registries.ENTITY_TYPE).containsKey(entity)) { ContentLog.LOGGER.error("{} gives spawning.weights for '{}', which is not an entity id, so it does nothing", def.owner(), id); }
            }
        }
    }
}
