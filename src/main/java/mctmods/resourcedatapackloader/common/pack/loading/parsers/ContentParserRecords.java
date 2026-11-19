package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.RecordDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserRecords {
    private static final String TYPE = "record";

    private ContentParserRecords() {}

    @Nullable static RecordDef record(ResourceLocation key, JsonObject json) {
        if (!TYPE.equals(Settings.lower(Json.getString(json, "type", ContentParser.DEFAULT_TYPE)))) { return null; }
        if (!json.has("jukebox") || !json.get("jukebox").isJsonObject()) {
            ContentLog.LOGGER.error("Record item {} has no jukebox section, so it registers nothing", key);
            return null;
        }
        JsonObject jukebox = json.getAsJsonObject("jukebox");
        String sound = Json.getString(jukebox, "sound", "").trim();
        if (sound.isEmpty()) {
            ContentLog.LOGGER.error("Record item {} has no jukebox.sound, so it registers nothing", key);
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(sound);
        if (id == null) {
            ContentLog.LOGGER.error("Record item {} has no jukebox.sound, so it registers nothing: '{}' is not a valid sound id", key, sound);
            return null;
        }
        int ticks = Json.getInt(jukebox, "ticks", RecordDef.UNENDING_TICKS);
        if (ticks < 1 || ticks > RecordDef.MOST_TICKS) {
            ContentLog.LOGGER.error("Record item {} sets jukebox.ticks to {}, which is outside 1 to {}, so the disc never ends", key, ticks, RecordDef.MOST_TICKS);
            ticks = RecordDef.UNENDING_TICKS;
        }
        int comparator = Json.getInt(jukebox, "comparator", -1);
        if (comparator < -1 || comparator > 15) {
            ContentLog.LOGGER.error("Record item {} sets jukebox.comparator to {}, which is outside 0 to 15, so it uses {}", key, comparator, RecordDef.DEFAULT_COMPARATOR);
            comparator = -1;
        }
        return new RecordDef(id, ticks, comparator);
    }
}
