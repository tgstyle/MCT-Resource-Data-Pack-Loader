package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.packs.PackType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

public final class LangGates {
    private static final String GATE_FILE = "gate";
    private static final String LANG_PREFIX = "lang/";
    private static final Index EMPTY = new Index(Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet());
    private static volatile Index index = EMPTY;

    private LangGates() {}

    public static void build() {
        Map<String, Map<String, List<Gate>>> normal = new HashMap<>();
        Map<String, Map<String, List<Gate>>> overriding = new HashMap<>();
        Set<String> options = new HashSet<>();
        for (RDPLPack pack : PackManager.get().getPacks()) { gather(pack, pack.isOverriding() ? overriding : normal, options); }
        index = normal.isEmpty() && overriding.isEmpty() ? EMPTY : new Index(normal, overriding, options);
    }

    public static void clear() { index = EMPTY; }

    private static void gather(RDPLPack pack, Map<String, Map<String, List<Gate>>> tier, Set<String> options) {
        Map<String, Map<String, Pending>> found = new TreeMap<>();
        pack.forEach(PackType.CLIENT_RESOURCES, PackManager.LANGGATES, PackManager.JSON, (namespace, id, contents) -> {
            int slash = id.lastIndexOf('/');
            if (slash <= 0) { return; }
            Pending pending = found.computeIfAbsent(namespace, k -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)).computeIfAbsent(id.substring(0, slash), k -> new Pending());
            String file = id.substring(slash + 1);
            if (GATE_FILE.equalsIgnoreCase(file)) { pending.gate = contents; }
            else { pending.codes.put(file, contents); }
        });
        for (Map.Entry<String, Map<String, Pending>> namespace : found.entrySet()) {
            for (Map.Entry<String, Pending> each : namespace.getValue().entrySet()) { register(pack, namespace.getKey(), each.getKey(), each.getValue(), tier, options); }
        }
    }

    private static void register(RDPLPack pack, String namespace, String name, Pending pending, Map<String, Map<String, List<Gate>>> tier, Set<String> options) {
        String label = namespace + ":" + PackManager.LANGGATES + "/" + name;
        if (pending.gate == null) {
            ContentLog.LOGGER.warn("Pack '{}': the language gate '{}/{}' is skipped, it has no gate.json", pack.getName(), namespace, name);
            return;
        }
        List<String> requires;
        try { requires = requires(JsonParser.parseString(pending.gate).getAsJsonObject()); }
        catch (RuntimeException malformed) {
            ContentLog.LOGGER.warn("Pack '{}': the language gate '{}/{}' is skipped, its gate.json is not a JSON object with a 'requires' list", pack.getName(), namespace, name);
            return;
        }
        for (String asked : requires) {
            if (asked.startsWith(PackRequirements.CONFIG_GATE)) { options.add(asked.substring(PackRequirements.CONFIG_GATE.length())); }
        }
        Map<String, List<Gate>> paths = tier.computeIfAbsent(namespace, k -> new HashMap<>());
        for (Map.Entry<String, String> code : pending.codes.entrySet()) {
            JsonObject entries = entriesOf(pack, namespace, name, code.getKey(), code.getValue());
            if (entries != null) { paths.computeIfAbsent(LANG_PREFIX + code.getKey().toLowerCase(Locale.ROOT) + PackManager.JSON_SUFFIX, k -> new ArrayList<>()).add(new Gate(label, requires, entries)); }
        }
    }

    private static List<String> requires(JsonObject gate) {
        try { return Json.strings(gate, "requires"); }
        catch (RuntimeException notAList) { return Collections.emptyList(); }
    }

    @Nullable private static JsonObject entriesOf(RDPLPack pack, String namespace, String name, String code, String contents) {
        try { return JsonParser.parseString(contents).getAsJsonObject(); }
        catch (RuntimeException malformed) {
            ContentLog.LOGGER.warn("Pack '{}': the language file '{}/{}/{}' is skipped, it is not a JSON object", pack.getName(), namespace, name, code);
            return null;
        }
    }

    public static List<Gate> active(boolean overriding, String namespace, String path) {
        Index held = index;
        if (held == EMPTY || !path.startsWith(LANG_PREFIX) || !path.endsWith(PackManager.JSON_SUFFIX)) { return Collections.emptyList(); }
        Map<String, List<Gate>> paths = (overriding ? held.overriding() : held.normal()).get(namespace);
        List<Gate> gates = paths == null ? null : paths.get(path.toLowerCase(Locale.ROOT));
        if (gates == null) { return Collections.emptyList(); }
        List<Gate> active = new ArrayList<>(gates.size());
        for (Gate gate : gates) {
            if (ContentRegistry.holds(gate.requires(), gate.label())) { active.add(gate); }
        }
        return active;
    }

    public static boolean dependsOn(String fileKey, String name) {
        Set<String> options = index.options();
        return options.contains(name) || options.contains(fileKey + ":" + name);
    }

    public static byte[] merge(@Nullable byte[] base, List<Gate> gates) {
        JsonObject result;
        if (base == null) { result = new JsonObject(); }
        else {
            try { result = JsonParser.parseString(new String(base, StandardCharsets.UTF_8)).getAsJsonObject(); }
            catch (RuntimeException malformed) { return base; }
        }
        for (Gate gate : gates) {
            for (Map.Entry<String, JsonElement> entry : gate.entries().entrySet()) { result.add(entry.getKey(), entry.getValue().deepCopy()); }
        }
        return Json.GSON.toJson(result).getBytes(StandardCharsets.UTF_8);
    }

    public record Gate(String label, List<String> requires, JsonObject entries) {}

    private record Index(Map<String, Map<String, List<Gate>>> normal, Map<String, Map<String, List<Gate>>> overriding, Set<String> options) {}

    private static final class Pending {
        @Nullable private String gate;
        private final Map<String, String> codes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }
}
