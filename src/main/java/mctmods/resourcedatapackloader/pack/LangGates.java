package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class LangGates {
    private static final String CONFIG_FILE = "gate";
    private static final String LANG_FOLDER = "lang/";
    private static final Gson WRITER = new GsonBuilder().disableHtmlEscaping().create();
    private static final Set<String> TOLD = ConcurrentHashMap.newKeySet();
    private static volatile Map<String, Map<String, List<Gate>>> normal = Map.of();
    private static volatile Map<String, Map<String, List<Gate>>> overriding = Map.of();

    private LangGates() {}

    public record Gate(String label, List<String> requires, String contents) {}

    private static final class Group {
        private final String namespace;
        private final String name;
        private final Map<String, String> codes = new LinkedHashMap<>();
        @Nullable private String config;

        Group(String namespace, String name) {
            this.namespace = namespace;
            this.name = name;
        }
    }

    public static void build(List<RDPLPack> packs) {
        Map<String, Map<String, List<Gate>>> low = new HashMap<>();
        Map<String, Map<String, List<Gate>>> high = new HashMap<>();
        for (RDPLPack pack : packs) {
            Map<String, Group> groups = new LinkedHashMap<>();
            pack.forEach(PackType.CLIENT_RESOURCES, PackManager.LANGGATES, PackManager.JSON, (namespace, id, contents) -> {
                int slash = id.lastIndexOf('/');
                if (slash <= 0) { return; }
                String name = id.substring(0, slash);
                String file = id.substring(slash + 1);
                Group group = groups.computeIfAbsent(namespace + ":" + name.toLowerCase(Locale.ROOT), k -> new Group(namespace, name));
                if (CONFIG_FILE.equalsIgnoreCase(file)) { group.config = contents; }
                else { group.codes.put(file.toLowerCase(Locale.ROOT), contents); }
            });
            List<Group> ordered = new ArrayList<>(groups.values());
            ordered.sort(Comparator.comparing(group -> group.name.toLowerCase(Locale.ROOT)));
            Map<String, Map<String, List<Gate>>> target = pack.isOverriding() ? high : low;
            for (Group group : ordered) { add(pack, group, target); }
        }
        normal = low;
        overriding = high;
        TOLD.clear();
    }

    private static void add(RDPLPack pack, Group group, Map<String, Map<String, List<Gate>>> target) {
        String label = group.namespace + ":" + PackManager.LANGGATES + "/" + group.name;
        List<String> requires = requirements(pack, group, label);
        if (requires == null) { return; }
        for (Map.Entry<String, String> code : group.codes.entrySet()) {
            Gate gate = new Gate(label, requires, code.getValue());
            target.computeIfAbsent(group.namespace, k -> new HashMap<>()).computeIfAbsent(LANG_FOLDER + code.getKey() + PackManager.JSON_SUFFIX, k -> new ArrayList<>()).add(gate);
        }
    }

    @Nullable private static List<String> requirements(RDPLPack pack, Group group, String label) {
        if (group.config == null) {
            ContentLog.LOGGER.warn("Pack '{}': {} has language files but no {}{}, so it is ignored", pack.getName(), label, CONFIG_FILE, PackManager.JSON_SUFFIX);
            return null;
        }
        JsonObject json = parse(group.config, pack.getName() + " " + label + "/" + CONFIG_FILE);
        if (json == null) { return null; }
        return Json.within("language gate", label, () -> List.copyOf(Json.strings(json, PackRequirements.REQUIRES)));
    }

    public static void clear() {
        normal = Map.of();
        overriding = Map.of();
        TOLD.clear();
    }

    public static List<Gate> active(boolean tier, String namespace, String path) {
        Map<String, List<Gate>> paths = (tier ? overriding : normal).get(namespace);
        List<Gate> gates = paths == null ? null : paths.get(path);
        if (gates == null) { return List.of(); }
        List<Gate> held = new ArrayList<>();
        for (Gate gate : gates) {
            if (ContentRegistry.holds(gate.requires(), gate.label())) { held.add(gate); }
        }
        return held;
    }

    public static boolean dependsOn(String fileKey, String option) {
        String bare = PackRequirements.CONFIG_GATE + option;
        String full = PackRequirements.CONFIG_GATE + fileKey + ":" + option;
        return reads(normal, bare, full) || reads(overriding, bare, full);
    }

    private static boolean reads(Map<String, Map<String, List<Gate>>> tier, String bare, String full) {
        for (Map<String, List<Gate>> paths : tier.values()) {
            for (List<Gate> gates : paths.values()) {
                for (Gate gate : gates) {
                    if (gate.requires().contains(bare) || gate.requires().contains(full)) { return true; }
                }
            }
        }
        return false;
    }

    @Nullable public static byte[] merge(String namespace, String path, @Nullable byte[] base, List<Gate> active) {
        JsonObject result = new JsonObject();
        if (base != null) {
            JsonObject parsed = parse(new String(base, StandardCharsets.UTF_8), namespace + ":" + path + " from the packs");
            if (parsed == null) { return null; }
            result = parsed;
        }
        for (Gate gate : active) {
            JsonObject parsed = parse(gate.contents(), gate.label() + " for " + path);
            if (parsed == null) { continue; }
            for (Map.Entry<String, JsonElement> held : parsed.entrySet()) { result.add(held.getKey(), held.getValue()); }
        }
        return WRITER.toJson(result).getBytes(StandardCharsets.UTF_8);
    }

    @Nullable private static JsonObject parse(String text, String label) {
        try {
            JsonObject json = Json.GSON.fromJson(text, JsonObject.class);
            if (json != null) { return json; }
        }
        catch (RuntimeException ignored) { }
        ContentLog.LOGGER.errorOnce(TOLD, label, "{} is not a JSON object, so it is left out of the language merge", label);
        return null;
    }
}
