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
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class ContentWeights {
    private static final int OWN = 1;
    private static final int NAME_META = 2;
    private static final int NAME = 3;
    private static final int ORE = 4;
    private static final int PREFIX = 5;
    private static final Map<ResourceLocation, Integer> DEFINED = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Integer> OVERRIDDEN = new LinkedHashMap<>();
    private static volatile State state = new State(new WeightDef.Builder().build(), new IdentityHashMap<>(), 0);
    private static final AtomicInteger GENERATION = new AtomicInteger();

    private ContentWeights() {}

    public static int generation() { return GENERATION.get(); }

    public static void define(ResourceLocation name, int thousandths) { DEFINED.put(name, thousandths); }

    public static void override(ResourceLocation name, int thousandths) { OVERRIDDEN.put(name, thousandths); }

    public static void clearOverrides() { OVERRIDDEN.clear(); }

    public static void load() {
        GENERATION.incrementAndGet();
        if (!Config.content.weights) {
            state = new State(new WeightDef.Builder().build(), new IdentityHashMap<>(), 0);
            WeightStacks.apply();
            return;
        }
        WeightDef.Builder settings = new WeightDef.Builder();
        List<Source> sources = new ArrayList<>();
        Json.eachObject(PackManager.WEIGHTS, "weight file", (key, json) -> read(settings, sources, key, json));
        own(sources, DEFINED);
        own(sources, OVERRIDDEN);
        int entries = sources.size();
        Map<Item, List<Candidate>> candidates = new IdentityHashMap<>();
        for (int order = 0; order < sources.size(); order++) {
            Source source = sources.get(order);
            int rank = order;
            source.selection.eachStack(stack -> candidates.computeIfAbsent(stack.getItem(), k -> new ArrayList<>()).add(new Candidate(source.level, rank, stack.getMetadata() == OreDictionary.WILDCARD_VALUE ? -1 : stack.getMetadata(), source.weight)));
        }
        Map<Item, Entry> table = new IdentityHashMap<>();
        for (Map.Entry<Item, List<Candidate>> held : candidates.entrySet()) { table.put(held.getKey(), Entry.of(held.getValue())); }
        state = new State(settings.build(), table, entries);
        WeightStacks.apply();
        if (entries > 0) { Summary.info("weights", "Loaded " + entries + " weight entries"); }
    }

    public static boolean any() {
        State now = state;
        return now.entries > 0 || now.settings.defaultWeight > 0 || now.settings.limited();
    }

    public static WeightDef settings() { return state.settings; }

    public static int of(ItemStack stack) {
        State now = state;
        if (stack.isEmpty()) { return 0; }
        Entry entry = now.table.get(stack.getItem());
        if (entry == null) { return now.settings.defaultWeight; }
        int weight = entry.at(stack.getMetadata());
        return weight < 0 ? now.settings.defaultWeight : weight;
    }

    public static int heaviest(Item item) {
        State now = state;
        Entry entry = now.table.get(item);
        int fallback = now.settings.defaultWeight;
        if (entry == null) { return fallback; }
        int top = entry.wild < 0 ? fallback : entry.wild;
        for (int weight : entry.byMeta) { top = Math.max(top, weight < 0 ? fallback : weight); }
        return top;
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
            selection.resolve();
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
        List<String> withMeta = new ArrayList<>();
        List<String> plain = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        for (String name : Json.strings(entry, "names")) {
            if (ItemSelection.isMetaName(name)) { withMeta.add(name); }
            else if (ItemSelection.isPrefixName(name)) { prefixes.add(name); }
            else { plain.add(name); }
        }
        List<String> spaces = Json.strings(entry, "namespaces");
        int before = sources.size();
        add(sources, key, NAME_META, "names", withMeta, weight);
        add(sources, key, NAME, "names", plain, weight);
        add(sources, key, ORE, "oreDict", Json.strings(entry, "oreDict"), weight);
        add(sources, key, PREFIX, "names", prefixes, weight);
        add(sources, key, PREFIX, "namespaces", spaces, weight);
        if (sources.size() == before) { ContentLog.LOGGER.error("An items entry in weight file {} selects no items, skipping it", key); }
    }

    private static void add(List<Source> sources, ResourceLocation key, int level, String member, List<String> values, int weight) {
        if (values.isEmpty()) { return; }
        ItemSelection selection = new ItemSelection("Weight", "weighs");
        selection.read(only(member, values), key);
        selection.resolve();
        sources.add(new Source(level, selection, weight));
    }

    private static JsonObject only(String member, List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) { array.add(value); }
        JsonObject json = new JsonObject();
        json.add(member, array);
        return json;
    }

    private static final class State {
        final WeightDef settings;
        final Map<Item, Entry> table;
        final int entries;

        State(WeightDef settings, Map<Item, Entry> table, int entries) {
            this.settings = settings;
            this.table = table;
            this.entries = entries;
        }
    }

    private static final class Source {
        final int level;
        final ItemSelection selection;
        final int weight;

        Source(int level, ItemSelection selection, int weight) {
            this.level = level;
            this.selection = selection;
            this.weight = weight;
        }
    }

    private static final class Candidate {
        final int level;
        final int order;
        final int meta;
        final int weight;

        Candidate(int level, int order, int meta, int weight) {
            this.level = level;
            this.order = order;
            this.meta = meta;
            this.weight = weight;
        }

        boolean beats(Candidate other) { return other == null || level < other.level || level == other.level && order > other.order; }
    }

    private static final class Entry {
        final int wild;
        final int[] byMeta;

        Entry(int wild, int[] byMeta) {
            this.wild = wild;
            this.byMeta = byMeta;
        }

        static Entry of(List<Candidate> candidates) {
            int highest = -1;
            Candidate wild = null;
            for (Candidate candidate : candidates) {
                if (candidate.meta < 0) { wild = candidate.beats(wild) ? candidate : wild; }
                else { highest = Math.max(highest, candidate.meta); }
            }
            Candidate[] best = new Candidate[highest + 1];
            for (Candidate candidate : candidates) {
                if (candidate.meta >= 0 && candidate.beats(best[candidate.meta])) { best[candidate.meta] = candidate; }
            }
            int[] byMeta = new int[best.length];
            for (int meta = 0; meta < best.length; meta++) {
                Candidate chosen = wild != null && !(best[meta] != null && best[meta].beats(wild)) ? wild : best[meta];
                byMeta[meta] = chosen == null ? -1 : chosen.weight;
            }
            return new Entry(wild == null ? -1 : wild.weight, byMeta);
        }

        int at(int meta) { return meta >= 0 && meta < byMeta.length ? byMeta[meta] : wild; }
    }
}
