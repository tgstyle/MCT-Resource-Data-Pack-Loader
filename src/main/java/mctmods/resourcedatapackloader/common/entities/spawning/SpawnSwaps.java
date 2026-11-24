package mctmods.resourcedatapackloader.common.entities.spawning;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.ContentEntityTypes;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnGateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnSwapDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.Lists;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class SpawnSwaps {
    private static final List<String> FILE_KEYS = Collections.unmodifiableList(Arrays.asList("entities", "becomes", "runs", "blocks", "biomes", "biomeTypes", "requireAll", "elsewhere", "extra", "fromSpawners", "dimensions", "structures", "radius", "timeFrom", "timeTo", "dayAtLeast", "advancement", "team", "objective", "scoreAtLeast", "dietAtLeast", "dietCount"));
    private static final List<String> ELSEWHERE_KEYS = Collections.unmodifiableList(Arrays.asList("chance", "lightBelow", "night"));
    private static final String MARK = "rdplSwapFresh";
    private static final String RAN = "rdplSpawnRan";
    private static final byte NATURAL = 1;
    private static final byte SPAWNER = 2;
    private static final int NIGHT_FROM = 13000;
    private static final int NIGHT_TO = 23000;
    private static final int MAX_EXTRA = 16;
    private static final List<SpawnSwapDef> RULES = new ArrayList<>();

    private SpawnSwaps() {}

    public static void load() {
        RULES.clear();
        Json.eachObject(PackManager.SPAWN_SWAPS, "spawn swap file", SpawnSwaps::read);
        if (!RULES.isEmpty()) { Summary.info("entities.swaps", "Loaded " + RULES.size() + " spawn swap(s)"); }
    }

    @SubscribeEvent public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (RULES.isEmpty() || event.getWorld().isRemote) { return; }
        Entity entity = event.getEntity();
        byte kind = event.getSpawner() != null || event.isSpawner() ? SPAWNER : NATURAL;
        if (matching(entity, kind).isEmpty()) { return; }
        entity.getEntityData().setByte(MARK, kind);
    }

    @SubscribeEvent(priority = EventPriority.HIGH) public static void onJoin(EntityJoinWorldEvent event) {
        if (RULES.isEmpty() || event.getWorld().isRemote || !(event.getEntity() instanceof EntityLiving) || !involved(event.getEntity())) { return; }
        NBTTagCompound held = event.getEntity().getEntityData();
        if (!held.hasKey(MARK)) { return; }
        byte kind = held.getByte(MARK);
        held.removeTag(MARK);
        if (EntitySwap.active()) { return; }
        World world = event.getWorld();
        BlockPos spot = new BlockPos(event.getEntity());
        for (SpawnSwapDef rule : matching(event.getEntity(), kind)) {
            if (!ContentSpawnGate.holds(rule.gate, event.getEntity()) || !(placeHolds(rule, world, spot) || elsewhereHolds(rule, world, spot))) { continue; }
            if (!replace(rule, world, event.getEntity(), false)) { return; }
            event.setCanceled(true);
            for (int copy = 0; copy < rule.extra; copy++) { replace(rule, world, event.getEntity(), true); }
            return;
        }
    }

    private static boolean replace(SpawnSwapDef rule, World world, Entity was, boolean needsRoom) {
        String chosen = PickDef.pick(rule.becomes, world.rand, null);
        if (chosen == null) { return false; }
        ResourceLocation wanted = new ResourceLocation(chosen);
        if (!EntityList.isRegistered(wanted)) {
            ContentLog.LOGGER.error("A spawn swap for {} wants {}, which nothing registers, so the spawn stays as it is", rule.entities, chosen);
            return false;
        }
        return EntitySwap.spawn(world, was, wanted, needsRoom);
    }

    private static boolean involved(Entity entity) {
        for (SpawnSwapDef rule : RULES) {
            if (swapsFor(rule, entity)) { return true; }
        }
        return false;
    }

    private static boolean swapsFor(SpawnSwapDef rule, Entity entity) { return rule.swaps() && MobStats.targets(rule.entities, entity); }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onJoinRuns(EntityJoinWorldEvent event) {
        if (RULES.isEmpty() || event.getWorld().isRemote) { return; }
        Entity entity = event.getEntity();
        List<String> functions = Lists.collect(RULES, rule -> !rule.runs.isEmpty() && MobStats.targets(rule.entities, entity), rule -> rule.runs);
        if (functions.isEmpty() || entity.getEntityData().hasKey(RAN)) { return; }
        entity.getEntityData().setBoolean(RAN, true);
        for (String function : functions) { Functions.runAs(entity, function, "A spawn swap for " + entity.getName()); }
    }

    private static List<SpawnSwapDef> matching(Entity entity, byte kind) {
        return Lists.collect(RULES, rule -> swapsFor(rule, entity) && (kind != SPAWNER || rule.fromSpawners), rule -> rule);
    }

    private static boolean placeHolds(SpawnSwapDef rule, World world, BlockPos spot) {
        if (!rule.placed()) { return true; }
        Biome biome = world.getBiome(spot);
        boolean any = false;
        boolean all = true;
        if (!rule.biomes.isEmpty()) {
            boolean held = ContentEntityTypes.matches(biome, rule.biomes, Collections.emptyList());
            any |= held;
            all &= held;
        }
        if (!rule.biomeTypes.isEmpty()) {
            boolean held = ContentEntityTypes.matches(biome, Collections.emptyList(), rule.biomeTypes);
            any |= held;
            all &= held;
        }
        if (!rule.blocks.isEmpty()) {
            boolean held = rule.blocks.contains(world.getBlockState(spot.down()));
            any |= held;
            all &= held;
        }
        return rule.requireAll ? all : any;
    }

    private static boolean elsewhereHolds(SpawnSwapDef rule, World world, BlockPos spot) {
        if (!rule.elsewhere) { return false; }
        if (rule.night && !CardPlace.within(CardPlace.timeOfDay(world), NIGHT_FROM, NIGHT_TO)) { return false; }
        if (rule.lightBelow > 0 && world.getLightFromNeighbors(spot) >= rule.lightBelow) { return false; }
        return world.rand.nextFloat() < rule.chance;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        List<String> entities = MobStats.required("Spawn swap file " + key, json, "entities", "entities");
        if (entities == null) {
            ContentLog.LOGGER.error("Spawn swap file {} names no entities, ignoring it", key);
            return;
        }
        List<PickDef> becomes = ContentParser.picks(json, "becomes", "entity");
        String runs = Json.getString(json, Json.RUNS, "");
        if (becomes.isEmpty() && runs.isEmpty()) {
            ContentLog.LOGGER.error("Spawn swap file {} names nothing it becomes and no function it runs, ignoring it", key);
            return;
        }
        BlockGroup blocks = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, "blocks")) { blocks.add(match, key, "blocks"); }
        if (json.has("blocks") && blocks.isEmpty()) {
            ContentLog.LOGGER.error("Spawn swap file {} names no registered blocks, ignoring it", key);
            return;
        }
        JsonObject elsewhere = Json.object(json, "elsewhere");
        Json.unknownKeys(elsewhere, "elsewhere", ELSEWHERE_KEYS);
        RULES.add(new SpawnSwapDef(entities, becomes, Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"), blocks, Json.getBoolean(json, "requireAll", false),
                SpawnGateDef.read(key, json, "Spawn swap file"), json.has("elsewhere"), Json.chance(elsewhere, "chance", 1.0F),
                Json.clamped(elsewhere, "lightBelow", 0, 0, 16), Json.getBoolean(elsewhere, "night", false), Json.clamped(json, "extra", 0, 0, MAX_EXTRA),
                Json.getBoolean(json, "fromSpawners", false), runs));
    }
}
