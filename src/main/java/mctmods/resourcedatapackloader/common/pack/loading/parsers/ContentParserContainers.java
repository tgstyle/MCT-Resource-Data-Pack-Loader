package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AttributeDef;
import mctmods.resourcedatapackloader.common.types.ContentItemTypes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public final class ContentParserContainers {
    private static final Map<String, String> BAUBLE_SLOTS = Map.of("amulet", "necklace", "ring", ContainerDef.RING_SLOT, "belt", "belt", "trinket", ContainerDef.ANY_SLOT, "head", "head", "body", "body", "charm", "charm");
    private static final Set<String> TOLD_BAUBLE = new HashSet<>();
    private static boolean toldNotBauble;

    private ContentParserContainers() {}

    static String remainder(JsonObject json) {
        String named = Json.getString(json, "containerItem", "").trim();
        if (!named.isEmpty()) { return named; }
        return Json.text(json, "container", "");
    }

    @Nullable static ContainerDef holds(ResourceLocation key, JsonObject json) { return GsonHelper.isObjectNode(json, "container") ? container(key, json) : null; }

    static boolean chested(JsonObject json) { return GsonHelper.isObjectNode(json, "container") && chestModel(json.getAsJsonObject("container")) != null; }

    @Nullable private static JsonElement chestModel(JsonObject held) {
        JsonElement asked = held.get("chestModel");
        return asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean()) ? asked : null;
    }

    @Nullable static ContainerDef container(ResourceLocation key, JsonObject json) {
        if (!json.has("container")) { return null; }
        JsonObject held = Json.getJsonObject(json, "container");
        int askedRows = Json.getInt(held, "rows", 3);
        int askedColumns = Json.getInt(held, "columns", 9);
        int rows = Mth.clamp(askedRows, 1, ContainerDef.MOST_ROWS);
        int columns = Mth.clamp(askedColumns, 1, ContainerDef.MOST_COLUMNS);
        if (askedRows != rows || askedColumns != columns) { ContentLog.LOGGER.error("The container on {} asks for {} by {}, which is past the largest a screen can show, so it is cut to {} by {}", key, askedColumns, askedRows, columns, rows); }
        String named = Settings.lower(Json.getString(held, "guiTexture", ""));
        ResourceLocation texture = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        int wide = Json.getInt(held, "guiWidth", 0);
        int tall = Json.getInt(held, "guiHeight", 0);
        if (texture != null && (wide <= 0 || tall <= 0)) {
            ContentLog.LOGGER.error("The container on {} names a guiTexture without a guiWidth and guiHeight, so the drawn background is used instead", key);
            texture = null;
        }
        JsonElement asked = chestModel(held);
        boolean chest = asked != null;
        ResourceLocation sheet = chest && asked.getAsJsonPrimitive().isString() ? sheetOf(Settings.lower(asked.getAsString()), key) : null;
        return new ContainerDef(rows, columns, Settings.lower(Json.getString(held, "lootTable", "")),
                chest, sheet, texture, wide, tall);
    }

    @Nullable static ContainerUpgradeDef upgrade(ResourceLocation key, String type, JsonObject json) {
        if (!ContentItemTypes.CONTAINER_UPGRADE.equals(type)) { return null; }
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
            paths.add(path(key, element.getAsJsonObject(), from, to));
        }
        if (paths.isEmpty()) { ContentLog.LOGGER.error("The container upgrade {} has no valid path, so it fits nothing", key); }
        boolean carry = "carry".equals(Json.choice(held, "unopenedLoot", "refuse", "Container upgrade " + key, List.of("refuse", "carry")));
        return new ContainerUpgradeDef(Collections.unmodifiableList(paths), Json.getBoolean(held, "consume", true), carry);
    }

    private static ContainerUpgradeDef.Path path(ResourceLocation key, JsonObject entry, String from, String to) {
        String owner = "Container upgrade " + key;
        ContainerUpgradeDef.Contents contents = ContainerUpgradeDef.Contents.valueOf(Json.choice(entry, "contents", "container", owner, List.of("container", "slots", "nbt")).toUpperCase(Locale.ROOT));
        for (String idle : idleKeys(contents)) {
            if (entry.has(idle)) { ContentLog.LOGGER.error("The container upgrade {} sets {} on the path from {}, which has no effect on this path", key, idle, from); }
        }
        boolean half = "half".equals(Json.choice(entry, "linked", "refuse", owner, List.of("refuse", "half")));
        boolean fit = "fit".equals(Json.choice(entry, "fewerSlots", "refuse", owner, List.of("refuse", "fit")));
        boolean carry = "carry".equals(Json.choice(entry, "state", "carry", owner, List.of("carry", "named")));
        return new ContainerUpgradeDef.Path(from, to, contents, half, fit, Json.getBoolean(entry, "strict", true), carry);
    }

    private static List<String> idleKeys(ContainerUpgradeDef.Contents contents) {
        if (contents == ContainerUpgradeDef.Contents.CONTAINER) { return List.of("linked", "fewerSlots", "strict", "state"); }
        return contents == ContainerUpgradeDef.Contents.NBT ? List.of("linked", "fewerSlots") : List.of();
    }

    static String curioSlot(ResourceLocation key, JsonObject json) {
        String top = slotNamed(key, json);
        if (!top.isEmpty() || !GsonHelper.isObjectNode(json, "container")) { return top; }
        return slotNamed(key, json.getAsJsonObject("container"));
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

    private static String slotNamed(ResourceLocation key, JsonObject held) {
        String asked = Settings.lower(Json.getString(held, "curioSlot", ""));
        if (!asked.isEmpty()) { return asked; }
        String old = Settings.lower(Json.getString(held, "bauble", ""));
        if (old.isEmpty()) { return ""; }
        String mapped = BAUBLE_SLOTS.get(old);
        if (mapped == null) {
            if (!toldNotBauble) {
                toldNotBauble = true;
                ContentLog.LOGGER.error("'{}' is not a Baubles slot, so the item is not worn. The slots are amulet, ring, belt, trinket, head, body and charm", old);
            }
            return "";
        }
        if (TOLD_BAUBLE.add(key.toString())) { ContentLog.LOGGER.info("{} names the 1.12.2 setting 'bauble' as '{}'. It is read as curioSlot '{}'; write curioSlot on this line", key, old, mapped); }
        return mapped;
    }

    @Nullable private static ResourceLocation sheetOf(String named, ResourceLocation key) {
        if (named.isEmpty()) {
            ContentLog.LOGGER.error("The container on {} names an empty chestModel texture, so the vanilla chest is drawn instead", key);
            return null;
        }
        ResourceLocation asked = ResourceLocation.tryParse(named);
        if (asked == null) {
            ContentLog.LOGGER.error("The container on {} names the chestModel texture '{}', which is not a valid id, so the vanilla chest is drawn instead", key, named);
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath(asked.getNamespace(), "textures/" + asked.getPath() + ".png");
    }
}
