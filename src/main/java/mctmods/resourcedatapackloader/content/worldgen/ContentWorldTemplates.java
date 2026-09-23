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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentWorldTemplates {
    private static final String AUTO = "auto";
    private static final String BUILT_IN = "rdpl";
    public static final List<String> ROLE_ORDER = List.of("ocean", "river", "beach", "mushroom", "swamp", "hills", "mountain", "jungle", "forest", "savanna", "sandy", "mesa", "snowy", "wasteland", "plains", "water");
    private static final List<String> ROLES = List.of("ocean", "river", "water", "beach", "mushroom", "swamp", "hills", "mountain", "jungle", "forest", "savanna", "sandy", "mesa", "snowy", "wasteland", "plains", "nether", "end");
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, WorldTemplateDef> DEFS = new LinkedHashMap<>();
    @Nullable private static WorldTemplateDef active;

    private ContentWorldTemplates() {}

    public static void load() {
        DEFS.clear();
        active = null;
        if (Config.content.loadOff()) { return; }
        for (WorldTemplateDef def : builtins()) { DEFS.put(def.key(), def); }
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
        return new WorldTemplateDef(key, GsonHelper.getAsString(json, "name", key.getPath()), settings, Json.strings(json, "requires"), GsonHelper.getAsString(json, "fallback", GsonHelper.getAsString(json, "default", WorldTemplateDef.VOID)).trim(), roles(key, json), switches(json), Json.strings(json, "dimensions"));
    }

    private static Map<String, String> roles(ResourceLocation key, JsonObject json) {
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> role : GsonHelper.getAsJsonObject(json, "roles", new JsonObject()).entrySet()) {
            String name = role.getKey().trim().toLowerCase(Locale.ROOT);
            if (!ROLES.contains(name)) {
                ContentLog.LOGGER.error("World template {} names role '{}', which is not one of {}, ignoring it", key, role.getKey(), String.join(", ", ROLES));
                continue;
            }
            if (!role.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("World template {} sets role '{}' to something that is not a biome name, ignoring it", key, role.getKey());
                continue;
            }
            out.put(name, role.getValue().getAsString().trim());
        }
        return Collections.unmodifiableMap(out);
    }

    private static Map<String, Boolean> switches(JsonObject json) {
        if (!json.has("structures")) { return Map.of(); }
        Map<String, Boolean> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "structures").entrySet()) {
            if (entry.getValue().isJsonPrimitive()) { out.put(entry.getKey().trim().toLowerCase(Locale.ROOT), entry.getValue().getAsBoolean()); }
        }
        return Map.copyOf(out);
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
        if (AUTO.equalsIgnoreCase(wanted)) {
            WorldTemplateDef chosen = null;
            List<ResourceLocation> fromPacks = new ArrayList<>();
            for (WorldTemplateDef def : usable) {
                if (BUILT_IN.equals(def.key().getNamespace())) { continue; }
                fromPacks.add(def.key());
                chosen = def;
            }
            if (fromPacks.size() > 1) { ContentLog.LOGGER.error("{} world templates are loaded and only one can be active: {}. '{}' is the one in force and the rest do nothing, settings and all. Put every setting in ONE template, and use its 'dimensions' list to say which dimensions it fills", fromPacks.size(), fromPacks, chosen.key()); }
            return chosen;
        }
        ContentLog.LOGGER.error("worldTemplate is '{}' but nothing defines it. Known templates: {}", wanted, DEFS.keySet());
        return null;
    }

    private static List<WorldTemplateDef> builtins() {
        Map<String, String> vanilla = new LinkedHashMap<>();
        vanilla.put("ocean", "minecraft:ocean");
        vanilla.put("river", "minecraft:river");
        vanilla.put("beach", "minecraft:beach");
        vanilla.put("mushroom", "minecraft:mushroom_fields");
        vanilla.put("swamp", "minecraft:swamp");
        vanilla.put("hills", "minecraft:windswept_hills");
        vanilla.put("mountain", "minecraft:windswept_hills");
        Map<String, String> water = new LinkedHashMap<>();
        water.put("river", "minecraft:river");
        water.put("beach", "minecraft:beach");
        return List.of(built("void", "Void", WorldTemplateDef.VOID, Map.of()), built("vanilla", "Vanilla shoreline", "minecraft:plains", vanilla), built("ocean", "Ocean world", "minecraft:ocean", water),
                built("plains", "Plains world", "minecraft:plains", Map.of()), built("desert", "Desert world", "minecraft:desert", Map.of()));
    }

    private static WorldTemplateDef built(String path, String name, String fallback, Map<String, String> roles) { return new WorldTemplateDef(ResourceLocation.fromNamespaceAndPath(BUILT_IN, path), name, null, List.of(), fallback, Collections.unmodifiableMap(roles), Map.of(), List.of()); }
}
