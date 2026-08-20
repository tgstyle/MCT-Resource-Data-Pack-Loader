package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class PackOptions {
    private static final Map<String, Map<String, Boolean>> VALUES = new LinkedHashMap<>();
    private static final Map<String, Map<String, Boolean>> LOADED = new LinkedHashMap<>();
    private static final Set<String> GATING = new LinkedHashSet<>();
    private static final List<String> WORLD_CHANGED = new ArrayList<>();
    private static final Map<String, Map<String, Boolean>> HIDDEN = new LinkedHashMap<>();
    private static final Map<String, Map<String, String>> ABOUT = new LinkedHashMap<>();
    private static Path HOME;

    private PackOptions() {}

    public static void reload(Path packRoot, Iterable<RDPLPack> packs) {
        VALUES.clear();
        LOADED.clear();
        HIDDEN.clear();
        ABOUT.clear();
        Path home = packRoot.resolve("config");
        HOME = home;
        for (RDPLPack pack : packs) {
            if (PackManager.ROOT_PACK.equals(pack.getName())) { continue; }
            JsonObject defaults = new JsonObject();
            Map<String, String> about = new LinkedHashMap<>();
            Set<String> shy = new HashSet<>();
            for (String fileName : pack.packFiles("config", "json")) {
                JsonObject read = booleansOf(pack, fileName, about, shy);
                if (read == null) { continue; }
                for (Map.Entry<String, JsonElement> entry : read.entrySet()) {
                    if (defaults.has(entry.getKey())) { ContentLog.LOGGER.warn("Pack '{}' defines option '{}' more than once across its config files, keeping the value from {}", pack.getName(), entry.getKey(), fileName); }
                    defaults.add(entry.getKey(), entry.getValue());
                }
            }
            if (defaults.entrySet().isEmpty()) { continue; }
            String key = pack.getName();
            if (defaults.has("hide") && defaults.get("hide").getAsBoolean()) {
                defaults.remove("hide");
                for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) { shy.add(entry.getKey()); }
            }
            defaults.remove("hide");
            Map<String, Boolean> unseen = new HashMap<>();
            JsonObject shown = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
                if (shy.contains(entry.getKey())) { unseen.put(entry.getKey(), entry.getValue().getAsBoolean()); }
                else { shown.add(entry.getKey(), entry.getValue()); }
            }
            if (!unseen.isEmpty()) { HIDDEN.put(key, unseen); }
            if (shown.entrySet().isEmpty()) { continue; }
            try {
                Files.createDirectories(home);
                Path held = home.resolve(key + ".json");
                JsonObject merged = merge(shown, held, key);
                Map<String, Boolean> options = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : merged.entrySet()) {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isBoolean()) { options.put(entry.getKey(), entry.getValue().getAsBoolean()); }
                }
                VALUES.put(key, options);
                ABOUT.put(key, about);
            }
            catch (IOException ex) { ContentLog.LOGGER.error("Could not write the options file for pack '{}'", key, ex); }
        }
        for (Map.Entry<String, Map<String, Boolean>> entry : VALUES.entrySet()) { LOADED.put(entry.getKey(), new LinkedHashMap<>(entry.getValue())); }
        if (!VALUES.isEmpty()) { ContentLog.LOGGER.info("Loaded {} pack option file(s) from {}", VALUES.size(), home); }
        List<String> stale = orphans();
        if (!stale.isEmpty()) { ContentLog.LOGGER.info("{} option file(s) in {} belong to no installed pack, left alone: {}. Use /rdpl config prune, or /rdplserver config prune on a dedicated server, to remove them", stale.size(), home, String.join(", ", stale)); }
    }

    public static List<String> orphans() {
        List<String> stale = new ArrayList<>();
        if (HOME == null || !Files.isDirectory(HOME)) { return stale; }
        try (Stream<Path> held = Files.list(HOME)) {
            held.filter(Files::isRegularFile)
                    .map(one -> one.getFileName().toString())
                    .filter(one -> one.endsWith(".json"))
                    .map(one -> one.substring(0, one.length() - 5))
                    .filter(one -> !VALUES.containsKey(one))
                    .sorted()
                    .forEach(stale::add);
        }
        catch (IOException ex) { ContentLog.LOGGER.warn("Could not look through {} for unused option files", HOME, ex); }
        return stale;
    }

    public static int prune() {
        List<String> stale = orphans();
        int gone = 0;
        for (String one : stale) {
            try {
                Files.delete(HOME.resolve(one + ".json"));
                ContentLog.LOGGER.debug("Removed unused option file '{}.json'", one);
                gone++;
            }
            catch (IOException ex) { ContentLog.LOGGER.error("Could not remove '{}.json'", one, ex); }
        }
        return gone;
    }

    private static JsonObject booleansOf(RDPLPack pack, String fileName, Map<String, String> about, Set<String> shy) {
        try {
            String text = pack.readPackFile("config/" + fileName);
            if (text == null) { return null; }
            JsonObject read = new JsonParser().parse(text).getAsJsonObject();
            JsonObject defaults = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : read.entrySet()) {
                JsonElement held = entry.getValue();
                if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { defaults.add(entry.getKey(), held); }
                else if (held.isJsonObject() && held.getAsJsonObject().has("default") && held.getAsJsonObject().get("default").isJsonPrimitive() && held.getAsJsonObject().get("default").getAsJsonPrimitive().isBoolean()) {
                    defaults.add(entry.getKey(), held.getAsJsonObject().get("default"));
                    if (held.getAsJsonObject().has("description")) { about.put(entry.getKey(), held.getAsJsonObject().get("description").getAsString()); }
                    if (held.getAsJsonObject().has("hide") && held.getAsJsonObject().get("hide").getAsBoolean()) { shy.add(entry.getKey()); }
                }
                else { ContentLog.LOGGER.warn("Pack '{}' option '{}' in {} is not true or false, or an object with a true/false 'default', so it is ignored", pack.getName(), entry.getKey(), fileName); }
            }
            return defaults;
        }
        catch (Exception ex) {
            ContentLog.LOGGER.error("Pack '{}' has a config/{} that is not a JSON object of true/false options, so it is ignored", pack.getName(), fileName, ex);
            return null;
        }
    }

    private static JsonObject merge(JsonObject defaults, Path held, String packName) throws IOException {
        JsonObject kept = new JsonObject();
        if (Files.isRegularFile(held)) {
            try { kept = new JsonParser().parse(new String(Files.readAllBytes(held), StandardCharsets.UTF_8)).getAsJsonObject(); }
            catch (Exception ex) { ContentLog.LOGGER.error("{} is not valid JSON, so it is rewritten with the pack's defaults", held, ex); }
        }
        JsonObject merged = new JsonObject();
        merged.addProperty("_note", "Options for " + packName + ". Changes apply on the next game start.");
        boolean differs = false;
        for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
            JsonElement mine = kept.get(entry.getKey());
            if (mine != null && mine.isJsonPrimitive() && mine.getAsJsonPrimitive().isBoolean()) { merged.add(entry.getKey(), mine); }
            else {
                merged.add(entry.getKey(), entry.getValue());
                differs = true;
            }
        }
        for (Map.Entry<String, JsonElement> entry : kept.entrySet()) {
            if (!"_note".equals(entry.getKey()) && !defaults.has(entry.getKey())) {
                ContentLog.LOGGER.debug("Option '{}' in {} is no longer defined by the pack, so it is dropped", entry.getKey(), held.getFileName());
                differs = true;
            }
        }
        if (differs || !Files.isRegularFile(held)) { Files.write(held, gsonPretty(merged).getBytes(StandardCharsets.UTF_8)); }
        return merged;
    }

    private static String gsonPretty(JsonObject held) { return new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(held); }

    public static Boolean option(String fileKey, String name) {
        Map<String, Boolean> options = VALUES.get(fileKey);
        Boolean held = options == null ? null : options.get(name);
        if (held != null) { return held; }
        Map<String, Boolean> unseen = HIDDEN.get(fileKey);
        return unseen == null ? null : unseen.get(name);
    }

    public static List<String> files() { return new ArrayList<>(VALUES.keySet()); }

    public static Map<String, Boolean> optionsOf(String fileKey) {
        Map<String, Boolean> held = VALUES.get(fileKey);
        return held == null ? new LinkedHashMap<>() : new LinkedHashMap<>(held);
    }

    public static Map<String, Boolean> loadedOptionsOf(String fileKey) {
        Map<String, Boolean> held = LOADED.get(fileKey);
        return held == null ? new LinkedHashMap<>() : new LinkedHashMap<>(held);
    }

    public static boolean applied() {
        for (Map.Entry<String, Map<String, Boolean>> entry : VALUES.entrySet()) {
            Map<String, Boolean> was = LOADED.get(entry.getKey());
            if (was == null) { continue; }
            for (Map.Entry<String, Boolean> option : entry.getValue().entrySet()) {
                if (!gates(entry.getKey(), option.getKey())) { continue; }
                if (!option.getValue().equals(was.get(option.getKey()))) { return false; }
            }
        }
        return true;
    }

    public static void gating(String asked) { GATING.add(asked); }

    public static Map<String, Boolean> gatingValues() {
        Map<String, Boolean> found = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Boolean>> entry : VALUES.entrySet()) {
            for (Map.Entry<String, Boolean> option : entry.getValue().entrySet()) {
                if (gates(entry.getKey(), option.getKey())) { found.put(entry.getKey() + ":" + option.getKey(), option.getValue()); }
            }
        }
        return found;
    }

    public static void worldChanged(List<String> changed) {
        WORLD_CHANGED.clear();
        WORLD_CHANGED.addAll(changed);
    }

    public static List<String> worldChanged() { return WORLD_CHANGED; }

    public static void worldTold() { WORLD_CHANGED.clear(); }

    public static boolean gates(String fileKey, String name) { return GATING.contains(name) || GATING.contains(fileKey + ":" + name); }

    public static void save(String fileKey, Map<String, Boolean> options) {
        Map<String, Boolean> held = VALUES.get(fileKey);
        if (held == null || HOME == null) { return; }
        held.clear();
        held.putAll(options);
        JsonObject out = new JsonObject();
        out.addProperty("_note", "Options for " + fileKey + ". Changes apply on the next game start.");
        for (Map.Entry<String, Boolean> entry : options.entrySet()) { out.addProperty(entry.getKey(), entry.getValue()); }
        try { Files.write(HOME.resolve(fileKey + ".json"), gsonPretty(out).getBytes(StandardCharsets.UTF_8)); }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not save {}", fileKey, ex); }
    }

    public static String about(String fileKey, String name) {
        Map<String, String> held = ABOUT.get(fileKey);
        return held == null ? null : held.get(name);
    }

    public static Boolean anywhere(String name) {
        Boolean found = null;
        for (Map<String, Boolean> options : VALUES.values()) {
            Boolean held = options.get(name);
            if (held == null) { continue; }
            if (!held) { return false; }
            found = true;
        }
        for (Map<String, Boolean> options : HIDDEN.values()) {
            Boolean held = options.get(name);
            if (held == null) { continue; }
            if (!held) { return false; }
            found = true;
        }
        return found;
    }
}
