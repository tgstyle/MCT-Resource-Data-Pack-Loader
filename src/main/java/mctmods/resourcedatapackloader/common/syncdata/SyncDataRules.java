package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public final class SyncDataRules {
    private static final List<String> FILE_KEYS = List.of("values");
    private static final List<String> VALUE_KEYS = List.of("name", "type", "default", "max", "persist");
    private static final Map<String, Declaration> DECLARED = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private SyncDataRules() {}

    public static final class Declaration {
        public final String name;
        public final int kind;
        @Nullable public final SyncValue fallback;
        public final boolean persist;
        public final int max;
        public final Identifier file;

        private Declaration(String name, int kind, @Nullable SyncValue fallback, boolean persist, int max, Identifier file) {
            this.name = name;
            this.kind = kind;
            this.fallback = fallback;
            this.persist = persist;
            this.max = max;
            this.file = file;
        }
    }

    public static void load() {
        DECLARED.clear();
        GENERATION.stale();
        if (Config.content.loadOff()) { return; }
        Json.eachObject(PackManager.SYNCDATA, "synced value file", SyncDataRules::read);
        if (!DECLARED.isEmpty()) { Summary.info("syncdata", "Loaded " + DECLARED.size() + " synced value declaration(s)"); }
    }

    private static void ensure() {
        if (GENERATION.stale()) { load(); }
    }

    private static void read(Identifier file, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        JsonArray values = Json.getJsonArray(json, "values", new JsonArray());
        for (JsonObject element : Json.objectsOrSkip(values, "A synced value entry in {} is not an object, skipping it", file)) { declare(file, element); }
    }

    private static void declare(Identifier file, JsonObject json) {
        Json.unknownKeys(json, "values", VALUE_KEYS);
        String name = Json.text(json, "name", "");
        if (!SyncData.valid(name)) {
            ContentLog.LOGGER.error("A synced value entry in {} has the name '{}', which is not 1 to 64 of a-z, 0-9, _ . : / -, skipping it", file, name);
            return;
        }
        String type = Json.text(json, "type", "text");
        int kind = SyncValue.kindOf(type);
        if (kind == 0) {
            ContentLog.LOGGER.error("The synced value {} in {} has the type '{}', which is not text, number or bool, skipping it", name, file, type);
            return;
        }
        Declaration other = DECLARED.get(name);
        if (other != null) {
            ContentLog.LOGGER.error("The synced value {} in {} is already declared in {}, skipping the later entry", name, file, other.file);
            return;
        }
        SyncValue fallback = json.has("default") ? typed(json.get("default"), kind) : null;
        if (json.has("default") && fallback == null) { ContentLog.LOGGER.error("The synced value {} in {} has a default that is not a {}, so it has none", name, file, type); }
        int max = Json.getInt(json, "max", SyncData.MAX_TEXT);
        if (max < 1 || max > SyncData.MAX_TEXT) {
            ContentLog.LOGGER.error("The synced value {} in {} has max {}, which is not 1 to 256, using 256", name, file, max);
            max = SyncData.MAX_TEXT;
        }
        DECLARED.put(name, new Declaration(name, kind, fallback, Json.getBoolean(json, "persist", true), max, file));
    }

    @Nullable private static SyncValue typed(JsonElement element, int kind) {
        Object plain = SyncValue.plain(element);
        SyncValue value = plain == null ? null : SyncValue.of(plain);
        return value != null && value.kind == kind ? value : null;
    }

    public static boolean any() {
        ensure();
        return !DECLARED.isEmpty();
    }

    @Nullable public static Declaration declared(String name) {
        ensure();
        return DECLARED.get(name);
    }

    public static Collection<Declaration> all() {
        ensure();
        return DECLARED.values();
    }

    @Nullable public static SyncValue defaultOf(String name) {
        Declaration declared = declared(name);
        return declared == null ? null : declared.fallback;
    }

    public static boolean persists(String name) {
        Declaration declared = declared(name);
        return declared == null || declared.persist;
    }

    public static int max(String name) {
        Declaration declared = declared(name);
        return declared == null ? SyncData.MAX_TEXT : declared.max;
    }
}
