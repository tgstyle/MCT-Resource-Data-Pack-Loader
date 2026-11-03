package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWeights;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
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
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.DefaultDataComponentsBoundEvent;

public final class ContentWeights {
    private static final int OWN = 1;
    private static final int NAME = 2;
    private static final int TAG = 3;
    private static final int PREFIX = 4;
    private static final Map<Identifier, Integer> DEFINED = new LinkedHashMap<>();
    private static final Map<Identifier, Integer> OVERRIDDEN = new LinkedHashMap<>();
    private static final AtomicInteger GENERATION = new AtomicInteger();
    private static volatile State state = State.none();

    private ContentWeights() {}

    public static int generation() { return GENERATION.get(); }

    public static void define(Identifier name, int thousandths) { DEFINED.put(name, thousandths); }

    public static void override(Identifier name, int thousandths) { OVERRIDDEN.put(name, thousandths); }

    public static void clearOverrides() { OVERRIDDEN.clear(); }

    public static void tagsUpdated() {
        State now = state;
        if (now.sources.isEmpty()) { return; }
        state = now.bound();
        GENERATION.incrementAndGet();
        WeightStacks.bindFloors();
        WeightStacks.apply();
    }

    public static void componentsBound(DefaultDataComponentsBoundEvent event) {
        if (event.getUpdateCause() != DefaultDataComponentsBoundEvent.UpdateCause.CLIENT_PACKET_RECEIVED || !event.shouldUpdateStaticData()) { return; }
        load();
    }

    public static void load() {
        GENERATION.incrementAndGet();
        if (!Config.content.weights()) {
            state = State.none();
            WeightStacks.apply();
            return;
        }
        WeightDef.Builder settings = new WeightDef.Builder();
        List<Source> sources = new ArrayList<>();
        Json.eachObject(PackManager.WEIGHTS, "weight file", (key, json) -> read(settings, sources, key, json));
        own(sources, DEFINED);
        own(sources, OVERRIDDEN);
        state = new State(settings.build(), sources, new IdentityHashMap<>()).bound();
        WeightStacks.apply();
        if (!sources.isEmpty()) { Summary.info("weights", "Loaded " + sources.size() + " weight entries"); }
    }

    public static boolean any() {
        State now = state;
        return !now.sources.isEmpty() || now.settings.defaultWeight > 0 || now.settings.limited();
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

    private static void own(List<Source> sources, Map<Identifier, Integer> held) {
        for (Map.Entry<Identifier, Integer> entry : held.entrySet()) {
            Item item = Registered.find(BuiltInRegistries.ITEM, entry.getKey());
            if (item == null || item == Items.AIR) {
                ContentLog.LOGGER.error("A weight is set on {}, which is no registered item or block, so it weighs nothing", entry.getKey());
                continue;
            }
            sources.add(new Source(OWN, Collections.singletonList(item), Collections.emptyList(), entry.getValue()));
        }
    }

    private static void read(WeightDef.Builder settings, List<Source> sources, Identifier key, JsonObject json) {
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

    private static void entry(List<Source> sources, Identifier key, JsonObject entry) {
        if (!entry.has("weight")) {
            ContentLog.LOGGER.error("An items entry in weight file {} has no weight, skipping it", key);
            return;
        }
        int weight = ContentParserWeights.weight(entry, key);
        if (weight == ContentParserWeights.UNSET) { return; }
        List<String> plain = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        for (String name : Json.strings(entry, "names")) {
            if (ItemSelection.isPrefixName(name)) { prefixes.add(name); }
            else { plain.add(name); }
        }
        int before = sources.size();
        add(sources, key, NAME, "names", plain, weight);
        add(sources, key, TAG, "tags", Json.strings(entry, "tags"), weight);
        add(sources, key, PREFIX, "names", prefixes, weight);
        add(sources, key, PREFIX, "namespaces", Json.strings(entry, "namespaces"), weight);
        if (sources.size() == before) { ContentLog.LOGGER.error("An items entry in weight file {} selects no items, skipping it", key); }
        if (entry.has("oreDict")) { ContentLog.LOGGER.warn("An items entry in weight file {} uses 'oreDict', which this line does not read. Name tags under 'tags' instead", key); }
    }

    private static void add(List<Source> sources, Identifier key, int level, String member, List<String> values, int weight) {
        if (values.isEmpty()) { return; }
        JsonArray array = new JsonArray();
        for (String value : values) { array.add(value); }
        JsonObject json = new JsonObject();
        json.add(member, array);
        ItemSelection selection = new ItemSelection("Weight", "weighs");
        selection.read(json, key);
        selection.unknown();
        List<Item> items = new ArrayList<>();
        for (Identifier id : selection.items()) { items.add(BuiltInRegistries.ITEM.getValue(id)); }
        List<TagKey<Item>> tags = new ArrayList<>();
        for (Identifier id : selection.tags()) { tags.add(TagKey.create(Registries.ITEM, id)); }
        sources.add(new Source(level, items, tags, weight));
    }

    private record Source(int level, List<Item> items, List<TagKey<Item>> tags, int weight) {}

    private record Candidate(int level, int order, int weight) {
        boolean beats(Candidate other) { return other == null || level < other.level || level == other.level && order > other.order; }
    }

    private record State(WeightDef settings, List<Source> sources, Map<Item, Integer> table) {
        static State none() { return new State(new WeightDef.Builder().build(), Collections.emptyList(), new IdentityHashMap<>()); }

        State bound() {
            Map<Item, Candidate> best = new IdentityHashMap<>();
            for (int order = 0; order < sources.size(); order++) {
                Source source = sources.get(order);
                Candidate candidate = new Candidate(source.level, order, source.weight);
                for (Item item : source.items) { offer(best, item, candidate); }
                for (TagKey<Item> tag : source.tags) {
                    for (Holder<Item> held : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) { offer(best, held.value(), candidate); }
                }
            }
            Map<Item, Integer> built = new IdentityHashMap<>();
            for (Map.Entry<Item, Candidate> chosen : best.entrySet()) { built.put(chosen.getKey(), chosen.getValue().weight); }
            return new State(settings, sources, built);
        }

        private static void offer(Map<Item, Candidate> best, Item item, Candidate candidate) {
            if (candidate.beats(best.get(item))) { best.put(item, candidate); }
        }
    }
}
