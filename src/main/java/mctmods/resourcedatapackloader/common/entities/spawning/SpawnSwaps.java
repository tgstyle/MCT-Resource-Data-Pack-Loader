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
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

public final class SpawnSwaps {
    private static final String MARK = "rdplSwapFresh";
    private static final String NATURAL = "natural";
    private static final String SPAWNER = "spawner";
    private static final int NIGHT_FROM = 13000;
    private static final int NIGHT_TO = 23000;
    private static final int MOST_EXTRA = 16;
    private static final int BRIGHTEST = 16;
    private static final List<Rule> RULES = new ArrayList<>();
    private static final Set<String> WARNED = new HashSet<>();

    private SpawnSwaps() {}

    public static void load() {
        RULES.clear();
        Json.eachObject(PackManager.SPAWN_SWAPS, "spawn swap file", SpawnSwaps::read);
        if (!RULES.isEmpty()) { Summary.info("entities.swaps", "Loaded " + RULES.size() + " spawn swap(s)"); }
    }

    public static void mark(MobSpawnEvent.FinalizeSpawn event) {
        if (RULES.isEmpty() || EntitySwap.active()) { return; }
        MobSpawnType type = event.getSpawnType();
        boolean spawner = type == MobSpawnType.SPAWNER;
        if (type != MobSpawnType.NATURAL && type != MobSpawnType.CHUNK_GENERATION && !spawner) { return; }
        Mob mob = event.getEntity();
        for (Rule rule : RULES) {
            if (MobStats.targets(rule.def().entities(), mob) && (!spawner || rule.def().fromSpawners())) {
                mob.getPersistentData().putString(MARK, spawner ? SPAWNER : NATURAL);
                return;
            }
        }
    }

    public static boolean replace(EntityJoinLevelEvent event) {
        if (RULES.isEmpty() || !(event.getEntity() instanceof Mob mob) || !(event.getLevel() instanceof ServerLevel level)) { return false; }
        CompoundTag data = mob.getPersistentData();
        if (!data.contains(MARK)) { return false; }
        boolean spawner = SPAWNER.equals(data.getString(MARK));
        data.remove(MARK);
        if (EntitySwap.active()) { return false; }
        for (Rule rule : RULES) {
            if (!MobStats.targets(rule.def().entities(), mob) || spawner && !rule.def().fromSpawners() || !applies(rule, level, mob)) { continue; }
            EntityType<?> chosen = pick(rule.def(), level);
            if (chosen == null) { return false; }
            boolean swapped = chosen != mob.getType() && EntitySwap.spawn(level, chosen, mob, false);
            if (chosen == mob.getType() || swapped) {
                for (int copy = 0; copy < rule.def().extra(); copy++) {
                    EntityType<?> more = pick(rule.def(), level);
                    if (more != null) { EntitySwap.spawn(level, more, mob, true); }
                }
            }
            event.setCanceled(swapped);
            return swapped;
        }
        return false;
    }

    private static boolean applies(Rule rule, ServerLevel level, Mob mob) {
        SpawnSwapDef def = rule.def();
        if (!ContentSpawnGate.holds(def.gate(), mob)) { return false; }
        BlockPos pos = mob.blockPosition();
        if (placed(rule, level, pos)) { return true; }
        if (!def.elsewhere()) { return false; }
        if (def.night() && !CardPlace.within(CardPlace.timeOfDay(level), NIGHT_FROM, NIGHT_TO)) { return false; }
        if (def.lightBelow() > 0 && level.getMaxLocalRawBrightness(pos) >= def.lightBelow()) { return false; }
        return level.getRandom().nextFloat() < def.chance();
    }

    private static boolean placed(Rule rule, ServerLevel level, BlockPos pos) {
        SpawnSwapDef def = rule.def();
        if (!def.placed()) { return true; }
        boolean all = def.requireAll();
        Holder<Biome> biome = level.getBiome(pos);
        if (!def.biomes().isEmpty()) {
            boolean hit = namedBiome(def.biomes(), biome);
            if (hit != all) { return hit; }
        }
        if (!def.biomeTypes().isEmpty()) {
            boolean hit = typedBiome(def.biomeTypes(), biome);
            if (hit != all) { return hit; }
        }
        return def.blocks().isEmpty() ? all : rule.blocks().contains(level.getBlockState(pos.below()));
    }

    private static boolean namedBiome(List<String> wanted, Holder<Biome> biome) {
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        return BiomeNames.matches(wanted, biome) || id != null && BiomeNames.named(id, wanted);
    }

    private static boolean typedBiome(List<String> types, Holder<Biome> biome) {
        for (String type : types) {
            TagKey<Biome> tag = BiomeNames.tag(type);
            if (tag != null && biome.is(tag)) { return true; }
        }
        return false;
    }

    @Nullable private static EntityType<?> pick(SpawnSwapDef def, ServerLevel level) {
        String chosen = PickDef.pick(def.becomes(), level.getRandom());
        if (chosen == null) { return null; }
        EntityType<?> wanted = EntityType.byString(chosen).orElse(null);
        if (wanted == null) { ContentLog.LOGGER.errorOnce(WARNED, def.key() + chosen, "Spawn swap file {} can make {}, which nothing registers, so that spawn stays as it is", def.key(), chosen); }
        return wanted;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        String owner = "Spawn swap file " + key;
        List<String> entities = json.has("entities") ? MobStats.entities(owner, "entities", json.get("entities")) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        List<PickDef> becomes = ContentWorldgenParser.picks(json, "becomes", "entity");
        if (becomes.isEmpty()) {
            ContentLog.LOGGER.error("{} names nothing under becomes, ignoring it", owner);
            return;
        }
        List<String> types = Json.strings(json, "biomeTypes");
        for (String type : types) {
            if (BiomeNames.tag(type) == null) { ContentLog.LOGGER.error("{} names biome type '{}', which no biome tag on this line answers to", owner, type); }
        }
        List<BlockMatchDef> blocks = ContentParser.matches(key, json, "blocks");
        JsonObject where = json.has("elsewhere") && json.get("elsewhere").isJsonObject() ? json.getAsJsonObject("elsewhere") : null;
        if (json.has("elsewhere") && where == null) { ContentLog.LOGGER.error("{} gives elsewhere that is not an object, so it is left out", owner); }
        boolean elsewhere = where != null;
        RULES.add(new Rule(new SpawnSwapDef(key, entities, becomes, Json.strings(json, "biomes"), types, blocks, Json.getBoolean(json, "requireAll", false),
                SpawnGateDef.read(key, json, "Spawn swap file"), elsewhere, elsewhere ? Json.ranged(where, "chance", 1.0F, 0.0F, 1.0F, owner) : 1.0F,
                elsewhere && where.has("lightBelow") ? Math.max(1, Math.min(BRIGHTEST, Json.getInt(where, "lightBelow", BRIGHTEST))) : 0, elsewhere && Json.getBoolean(where, "night", false),
                Math.max(0, Math.min(MOST_EXTRA, Json.getInt(json, "extra", 0))), Json.getBoolean(json, "fromSpawners", false))));
    }

    private static final class Rule {
        private final SpawnSwapDef def;
        @Nullable private BlockGroup group;

        private Rule(SpawnSwapDef def) { this.def = def; }

        private SpawnSwapDef def() { return def; }

        private BlockGroup blocks() {
            if (group == null) {
                group = new BlockGroup();
                for (BlockMatchDef match : def.blocks()) { group.add(match, "Spawn swap file " + def.key(), "blocks"); }
            }
            return group;
        }
    }
}
