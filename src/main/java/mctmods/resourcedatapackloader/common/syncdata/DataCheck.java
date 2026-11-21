package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class DataCheck {
    private static final List<String> CHECK_KEYS = Collections.unmodifiableList(Arrays.asList("name", "is", "isNot", "atLeast", "atMost", "set"));
    public final String name;
    @Nullable public final Object is;
    @Nullable public final Object isNot;
    @Nullable public final Double atLeast;
    @Nullable public final Double atMost;
    public final boolean set;

    private DataCheck(String name, @Nullable Object is, @Nullable Object isNot, @Nullable Double atLeast, @Nullable Double atMost, boolean set) {
        this.name = name;
        this.is = is;
        this.isNot = isNot;
        this.atLeast = atLeast;
        this.atMost = atMost;
        this.set = set;
    }

    public static List<DataCheck> parse(JsonElement element, String kind, String member, Object file) {
        List<DataCheck> checks = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement each : element.getAsJsonArray()) { add(checks, each, kind, member, file); }
        }
        else { add(checks, element, kind, member, file); }
        return checks;
    }

    private static void add(List<DataCheck> checks, JsonElement element, String kind, String member, Object file) {
        JsonObject json = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        Json.unknownKeys(json, member + " data check", CHECK_KEYS);
        String name = Json.text(json, "name", "");
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("{} {} in {} has a data check with no name, skipping the check", kind, member, file);
            return;
        }
        checks.add(new DataCheck(name, plain(json, "is"), plain(json, "isNot"), number(json, "atLeast"), number(json, "atMost"), Json.getBoolean(json, "set", true)));
    }

    @Nullable private static Object plain(JsonObject json, String key) { return json.has(key) ? SyncValue.plain(json.get(key)) : null; }

    @Nullable private static Double number(JsonObject json, String key) {
        Object value = plain(json, key);
        return value instanceof Double ? (Double) value : null;
    }

    public static boolean met(List<DataCheck> checks, DataReader reader) {
        if (checks.isEmpty()) { return true; }
        if (!reader.known()) { return false; }
        for (DataCheck check : checks) {
            if (!check.holds(reader.get(check.name))) { return false; }
        }
        return true;
    }

    private boolean holds(@Nullable SyncValue value) {
        if (value == null) { return !set; }
        if (!set) { return false; }
        if (is != null && !value.matches(is)) { return false; }
        if (isNot != null && value.matches(isNot)) { return false; }
        if (atLeast != null && (value.kind != SyncValue.NUMBER || value.number < atLeast)) { return false; }
        return atMost == null || value.kind == SyncValue.NUMBER && value.number <= atMost;
    }
}
