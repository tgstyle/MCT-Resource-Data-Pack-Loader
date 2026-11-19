package mctmods.resourcedatapackloader.common.entities.spawning;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnGateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnSwapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentWorldgenParser;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.util.Compat;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

public final class SpawnSwaps {
    private static final String FRESH = "rdplSwapFresh";
    private static final String RAN = "rdplSpawnRan";
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

    public static void mark(FinalizeSpawnEvent event) {
        if (SWAPS.isEmpty() || EntitySwap.active() || event.isSpawnCancelled()) { return; }
        EntitySpawnReason reason = event.getSpawnType();
        boolean spawner = EntitySpawnReason.isSpawner(reason);
        if (!spawner && reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION) { return; }
        Mob mob = event.getEntity();
        for (SpawnSwapDef swap : SWAPS) {
            if (swap.swaps() && (!spawner || swap.fromSpawners()) && MobStats.targets(swap.ids(), swap.kinds(), mob)) {
                mob.getPersistentData().putBoolean(FRESH, spawner);
                return;
            }
        }
    }

    public static boolean replaced(ServerLevel level, Entity entity) {
        if (SWAPS.isEmpty() || !(entity instanceof Mob)) { return false; }
        CompoundTag data = entity.getPersistentData();
        Optional<Boolean> fresh = data.getBoolean(FRESH);
        if (fresh.isEmpty()) { return false; }
        data.remove(FRESH);
        if (EntitySwap.active()) { return false; }
        for (SpawnSwapDef swap : SWAPS) {
            if (swap.swaps() && (!fresh.get() || swap.fromSpawners()) && MobStats.targets(swap.ids(), swap.kinds(), entity) && ContentSpawnGate.holds(swap.gate(), entity) && applies(swap, level, entity)) { return swap(level, entity, swap); }
        }
        return false;
    }

    public static void onJoinRuns(EntityJoinLevelEvent event) {
        if (SWAPS.isEmpty() || event.getLevel().isClientSide()) { return; }
        Entity entity = event.getEntity();
        List<String> functions = new ArrayList<>();
        for (SpawnSwapDef swap : SWAPS) {
            if (!swap.runs().isEmpty() && MobStats.targets(swap.ids(), swap.kinds(), entity)) { functions.add(swap.runs()); }
        }
        if (functions.isEmpty() || entity.getPersistentData().getBooleanOr(RAN, false)) { return; }
        entity.getPersistentData().putBoolean(RAN, true);
        for (String function : functions) { Functions.runAs(entity, function, "A spawn swap for " + entity.getName().getString()); }
    }

    private static boolean swap(ServerLevel level, Entity was, SpawnSwapDef swap) {
        EntityType<?> first = roll(level, swap);
        if (first == null || !EntitySwap.place(level, first, was, false)) { return false; }
        for (int copy = 0; copy < swap.extra(); copy++) {
            EntityType<?> more = roll(level, swap);
            if (more != null) { EntitySwap.place(level, more, was, true); }
        }
        return true;
    }

    @Nullable private static EntityType<?> roll(ServerLevel level, SpawnSwapDef swap) {
        String chosen = PickDef.pick(swap.becomes(), level.getRandom());
        return chosen == null ? null : Compat.entityType(chosen);
    }

    private static boolean applies(SpawnSwapDef swap, ServerLevel level, Entity entity) {
        if (!swap.placed() || placeHolds(swap, level, entity)) { return true; }
        SpawnSwapDef.Elsewhere elsewhere = swap.elsewhere();
        if (elsewhere == null) { return false; }
        if (elsewhere.lightBelow() > 0 && level.getMaxLocalRawBrightness(entity.blockPosition()) >= elsewhere.lightBelow()) { return false; }
        if (elsewhere.night() && !CardPlace.within(CardPlace.timeOfDay(level), NIGHT_FROM, NIGHT_TO)) { return false; }
        return level.getRandom().nextFloat() < elsewhere.chance();
    }

    private static boolean placeHolds(SpawnSwapDef swap, ServerLevel level, Entity entity) {
        BlockPos at = entity.blockPosition();
        Holder<Biome> biome = level.getBiome(at);
        Identifier id = biome.unwrapKey().map(ResourceKey::identifier).orElse(null);
        int given = 0;
        int met = 0;
        if (!swap.biomes().isEmpty()) {
            given++;
            if (ContentEntitySpawns.inBiomes(biome, id, swap.biomes(), List.of())) { met++; }
        }
        if (!swap.biomeTypes().isEmpty()) {
            given++;
            if (ContentEntitySpawns.inBiomes(biome, id, List.of(), swap.biomeTypes())) { met++; }
        }
        if (!swap.blocks().isEmpty()) {
            given++;
            if (swap.blocks().contains(level.getBlockState(at.below()))) { met++; }
        }
        return swap.requireAll() ? met == given : met > 0;
    }

    private static void read(Identifier key, JsonObject json) {
        String owner = "Spawn swap file " + key;
        List<Identifier> ids = new ArrayList<>();
        List<String> kinds = new ArrayList<>();
        if (!json.has("entities") || !MobStats.entities(owner, "entities", json.get("entities"), ids, kinds) || ids.isEmpty() && kinds.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        List<PickDef> becomes = new ArrayList<>();
        for (PickDef pick : ContentWorldgenParser.picks(json, "becomes", "entity")) {
            if (Compat.entityType(pick.name()) == null) { ContentLog.LOGGER.error("{} can become {}, which nothing registers, leaving it out", owner, pick.name()); }
            else { becomes.add(pick); }
        }
        String runs = Json.getString(json, "runs", "");
        if (becomes.isEmpty() && runs.isEmpty()) {
            ContentLog.LOGGER.error("{} has no becomes entry that can be made and no function it runs, ignoring it", owner);
            return;
        }
        BlockGroup blocks = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, "blocks")) { blocks.add(match, owner, "blocks"); }
        if (json.has("blocks") && blocks.isEmpty()) {
            ContentLog.LOGGER.error("{} names no block that exists, ignoring it", owner);
            return;
        }
        SpawnGateDef gate = SpawnGateDef.read(key, json, "Spawn swap file");
        SWAPS.add(new SpawnSwapDef(ids, kinds, gate, List.copyOf(becomes), Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"), blocks, Json.getBoolean(json, "requireAll", false),
                elsewhere(owner, json), Mth.clamp(Json.getInt(json, "extra", 0), 0, MAX_EXTRA), Json.getBoolean(json, "fromSpawners", false), runs));
    }

    @Nullable private static SpawnSwapDef.Elsewhere elsewhere(String owner, JsonObject json) {
        if (!json.has("elsewhere")) { return null; }
        JsonElement held = json.get("elsewhere");
        if (!held.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives elsewhere that is not an object, leaving it out", owner);
            return null;
        }
        JsonObject object = held.getAsJsonObject();
        return new SpawnSwapDef.Elsewhere(Mth.clamp(Json.getFloat(object, "chance", 1.0F), 0.0F, 1.0F), object.has("lightBelow") ? Mth.clamp(Json.getInt(object, "lightBelow", 16), 1, 16) : 0, Json.getBoolean(object, "night", false));
    }
}
