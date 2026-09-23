package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.level.LevelEvent;
import java.util.IdentityHashMap;
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
    private static final Map<String, Integer> BLOCKED = new LinkedHashMap<>();
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
    private static volatile Map<ChunkGenerator, Map<PlacedFeature, Refused>> scoped = Map.of();
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

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.GENERATORS)) { return false; }
        return blocking() || !ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators()).isEmpty()
                || !ContentControl.list(ContentControl.GENERATORS, "generatorTypes", Config.worldgen.generatorTypes()).isEmpty();
    }

    public static void load() {
        whitelist = lower(ContentControl.list(ContentControl.GENERATORS, "generatorWhitelist", Config.worldgen.generatorWhitelist()));
        named = lower(ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators()));
        Set<String> wanted = new LinkedHashSet<>();
        for (String name : ContentControl.list(ContentControl.GENERATORS, "generatorTypes", Config.worldgen.generatorTypes())) {
            String kind = name.trim().toLowerCase(Locale.ROOT);
            if (kind.isEmpty()) { continue; }
            if (!PATTERNS.containsKey(kind) && !kind.equals(UNKNOWN)) {
                ContentLog.LOGGER.error("generatorTypes names '{}', which is not one of {} or {}, ignoring it", kind, PATTERNS.keySet(), UNKNOWN);
                continue;
            }
            wanted.add(kind);
        }
        kinds = wanted;
        Map<String, String> patterns = new LinkedHashMap<>();
        for (String entry : ContentControl.list(ContentControl.GENERATORS, "generatorTypeMap", Config.worldgen.generatorTypeMap())) {
            String[] parts = pair(entry);
            if (parts == null) { continue; }
            String pattern = parts[0].toLowerCase(Locale.ROOT);
            String kind = parts[1].toLowerCase(Locale.ROOT);
            if (!PATTERNS.containsKey(kind) && !kind.equals(UNKNOWN)) {
                ContentLog.LOGGER.error("generatorTypeMap entry '{}' names a type that is not one of {} or {}, ignoring it", entry, PATTERNS.keySet(), UNKNOWN);
                continue;
            }
            patterns.put(pattern, kind);
        }
        mapped = patterns;
        synchronized (BLOCKED) { BLOCKED.clear(); }
        REPORTED.clear();
        if (!enabled()) { return; }
        if (blocking()) { Summary.info("generators", "Blocking third party world generation except from " + whitelist); }
        else if (!named.isEmpty()) { Summary.info("generators", "Blocking world generation from " + named); }
        if (!kinds.isEmpty()) { Summary.info("generators.types", (typesAreBlacklist() ? "Blocking these world generator types outright: " : "Allowing only these world generator types to generate: ") + kinds); }
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Map<ChunkGenerator, Map<PlacedFeature, Refused>> next = level.dimension() == Level.OVERWORLD ? new IdentityHashMap<>() : new IdentityHashMap<>(scoped);
        if (enabled() && inScope(level.dimension().location().toString())) {
            boolean byMod = blocking();
            Map<PlacedFeature, Refused> features = new IdentityHashMap<>();
            for (Map.Entry<ResourceKey<PlacedFeature>, PlacedFeature> entry : level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).entrySet()) {
                Refused refused = refused(entry.getKey().location(), byMod);
                if (refused != null) { features.put(entry.getValue(), refused); }
            }
            next.put(level.getChunkSource().getGenerator(), features);
            ContentLog.LOGGER.debug("Generator blocking refuses {} placed feature(s) in {}", features.size(), level.dimension().location());
        }
        logging = ContentControl.flag(ContentControl.GENERATORS, "logBlockedGenerators", Config.worldgen.logBlockedGenerators());
        scoped = next;
    }

    public static boolean refuses(ChunkGenerator generator, PlacedFeature feature) {
        Map<PlacedFeature, Refused> held = scoped.get(generator);
        Refused refused = held == null ? null : held.get(feature);
        if (refused == null) { return false; }
        count(refused);
        return true;
    }

    public static Map<String, Integer> blocked() {
        synchronized (BLOCKED) { return Map.copyOf(BLOCKED); }
    }

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

    private static boolean matches(String id) {
        for (String entry : named) {
            if (id.contains(entry)) { return true; }
        }
        return false;
    }

    private static boolean inScope(String dimension) {
        List<String> dimensions = ContentControl.list(ContentControl.GENERATORS, "blockGeneratorDimensions", Config.worldgen.blockGeneratorDimensions());
        if (dimensions.isEmpty()) { return true; }
        boolean listed = false;
        for (String wanted : dimensions) {
            if (ContentFormats.dimensionId(wanted).equals(dimension)) {
                listed = true;
                break;
            }
        }
        return listed != ContentControl.flag(ContentControl.GENERATORS, "blockGeneratorDimensionsAreBlacklist", Config.worldgen.blockGeneratorDimensionsAreBlacklist());
    }

    private static void count(Refused refused) {
        synchronized (BLOCKED) { BLOCKED.merge(refused.owner() + " " + refused.kind(), 1, Integer::sum); }
        if (!logging) { return; }
        if (REPORTED.add(refused.owner() + "/" + refused.name())) { ContentLog.LOGGER.info("Blocking {} world generator {} from {}. Use /rdplserver generators to see running totals", refused.kind(), refused.name(), refused.owner()); }
    }

    private static Set<String> lower(List<String> values) {
        Set<String> out = new LinkedHashSet<>();
        for (String value : values) {
            String trimmed = value.trim().toLowerCase(Locale.ROOT);
            if (!trimmed.isEmpty()) { out.add(trimmed); }
        }
        return out;
    }

    @Nullable private static String[] pair(String entry) {
        int split = entry.indexOf('=');
        String left = split < 0 ? "" : entry.substring(0, split).trim();
        String right = split < 0 ? "" : entry.substring(split + 1).trim();
        if (left.isEmpty() || right.isEmpty()) {
            ContentLog.LOGGER.error("generatorTypeMap entry '{}' is not written as pattern=type, ignoring it", entry);
            return null;
        }
        return new String[] { left, right };
    }

    private record Refused(String owner, String kind, String name) {}
}
