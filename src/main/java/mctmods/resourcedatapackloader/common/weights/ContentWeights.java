package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWeights;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentWeights {
    private static final int OWN = 1;
    private static final int NAME = 2;
    private static final int TAG = 3;
    private static final int PREFIX = 4;
    private static final Map<ResourceLocation, Integer> DEFINED = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Integer> OVERRIDDEN = new LinkedHashMap<>();
    private static final AtomicInteger GENERATION = new AtomicInteger();
    private static volatile State state = new State(new WeightDef.Builder().build(), new IdentityHashMap<>(), 0);
    private static volatile Parsed parsed = new Parsed(new WeightDef.Builder().build(), new ArrayList<>());

    private ContentWeights() {}

    public static int generation() { return GENERATION.get(); }

    public static void define(ResourceLocation name, int thousandths) { DEFINED.put(name, thousandths); }

    public static void override(ResourceLocation name, int thousandths) { OVERRIDDEN.put(name, thousandths); }

    public static void clearOverrides() { OVERRIDDEN.clear(); }

    public static void load() {
        if (!Config.content.weights()) {
            parsed = new Parsed(new WeightDef.Builder().build(), new ArrayList<>());
            state = new State(parsed.settings, new IdentityHashMap<>(), 0);
            GENERATION.incrementAndGet();
            WeightStacks.apply();
            return;
        }
        WeightDef.Builder settings = new WeightDef.Builder();
        List<Source> sources = new ArrayList<>();
        Json.eachObject(PackManager.WEIGHTS, "weight file", (key, json) -> read(settings, sources, key, json));
        own(sources, DEFINED);
        own(sources, OVERRIDDEN);
        parsed = new Parsed(settings.build(), sources);
        build();
        if (!sources.isEmpty()) { Summary.info("weights", "Loaded " + sources.size() + " weight entries"); }
    }

    public static void build() {
        Parsed now = parsed;
        Map<Item, Candidate> best = new IdentityHashMap<>();
        for (int order = 0; order < now.sources.size(); order++) {
            Source source = now.sources.get(order);
            Candidate candidate = new Candidate(source.level, order, source.weight);
            for (Item item : items(source.selection)) {
                Candidate held = best.get(item);
                if (candidate.beats(held)) { best.put(item, candidate); }
            }
        }
        Map<Item, Integer> table = new IdentityHashMap<>();
        best.forEach((item, candidate) -> table.put(item, candidate.weight));
        state = new State(now.settings, table, now.sources.size());
        GENERATION.incrementAndGet();
        WeightStacks.apply();
    }

    public static boolean any() {
        State now = state;
        return now.entries > 0 || now.settings.defaultWeight > 0 || now.settings.limited();
    }

    public static WeightDef settings() { return state.settings; }

    public static int of(ItemStack stack) {
        State now = state;
        if (stack.isEmpty()) { return 0; }
        Integer weight = now.table.get(stack.getItem());
        return weight == null ? now.settings.defaultWeight : weight;
    }

    public static int heaviest(Item item) {
        State now = state;
        Integer weight = now.table.get(item);
        return weight == null ? now.settings.defaultWeight : weight;
    }

    private static List<Item> items(ItemSelection selection) {
        List<Item> found = new ArrayList<>();
        for (ResourceLocation id : selection.items()) { found.add(ForgeRegistries.ITEMS.getValue(id)); }
        for (ResourceLocation tag : selection.tags()) {
            for (Item item : Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(TagKey.create(Registries.ITEM, tag))) { found.add(item); }
        }
        return found;
    }

    private static void own(List<Source> sources, Map<ResourceLocation, Integer> held) {
        for (Map.Entry<ResourceLocation, Integer> entry : held.entrySet()) {
            Item item = ForgeRegistries.ITEMS.getValue(entry.getKey());
            if (item == null || item == Items.AIR) {
                ContentLog.LOGGER.error("A weight is set on {}, which is no registered item or block, so it weighs nothing", entry.getKey());
                continue;
            }
            ItemSelection selection = new ItemSelection("Weight", "weighs");
            selection.read(only("names", Collections.singletonList(entry.getKey().toString())), entry.getKey());
            sources.add(new Source(OWN, selection, entry.getValue()));
        }
    }

    private static void read(WeightDef.Builder settings, List<Source> sources, ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        boolean defines = ContentParserWeights.apply(settings, key, json);
        int before = sources.size();
        if (json.has("items")) {
            for (JsonElement element : Json.getJsonArray(json, "items", new JsonArray())) {
                if (element.isJsonObject()) { entry(sources, key, element.getAsJsonObject()); }
                else { ContentLog.LOGGER.error("An items entry in weight file {} is not an object, skipping it", key); }
            }
        }
        if (!defines && sources.size() == before && !json.has("bonuses") && !json.has("tiers")) { ContentLog.LOGGER.error("Weight file {} sets no weight, default or limit, so it does nothing", key); }
    }

    private static void entry(List<Source> sources, ResourceLocation key, JsonObject entry) {
        if (!entry.has("weight")) {
            ContentLog.LOGGER.error("An items entry in weight file {} has no weight, skipping it", key);
            return;
        }
        int weight = ContentParserWeights.weight(entry, key);
        if (weight == ContentParserWeights.UNSET) { return; }
        List<String> plain = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        for (String name : Json.strings(entry, "names")) {
            if (name.endsWith("*") && name.split(":").length == 2) { prefixes.add(name); }
            else { plain.add(name); }
        }
        int before = sources.size();
        add(sources, key, NAME, "names", plain, weight);
        add(sources, key, TAG, "tags", Json.strings(entry, "tags"), weight);
        add(sources, key, PREFIX, "names", prefixes, weight);
        add(sources, key, PREFIX, "namespaces", Json.strings(entry, "namespaces"), weight);
        if (sources.size() == before) { ContentLog.LOGGER.error("An items entry in weight file {} selects no items, skipping it", key); }
    }

    private static void add(List<Source> sources, ResourceLocation key, int level, String member, List<String> values, int weight) {
        if (values.isEmpty()) { return; }
        ItemSelection selection = new ItemSelection("Weight", "weighs");
        selection.read(only(member, values), key);
        selection.unknown();
        sources.add(new Source(level, selection, weight));
    }

    private static JsonObject only(String member, List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) { array.add(value); }
        JsonObject json = new JsonObject();
        json.add(member, array);
        return json;
    }

    private record State(WeightDef settings, Map<Item, Integer> table, int entries) {}

    private record Parsed(WeightDef settings, List<Source> sources) {}

    private record Source(int level, ItemSelection selection, int weight) {}

    private record Candidate(int level, int order, int weight) {
        boolean beats(Candidate other) { return other == null || level < other.level || level == other.level && order > other.order; }
    }
}
