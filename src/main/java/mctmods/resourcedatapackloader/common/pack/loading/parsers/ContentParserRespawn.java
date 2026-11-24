package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserRespawn {
    private static final String KEY = "respawn";
    private static final List<String> RESPAWN_KEYS = List.of("after", "loot", "rebuild", "playerDistance", "spawners", "mobs", "mobCap");

    private ContentParserRespawn() {}

    @Nullable public static RespawnDef parse(ResourceLocation key, JsonObject owner) {
        if (!owner.has(KEY)) { return null; }
        JsonObject json = Json.objectOrNull(owner, KEY);
        if (json == null) {
            ContentLog.LOGGER.error("Definition {} writes 'respawn' as something other than an object, so it does not respawn", key);
            return null;
        }
        Json.unknownKeys(json, KEY, RESPAWN_KEYS);
        String where = "Respawn of " + key;
        if (!json.has("after")) {
            ContentLog.LOGGER.error("{} respawn needs after, the seconds before it comes back, so it does not respawn", where);
            return null;
        }
        int after = Json.getInt(json, "after", 0);
        if (after < 0) {
            ContentLog.LOGGER.error("{} sets after to {}, which is below 0, so it does not respawn", where, after);
            return null;
        }
        if (after == 0) { return null; }
        if (after < RespawnDef.MIN_AFTER) {
            ContentLog.LOGGER.error("{} sets after to {}, below the least of {} seconds, so {} is used", where, after, RespawnDef.MIN_AFTER, RespawnDef.MIN_AFTER);
            after = RespawnDef.MIN_AFTER;
        }
        String loot = Json.choice(json, "loot", "emptied", where, Arrays.asList("none", "emptied", "all"));
        String rebuild = Json.choice(json, "rebuild", "none", where, Arrays.asList("none", "broken", "full"));
        return new RespawnDef(after,
                within(json, "playerDistance", RespawnDef.DEFAULT_DISTANCE, RespawnDef.MOST_DISTANCE, where),
                RespawnDef.Loot.valueOf(loot.toUpperCase(Locale.ROOT)),
                Json.getBoolean(json, "spawners", true),
                Json.getBoolean(json, "mobs", true),
                within(json, "mobCap", 0, RespawnDef.MOST_CAP, where),
                RespawnDef.Rebuild.valueOf(rebuild.toUpperCase(Locale.ROOT)));
    }

    private static int within(JsonObject json, String member, int fallback, int most, String where) {
        if (!json.has(member)) { return fallback; }
        int value = Json.getInt(json, member, fallback);
        if (value >= 0 && value <= most) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside 0 to {}, so {} is used", where, member, value, most, fallback);
        return fallback;
    }
}
