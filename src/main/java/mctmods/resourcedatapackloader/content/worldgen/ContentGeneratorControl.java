package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import java.io.File;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentGeneratorControl {
    public static final String UNKNOWN = "unknown";
    private static final Map<String, List<String>> PATTERNS = new LinkedHashMap<>();
    private static final Blocked BLOCKED = new Blocked();
    private static final TemplateMemo<Boolean> BLOCKING = new TemplateMemo<>();
    private static final Map<Class<?>, String> OWNERS = new HashMap<>();
    private static final Map<Class<?>, String> KINDS = new HashMap<>();
    private static final Set<String> REPORTED = new HashSet<>();
    private static Set<String> whitelist;
    private static Set<String> named;
    private static Set<String> kinds;
    private static Map<String, String> mapped;
    private static Set<Integer> dimensions;

    static {
        PATTERNS.put("structures", Arrays.asList("structure", "dungeon", "ruin", "village", "island", "temple", "shrine", "tower", "ship", "pyramid", "obelisk", "monolith", "camp", "hut", "house", "spawner", "nest", "well"));
        PATTERNS.put("ores", Arrays.asList("ore", "mineral", "vein", "gem", "yellorite", "clathrate"));
        PATTERNS.put("flora", Arrays.asList("tree", "sapling", "plant", "flower", "bush", "shrub", "grass", "vine", "mushroom", "fungus", "cane", "reed", "crop", "berry", "leaf"));
        PATTERNS.put("lakes", Arrays.asList("lake", "pool", "pond", "spring", "geyser"));
        PATTERNS.put("terrain", Arrays.asList("stone", "rock", "boulder", "geode", "crystal", "cave", "basalt", "limestone", "marble", "sand", "clay", "gravel", "dirt", "deposit", "cluster"));
    }

    private ContentGeneratorControl() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.GENERATORS)) { return false; }
        return ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators)
                || ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators).length > 0
                || ContentControl.list(ContentControl.GENERATORS, "generatorTypes", Config.worldgen.generatorTypes).length > 0;
    }

    public static void load() {
        whitelist = Settings.lower(ContentControl.list(ContentControl.GENERATORS, "generatorWhitelist", Config.worldgen.generatorWhitelist));
        named = Settings.lower(ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators));
        kinds = new LinkedHashSet<>();
        for (String name : ContentControl.list(ContentControl.GENERATORS, "generatorTypes", Config.worldgen.generatorTypes)) {
            String kind = name.trim().toLowerCase(Locale.ROOT);
            if (kind.isEmpty()) { continue; }
            if (!PATTERNS.containsKey(kind) && !kind.equals(UNKNOWN)) {
                ContentLog.LOGGER.error("generatorTypes names '{}', which is not one of {} or {}, ignoring it", kind, PATTERNS.keySet(), UNKNOWN);
                continue;
            }
            kinds.add(kind);
        }
        mapped = new LinkedHashMap<>();
        for (String entry : ContentControl.list(ContentControl.GENERATORS, "generatorTypeMap", Config.worldgen.generatorTypeMap)) {
            String[] parts = Settings.pair(entry, "generatorTypeMap", "pattern=type");
            if (parts == null) { continue; }
            String pattern = parts[0].toLowerCase(Locale.ROOT);
            String kind = parts[1].toLowerCase(Locale.ROOT);
            if (!PATTERNS.containsKey(kind) && !kind.equals(UNKNOWN)) {
                ContentLog.LOGGER.error("generatorTypeMap entry '{}' names a type that is not one of {} or {}, ignoring it", entry, PATTERNS.keySet(), UNKNOWN);
                continue;
            }
            mapped.put(pattern, kind);
        }
        dimensions = new HashSet<>();
        for (int dimension : ContentControl.numbers(ContentControl.GENERATORS, "blockGeneratorDimensions", Config.worldgen.blockGeneratorDimensions)) { dimensions.add(dimension); }
        KINDS.clear();
        BLOCKED.clear();
        REPORTED.clear();
        if (ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators)) { Summary.info("generators", "Blocking third party world generation except from " + whitelist); }
        else if (!named.isEmpty()) { Summary.info("generators", "Blocking world generation from " + named); }
        if (!kinds.isEmpty()) { Summary.info("generators.types", (typesAreBlacklist() ? "Blocking these world generator types outright: " : "Allowing only these world generator types to generate: ") + kinds); }
    }

    public static boolean rejects(IWorldGenerator generator, World world) {
        if (whitelist == null) { load(); }
        if (generator == null || world == null) { return false; }
        String owner = owner(generator.getClass());
        if (owner.equals("resourcedatapackloader")) { return false; }
        if (ContentVoidWorld.appliesTo(world)) {
            count(owner, generator, kind(generator.getClass(), owner));
            return true;
        }
        if (!inScope(world)) { return false; }
        if (!named.isEmpty() && (named.contains(owner) || matches(generator.getClass().getName().toLowerCase(Locale.ROOT)))) {
            count(owner, generator, kind(generator.getClass(), owner));
            return true;
        }
        String kind = kind(generator.getClass(), owner);
        if (kindBlocked(kind)) {
            count(owner, generator, kind);
            return true;
        }
        if (!BLOCKING.get(() -> ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators)) || whitelist.contains(owner)) { return false; }
        count(owner, generator, kind);
        return true;
    }

    public static String kind(Class<?> type, String owner) {
        String cached = KINDS.get(type);
        if (cached != null) { return cached; }
        String kind = resolveKind(type, owner);
        KINDS.put(type, kind);
        return kind;
    }

    public static Map<String, Integer> blocked() { return BLOCKED.map(); }

    private static boolean typesAreBlacklist() { return ContentControl.flag(ContentControl.GENERATORS, "generatorTypesAreBlacklist", Config.worldgen.generatorTypesAreBlacklist); }

    private static boolean kindBlocked(String kind) {
        if (kinds.isEmpty()) { return false; }
        return kinds.contains(kind) == typesAreBlacklist();
    }

    private static String resolveKind(Class<?> type, String owner) {
        String simple = type.getSimpleName().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : mapped.entrySet()) {
            if (owner.equals(entry.getKey()) || simple.contains(entry.getKey())) { return entry.getValue(); }
        }
        for (Map.Entry<String, List<String>> group : PATTERNS.entrySet()) {
            for (String pattern : group.getValue()) {
                if (simple.contains(pattern)) { return group.getKey(); }
            }
        }
        return UNKNOWN;
    }

    private static boolean matches(String type) {
        for (String entry : named) {
            if (type.contains(entry)) { return true; }
        }
        return false;
    }

    private static boolean inScope(World world) {
        if (dimensions.isEmpty()) { return true; }
        return dimensions.contains(world.provider.getDimension()) != ContentControl.flag(ContentControl.GENERATORS, "blockGeneratorDimensionsAreBlacklist", Config.worldgen.blockGeneratorDimensionsAreBlacklist);
    }

    private static void count(String owner, IWorldGenerator generator, String kind) {
        BLOCKED.count(owner + " " + kind);
        if (!ContentControl.flag(ContentControl.GENERATORS, "logBlockedGenerators", Config.worldgen.logBlockedGenerators)) { return; }
        if (REPORTED.add(owner + "/" + generator.getClass().getName())) {
            ContentLog.LOGGER.info("Blocking {} world generator {} from {}. Use /rdplserver generators to see running totals", kind, generator.getClass().getSimpleName(), owner);
        }
    }

    public static String owner(IWorldGenerator generator) { return owner(generator.getClass()); }

    public static String owner(Class<?> type) {
        String cached = OWNERS.get(type);
        if (cached != null) { return cached; }
        String owner = resolve(type);
        OWNERS.put(type, owner);
        return owner;
    }

    private static String resolve(Class<?> type) {
        if (type.getName().startsWith("net.minecraft.")) { return "minecraft"; }
        CodeSource source = type.getProtectionDomain() == null ? null : type.getProtectionDomain().getCodeSource();
        if (source == null || source.getLocation() == null) { return UNKNOWN; }
        String location = source.getLocation().getPath();
        for (ModContainer container : Loader.instance().getModList()) {
            File file = container.getSource();
            if (file == null) { continue; }
            if (location.endsWith(file.getName()) || location.contains(file.getName())) { return container.getModId().toLowerCase(Locale.ROOT); }
        }
        return UNKNOWN;
    }
}
