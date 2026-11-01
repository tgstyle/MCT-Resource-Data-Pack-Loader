package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AttributeDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

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

    @Nullable static ContainerUpgradeDef upgrade(ResourceLocation key, String type, JsonObject json) {
        if (!"container_upgrade".equals(type)) { return null; }
        if (!json.has("upgrade")) {
            ContentLog.LOGGER.error("The container upgrade {} has no upgrade block, so it fits nothing", key);
            return ContainerUpgradeDef.PLAIN;
        }
        JsonObject held = Json.getJsonObject(json, "upgrade", new JsonObject());
        List<ContainerUpgradeDef.Path> paths = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(held, "paths", new JsonArray())) {
            String from = element.isJsonObject() ? Json.getString(element.getAsJsonObject(), "fromBlock", "").trim() : "";
            String to = element.isJsonObject() ? Json.getString(element.getAsJsonObject(), "toBlock", "").trim() : "";
            if (from.isEmpty() || to.isEmpty()) {
                ContentLog.LOGGER.error("A path in the container upgrade {} names no fromBlock or no toBlock, skipping it", key);
                continue;
            }
            paths.add(new ContainerUpgradeDef.Path(from, to));
        }
        if (paths.isEmpty()) { ContentLog.LOGGER.error("The container upgrade {} has no valid path, so it fits nothing", key); }
        boolean carry = "carry".equals(Json.choice(held, "unopenedLoot", "refuse", "Container upgrade " + key, Arrays.asList("refuse", "carry")));
        return new ContainerUpgradeDef(Collections.unmodifiableList(paths), Json.getBoolean(held, "consume", true), carry);
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
