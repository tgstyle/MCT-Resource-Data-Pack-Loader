package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentWorldTemplates {
    private static final String AUTO = "auto";
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, WorldTemplateDef> DEFS = new LinkedHashMap<>();
    @Nullable private static WorldTemplateDef active;

    private ContentWorldTemplates() {}

    public static void load() {
        DEFS.clear();
        active = null;
        if (!Config.content.load()) { return; }
        Json.eachFile(PackManager.WORLDTEMPLATES, "world template", (key, contents) -> {
            WorldTemplateDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        active = select();
        ContentControl.check(active);
        if (active != null) { Summary.info("worldtemplate", "Using world template " + active.key() + " (" + active.name() + ") for its settings"); }
    }

    @Nullable public static WorldTemplateDef active() { return active; }

    @Nullable private static WorldTemplateDef parse(ResourceLocation key, String contents) {
        JsonObject json;
        try { json = GSON.fromJson(contents, JsonObject.class); }
        catch (JsonParseException ex) {
            ContentLog.LOGGER.error("World template {} could not be read, ignoring it", key, ex);
            return null;
        }
        if (json == null) {
            ContentLog.LOGGER.error("World template {} is empty, ignoring it", key);
            return null;
        }
        JsonObject settings = json.has("settings") ? GsonHelper.getAsJsonObject(json, "settings") : null;
        return new WorldTemplateDef(key, GsonHelper.getAsString(json, "name", key.getPath()), settings, Json.strings(json, "requires"));
    }

    @Nullable private static WorldTemplateDef select() {
        String wanted = Config.worldgen.worldTemplate().trim();
        if (wanted.isEmpty()) { return null; }
        List<WorldTemplateDef> usable = new ArrayList<>();
        for (Map.Entry<ResourceLocation, WorldTemplateDef> entry : DEFS.entrySet()) {
            if (ContentRegistry.available(entry.getValue().requires(), entry.getKey())) { usable.add(entry.getValue()); }
        }
        for (WorldTemplateDef def : usable) {
            if (def.key().toString().equalsIgnoreCase(wanted) || def.key().getPath().equalsIgnoreCase(wanted)) { return def; }
        }
        if (AUTO.equalsIgnoreCase(wanted)) { return usable.isEmpty() ? null : usable.get(usable.size() - 1); }
        ContentLog.LOGGER.error("worldTemplate is '{}' but nothing defines it. Known templates: {}", wanted, DEFS.keySet());
        return null;
    }
}
