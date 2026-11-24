package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.RecordDef;
import mctmods.resourcedatapackloader.common.types.ContentItemTypes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

final class ContentParserRecords {
    private static final List<String> JUKEBOX_KEYS = List.of("sound", "ticks", "comparator");

    private ContentParserRecords() {}

    @Nullable static RecordDef record(ResourceLocation key, JsonObject json) {
        if (!ContentItemTypes.RECORD.equals(Settings.lower(Json.getString(json, "type", "")))) { return null; }
        JsonObject jukebox = Json.objectOrNull(json, "jukebox");
        if (jukebox == null) {
            ContentLog.LOGGER.error("Record item {} has no jukebox section, so it registers nothing", key);
            return null;
        }
        Json.unknownKeys(jukebox, "jukebox", JUKEBOX_KEYS);
        ResourceLocation sound = ContentParser.location(Json.getString(jukebox, "sound", "").trim());
        if (sound == null) {
            ContentLog.LOGGER.error("Record item {} has no jukebox.sound, so it registers nothing", key);
            return null;
        }
        int ticks = Json.getInt(jukebox, "ticks", -1);
        if (jukebox.has("ticks") && ticks < 1) {
            ContentLog.LOGGER.error("Record item {} sets jukebox.ticks to {}, which is below 1, so the song never ends", key, ticks);
            ticks = -1;
        }
        int comparator = Json.getInt(jukebox, "comparator", -1);
        if (comparator < -1 || comparator > 15) {
            ContentLog.LOGGER.error("Record item {} sets jukebox.comparator to {}, which is outside 0 to 15, so the jukebox gives level 1", key, comparator);
            comparator = -1;
        }
        return new RecordDef(sound, ticks, comparator);
    }
}
