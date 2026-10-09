package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.DimensionValues;
import mctmods.resourcedatapackloader.util.ScopedFeatures;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.level.LevelEvent;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentGeneratorControl {
    public static final String UNKNOWN = "unknown";
    private static final Map<String, List<String>> PATTERNS = new LinkedHashMap<>();
    private static final Blocked BLOCKED = new Blocked();
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
    private static final ScopedFeatures<Refused> SCOPED = new ScopedFeatures<>();
    private static volatile boolean logging;
    private static Set<String> whitelist = Set.of();
    private static Set<String> named = Set.of();
    private static Set<String> kinds = Set.of();
    private static Map<String, String> mapped = Map.of();

    static {
        PATTERNS.put("structures", List.of("structure", "dungeon", "ruin", "village", "island", "temple", "shrine", "tower", "ship", "pyramid", "obelisk", "monolith", "camp", "hut", "house", "spawner", "nest", "well"));
        PATTERNS.put("ores", List.of("ore", "mineral", "vein", "gem", "yellorite", "clathrate"));
        PATTERNS.put("flora", List.of("tree", "sapling", "plant", "flower", "bush", "shrub", "grass", "vine", "mushroom", "fungus", "cane", "reed", "crop", "berry", "leaf"));
        PATTERNS.put("lakes", List.of("lake", "pool", "pond", "spring", "geyser"));
        PATTERNS.put("terrain", List.of("stone", "rock", "boulder", "geode", "crystal", "cave", "basalt", "limestone", "marble", "sand", "clay", "gravel", "dirt", "deposit", "cluster"));
    }

    private ContentGeneratorControl() {}

    private static List<String> blockedGenerators() { return ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators()); }

    private static List<String> generatorTypes() { return ContentControl.list(ContentControl.GENERATORS, "generatorTypes", Config.worldgen.generatorTypes()); }

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.GENERATORS)) { return false; }
        return blocking() || !blockedGenerators().isEmpty()
                || !generatorTypes().isEmpty();
    }

    public static void load() {
        whitelist = Settings.lower(ContentControl.list(ContentControl.GENERATORS, "generatorWhitelist", Config.worldgen.generatorWhitelist()));
        named = Settings.lower(blockedGenerators());
        Set<String> wanted = new LinkedHashSet<>();
        for (String name : generatorTypes()) {
            String kind = Settings.lower(name);
            if (kind.isEmpty()) { continue; }
            if (invalid(kind)) {
                ContentLog.LOGGER.error("generatorTypes names '{}', which is not one of {} or {}, ignoring it", kind, PATTERNS.keySet(), UNKNOWN);
                continue;
            }
            wanted.add(kind);
        }
        kinds = wanted;
        Map<String, String> patterns = new LinkedHashMap<>();
        for (String entry : ContentControl.list(ContentControl.GENERATORS, "generatorTypeMap", Config.worldgen.generatorTypeMap())) {
            String[] parts = Settings.pair(entry, "generatorTypeMap", "pattern=type");
            if (parts == null) { continue; }
            String pattern = parts[0].toLowerCase(Locale.ROOT);
            String kind = parts[1].toLowerCase(Locale.ROOT);
            if (invalid(kind)) {
                ContentLog.LOGGER.error("generatorTypeMap entry '{}' names a type that is not one of {} or {}, ignoring it", entry, PATTERNS.keySet(), UNKNOWN);
                continue;
            }
            patterns.put(pattern, kind);
        }
        mapped = patterns;
        BLOCKED.clear();
        REPORTED.clear();
        if (!enabled()) { return; }
        if (blocking()) { Summary.info("generators", "Blocking third party world generation except from " + whitelist); }
        else if (!named.isEmpty()) { Summary.info("generators", "Blocking world generation from " + named); }
        if (!kinds.isEmpty()) { Summary.info("generators.types", (typesAreBlacklist() ? "Blocking these world generator types outright: " : "Allowing only these world generator types to generate: ") + kinds); }
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        boolean active = enabled() && inScope(level.dimension().location().toString());
        boolean byMod = blocking();
        logging = ContentControl.flag(ContentControl.GENERATORS, "logBlockedGenerators", Config.worldgen.logBlockedGenerators());
        int count = SCOPED.load(level, active, (id, feature) -> refused(id, byMod));
        if (active) { ContentLog.LOGGER.debug("Generator blocking refuses {} placed feature(s) in {}", count, level.dimension().location()); }
    }

    public static boolean refuses(ChunkGenerator generator, PlacedFeature feature) {
        Refused refused = SCOPED.get(generator, feature);
        if (refused == null) { return false; }
        count(refused);
        return true;
    }

    public static Map<String, Integer> blocked() { return BLOCKED.map(); }

    @Nullable private static Refused refused(ResourceLocation id, boolean byMod) {
        String owner = id.getNamespace().toLowerCase(Locale.ROOT);
        if (owner.equals("minecraft") || owner.equals(ResourceDataPackLoader.MOD_ID) || ContentWorldgen.entry(id) != null) { return null; }
        String path = id.getPath().toLowerCase(Locale.ROOT);
        String kind = kind(owner, path);
        if (!named.isEmpty() && (named.contains(owner) || matches(id.toString().toLowerCase(Locale.ROOT)))) { return new Refused(owner, kind, path); }
        if (kindBlocked(kind)) { return new Refused(owner, kind, path); }
        if (!byMod || whitelist.contains(owner)) { return null; }
        return new Refused(owner, kind, path);
    }

    private static boolean blocking() { return ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators()); }

    private static boolean typesAreBlacklist() { return ContentControl.flag(ContentControl.GENERATORS, "generatorTypesAreBlacklist", Config.worldgen.generatorTypesAreBlacklist()); }

    private static boolean kindBlocked(String kind) {
        if (kinds.isEmpty()) { return false; }
        return kinds.contains(kind) == typesAreBlacklist();
    }

    private static String kind(String owner, String path) {
        for (Map.Entry<String, String> entry : mapped.entrySet()) {
            if (owner.equals(entry.getKey()) || path.contains(entry.getKey())) { return entry.getValue(); }
        }
        for (Map.Entry<String, List<String>> group : PATTERNS.entrySet()) {
            for (String pattern : group.getValue()) {
                if (path.contains(pattern)) { return group.getKey(); }
            }
        }
        return UNKNOWN;
    }

    private static boolean invalid(String kind) { return !PATTERNS.containsKey(kind) && !kind.equals(UNKNOWN); }

    private static boolean matches(String id) {
        for (String entry : named) {
            if (id.contains(entry)) { return true; }
        }
        return false;
    }

    private static boolean inScope(String dimension) {
        List<String> dimensions = ContentControl.list(ContentControl.GENERATORS, "blockGeneratorDimensions", Config.worldgen.blockGeneratorDimensions());
        return DimensionValues.listed(dimension, dimensions, ContentControl.flag(ContentControl.GENERATORS, "blockGeneratorDimensionsAreBlacklist", Config.worldgen.blockGeneratorDimensionsAreBlacklist()));
    }

    private static void count(Refused refused) {
        BLOCKED.count(refused.owner() + " " + refused.kind());
        if (!logging) { return; }
        if (REPORTED.add(refused.owner() + "/" + refused.name())) { ContentLog.LOGGER.info("Blocking {} world generator {} from {}. Use /rdplserver generators to see running totals", refused.kind(), refused.name(), refused.owner()); }
    }

    private record Refused(String owner, String kind, String name) {}
}
