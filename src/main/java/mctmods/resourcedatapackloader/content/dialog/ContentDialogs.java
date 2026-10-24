package mctmods.resourcedatapackloader.content.dialog;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.card.CardParser;
import mctmods.resourcedatapackloader.content.card.CardWhen;
import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.IdNames;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentDialogs {
    private static final int MOST_NODES = 64;
    private static final int MOST_LINES = 16;
    private static final int MOST_REPLIES = 8;
    private static final Map<String, DialogDef> BY_KEY = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentDialogs() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        BY_KEY.clear();
        if (!Config.content.load) { return; }
        Map<ResourceLocation, DialogDef> read = new LinkedHashMap<>();
        Json.eachFile(PackManager.DIALOGS, "dialog", (key, contents) -> {
            DialogDef def = parse(key, contents);
            if (def != null) { read.put(key, def); }
        });
        for (Map.Entry<ResourceLocation, DialogDef> entry : read.entrySet()) {
            if (ContentRegistry.available(entry.getValue().requires, entry.getKey())) { BY_KEY.put(entry.getValue().key, entry.getValue()); }
        }
        if (!BY_KEY.isEmpty()) { Summary.info("dialogs", "Loaded " + BY_KEY.size() + " dialog(s)"); }
        for (EntityVariantDef variant : ContentEntities.variants()) {
            if (!variant.dialog.isEmpty() && find(variant.dialog) == null) { ContentLog.LOGGER.error("Entity variant {} names the dialog {}, which no pack provides, so it has no dialog", variant.registryName, variant.dialog); }
        }
    }

    @Nullable public static DialogDef find(String asked) {
        DialogDef exact = BY_KEY.get(asked);
        if (exact != null) { return exact; }
        for (DialogDef def : BY_KEY.values()) {
            if (IdNames.path(def.key).equals(asked)) { return def; }
        }
        return null;
    }

    @Nullable private static DialogDef parse(ResourceLocation id, String contents) {
        JsonObject json = Json.object(id, contents, "dialog file");
        if (json == null) { return null; }
        String key = id.toString();
        String start = Json.getString(json, "start", "").trim();
        Map<String, DialogDef.Node> nodes = nodes(key, json);
        if (!nodes.containsKey(start)) {
            ContentLog.LOGGER.error("Dialog {} starts at '{}', which is not one of its nodes, so it is skipped", key, start);
            return null;
        }
        return new DialogDef(key, start, optional(json, "title"), optional(json, "icon"), optional(json, "color"), optional(json, "image"),
                json.has("background") ? Json.getBoolean(json, "background") : null, optional(json, "font"), Json.strings(json, "requires"), nodes);
    }

    private static Map<String, DialogDef.Node> nodes(String key, JsonObject json) {
        JsonObject raw = Json.getJsonObject(json, "nodes", new JsonObject());
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : raw.entrySet()) {
            if (!entry.getValue().isJsonObject()) { ContentLog.LOGGER.error("Dialog {} has a node '{}' that is not an object, skipping it", key, entry.getKey()); }
            else if (names.size() >= MOST_NODES) { ContentLog.LOGGER.error("Dialog {} has more than {} nodes, skipping '{}'", key, MOST_NODES, entry.getKey()); }
            else { names.add(entry.getKey()); }
        }
        Map<String, DialogDef.Node> nodes = new LinkedHashMap<>();
        for (String name : names) { nodes.put(name, node(key, name, raw.getAsJsonObject(name), names)); }
        return nodes;
    }

    private static DialogDef.Node node(String key, String name, JsonObject json, List<String> names) {
        List<String> lines = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "lines", new JsonArray())) {
            if (!element.isJsonPrimitive()) { ContentLog.LOGGER.error("Dialog {} node {} has a line that is not text, skipping it", key, name); }
            else if (lines.size() < MOST_LINES) { lines.add(Json.asString(element, "lines", "")); }
        }
        List<DialogDef.Reply> replies = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "replies", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("Dialog {} node {} has a reply that is not an object, skipping it", key, name);
                continue;
            }
            DialogDef.Reply reply = reply(key, name, element.getAsJsonObject(), names);
            if (reply != null && replies.size() < MOST_REPLIES) { replies.add(reply); }
        }
        return new DialogDef.Node(optional(json, "title"), Collections.unmodifiableList(lines), Collections.unmodifiableList(replies));
    }

    @Nullable private static DialogDef.Reply reply(String key, String node, JsonObject json, List<String> names) {
        String text = Json.getString(json, "text", "");
        if (text.trim().isEmpty()) {
            ContentLog.LOGGER.error("Dialog {} node {} has a reply with no text, skipping it", key, node);
            return null;
        }
        String target = Json.getString(json, "goto", "").trim();
        if (!target.isEmpty() && !names.contains(target)) {
            ContentLog.LOGGER.error("Dialog {} node {} has a reply '{}' that goes to '{}', which is not one of its nodes, so it is skipped", key, node, text, target);
            return null;
        }
        CardWhen when = json.has("when") ? CardParser.when(Json.getJsonObject(json, "when")) : null;
        return new DialogDef.Reply(text, target, Json.getString(json, "card", "").trim(), Json.getString(json, "runs", "").trim(), when, target.isEmpty() && Json.getBoolean(json, "close", true));
    }

    @Nullable private static String optional(JsonObject json, String member) { return json.has(member) ? Json.getString(json, member).trim() : null; }
}
