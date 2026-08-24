package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentStructurePlacement {
    public static final String TEMPLES = "temples";
    public static final String MONUMENTS = "monuments";
    public static final String MANSIONS = "mansions";
    public static final String MINESHAFTS = "mineshafts";
    public static final String STRONGHOLDS = "strongholds";
    public static final String FORTRESSES = "netherbridges";
    public static final String ENDCITIES = "endcities";
    public static final String DUNGEONS = "dungeons";
    public static final String VILLAGES = "villages";
    private static final Map<String, Integer> SPACING = new LinkedHashMap<>();
    private static final Map<String, Integer> SEPARATION = new LinkedHashMap<>();
    private static final Map<String, Integer> SPAWN_DISTANCE = new LinkedHashMap<>();
    private static final Map<String, Set<String>> BIOMES = new LinkedHashMap<>();
    private static final Map<String, Boolean> BLACKLISTS = new LinkedHashMap<>();
    private static final Map<String, List<String>> SPAWNS = new LinkedHashMap<>();
    private static final Map<String, List<ResourceLocation>> SPAWNERS = new LinkedHashMap<>();
    private static final Map<String, List<long[]>> AT = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentStructurePlacement() {}

    public static int spacing(String key, int fallback) { return value(SPACING, key, fallback, 1); }

    public static int separation(String key, int fallback) { return value(SEPARATION, key, fallback, 0); }

    public static double chance(String key, double fallback) {
        int wanted = value(SPACING, key, -1, 1);
        if (wanted <= 0) { return fallback; }
        return 1.0D / wanted;
    }

    public static List<long[]> pins(String key) {
        load();
        return AT.get(key);
    }

    public static boolean allows(String key, World world, int chunkX, int chunkZ) {
        load();
        if (ContentControl.off(ContentControl.STRUCTURES) || world == null) { return true; }
        List<long[]> pinned = AT.get(key);
        if (pinned != null) {
            for (long[] at : pinned) {
                if ((int) at[0] >> 4 == chunkX && (int) at[1] >> 4 == chunkZ) { return true; }
            }
            return false;
        }
        int least = SPAWN_DISTANCE.getOrDefault(key, 0);
        if (least > 0) {
            BlockPos spawn = world.getSpawnPoint();
            double dx = (chunkX * 16 + 8) - spawn.getX();
            double dz = (chunkZ * 16 + 8) - spawn.getZ();
            if (dx * dx + dz * dz < (double) least * least) { return false; }
        }
        Set<String> wanted = BIOMES.get(key);
        if (wanted == null || wanted.isEmpty()) { return true; }
        Biome biome = world.getBiomeProvider().getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8));
        return matches(biome, wanted) != BLACKLISTS.getOrDefault(key, Boolean.FALSE);
    }

    public static void spawns(String key, List<Biome.SpawnListEntry> entries) {
        load();
        if (ContentControl.off(ContentControl.STRUCTURES)) { return; }
        List<String> wanted = SPAWNS.get(key);
        if (wanted == null) { return; }
        entries.clear();
        for (String entry : wanted) {
            Biome.SpawnListEntry made = spawn(key, entry);
            if (made != null) { entries.add(made); }
        }
        Summary.info("structures.spawns." + key, "Structure " + key + " spawns " + entries.size() + " kind(s) of mob");
    }

    public static ResourceLocation spawner(String key, ResourceLocation fallback, Random random) {
        load();
        if (ContentControl.off(ContentControl.STRUCTURES)) { return fallback; }
        List<ResourceLocation> wanted = SPAWNERS.get(key);
        if (wanted == null || wanted.isEmpty()) { return fallback; }
        return wanted.get(random.nextInt(wanted.size()));
    }

    public static List<Biome> filtered(String key, List<Biome> original) {
        List<Biome> copy = new ArrayList<>(original);
        filter(key, copy);
        return copy;
    }

    public static void filter(String key, List<Biome> biomes) {
        load();
        if (ContentControl.off(ContentControl.STRUCTURES)) { return; }
        Set<String> wanted = BIOMES.get(key);
        if (wanted == null || wanted.isEmpty()) { return; }
        boolean blacklist = BLACKLISTS.getOrDefault(key, Boolean.FALSE);
        List<Biome> kept = new ArrayList<>();
        int empty = 0;
        for (Biome biome : biomes) {
            if (biome == null) {
                empty++;
                continue;
            }
            if (matches(biome, wanted) != blacklist) { kept.add(biome); }
        }
        if (empty > 0) { ContentLog.LOGGER.warn("Something put {} empty biome slot(s) in the list of biomes {} may generate in. They are left out, since nothing can be built in a biome that is not there", empty, key); }
        if (!blacklist) {
            for (String name : wanted) {
                Biome named = Biome.REGISTRY.getObject(new ResourceLocation(name));
                if (named != null && !kept.contains(named)) { kept.add(named); }
            }
        }
        if (kept.isEmpty()) {
            ContentLog.LOGGER.warn("Biome settings for {} leave no biome at all, so the vanilla list is left alone", key);
            return;
        }
        biomes.clear();
        biomes.addAll(kept);
    }

    public static void reload() { loaded = false; }

    private static int value(Map<String, Integer> from, String key, int fallback, int least) {
        load();
        if (ContentControl.off(ContentControl.STRUCTURES)) { return fallback; }
        Integer wanted = from.get(key);
        if (wanted == null || wanted <= 0) { return fallback; }
        return Math.max(least, wanted);
    }

    private static boolean matches(Biome biome, Set<String> wanted) {
        if (biome == null) { return false; }
        ResourceLocation name = biome.getRegistryName();
        if (name != null && wanted.contains(name.toString().toLowerCase(Locale.ROOT))) { return true; }
        if (wanted.contains(ContentBiomeControl.shownName(biome).toLowerCase(Locale.ROOT))) { return true; }
        for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
            if (wanted.contains(type.getName().toLowerCase(Locale.ROOT))) { return true; }
        }
        return false;
    }

    private static void load() {
        if (loaded) { return; }
        loaded = true;
        numbers(SPACING, "structureSpacing", Config.worldgen.structureSpacing);
        numbers(SEPARATION, "structureSeparation", Config.worldgen.structureSeparation);
        numbers(SPAWN_DISTANCE, "structureMinDistanceFromSpawn", Config.worldgen.structureMinDistanceFromSpawn);
        pins(Config.worldgen.structureAt);
        lists(Config.worldgen.structureBiomes);
        spawnLists(Config.worldgen.structureSpawns);
        spawnerLists(Config.worldgen.structureSpawners);
        flags(Config.worldgen.structureBiomesAreBlacklist);
        if (!SPACING.isEmpty() || !BIOMES.isEmpty()) { Summary.info("structures.placement", "Structure placement changed for " + union() + " by pack or config"); }
    }

    private static Set<String> union() {
        Set<String> keys = new LinkedHashSet<>(SPACING.keySet());
        keys.addAll(SEPARATION.keySet());
        keys.addAll(SPAWN_DISTANCE.keySet());
        keys.addAll(BIOMES.keySet());
        return keys;
    }

    private static void numbers(Map<String, Integer> into, String setting, String[] fallback) {
        into.clear();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, setting, fallback)) {
            String[] parts = split(entry, setting);
            if (parts == null) { continue; }
            try { into.put(parts[0], Integer.parseInt(parts[1].trim())); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("{} entry '{}' does not end in a number, ignoring it", setting, entry); }
        }
    }

    public static boolean pinned(String key, int chunkX, int chunkZ) {
        load();
        List<long[]> pinned = AT.get(key);
        if (pinned == null) { return false; }
        for (long[] at : pinned) {
            if ((int) at[0] >> 4 == chunkX && (int) at[1] >> 4 == chunkZ) { return true; }
        }
        return false;
    }

    private static void pins(String[] fallback) {
        AT.clear();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAt", fallback)) {
            String[] parts = split(entry, "structureAt");
            if (parts == null) { continue; }
            String[] coords = parts[1].split(",");
            if (coords.length != 2) {
                ContentLog.LOGGER.error("structureAt entry '{}' does not end in x,z, ignoring it", entry);
                continue;
            }
            try { AT.computeIfAbsent(parts[0], held -> new java.util.ArrayList<>()).add(new long[] {Long.parseLong(coords[0].trim()), Long.parseLong(coords[1].trim())}); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("structureAt entry '{}' does not end in x,z, ignoring it", entry); }
        }
    }

    private static void flags(String[] fallback) {
        BLACKLISTS.clear();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureBiomesAreBlacklist", fallback)) {
            String[] parts = split(entry, "structureBiomesAreBlacklist");
            if (parts == null) { continue; }
            BLACKLISTS.put(parts[0], Boolean.parseBoolean(parts[1].trim()));
        }
    }

    private static void lists(String[] fallback) {
        BIOMES.clear();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureBiomes", fallback)) {
            String[] parts = split(entry, "structureBiomes");
            if (parts == null) { continue; }
            Set<String> names = new LinkedHashSet<>(BIOMES.getOrDefault(parts[0], Collections.emptySet()));
            for (String name : parts[1].split(",")) {
                String plain = name.trim().toLowerCase(Locale.ROOT);
                if (!plain.isEmpty()) { names.add(plain); }
            }
            BIOMES.put(parts[0], Collections.unmodifiableSet(names));
        }
    }

    @Nullable private static Biome.SpawnListEntry spawn(String key, String entry) {
        String[] parts = entry.split(":");
        if (parts.length < 5) {
            ContentLog.LOGGER.error("structureSpawns entry '{}' for {} is not written as namespace:entity:weight:least:most, ignoring it", entry, key);
            return null;
        }
        ResourceLocation location = new ResourceLocation(parts[0], parts[1]);
        EntityEntry registered = ForgeRegistries.ENTITIES.containsKey(location) ? ForgeRegistries.ENTITIES.getValue(location) : null;
        if (registered == null) {
            ContentLog.LOGGER.error("structureSpawns entry '{}' for {} names an entity nothing registers, ignoring it", entry, key);
            return null;
        }
        if (!EntityLiving.class.isAssignableFrom(registered.getEntityClass())) {
            ContentLog.LOGGER.error("structureSpawns entry '{}' for {} names an entity that is not a living one, ignoring it", entry, key);
            return null;
        }
        try {
            int weight = Math.max(1, Integer.parseInt(parts[2].trim()));
            int least = Math.max(1, Integer.parseInt(parts[3].trim()));
            int most = Math.max(least, Integer.parseInt(parts[4].trim()));
            return new Biome.SpawnListEntry(registered.getEntityClass().asSubclass(EntityLiving.class), weight, least, most);
        }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("structureSpawns entry '{}' for {} does not end in three numbers, ignoring it", entry, key);
            return null;
        }
    }

    private static void spawnLists(String[] fallback) {
        SPAWNS.clear();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSpawns", fallback)) {
            String[] parts = split(entry, "structureSpawns");
            if (parts == null) { continue; }
            List<String> made = new ArrayList<>();
            for (String one : parts[1].split(",")) {
                String plain = one.trim();
                if (!plain.isEmpty()) { made.add(plain); }
            }
            SPAWNS.put(parts[0], Collections.unmodifiableList(made));
        }
    }

    private static void spawnerLists(String[] fallback) {
        SPAWNERS.clear();
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureSpawners", fallback)) {
            String[] parts = split(entry, "structureSpawners");
            if (parts == null) { continue; }
            List<ResourceLocation> made = new ArrayList<>();
            for (String one : parts[1].split(",")) {
                String plain = one.trim();
                if (plain.isEmpty()) { continue; }
                ResourceLocation location = new ResourceLocation(plain);
                if (!ForgeRegistries.ENTITIES.containsKey(location)) {
                    ContentLog.LOGGER.error("structureSpawners entry '{}' names {}, which nothing registers, ignoring that name", entry, location);
                    continue;
                }
                made.add(location);
            }
            SPAWNERS.put(parts[0], Collections.unmodifiableList(made));
        }
    }

    private static String[] split(String entry, String setting) {
        String[] parts = Settings.pair(entry, setting, "structure=value");
        if (parts == null) { return null; }
        String key = ContentStructures.normalise(parts[0]);
        if (!ContentStructures.known(key)) {
            ContentLog.LOGGER.error("{} entry '{}' names '{}', which is not one of {}, ignoring it", setting, entry, key, ContentStructures.describe());
            return null;
        }
        return new String[] { key, parts[1] };
    }
}
