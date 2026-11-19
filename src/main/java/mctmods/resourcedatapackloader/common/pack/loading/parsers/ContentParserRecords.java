package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.generated.ContentFormats;
import mctmods.resourcedatapackloader.common.pack.definitions.items.RecordDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

final class ContentParserRecords {
    private static final String TYPE = "record";

    private ContentParserRecords() {}

    @Nullable static RecordDef record(Identifier key, String type, JsonObject json) {
        if (!TYPE.equals(type)) { return null; }
        if (!json.has("jukebox") || !json.get("jukebox").isJsonObject()) {
            ContentLog.LOGGER.error("Record item {} has no jukebox section, so it registers nothing", key);
            return null;
        }
        JsonObject jukebox = Json.getJsonObject(json, "jukebox");
        Identifier sound = ContentParser.location(Json.getString(jukebox, "sound", ""));
        if (sound == null) {
            ContentLog.LOGGER.error("Record item {} has no jukebox.sound, so it registers nothing", key);
            return null;
        }
        int ticks = Json.getInt(jukebox, "ticks", 0);
        if (jukebox.has("ticks") && ticks < 1) { ContentLog.LOGGER.error("Record item {} sets jukebox.ticks to {}, which is below 1, so it plays for {} ticks", key, ticks, ContentFormats.MAX_SONG_TICKS); }
        int comparator = Json.getInt(jukebox, "comparator", -1);
        if (comparator < -1 || comparator > 15) {
            ContentLog.LOGGER.error("Record item {} sets jukebox.comparator to {}, which is outside 0 to 15, so it uses {}", key, comparator, ContentFormats.DEFAULT_COMPARATOR);
            comparator = -1;
        }
        return new RecordDef(sound, ticks < 1 ? ContentFormats.MAX_SONG_TICKS : Math.min(ticks, ContentFormats.MAX_SONG_TICKS), comparator);
    }
}
