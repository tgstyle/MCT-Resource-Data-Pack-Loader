package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.HashSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.Locale;
import java.util.Set;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentParserContainers {
    private static final Map<String, String> BAUBLE_SLOTS = Map.of("amulet", "necklace", "ring", ContainerDef.RING_SLOT, "belt", "belt", "trinket", ContainerDef.ANY_SLOT, "head", "head", "body", "body", "charm", "charm");
    private static final Set<String> TOLD_BAUBLE = new HashSet<>();
    private static boolean toldNotBauble;

    private ContentParserContainers() {}

    static String remainder(JsonObject json) {
        String named = GsonHelper.getAsString(json, "containerItem", "").trim();
        if (!named.isEmpty()) { return named; }
        return json.has("container") && json.get("container").isJsonPrimitive() ? GsonHelper.getAsString(json, "container", "").trim() : "";
    }

    @Nullable static ContainerDef holds(ResourceLocation key, JsonObject json) {
        return json.has("container") && json.get("container").isJsonObject() ? container(key, json) : null;
    }

    static boolean chested(JsonObject json) {
        if (!json.has("container") || !json.get("container").isJsonObject()) { return false; }
        JsonElement asked = json.getAsJsonObject("container").get("chestModel");
        return asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean());
    }

    @Nullable static ContainerDef container(ResourceLocation key, JsonObject json) {
        if (!json.has("container")) { return null; }
        JsonObject held = GsonHelper.getAsJsonObject(json, "container");
        int askedRows = GsonHelper.getAsInt(held, "rows", 3);
        int askedColumns = GsonHelper.getAsInt(held, "columns", 9);
        int rows = Mth.clamp(askedRows, 1, ContainerDef.MOST_ROWS);
        int columns = Mth.clamp(askedColumns, 1, ContainerDef.MOST_COLUMNS);
        if (askedRows != rows || askedColumns != columns) {
            ContentLog.LOGGER.error("The container on {} asks for {} by {}, which is past the largest a screen can show, so it is cut to {} by {}", key, askedColumns, askedRows, columns, rows);
        }
        String named = GsonHelper.getAsString(held, "guiTexture", "").trim().toLowerCase(Locale.ROOT);
        ResourceLocation texture = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        int wide = GsonHelper.getAsInt(held, "guiWidth", 0);
        int tall = GsonHelper.getAsInt(held, "guiHeight", 0);
        if (texture != null && (wide <= 0 || tall <= 0)) {
            ContentLog.LOGGER.error("The container on {} names a guiTexture without a guiWidth and guiHeight, so the drawn background is used instead", key);
            texture = null;
        }
        JsonElement asked = held.get("chestModel");
        boolean chest = asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean());
        ResourceLocation sheet = chest && asked.getAsJsonPrimitive().isString() ? sheetOf(asked.getAsString().trim().toLowerCase(Locale.ROOT), key) : null;
        return new ContainerDef(rows, columns, GsonHelper.getAsString(held, "lootTable", "").trim().toLowerCase(Locale.ROOT),
                chest, sheet, texture, wide, tall, curioSlot(key, held));
    }

    private static String curioSlot(ResourceLocation key, JsonObject held) {
        String asked = GsonHelper.getAsString(held, "curioSlot", "").trim().toLowerCase(Locale.ROOT);
        if (!asked.isEmpty()) { return asked; }
        String old = GsonHelper.getAsString(held, "bauble", "").trim().toLowerCase(Locale.ROOT);
        if (old.isEmpty()) { return ""; }
        String mapped = BAUBLE_SLOTS.get(old);
        if (mapped == null) {
            if (!toldNotBauble) {
                toldNotBauble = true;
                ContentLog.LOGGER.error("'{}' is not a Baubles slot, so the item is not worn. The slots are amulet, ring, belt, trinket, head, body and charm", old);
            }
            return "";
        }
        if (TOLD_BAUBLE.add(key.toString())) {
            ContentLog.LOGGER.info("The container on {} names the 1.12.2 setting 'bauble' as '{}'. It is read as curioSlot '{}'; write curioSlot on this line", key, old, mapped);
        }
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
