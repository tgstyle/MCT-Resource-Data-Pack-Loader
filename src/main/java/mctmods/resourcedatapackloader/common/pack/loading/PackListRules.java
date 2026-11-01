package mctmods.resourcedatapackloader.common.pack.loading;

import mctmods.resourcedatapackloader.common.pack.io.RDPLResourcePack;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class PackListRules {
    public static final String VANILLA = "vanilla";
    private static final String FILE_PREFIX = "file/";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private static final String MARKER = "packlist.seen";
    private static final Map<String, Rule> RULES = new HashMap<>();
    private static final List<String> DEFAULTS = new ArrayList<>();
    private static int allowedFailures;
    private static int failures;
    private static boolean fellBack;
    private static boolean loaded;

    private PackListRules() {}

    public static final class Rule {
        @Nullable public String title;
        @Nullable public String description;
        @Nullable public Boolean top;
        public boolean compatible;
        public boolean hidden;
        public boolean required;
        public boolean fixed;

        public boolean alwaysOn() { return required || hidden || fixed; }
    }

    public static String id(String raw) { return raw.startsWith(FILE_PREFIX) ? raw.substring(FILE_PREFIX.length()) : raw; }
    public static boolean own(String raw) { return RDPLResourcePack.ID.equals(id(raw)) || RDPLResourcePack.ID_OVERRIDING.equals(id(raw)); }

    @Nullable public static Rule rule(String raw) {
        load();
        return RULES.get(id(raw));
    }

    public static Map<String, Rule> rules() {
        load();
        return Collections.unmodifiableMap(RULES);
    }

    public static List<String> defaults() {
        load();
        return Collections.unmodifiableList(DEFAULTS);
    }

    public static boolean firstRun(boolean selectedEmpty) {
        load();
        if (DEFAULTS.isEmpty()) { return false; }
        Path root = PackManager.get().getRoot();
        if (root == null) { return false; }
        Path marker = root.resolve("config").resolve(MARKER);
        if (Files.exists(marker)) { return false; }
        try {
            Files.createDirectories(marker.getParent());
            Files.createFile(marker);
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not write {}, so default packs may turn on again next start", marker, ex); }
        if (selectedEmpty) { ContentLog.LOGGER.info("Pack list: first run, turning on {}", DEFAULTS); }
        return selectedEmpty;
    }

    public static List<String> afterFailure() {
        load();
        failures++;
        ContentLog.LOGGER.info("Pack list: failed reload {}", failures);
        if (allowedFailures <= 0 || fellBack || failures < allowedFailures || DEFAULTS.isEmpty()) { return Collections.emptyList(); }
        fellBack = true;
        ContentLog.LOGGER.info("Pack list: falling back to {}", DEFAULTS);
        return Collections.unmodifiableList(DEFAULTS);
    }

    private static synchronized void load() {
        if (loaded) { return; }
        loaded = true;
        PackManager.get().forEachAssets(PackManager.PACKLIST, PackManager.JSON, (namespace, path, contents) -> read(namespace + ":" + path, contents));
        if (!RULES.isEmpty() || !DEFAULTS.isEmpty()) { ContentLog.LOGGER.info("Pack list: {} rule(s), default packs {}, fall back after {} failed reload(s)", RULES.size(), DEFAULTS, allowedFailures); }
    }

    private static void read(String file, String contents) {
        try {
            JsonObject json = Json.GSON.fromJson(contents, JsonObject.class);
            if (json == null) { return; }
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                switch (entry.getKey()) {
                    case "defaultPacks":
                        DEFAULTS.clear();
                        for (JsonElement one : entry.getValue().getAsJsonArray()) { DEFAULTS.add(id(one.getAsString())); }
                        break;
                    case "reloadFailures":
                        allowedFailures = entry.getValue().getAsInt();
                        break;
                    case "packs":
                        for (Map.Entry<String, JsonElement> pack : entry.getValue().getAsJsonObject().entrySet()) { RULES.put(id(pack.getKey()), rule(file, pack.getKey(), pack.getValue().getAsJsonObject(), RULES.get(id(pack.getKey())))); }
                        break;
                    default:
                        ContentLog.LOGGER.error("Pack list {} sets '{}', which is not a setting anything reads, so it does nothing", file, entry.getKey());
                }
            }
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Pack list {} could not be read, so what it sets is ignored: {}", file, ex.getMessage()); }
    }

    private static Rule rule(String file, String id, JsonObject json, @Nullable Rule was) {
        Rule rule = was == null ? new Rule() : was;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonElement value = entry.getValue();
            switch (entry.getKey()) {
                case "title":
                    rule.title = value.getAsString();
                    break;
                case "description":
                    rule.description = value.getAsString();
                    break;
                case "compatible":
                    rule.compatible = value.getAsBoolean();
                    break;
                case "hidden":
                    rule.hidden = value.getAsBoolean();
                    break;
                case "required":
                    rule.required = value.getAsBoolean();
                    break;
                case "fixedPosition":
                    rule.fixed = value.getAsBoolean();
                    break;
                case "position":
                    String place = value.getAsString();
                    if (own(id)) { ContentLog.LOGGER.error("Pack list {} gives {} the position '{}', which does nothing, since RDPL's own packs are placed by their tier", file, id, place); }
                    else if (!TOP.equals(place) && !BOTTOM.equals(place)) { ContentLog.LOGGER.error("Pack list {} gives {} the position '{}', which is not top or bottom", file, id, place); }
                    else { rule.top = TOP.equals(place); }
                    break;
                default:
                    ContentLog.LOGGER.error("Pack list {} sets '{}' on {}, which is not a setting anything reads, so it does nothing", file, entry.getKey(), id);
            }
        }
        return rule;
    }
}
