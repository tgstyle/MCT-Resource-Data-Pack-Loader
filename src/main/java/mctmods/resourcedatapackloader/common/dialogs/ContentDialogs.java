package mctmods.resourcedatapackloader.common.dialogs;

import mctmods.resourcedatapackloader.common.cards.CardWhen;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.DialogDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.CardParser;
import mctmods.resourcedatapackloader.common.syncdata.DataSets;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.common.pack.loading.parsers.CardParser.optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentDialogs {
    private static final List<String> DIALOG_KEYS = Collections.unmodifiableList(Arrays.asList("start", "title", "icon", "color", "image", "background", "font", "titleColor", "textColor", "replyColor", "hoverColor", "hoverBackground", "history", "requires", "nodes"));
    private static final List<String> NODE_KEYS = Collections.unmodifiableList(Arrays.asList("title", "lines", "replies"));
    private static final List<String> REPLY_KEYS = Collections.unmodifiableList(Arrays.asList("text", "trade", "goto", "when", "card", "runs", "close", "sets", "worldSets"));
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
        Summary.loaded("dialogs", BY_KEY.size(), "dialog(s)");
        for (EntityVariantDef variant : ContentEntities.variants()) {
            if (!variant.dialog.isEmpty() && find(variant.dialog) == null) { ContentLog.LOGGER.error("Entity variant {} names the dialog {}, which no pack provides, so it has no dialog", variant.registryName, variant.dialog); }
        }
    }

    public static List<String> keys() { return new ArrayList<>(BY_KEY.keySet()); }

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
        Json.unknownKeys(json, "the top level", DIALOG_KEYS);
        String key = id.toString();
        String start = Json.getString(json, "start", "").trim();
        Map<String, DialogDef.Node> nodes = nodes(key, json);
        if (!nodes.containsKey(start)) {
            ContentLog.LOGGER.error("Dialog {} starts at '{}', which is not one of its nodes, so it is skipped", key, start);
            return null;
        }
        return new DialogDef(key, start, optional(json, "title"), optional(json, "icon"), optional(json, "color"), optional(json, "image"),
                Json.flagOrNull(json, "background"), optional(json, "font"),
                optional(json, "titleColor"), optional(json, "textColor"), optional(json, "replyColor"), optional(json, "hoverColor"), optional(json, "hoverBackground"),
                Json.flagOrNull(json, "history"), Json.strings(json, ContentRegistry.REQUIRES), nodes);
    }

    private static Map<String, DialogDef.Node> nodes(String key, JsonObject json) {
        JsonObject raw = Json.getJsonObject(json, "nodes", new JsonObject());
        List<String> names = new ArrayList<>();
        Json.eachCapped(raw.entrySet(), MOST_NODES, entry -> entry.getValue().isJsonObject(), entry -> names.add(entry.getKey()),
                entry -> ContentLog.LOGGER.error("Dialog {} has a node '{}' that is not an object, skipping it", key, entry.getKey()),
                entry -> ContentLog.LOGGER.error("Dialog {} has more than {} nodes, skipping '{}'", key, MOST_NODES, entry.getKey()));
        Map<String, DialogDef.Node> nodes = new LinkedHashMap<>();
        for (String name : names) { nodes.put(name, node(key, name, raw.getAsJsonObject(name), names)); }
        return nodes;
    }

    private static DialogDef.Node node(String key, String name, JsonObject json, List<String> names) {
        Json.unknownKeys(json, "nodes." + name, NODE_KEYS);
        List<String> lines = new ArrayList<>();
        Json.eachCapped(Json.getJsonArray(json, "lines", new JsonArray()), MOST_LINES, JsonElement::isJsonPrimitive, element -> lines.add(Json.asString(element, "lines", "")),
                element -> ContentLog.LOGGER.error("Dialog {} node {} has a line that is not text, skipping it", key, name),
                element -> { });
        List<DialogDef.Reply> replies = new ArrayList<>();
        Json.eachCapped(Json.getJsonArray(json, "replies", new JsonArray()), MOST_REPLIES, JsonElement::isJsonObject, element -> {
            DialogDef.Reply reply = reply(key, name, element.getAsJsonObject(), names);
            return reply != null && replies.add(reply);
        }, element -> ContentLog.LOGGER.error("Dialog {} node {} has a reply that is not an object, skipping it", key, name),
                element -> reply(key, name, element.getAsJsonObject(), names));
        return new DialogDef.Node(optional(json, "title"), Collections.unmodifiableList(lines), Collections.unmodifiableList(replies));
    }

    @Nullable private static DialogDef.Reply reply(String key, String node, JsonObject json, List<String> names) {
        Json.unknownKeys(json, "nodes." + node + ".replies", REPLY_KEYS);
        String text = Json.getString(json, "text", "");
        if (text.trim().isEmpty()) {
            ContentLog.LOGGER.error("Dialog {} node {} has a reply with no text, skipping it", key, node);
            return null;
        }
        boolean trade = Json.getBoolean(json, "trade", false);
        String target = trade ? "" : Json.getString(json, "goto", "").trim();
        if (!target.isEmpty() && !names.contains(target)) {
            ContentLog.LOGGER.error("Dialog {} node {} has a reply '{}' that goes to '{}', which is not one of its nodes, so it is skipped", key, node, text, target);
            return null;
        }
        CardWhen when = json.has("when") ? CardParser.when(Json.getJsonObject(json, "when"), new ResourceLocation(key), "Dialog") : null;
        return new DialogDef.Reply(text, target, Json.getString(json, "card", "").trim(), Json.getString(json, Json.RUNS, "").trim(), when, trade || target.isEmpty() && Json.getBoolean(json, "close", true), trade, DataSets.parse(json, "Dialog", key, "node " + node));
    }
}
