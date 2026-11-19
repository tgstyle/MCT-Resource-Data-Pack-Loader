package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;

public final class DataSets {
    public static final DataSets NONE = new DataSets(new LinkedHashMap<>(), new LinkedHashMap<>());
    private final Map<String, Object> player;
    private final Map<String, Object> world;

    private DataSets(Map<String, Object> player, Map<String, Object> world) {
        this.player = player;
        this.world = world;
    }

    public static DataSets parse(JsonObject json, String kind, String key, Object file) {
        Map<String, Object> player = read(Json.object(json, "sets"), kind, key, file);
        Map<String, Object> world = read(Json.object(json, "worldSets"), kind, key, file);
        return player.isEmpty() && world.isEmpty() ? NONE : new DataSets(player, world);
    }

    private static Map<String, Object> read(JsonObject json, String kind, String key, Object file) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Object value = SyncValue.plain(entry.getValue());
            if (value == null) { ContentLog.LOGGER.error("{} {} in {} sets the data value {} to something that is not text, a number or true/false, skipping that value", kind, key, file, entry.getKey()); }
            else { values.put(entry.getKey(), value); }
        }
        return values;
    }

    public boolean isEmpty() { return this == NONE; }

    public void apply(ServerPlayer subject, String asking) {
        for (Map.Entry<String, Object> entry : player.entrySet()) { SyncData.set(subject.level(), subject, entry.getKey(), entry.getValue(), asking); }
        for (Map.Entry<String, Object> entry : world.entrySet()) { SyncData.set(subject.level(), null, entry.getKey(), entry.getValue(), asking); }
    }
}
