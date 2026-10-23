package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class LangGates {
    public static final String GATE_FILE = "gate";
    private static final String LANG = "lang/";
    private static volatile Map<Boolean, Map<String, Map<String, List<Part>>>> index = Map.of();
    private static volatile Set<String> used = Set.of();

    private LangGates() {}

    public record Part(String key, List<String> requires, JsonObject entries) {}

    private static final class Builder {
        private final int order;
        private final boolean overriding;
        private final String namespace;
        private final String name;
        @Nullable private List<String> requires;
        private boolean broken;
        private final Map<String, JsonObject> files = new LinkedHashMap<>();

        Builder(int order, boolean overriding, String namespace, String name) {
            this.order = order;
            this.overriding = overriding;
            this.namespace = namespace;
            this.name = name;
        }
    }

    public static void build(List<RDPLPack> packs) {
        Map<String, Builder> found = new LinkedHashMap<>();
        for (int i = 0; i < packs.size(); i++) {
            RDPLPack pack = packs.get(i);
            int order = i;
            pack.forEach(PackType.CLIENT_RESOURCES, PackManager.LANGGATES, PackManager.JSON, (namespace, id, contents) -> {
                int slash = id.lastIndexOf('/');
                if (slash <= 0) { return; }
                String name = id.substring(0, slash);
                String file = id.substring(slash + 1);
                JsonObject parsed = parse(contents);
                String groupKey = order + "|" + namespace + "|" + name.toLowerCase(Locale.ROOT);
                if (parsed == null) {
                    ContentLog.LOGGER.warn("Pack '{}': langgates/{}/{}.json is not a JSON object, so it is skipped", pack.getName(), name, file);
                    if (GATE_FILE.equalsIgnoreCase(file)) { found.computeIfAbsent(groupKey, _ -> new Builder(order, pack.isOverriding(), namespace, name)).broken = true; }
                    return;
                }
                Builder builder = found.computeIfAbsent(groupKey, _ -> new Builder(order, pack.isOverriding(), namespace, name));
                if (GATE_FILE.equalsIgnoreCase(file)) { builder.requires = List.copyOf(Json.strings(parsed, PackRequirements.REQUIRES)); }
                else { builder.files.put(file.toLowerCase(Locale.ROOT), parsed); }
            });
        }
        List<Builder> ordered = new ArrayList<>(found.values());
        ordered.sort(Comparator.comparingInt((Builder b) -> b.order).thenComparing(b -> b.name, String.CASE_INSENSITIVE_ORDER));
        Map<Boolean, Map<String, Map<String, List<Part>>>> built = new HashMap<>();
        Set<String> options = new HashSet<>();
        for (Builder builder : ordered) {
            if (builder.requires == null) {
                if (!builder.broken) { ContentLog.LOGGER.warn("Skipping langgates/{} in namespace '{}': it has no gate.json", builder.name, builder.namespace); }
                continue;
            }
            String key = builder.namespace + ":" + PackManager.LANGGATES + "/" + builder.name.toLowerCase(Locale.ROOT);
            for (String asked : builder.requires) {
                if (asked.startsWith(PackRequirements.CONFIG_GATE)) { options.add(asked.substring(PackRequirements.CONFIG_GATE.length())); }
            }
            for (Map.Entry<String, JsonObject> file : builder.files.entrySet()) {
                built.computeIfAbsent(builder.overriding, _ -> new HashMap<>())
                        .computeIfAbsent(builder.namespace, _ -> new HashMap<>())
                        .computeIfAbsent(LANG + file.getKey() + PackManager.JSON_SUFFIX, _ -> new ArrayList<>())
                        .add(new Part(key, builder.requires, file.getValue()));
            }
        }
        used = options;
        index = built;
    }

    public static void clear() {
        index = Map.of();
        used = Set.of();
    }

    public static List<Part> active(PackType type, String namespace, String path, boolean overriding) {
        if (type != PackType.CLIENT_RESOURCES || !path.startsWith(LANG)) { return List.of(); }
        Map<String, Map<String, List<Part>>> tier = index.get(overriding);
        Map<String, List<Part>> paths = tier == null ? null : tier.get(namespace);
        List<Part> parts = paths == null ? null : paths.get(path.toLowerCase(Locale.ROOT));
        if (parts == null) { return List.of(); }
        List<Part> holding = new ArrayList<>();
        for (Part part : parts) {
            if (ContentRegistry.holds(part.requires(), part.key())) { holding.add(part); }
        }
        return holding;
    }

    public static boolean dependsOn(String file, String name) { return used.contains(name) || used.contains(file + ":" + name); }

    public static byte[] merge(@Nullable byte[] base, List<Part> parts) {
        JsonObject out = new JsonObject();
        if (base != null) {
            JsonObject parsed = parse(new String(base, StandardCharsets.UTF_8));
            if (parsed == null) { return base; }
            out = parsed;
        }
        for (Part part : parts) {
            for (Map.Entry<String, JsonElement> entry : part.entries().entrySet()) { out.add(entry.getKey(), entry.getValue()); }
        }
        return Json.GSON.toJson(out).getBytes(StandardCharsets.UTF_8);
    }

    @Nullable private static JsonObject parse(String contents) {
        try { return Json.GSON.fromJson(contents, JsonObject.class); }
        catch (RuntimeException malformed) { return null; }
    }
}
