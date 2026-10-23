package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentParserContainers {
    private ContentParserContainers() {}

    static String remainder(JsonObject json) {
        String named = Json.getString(json, "containerItem", "").trim();
        if (!named.isEmpty()) { return named; }
        return JsonUtils.isJsonPrimitive(json, "container") ? Json.getString(json, "container", "") : "";
    }

    @Nullable static ContainerDef holds(ResourceLocation key, JsonObject json) { return json.has("container") && json.get("container").isJsonObject() ? container(key, json) : null; }

    @Nullable static ContainerDef container(ResourceLocation key, JsonObject json) {
        if (!json.has("container")) { return null; }
        JsonObject held = Json.getJsonObject(json, "container");
        int askedRows = Json.getInt(held, "rows", 3);
        int askedColumns = Json.getInt(held, "columns", 9);
        int rows = MathHelper.clamp(askedRows, 1, ContainerDef.MOST_ROWS);
        int columns = MathHelper.clamp(askedColumns, 1, ContainerDef.MOST_COLUMNS);
        if (askedRows != rows || askedColumns != columns) { ContentLog.LOGGER.error("The container on {} asks for {} by {}, which is past the largest a screen can show, so it is cut to {} by {}", key, askedColumns, askedRows, columns, rows); }
        String named = Json.getString(held, "guiTexture", "").trim();
        ResourceLocation texture = named.isEmpty() ? null : new ResourceLocation(named);
        int wide = Json.getInt(held, "guiWidth", 0);
        int tall = Json.getInt(held, "guiHeight", 0);
        if (texture != null && (wide <= 0 || tall <= 0)) {
            ContentLog.LOGGER.error("The container on {} names a guiTexture without a guiWidth and guiHeight, so the drawn background is used instead", key);
            texture = null;
        }
        JsonElement asked = held.get("chestModel");
        boolean chest = asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean());
        ResourceLocation sheet = chest && asked.getAsJsonPrimitive().isString() ? sheetOf(asked.getAsString().trim(), key.toString()) : null;
        return new ContainerDef(rows, columns, Json.getString(held, "lootTable", "").trim(),
                chest, sheet, texture, wide, tall);
    }

    static String bauble(JsonObject json) {
        String named = Settings.lower(Json.getString(json, "bauble", ""));
        if (!named.isEmpty() || !json.has("container") || !json.get("container").isJsonObject()) { return named; }
        return Settings.lower(Json.getString(json.getAsJsonObject("container"), "bauble", ""));
    }

    static List<AttributeDef> wornAttributes(ResourceLocation key, JsonObject json) {
        List<AttributeDef> found = new ArrayList<>();
        if (!json.has("wornAttributes")) { return found; }
        for (JsonElement element : Json.getJsonArray(json, "wornAttributes")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A worn attribute in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String attribute = Json.getString(entry, "attribute", "");
            int operation = Json.getInt(entry, "operation", 0);
            if (attribute.isEmpty() || operation < 0 || operation > 2) {
                ContentLog.LOGGER.error("A worn attribute in {} names no attribute or an operation outside 0 to 2, skipping it", key);
                continue;
            }
            found.add(new AttributeDef(attribute, "", Json.getFloat(entry, "amount", 0.0F), operation));
        }
        return found;
    }

    static boolean chested(JsonObject json) {
        if (!json.has("container") || !json.get("container").isJsonObject()) { return false; }
        JsonElement asked = json.getAsJsonObject("container").get("chestModel");
        return asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean());
    }

    @Nullable private static ResourceLocation sheetOf(String named, String key) {
        if (named.isEmpty()) {
            ContentLog.LOGGER.error("The container on {} names an empty chestModel texture, so the vanilla chest is drawn instead", key);
            return null;
        }
        ResourceLocation asked = new ResourceLocation(named);
        return new ResourceLocation(asked.getNamespace(), "textures/" + asked.getPath() + ".png");
    }

    @Nullable static ResourceLocation opensWith(JsonObject json) {
        String named = Json.getString(json, "opensWith", "").trim();
        return named.isEmpty() ? null : new ResourceLocation(named);
    }
}
