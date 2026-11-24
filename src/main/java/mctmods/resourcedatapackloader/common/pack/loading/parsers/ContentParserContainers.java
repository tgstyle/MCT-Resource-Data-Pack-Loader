package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.blocks.compress.CompressedAssets;
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
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class ContentParserContainers {
    private static final List<String> CONTAINER_KEYS = Collections.unmodifiableList(Arrays.asList("rows", "columns", "guiTexture", "guiWidth", "guiHeight", "chestModel", "ender", "lootTable", "keepContents", "enderAutomation", "bauble"));
    private static final List<String> UPGRADE_KEYS = Collections.unmodifiableList(Arrays.asList("paths", "unopenedLoot", "consume"));
    private static final List<String> PATH_KEYS = Collections.unmodifiableList(Arrays.asList("fromBlock", "toBlock", "contents", "linked", "fewerSlots", "strict", "state"));
    private static final List<String> ATTRIBUTE_KEYS = Collections.unmodifiableList(Arrays.asList("attribute", "operation", "amount"));
    private static final List<String> BLOCK_KEYS = Collections.unmodifiableList(Arrays.asList("inherits", "variants", "type", "material", "mapColor", "soundType", "expDrop", "opaque", "maxAge", "creativeTab", "harvestTool", "harvestToolLevel", "silkHarvest", "explosionResistanceDivisor", "requires", "renderLayer", "fullCube", "lightOpacity", "slipperiness", "bounds", "flammability", "fireSpread", "modelBlock", "modelMeta", "itemModel", "particle", "smoke", "particleColor", "seed", "produce", "sapling", "portal", "growth", "plantTypes", "behavesAs", "tint", "leafSapling", "leafSaplingChance", "opensWith", "openSound", "container", "bell", "seasonSensor", "spreadsOnto", "needsOxygen", "portway", "weight", "tall", "blocksSpawns", "blocksWeather", "tagged", "compressable"));
    private static final List<String> ITEM_KEYS = Collections.unmodifiableList(Arrays.asList("inherits", "variants", "type", "creativeTab", "alwaysEdible", "requires", "useDuration", "eat", "material", "toolClass", "slot", "crop", "soil", "potionTypes", "attackSpeed", "cooldown", "rocket", "rolls", "rollStore", "passesTurn", "wornEffects", "sleepingBag", "runs", "scriptFile", "script", "locates", "locatesEye", "locatesSound", "locatesRange", "containerItem", "container", "bauble", "opensWith", "wornAttributes", "upgrade", "attackReach", "fireSeconds", "sweep", "combo", "finisher", "portway", "weight", "firework", "seasonClock", "jukebox"));

    private static final List<String> UNOPENED_LOOT = Collections.unmodifiableList(Arrays.asList("refuse", "carry"));
    private static final List<String> CONTENTS = Collections.unmodifiableList(Arrays.asList("container", "slots", "nbt"));
    private static final List<String> LINKED = Collections.unmodifiableList(Arrays.asList("refuse", "half"));
    private static final List<String> FEWER_SLOTS = Collections.unmodifiableList(Arrays.asList("refuse", "fit"));
    private static final List<String> STATE = Collections.unmodifiableList(Arrays.asList("carry", "named"));
    private static final List<String> IDLE_CONTAINER = Collections.unmodifiableList(Arrays.asList("linked", "fewerSlots", "strict", "state"));
    private static final List<String> IDLE_NBT = Collections.unmodifiableList(Arrays.asList("linked", "fewerSlots"));

    private ContentParserContainers() {}

    static void itemKeys(JsonObject json) { Json.unknownKeys(json, "the top level", ITEM_KEYS); }

    static void blockKeys(JsonObject json) { Json.unknownKeys(json, "the top level", BLOCK_KEYS); }

    static String remainder(JsonObject json) {
        String named = Json.getString(json, "containerItem", "").trim();
        if (!named.isEmpty()) { return named; }
        return JsonUtils.isJsonPrimitive(json, "container") ? Json.getString(json, "container", "") : "";
    }

    @Nullable static ContainerDef holds(ResourceLocation key, JsonObject json) { return containerObject(json) == null ? null : container(key, json); }

    @Nullable private static JsonObject containerObject(JsonObject json) { return Json.objectOrNull(json, "container"); }

    private static boolean chestModel(@Nullable JsonElement asked) { return asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean()); }

    @Nullable static ContainerDef container(ResourceLocation key, JsonObject json) {
        if (!json.has("container")) { return null; }
        JsonObject held = Json.getJsonObject(json, "container");
        Json.unknownKeys(held, "container", CONTAINER_KEYS);
        int askedRows = Json.getInt(held, "rows", 3);
        int askedColumns = Json.getInt(held, "columns", 9);
        int rows = MathHelper.clamp(askedRows, 1, ContainerDef.MOST_ROWS);
        int columns = MathHelper.clamp(askedColumns, 1, ContainerDef.MOST_COLUMNS);
        if (askedRows != rows || askedColumns != columns) { ContentLog.LOGGER.error("The container on {} asks for {} by {}, which is past the largest a screen can show, so it is cut to {} by {}", key, askedColumns, askedRows, columns, rows); }
        ResourceLocation texture = Json.location(held, "guiTexture");
        int wide = Json.getInt(held, "guiWidth", 0);
        int tall = Json.getInt(held, "guiHeight", 0);
        if (texture != null && (wide <= 0 || tall <= 0)) {
            ContentLog.LOGGER.error("The container on {} names a guiTexture without a guiWidth and guiHeight, so the drawn background is used instead", key);
            texture = null;
        }
        JsonElement asked = held.get("chestModel");
        boolean chest = chestModel(asked);
        ResourceLocation sheet = chest && asked.getAsJsonPrimitive().isString() ? sheetOf(asked.getAsString().trim(), key.toString()) : null;
        boolean ender = Json.getBoolean(held, "ender", false);
        String loot = Json.getString(held, "lootTable", "").trim();
        boolean keep = Json.getBoolean(held, "keepContents", false);
        if (ender && (!loot.isEmpty() || keep)) { ContentLog.LOGGER.error("The container on {} sets ender, which opens each player's own store, so its lootTable and keepContents are ignored", key); }
        boolean automation = Json.getBoolean(held, "enderAutomation", false);
        if (automation && !ender) { ContentLog.LOGGER.error("The container on {} sets enderAutomation without ender, so it is ignored", key); }
        return new ContainerDef(rows, columns, ender ? "" : loot, chest, sheet, texture, wide, tall, keep && !ender, ender, ender && automation);
    }

    @Nullable static ContainerUpgradeDef upgrade(ResourceLocation key, String type, JsonObject json) {
        if (!"container_upgrade".equals(type)) { return null; }
        if (!json.has("upgrade")) {
            ContentLog.LOGGER.error("The container upgrade {} has no upgrade block, so it fits nothing", key);
            return ContainerUpgradeDef.PLAIN;
        }
        JsonObject held = Json.getJsonObject(json, "upgrade", new JsonObject());
        Json.unknownKeys(held, "upgrade", UPGRADE_KEYS);
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
        boolean carry = Json.choiceIs(held, "unopenedLoot", "refuse", "Container upgrade " + key, UNOPENED_LOOT, "carry");
        return new ContainerUpgradeDef(Collections.unmodifiableList(paths), Json.getBoolean(held, "consume", true), carry);
    }

    private static ContainerUpgradeDef.Path path(ResourceLocation key, JsonObject entry, String from, String to) {
        String owner = "Container upgrade " + key;
        Json.unknownKeys(entry, "upgrade.paths", PATH_KEYS);
        ContainerUpgradeDef.Contents contents = ContainerUpgradeDef.Contents.valueOf(Json.choice(entry, "contents", "container", owner, CONTENTS).toUpperCase(Locale.ROOT));
        for (String idle : Json.present(entry, idleKeys(contents))) { ContentLog.LOGGER.error("The container upgrade {} sets {} on the path from {}, which has no effect on this path", key, idle, from); }
        boolean half = Json.choiceIs(entry, "linked", "refuse", owner, LINKED, "half");
        boolean fit = Json.choiceIs(entry, "fewerSlots", "refuse", owner, FEWER_SLOTS, "fit");
        boolean carry = Json.choiceIs(entry, "state", "carry", owner, STATE, "carry");
        return new ContainerUpgradeDef.Path(from, to, contents, half, fit, Json.getBoolean(entry, "strict", true), carry);
    }

    private static List<String> idleKeys(ContainerUpgradeDef.Contents contents) {
        if (contents == ContainerUpgradeDef.Contents.CONTAINER) { return IDLE_CONTAINER; }
        return contents == ContainerUpgradeDef.Contents.NBT ? IDLE_NBT : Collections.emptyList();
    }

    static String bauble(JsonObject json) {
        String named = Settings.lower(Json.getString(json, "bauble", ""));
        JsonObject held = containerObject(json);
        if (!named.isEmpty() || held == null) { return named; }
        return Settings.lower(Json.getString(held, "bauble", ""));
    }

    static List<AttributeDef> wornAttributes(ResourceLocation key, JsonObject json) {
        List<AttributeDef> found = new ArrayList<>();
        if (!json.has("wornAttributes")) { return found; }
        for (JsonObject entry : Json.objectsOrSkip(Json.getJsonArray(json, "wornAttributes"), "A worn attribute in {} is not an object, skipping it", key)) {
            Json.unknownKeys(entry, "wornAttributes", ATTRIBUTE_KEYS);
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
        JsonObject held = containerObject(json);
        return held != null && chestModel(held.get("chestModel"));
    }

    @Nullable private static ResourceLocation sheetOf(String named, String key) {
        if (named.isEmpty()) {
            ContentLog.LOGGER.error("The container on {} names an empty chestModel texture, so the vanilla chest is drawn instead", key);
            return null;
        }
        ResourceLocation asked = new ResourceLocation(named);
        return new ResourceLocation(asked.getNamespace(), CompressedAssets.texture(asked.getPath()));
    }
}
