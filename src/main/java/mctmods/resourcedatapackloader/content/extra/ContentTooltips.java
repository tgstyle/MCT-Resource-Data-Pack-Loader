package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContentTooltips {
    private static final Map<Item, List<String>> ITEMS = new HashMap<>();
    private static final Map<Identifier, List<String>> ENCHANTMENTS = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentTooltips() {}

    public static boolean none() {
        if (!GENERATION.stale()) { return ITEMS.isEmpty() && ENCHANTMENTS.isEmpty(); }
        ITEMS.clear();
        ENCHANTMENTS.clear();
        if (!Config.content.tooltips() || Config.content.vanillaClients()) { return true; }
        Json.eachObject(PackManager.TOOLTIPS, "tooltip file", ContentTooltips::read);
        boolean none = ITEMS.isEmpty() && ENCHANTMENTS.isEmpty();
        if (!none) { Summary.info("tooltips", "Loaded tooltip lines for " + ITEMS.size() + " item(s) and " + ENCHANTMENTS.size() + " enchantment(s)"); }
        return none;
    }

    private static void read(Identifier key, JsonObject json) {
        for (JsonElement element : Json.getJsonArray(json, "items", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An items entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            List<String> lines = Json.strings(entry, "lines");
            if (lines.isEmpty()) {
                ContentLog.LOGGER.error("An items entry in {} has no lines, skipping it", key);
                continue;
            }
            Item item = ContentStacks.find(key, Json.getString(entry, "item", ""));
            if (item != null) { ITEMS.computeIfAbsent(item, ignored -> new ArrayList<>()).addAll(lines); }
        }
        for (JsonElement element : Json.getJsonArray(json, "enchantments", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An enchantments entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            List<String> lines = Json.strings(entry, "lines");
            String name = Json.getString(entry, "enchantment", "");
            Identifier enchantment = Identifier.tryParse(name);
            if (enchantment == null || lines.isEmpty()) {
                ContentLog.LOGGER.error("An enchantments entry in {} needs an enchantment id and at least one line, skipping '{}'", key, name);
                continue;
            }
            ENCHANTMENTS.computeIfAbsent(enchantment, ignored -> new ArrayList<>()).addAll(lines);
        }
    }

    public static List<String> forItem(ItemStack stack) { return ITEMS.getOrDefault(stack.getItem(), List.of()); }

    public static List<String> forEnchantment(Identifier enchantment) { return ENCHANTMENTS.getOrDefault(enchantment, List.of()); }
}
