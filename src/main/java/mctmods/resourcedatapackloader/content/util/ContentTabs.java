package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTabs {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, String> ICONS = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> SOURCES = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentTabs() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        PackManager.get().forEach(PackManager.TABS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in creative tab {}, ignoring it", key, ex); }
        });
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Creative tab {} is empty, ignoring it", key);
            return;
        }
        String label = JsonUtils.getString(json, "label", key.getPath());
        String icon = JsonUtils.getString(json, "icon", "");
        if (icon.isEmpty()) {
            ContentLog.LOGGER.error("Creative tab {} has no icon, it will fall back to the first block that uses it", key);
            return;
        }
        ICONS.put(label, icon);
        SOURCES.put(label, key);
    }

    @Nullable public static String icon(String label) {
        load();
        return ICONS.get(label);
    }

    public static ResourceLocation source(String label) {
        ResourceLocation key = SOURCES.get(label);
        return key == null ? new ResourceLocation("resourcedatapackloader", label) : key;
    }
}
