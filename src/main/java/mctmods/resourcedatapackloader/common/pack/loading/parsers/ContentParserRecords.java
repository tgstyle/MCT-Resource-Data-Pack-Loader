package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.RecordDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentParserRecords {
    private static final String TYPE = "record";
    private static final List<String> JUKEBOX_KEYS = Collections.unmodifiableList(Arrays.asList("sound", "comparator"));

    private ContentParserRecords() {}

    @Nullable static RecordDef record(ResourceLocation key, String type, JsonObject json) {
        if (!TYPE.equals(type)) { return null; }
        if (!json.has("jukebox") || !json.get("jukebox").isJsonObject()) {
            ContentLog.LOGGER.error("Record item {} has no jukebox section, so it registers nothing", key);
            return null;
        }
        JsonObject jukebox = Json.getJsonObject(json, "jukebox");
        Json.unknownKeys(jukebox, "jukebox", JUKEBOX_KEYS);
        String sound = Json.getString(jukebox, "sound", "").trim();
        if (sound.isEmpty()) {
            ContentLog.LOGGER.error("Record item {} has no jukebox.sound, so it registers nothing", key);
            return null;
        }
        int comparator = Json.getInt(jukebox, "comparator", -1);
        if (comparator < -1 || comparator > 15) {
            ContentLog.LOGGER.error("Record item {} sets jukebox.comparator to {}, which is outside 0 to 15, so the jukebox keeps its own level", key, comparator);
            comparator = -1;
        }
        return new RecordDef(new ResourceLocation(sound), comparator);
    }
}
