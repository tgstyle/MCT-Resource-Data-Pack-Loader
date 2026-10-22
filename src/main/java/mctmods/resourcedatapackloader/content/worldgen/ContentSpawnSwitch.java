package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.SpawnSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentSpawnSwitch {
    private static final String WEIGHTS = "weights";
    private static final List<String> RATES = Arrays.asList("surfaceDayMonsterRate", "surfaceNightMonsterRate", "undergroundDayMonsterRate", "undergroundNightMonsterRate");
    private static final List<String> CAPS = Arrays.asList("monsterCap", "creatureCap", "ambientCap", "waterCreatureCap");
    private static final int VANILLA_DIV = 289;
    private static final Map<Integer, Gate[]> GATES = new ConcurrentHashMap<>();
    private static int pendingUnits;
    private static int current;
    private static boolean checked;

    private ContentSpawnSwitch() {}

    private static final class Gate {
        int count;
        int units;
        int div;
        long tick = -1L;
    }

    @Nullable static SpawnSwitchDef parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives spawning that is not an object of rate, cap and weights keys, ignoring it", owner);
            return null;
        }
        float[] rates = SpawnSwitchDef.filled(-1.0F);
        int[] caps = SpawnSwitchDef.filled(-1);
        Map<String, Float> weights = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            int rate = RATES.indexOf(key);
            int cap = CAPS.indexOf(key);
            if (rate >= 0) {
                Float value = number(owner, "spawning." + key, entry.getValue(), false);
                if (value != null) { rates[rate] = value; }
            }
            else if (cap >= 0) {
                Float value = number(owner, "spawning." + key, entry.getValue(), true);
                if (value != null) { caps[cap] = value.intValue(); }
            }
            else if (WEIGHTS.equals(key)) { weigh(owner, entry.getValue(), weights); }
            else { ContentLog.LOGGER.error("{} gives spawning.{}, which is not a spawning key, ignoring it", owner, key); }
        }
        return new SpawnSwitchDef(owner, rates, caps, weights);
    }

    private static void weigh(String owner, JsonElement value, Map<String, Float> into) {
        if (!value.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives spawning.weights that is not an object of entity id to multiplier, ignoring it", owner);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : ((JsonObject) value).entrySet()) {
            Float multiplier = number(owner, "spawning.weights." + entry.getKey(), entry.getValue(), false);
            if (multiplier == null) { continue; }
            String id = Settings.lower(entry.getKey());
            into.put(id.indexOf(':') < 0 ? "minecraft:" + id : id, multiplier);
        }
    }

    @Nullable static Float number(String owner, String key, JsonElement value, boolean whole) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            float number = value.getAsFloat();
            if (number >= 0.0F && number < Float.POSITIVE_INFINITY && (!whole || number == Math.floor(number))) { return number; }
        }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not {} of 0 or more, ignoring it", owner, key, value, whole ? "a whole number" : "a number");
        return null;
    }

    private static int slot(EnumCreatureType type) {
        switch (type) {
            case MONSTER: return 0;
            case CREATURE: return 1;
            case AMBIENT: return 2;
            case WATER_CREATURE: return 3;
            default: return -1;
        }
    }

    @Nullable private static SpawnSwitchDef at(World world, BlockPos pos) {
        if (!ContentControl.packDecides(ContentControl.SPAWNING)) { return null; }
        return ContentEnvironments.spawning(world, pos);
    }

    public static float rate(World world, BlockPos pos, boolean sky, boolean day) {
        SpawnSwitchDef def = at(world, pos);
        return def == null ? -1.0F : def.rate(sky, day);
    }

    public static int ceiling(int dimension, EnumCreatureType type) {
        int normal = type.getMaxNumberOfCreature();
        int slot = slot(type);
        if (slot < 0 || !ContentControl.packDecides(ContentControl.SPAWNING)) { return normal; }
        int most = -1;
        for (SpawnSwitchDef def : ContentEnvironments.spawnings(dimension)) { most = Math.max(most, def.cap(slot) < 0 ? normal : def.cap(slot)); }
        return most < 0 ? normal : most;
    }

    public static int ceiling(EnumCreatureType type) { return ceiling(current, type); }

    private static int capAt(World world, BlockPos pos, EnumCreatureType type) {
        int normal = type.getMaxNumberOfCreature();
        int slot = slot(type);
        SpawnSwitchDef def = slot < 0 ? null : at(world, pos);
        return def == null || def.cap(slot) < 0 ? normal : def.cap(slot);
    }

    public static int units(int units) {
        pendingUnits = units;
        return units;
    }

    public static int record(World world, EnumCreatureType type, int count) {
        store(world, type, count, pendingUnits, VANILLA_DIV);
        current = world.provider.getDimension();
        return count;
    }

    public static int gate(World world, EnumCreatureType type, int count, int units, int div) {
        store(world, type, count, units, div);
        return ceiling(world.provider.getDimension(), type);
    }

    private static void store(World world, EnumCreatureType type, int count, int units, int div) {
        Gate[] gates = GATES.computeIfAbsent(world.provider.getDimension(), key -> new Gate[EnumCreatureType.values().length]);
        int index = type.ordinal();
        if (index >= gates.length) { return; }
        if (gates[index] == null) { gates[index] = new Gate(); }
        Gate gate = gates[index];
        gate.count = count;
        gate.units = units;
        gate.div = div;
        gate.tick = world.getTotalWorldTime();
    }

    public static boolean overCap(World world, Entity mob, BlockPos pos) {
        if (!ContentControl.packDecides(ContentControl.SPAWNING)) { return false; }
        Gate[] gates = GATES.get(world.provider.getDimension());
        if (gates == null) { return false; }
        for (EnumCreatureType type : EnumCreatureType.values()) {
            if (!mob.isCreatureType(type, true)) { continue; }
            Gate gate = type.ordinal() < gates.length ? gates[type.ordinal()] : null;
            if (gate == null || gate.tick != world.getTotalWorldTime()) { return false; }
            int cap = capAt(world, pos, type);
            return cap < ceiling(world.provider.getDimension(), type) && gate.count > (long) cap * gate.units / gate.div;
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        SpawnSwitchDef def = at(event.getWorld(), event.getPos());
        if (def == null || def.weights.isEmpty()) { return; }
        ListIterator<Biome.SpawnListEntry> entries = event.getList().listIterator();
        while (entries.hasNext()) {
            Biome.SpawnListEntry entry = entries.next();
            ResourceLocation id = EntityList.getKey(entry.entityClass);
            Float multiplier = id == null ? null : def.weights.get(id.toString());
            if (multiplier == null) { continue; }
            if (multiplier <= 0.0F) { entries.remove(); }
            else { entries.set(def.scaled(entry, multiplier)); }
        }
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        if (checked || event.getWorld().isRemote) { return; }
        checked = true;
        for (SpawnSwitchDef def : ContentEnvironments.allSpawning()) {
            for (String id : def.weights.keySet()) {
                if (!EntityList.isRegistered(new ResourceLocation(id))) { ContentLog.LOGGER.error("{} gives spawning.weights for '{}', which is not an entity, so it changes nothing", def.owner, id); }
            }
        }
    }

    public static void serverStopped() {
        GATES.clear();
        checked = false;
        current = 0;
    }
}
