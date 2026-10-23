package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class LangGates {
    private static final String PREFIX = PackManager.LANGGATES + "/";
    private static final String GATE_FILE = "gate.json";
    private static final String LANG_SUFFIX = ".lang";
    private static final String ESCAPES = "PARSE_ESCAPES";
    private static final Pattern FORMAT = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static final Pattern LINES = Pattern.compile("\r\n|\r|\n");
    private static volatile Map<String, Map<String, List<Gate>>> normal = Collections.emptyMap();
    private static volatile Map<String, Map<String, List<Gate>>> overriding = Collections.emptyMap();

    private LangGates() {}

    static void build() {
        Map<String, Map<String, List<Gate>>> builtNormal = new HashMap<>();
        Map<String, Map<String, List<Gate>>> builtOverriding = new HashMap<>();
        List<RDPLPack> packs = PackManager.get().getPacks();
        for (int order = 0; order < packs.size(); order++) {
            RDPLPack pack = packs.get(order);
            Map<String, Map<String, List<Gate>>> target = pack.isOverriding() ? builtOverriding : builtNormal;
            for (String namespace : pack.getNamespaces()) { gather(pack, order, namespace, target); }
        }
        Comparator<Gate> sequence = Comparator.comparingInt((Gate gate) -> gate.order).thenComparing(gate -> gate.name);
        for (Map<String, Map<String, List<Gate>>> tier : Arrays.asList(builtNormal, builtOverriding)) {
            for (Map<String, List<Gate>> paths : tier.values()) {
                for (List<Gate> gates : paths.values()) { gates.sort(sequence); }
            }
        }
        normal = builtNormal;
        overriding = builtOverriding;
    }

    static void clear() {
        normal = Collections.emptyMap();
        overriding = Collections.emptyMap();
    }

    private static void gather(RDPLPack pack, int order, String namespace, Map<String, Map<String, List<Gate>>> target) {
        Map<String, Group> groups = new LinkedHashMap<>();
        for (String path : pack.getPaths(namespace)) {
            if (!path.regionMatches(true, 0, PREFIX, 0, PREFIX.length())) { continue; }
            int slash = path.lastIndexOf('/');
            if (slash <= PREFIX.length()) { continue; }
            String name = path.substring(PREFIX.length(), slash).toLowerCase(Locale.ROOT);
            String file = path.substring(slash + 1).toLowerCase(Locale.ROOT);
            if (file.equals(GATE_FILE)) { groups.computeIfAbsent(name, k -> new Group()).config = path; }
            else if (file.endsWith(LANG_SUFFIX)) { groups.computeIfAbsent(name, k -> new Group()).langs.put("lang/" + file, path); }
        }
        for (Map.Entry<String, Group> entry : groups.entrySet()) {
            Group group = entry.getValue();
            if (group.langs.isEmpty()) { continue; }
            if (group.config == null) {
                ContentLog.LOGGER.warn("Pack '{}': {}:{}{} has language files but no {}, so it is ignored", pack.getName(), namespace, PREFIX, entry.getKey(), GATE_FILE);
                continue;
            }
            List<String> requires = requires(pack, namespace, group.config);
            if (requires == null) { continue; }
            for (Map.Entry<String, String> lang : group.langs.entrySet()) {
                Gate gate = new Gate(pack, namespace, entry.getKey(), order, lang.getValue(), requires);
                target.computeIfAbsent(namespace, k -> new HashMap<>()).computeIfAbsent(lang.getKey(), k -> new ArrayList<>()).add(gate);
            }
        }
    }

    @Nullable private static List<String> requires(RDPLPack pack, String namespace, String config) {
        try {
            JsonObject json = Json.GSON.fromJson(pack.read(namespace, config), JsonObject.class);
            return json == null ? Collections.emptyList() : Json.strings(json, "requires");
        }
        catch (IOException | RuntimeException ex) {
            ContentLog.LOGGER.warn("Pack '{}': could not read {}:{}, so that language gate is ignored: {}", pack.getName(), namespace, config, ex.getMessage());
            return null;
        }
    }

    private static List<Gate> gatesAt(String namespace, String path, boolean tierOverriding) {
        Map<String, List<Gate>> paths = (tierOverriding ? overriding : normal).get(namespace);
        List<Gate> gates = paths == null ? null : paths.get(path.toLowerCase(Locale.ROOT));
        return gates == null ? Collections.emptyList() : gates;
    }

    private static boolean holds(Gate gate) { return ContentRegistry.holds(gate.requires, new ResourceLocation(gate.namespace, PREFIX + gate.name)); }

    static boolean active(String namespace, String path, boolean tierOverriding) {
        for (Gate gate : gatesAt(namespace, path, tierOverriding)) {
            if (holds(gate)) { return true; }
        }
        return false;
    }

    public static boolean dependsOn(String file, String name) {
        for (Map<String, Map<String, List<Gate>>> tier : Arrays.asList(normal, overriding)) {
            for (Map<String, List<Gate>> paths : tier.values()) {
                for (List<Gate> gates : paths.values()) {
                    for (Gate gate : gates) {
                        for (String asked : gate.requires) {
                            if (!asked.startsWith("config:")) { continue; }
                            String option = asked.substring("config:".length());
                            if (option.equals(name) || option.equals(file + ":" + name)) { return true; }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nullable static InputStream merged(String namespace, String path, boolean tierOverriding, @Nullable byte[] base) {
        List<String> parts = new ArrayList<>();
        if (base != null) { parts.add(new String(base, StandardCharsets.UTF_8)); }
        int own = parts.size();
        for (Gate gate : gatesAt(namespace, path, tierOverriding)) {
            if (!holds(gate)) { continue; }
            try (InputStream stream = gate.pack.open(namespace, gate.path)) { parts.add(new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8)); }
            catch (IOException ex) { ContentLog.LOGGER.error("Pack '{}': could not read {}:{}", gate.pack.getName(), namespace, gate.path, ex); }
        }
        if (parts.size() == own) { return null; }
        String combined = combine(parts);
        return combined == null ? null : new ByteArrayInputStream(combined.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable private static String combine(List<String> parts) {
        boolean enhanced = false;
        for (String part : parts) { enhanced |= escapes(part); }
        if (!enhanced) {
            StringBuilder out = new StringBuilder();
            for (String part : parts) {
                if (out.length() > 0) { out.append('\n'); }
                out.append(part);
            }
            return out.toString();
        }
        Properties table = new Properties();
        for (String part : parts) {
            if (escapes(part)) {
                Properties read = new Properties();
                try { read.load(new InputStreamReader(new ByteArrayInputStream(part.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)); }
                catch (IOException | IllegalArgumentException ex) {
                    ContentLog.LOGGER.warn("A language file marked {} could not be read, so it is skipped: {}", ESCAPES, ex.getMessage());
                    continue;
                }
                for (String key : read.stringPropertyNames()) { table.setProperty(key, read.getProperty(key)); }
                continue;
            }
            for (String line : LINES.split(part)) {
                int equals = line.indexOf('=');
                if (line.isEmpty() || line.charAt(0) == '#' || equals < 0) { continue; }
                table.setProperty(line.substring(0, equals), FORMAT.matcher(line.substring(equals + 1)).replaceAll("%$1s"));
            }
        }
        StringWriter out = new StringWriter();
        out.write("#" + ESCAPES + "\n");
        try { table.store(out, null); }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not write the merged language file", ex);
            return null;
        }
        return out.toString();
    }

    private static boolean escapes(String part) {
        for (String line : LINES.split(part)) {
            if (!line.isEmpty() && line.charAt(0) == '#' && line.substring(1).trim().equals(ESCAPES)) { return true; }
        }
        return false;
    }

    private static final class Group {
        @Nullable String config;
        final Map<String, String> langs = new LinkedHashMap<>();
    }

    private static final class Gate {
        final RDPLPack pack;
        final String namespace;
        final String name;
        final int order;
        final String path;
        final List<String> requires;

        Gate(RDPLPack pack, String namespace, String name, int order, String path, List<String> requires) {
            this.pack = pack;
            this.namespace = namespace;
            this.name = name;
            this.order = order;
            this.path = path;
            this.requires = requires;
        }
    }
}
