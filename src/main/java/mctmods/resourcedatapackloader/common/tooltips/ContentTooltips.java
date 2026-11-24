package mctmods.resourcedatapackloader.common.tooltips;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.syncdata.DataCheck;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentTooltips {
    public static final int NO_POSITION = Integer.MIN_VALUE;
    private static final String DATA = "data";
    private static final String SHIFT = "shift";
    private static final String POSITION = "position";
    private static final String REPLACE = "replace";
    private static final List<String> FILE_KEYS = List.of("items", "enchantments");
    private static final List<String> ITEM_KEYS = List.of("item", "lines", SHIFT, POSITION, REPLACE, "when");
    private static final List<String> ENCHANTMENT_KEYS = List.of("enchantment", "lines", SHIFT, POSITION, REPLACE, "when");
    private static final Map<Item, List<Rule>> ITEMS = new HashMap<>();
    private static final Map<Enchantment, List<Rule>> ENCHANTMENTS = new HashMap<>();
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

    @Nullable private static Rule rule(ResourceLocation key, JsonObject entry, String kind, List<String> lines) {
        boolean shift = Json.getBoolean(entry, SHIFT, false);
        boolean replace = Json.getBoolean(entry, REPLACE, false);
        int position = Json.getInt(entry, POSITION, NO_POSITION);
        if (position < 0 && position != NO_POSITION) {
            ContentLog.LOGGER.error("An {} entry in {} has position {}, which is not a whole number of 0 or more, skipping it", kind, key, position);
            return null;
        }
        if (replace && position == NO_POSITION) {
            ContentLog.LOGGER.error("An {} entry in {} sets replace without position, so its lines are added at the end", kind, key);
            replace = false;
        }
        List<DataCheck> data = Collections.emptyList();
        JsonObject when = Json.object(entry, "when");
        for (Map.Entry<String, JsonElement> each : when.entrySet()) {
            if (!DATA.equals(each.getKey())) {
                ContentLog.LOGGER.error("A tooltip entry in {} has a when key '{}', which tooltips cannot test, skipping the entry", key, each.getKey());
                return null;
            }
            data = DataCheck.parse(each.getValue(), "A tooltip entry", "when", key);
        }
        List<Line> parsed = new ArrayList<>();
        for (String raw : lines) { parsed.add(new Line(key, raw)); }
        return new Rule(parsed, shift, position, replace, data);
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        for (JsonObject entry : Json.objectsOrSkip(Json.getJsonArray(json, "items", new JsonArray()), "An items entry in {} is not an object, skipping it", key)) {
            Json.unknownKeys(entry, "items", ITEM_KEYS);
            List<String> lines = Json.strings(entry, "lines");
            if (lines.isEmpty()) {
                ContentLog.LOGGER.error("An items entry in {} has no lines, skipping it", key);
                continue;
            }
            Item item = ContentStacks.find(key, Json.getString(entry, "item", ""));
            if (item == null) { continue; }
            Rule rule = rule(key, entry, "items", lines);
            if (rule != null) { ITEMS.computeIfAbsent(item, ignored -> new ArrayList<>()).add(rule); }
        }
        for (JsonObject entry : Json.objectsOrSkip(Json.getJsonArray(json, "enchantments", new JsonArray()), "An enchantments entry in {} is not an object, skipping it", key)) {
            Json.unknownKeys(entry, "enchantments", ENCHANTMENT_KEYS);
            List<String> lines = Json.strings(entry, "lines");
            String name = Json.getString(entry, "enchantment", "");
            Enchantment enchantment = Registered.find(ForgeRegistries.ENCHANTMENTS, ResourceLocation.tryParse(name));
            if (enchantment == null || lines.isEmpty()) {
                ContentLog.LOGGER.error("An enchantments entry in {} needs a known enchantment and at least one line, skipping '{}'", key, name);
                continue;
            }
            Rule rule = rule(key, entry, "enchantments", lines);
            if (rule != null) { ENCHANTMENTS.computeIfAbsent(enchantment, ignored -> new ArrayList<>()).add(rule); }
        }
    }

    public static List<Rule> forItem(ItemStack stack) { return ITEMS.getOrDefault(stack.getItem(), List.of()); }

    public static List<Rule> forEnchantment(Enchantment enchantment) { return ENCHANTMENTS.getOrDefault(enchantment, List.of()); }

    public static final class Line {
        private final Object file;
        private final String raw;
        private String seen;
        private TooltipText seenText;

        private Line(Object file, String raw) {
            this.file = file;
            this.raw = raw;
            this.seen = raw;
            this.seenText = TooltipText.parse(file, raw);
        }

        public String raw() { return raw; }

        public String render(ItemStack stack, String translated) {
            if (translated.indexOf('{') < 0) { return translated; }
            if (!translated.equals(seen)) {
                seen = translated;
                seenText = TooltipText.parse(file, translated);
            }
            return seenText.plain() ? translated : seenText.fill(stack);
        }
    }

    public static final class Rule {
        public final List<Line> lines;
        public final boolean shift;
        public final int position;
        public final boolean replace;
        public final List<DataCheck> data;

        private Rule(List<Line> lines, boolean shift, int position, boolean replace, List<DataCheck> data) {
            this.data = data;
            this.lines = lines;
            this.shift = shift;
            this.position = position;
            this.replace = replace;
        }
    }
}
