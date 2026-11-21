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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public final class ContentParserContainers {
    private static final Map<String, String> BAUBLE_SLOTS = Map.of("amulet", "necklace", "ring", ContainerDef.RING_SLOT, "belt", "belt", "trinket", ContainerDef.ANY_SLOT, "head", "head", "body", "body", "charm", "charm");
    private static final Set<String> TOLD_BAUBLE = new HashSet<>();
    private static final List<String> CONTAINER_KEYS = List.of("rows", "columns", "guiTexture", "guiWidth", "guiHeight", "chestModel", "ender", "lootTable", "keepContents", "enderAutomation", "curioSlot", "bauble");
    private static final List<String> UPGRADE_KEYS = List.of("paths", "unopenedLoot", "consume");
    private static final List<String> PATH_KEYS = List.of("fromBlock", "toBlock", "contents", "linked", "fewerSlots", "strict", "state");
    private static final List<String> ATTRIBUTE_KEYS = List.of("attribute", "operation", "amount");
    private static final List<String> BLOCK_KEYS = List.of("inherits", "variants", "type", "material", "mapColor", "soundType", "expDrop", "opaque", "maxAge", "creativeTab", "harvestTool", "harvestToolLevel", "silkHarvest", "explosionResistanceDivisor", "requires", "renderLayer", "fullCube", "lightOpacity", "slipperiness", "bounds", "flammability", "fireSpread", "modelBlock", "itemModel", "particle", "smoke", "particleColor", "seed", "produce", "sapling", "portal", "growth", "plantTypes", "behavesAs", "tint", "leafSapling", "leafSaplingChance", "opensWith", "openSound", "container", "bell", "seasonSensor", "spreadsOnto", "needsOxygen", "portway", "weight", "tall", "blocksSpawns", "blocksWeather", "tagged", "compressable");
    private static final List<String> ITEM_KEYS = List.of("inherits", "variants", "type", "creativeTab", "alwaysEdible", "requires", "useDuration", "eat", "material", "toolClass", "slot", "crop", "soil", "potionTypes", "attackSpeed", "cooldown", "rocket", "rolls", "rollStore", "passesTurn", "wornEffects", "sleepingBag", "runs", "scriptFile", "script", "locates", "locatesEye", "locatesSound", "locatesRange", "containerItem", "container", "curioSlot", "bauble", "wornAttributes", "upgrade", "attackReach", "fireSeconds", "sweep", "combo", "finisher", "portway", "weight", "firework", "seasonClock", "jukebox");
    private static boolean toldNotBauble;

    private ContentParserContainers() {}

    static void itemKeys(JsonObject json) { Json.unknownKeys(json, "the top level", ITEM_KEYS); }

    static void blockKeys(JsonObject json) { Json.unknownKeys(json, "the top level", BLOCK_KEYS); }

    static String remainder(JsonObject json) {
        String named = Json.getString(json, "containerItem", "").trim();
        if (!named.isEmpty()) { return named; }
        return Json.text(json, "container", "");
    }

    @Nullable static ContainerDef holds(Identifier key, JsonObject json) { return GsonHelper.isObjectNode(json, "container") ? container(key, json) : null; }

    static boolean chested(JsonObject json) { return GsonHelper.isObjectNode(json, "container") && chestModel(json.getAsJsonObject("container")) != null; }

    @Nullable private static JsonElement chestModel(JsonObject held) {
        JsonElement asked = held.get("chestModel");
        return asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean()) ? asked : null;
    }

    @Nullable static ContainerDef container(Identifier key, JsonObject json) {
        if (!json.has("container")) { return null; }
        JsonObject held = Json.getJsonObject(json, "container");
        Json.unknownKeys(held, "container", CONTAINER_KEYS);
        int askedRows = Json.getInt(held, "rows", 3);
        int askedColumns = Json.getInt(held, "columns", 9);
        int rows = Mth.clamp(askedRows, 1, ContainerDef.MOST_ROWS);
        int columns = Mth.clamp(askedColumns, 1, ContainerDef.MOST_COLUMNS);
        if (askedRows != rows || askedColumns != columns) { ContentLog.LOGGER.error("The container on {} asks for {} by {}, which is past the largest a screen can show, so it is cut to {} by {}", key, askedColumns, askedRows, columns, rows); }
        String named = Settings.lower(Json.getString(held, "guiTexture", ""));
        Identifier texture = named.isEmpty() ? null : Identifier.tryParse(named);
        int wide = Json.getInt(held, "guiWidth", 0);
        int tall = Json.getInt(held, "guiHeight", 0);
        if (texture != null && (wide <= 0 || tall <= 0)) {
            ContentLog.LOGGER.error("The container on {} names a guiTexture without a guiWidth and guiHeight, so the drawn background is used instead", key);
            texture = null;
        }
        JsonElement asked = chestModel(held);
        boolean chest = asked != null;
        Identifier sheet = chest && asked.getAsJsonPrimitive().isString() ? sheetOf(Settings.lower(asked.getAsString()), key) : null;
        boolean ender = Json.getBoolean(held, "ender", false);
        String loot = Settings.lower(Json.getString(held, "lootTable", ""));
        boolean keep = Json.getBoolean(held, "keepContents", false);
        if (ender && (!loot.isEmpty() || keep)) { ContentLog.LOGGER.error("The container on {} sets ender, which opens each player's own store, so its lootTable and keepContents are ignored", key); }
        boolean automation = Json.getBoolean(held, "enderAutomation", false);
        if (automation && !ender) { ContentLog.LOGGER.error("The container on {} sets enderAutomation without ender, so it is ignored", key); }
        return new ContainerDef(rows, columns, ender ? "" : loot, chest, sheet, texture, wide, tall, keep && !ender, ender, ender && automation);
    }

    @Nullable static ContainerUpgradeDef upgrade(Identifier key, JsonObject json) {
        if (!"container_upgrade".equals(Settings.lower(Json.getString(json, "type", "")))) { return null; }
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
        boolean carry = "carry".equals(Json.choice(held, "unopenedLoot", "refuse", "Container upgrade " + key, Arrays.asList("refuse", "carry")));
        return new ContainerUpgradeDef(Collections.unmodifiableList(paths), Json.getBoolean(held, "consume", true), carry);
    }

    private static ContainerUpgradeDef.Path path(Identifier key, JsonObject entry, String from, String to) {
        String owner = "Container upgrade " + key;
        Json.unknownKeys(entry, "upgrade.paths", PATH_KEYS);
        ContainerUpgradeDef.Contents contents = ContainerUpgradeDef.Contents.valueOf(Json.choice(entry, "contents", "container", owner, Arrays.asList("container", "slots", "nbt")).toUpperCase(Locale.ROOT));
        for (String idle : idleKeys(contents)) {
            if (entry.has(idle)) { ContentLog.LOGGER.error("The container upgrade {} sets {} on the path from {}, which has no effect on this path", key, idle, from); }
        }
        boolean half = "half".equals(Json.choice(entry, "linked", "refuse", owner, Arrays.asList("refuse", "half")));
        boolean fit = "fit".equals(Json.choice(entry, "fewerSlots", "refuse", owner, Arrays.asList("refuse", "fit")));
        boolean carry = "carry".equals(Json.choice(entry, "state", "carry", owner, Arrays.asList("carry", "named")));
        return new ContainerUpgradeDef.Path(from, to, contents, half, fit, Json.getBoolean(entry, "strict", true), carry);
    }

    private static List<String> idleKeys(ContainerUpgradeDef.Contents contents) {
        if (contents == ContainerUpgradeDef.Contents.CONTAINER) { return Arrays.asList("linked", "fewerSlots", "strict", "state"); }
        return contents == ContainerUpgradeDef.Contents.NBT ? Arrays.asList("linked", "fewerSlots") : Collections.emptyList();
    }

    static String curioSlot(Identifier key, JsonObject json) {
        String top = slotNamed(key, json);
        if (!top.isEmpty() || !GsonHelper.isObjectNode(json, "container")) { return top; }
        return slotNamed(key, json.getAsJsonObject("container"));
    }

    static List<AttributeDef> wornAttributes(Identifier key, JsonObject json) {
        List<AttributeDef> found = new ArrayList<>();
        if (!json.has("wornAttributes")) { return found; }
        for (JsonElement element : Json.getJsonArray(json, "wornAttributes")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A worn attribute in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            Json.unknownKeys(entry, "wornAttributes", ATTRIBUTE_KEYS);
            String attribute = Json.getString(entry, "attribute", "");
            int operation = Json.getInt(entry, "operation", 0);
            if (attribute.isEmpty() || operation < 0 || operation > 2) {
                ContentLog.LOGGER.error("A worn attribute in {} names no attribute or an operation outside 0 to 2, skipping it", key);
                continue;
            }
            found.add(new AttributeDef(attribute, Json.getFloat(entry, "amount", 0.0F), operation));
        }
        return found;
    }

    private static String slotNamed(Identifier key, JsonObject held) {
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

    @Nullable private static Identifier sheetOf(String named, Identifier key) {
        if (named.isEmpty()) {
            ContentLog.LOGGER.error("The container on {} names an empty chestModel texture, so the vanilla chest is drawn instead", key);
            return null;
        }
        Identifier asked = Identifier.tryParse(named);
        if (asked == null) {
            ContentLog.LOGGER.error("The container on {} names the chestModel texture '{}', which is not a valid id, so the vanilla chest is drawn instead", key, named);
            return null;
        }
        return Identifier.fromNamespaceAndPath(asked.getNamespace(), "textures/" + asked.getPath() + ".png");
    }
}
