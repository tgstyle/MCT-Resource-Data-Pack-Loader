package mctmods.resourcedatapackloader.common.entities.spawning;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnGateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnSwapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentWorldgenParser;
import mctmods.resourcedatapackloader.common.util.Lists;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

public final class SpawnSwaps {
    private static final List<String> FILE_KEYS = List.of("entities", "becomes", "runs", "blocks", "biomes", "biomeTypes", "requireAll", "elsewhere", "extra", "fromSpawners", "dimensions", "structures", "radius", "timeFrom", "timeTo", "dayAtLeast", "advancement", "team", "objective", "scoreAtLeast", "dietAtLeast", "dietCount");
    private static final List<String> ELSEWHERE_KEYS = List.of("chance", "lightBelow", "night");
    private static final String FRESH = "rdplSwapFresh";
    private static final String RAN = "rdplSpawnRan";
    private static final byte NATURAL = 1;
    private static final byte SPAWNER = 2;
    private static final int NIGHT_FROM = 13000;
    private static final int NIGHT_TO = 23000;
    private static final int MAX_EXTRA = 16;
    private static final List<SpawnSwapDef> SWAPS = new ArrayList<>();

    private SpawnSwaps() {}

    public static void load() {
        SWAPS.clear();
        Json.eachObject(PackManager.SPAWN_SWAPS, "spawn swap file", SpawnSwaps::read);
        if (!SWAPS.isEmpty()) { Summary.info("entities.swaps", "Loaded " + SWAPS.size() + " spawn swap(s)"); }
    }

    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (SWAPS.isEmpty() || event.getLevel().isClientSide()) { return; }
        MobSpawnType type = event.getSpawnType();
        byte origin;
        if (type == MobSpawnType.NATURAL || type == MobSpawnType.CHUNK_GENERATION) { origin = NATURAL; }
        else if (type == MobSpawnType.SPAWNER || type == MobSpawnType.TRIAL_SPAWNER) { origin = SPAWNER; }
        else { return; }
        Entity entity = event.getEntity();
        for (SpawnSwapDef swap : SWAPS) {
            if (swap.swaps() && MobStats.targets(swap.entities(), entity) && (origin == NATURAL || swap.fromSpawners())) {
                entity.getPersistentData().putByte(FRESH, origin);
                return;
            }
        }
    }

    public static boolean replace(EntityJoinLevelEvent event) {
        if (SWAPS.isEmpty()) { return false; }
        Entity entity = event.getEntity();
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(FRESH)) { return false; }
        byte origin = data.getByte(FRESH);
        data.remove(FRESH);
        if (!(event.getLevel() instanceof ServerLevel level) || EntitySwap.active()) { return false; }
        SpawnSwapDef swap = matching(level, entity, origin);
        if (swap == null || !replaced(level, entity, swap, false)) { return false; }
        event.setCanceled(true);
        for (int copy = 0; copy < swap.extra(); copy++) { replaced(level, entity, swap, true); }
        return true;
    }

    public static void onJoinRuns(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (SWAPS.isEmpty() || event.getLevel().isClientSide() || entity.getPersistentData().contains(RAN)) { return; }
        List<String> functions = Lists.collect(SWAPS, swap -> !swap.runs().isEmpty() && MobStats.targets(swap.entities(), entity), SpawnSwapDef::runs);
        if (functions.isEmpty()) { return; }
        entity.getPersistentData().putBoolean(RAN, true);
        for (String function : functions) { Functions.runAs(entity, function, "A spawn swap for " + entity.getType()); }
    }

    @Nullable private static SpawnSwapDef matching(ServerLevel level, Entity entity, byte origin) {
        for (SpawnSwapDef swap : SWAPS) {
            if (!swap.swaps() || !MobStats.targets(swap.entities(), entity) || origin == SPAWNER && !swap.fromSpawners() || !ContentSpawnGate.holds(swap.gate(), entity)) { continue; }
            if (placeHolds(level, entity, swap) || elsewhereHolds(level, entity, swap)) { return swap; }
        }
        return null;
    }

    private static boolean replaced(ServerLevel level, Entity entity, SpawnSwapDef swap, boolean needsRoom) {
        String chosen = PickDef.pick(swap.becomes(), level.getRandom());
        EntityType<?> wanted = chosen == null ? null : EntityType.byString(chosen).orElse(null);
        if (wanted == null) {
            ContentLog.LOGGER.error("A spawn swap for {} can become {}, which nothing registers, so the mob stays as it is", entity.getType(), chosen);
            return false;
        }
        return EntitySwap.spawn(level, entity, wanted, needsRoom);
    }

    private static boolean placeHolds(ServerLevel level, Entity entity, SpawnSwapDef swap) {
        if (!swap.placed()) { return true; }
        BlockPos pos = entity.blockPosition();
        Holder<Biome> biome = level.getBiome(pos);
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        boolean all = true;
        boolean any = false;
        if (!swap.biomes().isEmpty()) {
            boolean held = ContentEntitySpawns.inBiomes(id, swap.biomes());
            all &= held;
            any |= held;
        }
        if (!swap.biomeTypes().isEmpty()) {
            boolean held = ContentEntitySpawns.ofTypes(biome, swap.biomeTypes());
            all &= held;
            any |= held;
        }
        if (!swap.blocks().isEmpty()) {
            boolean held = onBlock(level.getBlockState(pos.below()), swap.blocks());
            all &= held;
            any |= held;
        }
        return swap.requireAll() ? all : any;
    }

    private static boolean elsewhereHolds(ServerLevel level, Entity entity, SpawnSwapDef swap) {
        SpawnSwapDef.Elsewhere elsewhere = swap.elsewhere();
        if (elsewhere == null) { return false; }
        if (elsewhere.night() && !CardPlace.within(CardPlace.timeOfDay(level), NIGHT_FROM, NIGHT_TO)) { return false; }
        if (elsewhere.lightBelow() > 0 && level.getMaxLocalRawBrightness(entity.blockPosition()) >= elsewhere.lightBelow()) { return false; }
        return elsewhere.chance() >= 1.0F || level.getRandom().nextFloat() < elsewhere.chance();
    }

    private static boolean onBlock(BlockState state, List<BlockMatchDef> matches) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        for (BlockMatchDef match : matches) {
            if (!match.block().equals(id)) { continue; }
            boolean same = true;
            for (Map.Entry<String, String> wanted : match.properties().entrySet()) {
                Property<?> property = state.getBlock().getStateDefinition().getProperty(wanted.getKey());
                same &= property != null && valueName(property, state).equals(wanted.getValue());
            }
            if (same) { return true; }
        }
        return false;
    }

    private static <T extends Comparable<T>> String valueName(Property<T> property, BlockState state) { return property.getName(state.getValue(property)); }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        String owner = "Spawn swap file " + key;
        List<String> entities = json.has("entities") ? MobStats.entities(owner, "entities", json.get("entities")) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        List<PickDef> becomes = ContentWorldgenParser.picks(json, "becomes", "entity");
        String runs = Json.getString(json, Json.RUNS, "").trim();
        if (becomes.isEmpty() && runs.isEmpty()) {
            ContentLog.LOGGER.error("{} names nothing the entities become and no function it runs, ignoring it", owner);
            return;
        }
        SpawnSwapDef.Elsewhere elsewhere = null;
        if (json.has("elsewhere")) {
            JsonObject held = Json.object(json, "elsewhere");
            Json.unknownKeys(held, "elsewhere", ELSEWHERE_KEYS);
            elsewhere = new SpawnSwapDef.Elsewhere(Mth.clamp(Json.getFloat(held, "chance", 1.0F), 0.0F, 1.0F), held.has("lightBelow") ? Mth.clamp(Json.getInt(held, "lightBelow", 0), 1, 16) : 0, Json.flag(held, "night"));
        }
        SWAPS.add(new SpawnSwapDef(entities, becomes, Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"), ContentParser.matches(key, json, "blocks"), Json.flag(json, "requireAll"),
                SpawnGateDef.read(key, json, "Spawn swap file"), elsewhere, Json.clamped(json, "extra", 0, 0, MAX_EXTRA), Json.flag(json, "fromSpawners"), runs));
    }
}
