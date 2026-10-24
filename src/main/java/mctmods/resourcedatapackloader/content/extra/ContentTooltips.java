package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContentTooltips {
    private static final List<Entry> ITEMS = new ArrayList<>();
    private static final Map<Enchantment, List<String>> ENCHANTMENTS = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentTooltips() {}

    public static boolean none() {
        if (GENERATION.stale()) {
            ITEMS.clear();
            ENCHANTMENTS.clear();
            if (Config.content.tooltips && !Config.content.vanillaClients) {
                Json.eachObject(PackManager.TOOLTIPS, "tooltip file", ContentTooltips::read);
                if (wanted()) { Summary.info("tooltips", "Loaded tooltip lines for " + ITEMS.size() + " item entry/entries and " + ENCHANTMENTS.size() + " enchantment(s)"); }
            }
        }
        return !wanted();
    }

    private static boolean wanted() { return !ITEMS.isEmpty() || !ENCHANTMENTS.isEmpty(); }

    private static void read(ResourceLocation key, JsonObject json) {
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
            ItemStack stack = ContentStacks.parse(key, Json.getString(entry, "item", ""), 1);
            if (!stack.isEmpty()) { ITEMS.add(new Entry(stack, lines)); }
        }
        for (JsonElement element : Json.getJsonArray(json, "enchantments", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An enchantments entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            List<String> lines = Json.strings(entry, "lines");
            String name = Json.getString(entry, "enchantment", "");
            Enchantment enchantment = name.isEmpty() ? null : Registries.find(ForgeRegistries.ENCHANTMENTS, new ResourceLocation(name));
            if (enchantment == null || lines.isEmpty()) {
                ContentLog.LOGGER.error("An enchantments entry in {} needs a known enchantment and at least one line, skipping '{}'", key, name);
                continue;
            }
            ENCHANTMENTS.computeIfAbsent(enchantment, ignored -> new ArrayList<>()).addAll(lines);
        }
    }

    public static List<String> forItem(ItemStack stack) {
        List<String> lines = new ArrayList<>();
        for (Entry entry : ITEMS) {
            if (ContentStacks.matches(stack, entry.stack.getItem(), entry.stack.getMetadata())) { lines.addAll(entry.lines); }
        }
        return lines;
    }

    public static List<String> forEnchantment(Enchantment enchantment) { return ENCHANTMENTS.getOrDefault(enchantment, new ArrayList<>()); }

    private static final class Entry {
        private final ItemStack stack;
        private final List<String> lines;

        private Entry(ItemStack stack, List<String> lines) {
            this.stack = stack;
            this.lines = lines;
        }
    }
}
