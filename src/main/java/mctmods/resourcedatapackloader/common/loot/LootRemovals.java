package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class LootRemovals {
    private static final String TARGET = "target";
    private static final String CLEAR = "clear";
    private static final String ENTRIES = "entries";
    private static final String ITEMS = "items";
    private static final String MAIN = "main";
    private static final String POOL_PREFIX = "pool";
    private static final List<String> REMOVAL_KEYS = List.of(TARGET, CLEAR, ENTRIES, ITEMS);
    private static final List<String> PICK_KEYS = List.of("pool", "entry");
    private static final Map<ResourceLocation, Removal> BY_TABLE = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private LootRemovals() {}

    private record Pick(ResourceLocation file, String pool, ResourceLocation entry) {}

    private static final class Removal {
        boolean clear;
        final List<Pick> picks = new ArrayList<>();
        final Map<ResourceLocation, ResourceLocation> items = new LinkedHashMap<>();
    }

    private static void reload() {
        BY_TABLE.clear();
        GENERATION.stale();
        if (Config.data.lootRemovalsOff()) { return; }
        int[] count = new int[1];
        Json.eachObject(PackManager.LOOT_REMOVALS, "loot removal", (key, json) -> read(key, json, count));
        if (count[0] > 0) { Summary.info("loot.removed", "Loaded " + count[0] + " loot removal(s) across " + BY_TABLE.size() + " table(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json, int[] count) {
        Json.unknownKeys(json, "the top level", REMOVAL_KEYS);
        String target = Json.getString(json, TARGET, "");
        if (target.isEmpty()) {
            ContentLog.LOGGER.error("Loot removal {} has no target table, ignoring it", key);
            return;
        }
        ResourceLocation table = ContentParser.location(target);
        if (table == null) {
            ContentLog.LOGGER.error("Loot removal {} names the table '{}', which is not a valid id, ignoring it", key, target);
            return;
        }
        if (!json.has(CLEAR) && !json.has(ENTRIES) && !json.has(ITEMS)) {
            ContentLog.LOGGER.error("Loot removal {} has none of clear, entries or items, ignoring it", key);
            return;
        }
        Removal removal = BY_TABLE.computeIfAbsent(table, k -> new Removal());
        if (Json.getBoolean(json, CLEAR, false)) {
            removal.clear = true;
            count[0]++;
            return;
        }
        if (GsonHelper.isArrayNode(json, ENTRIES)) {
            for (JsonElement element : json.getAsJsonArray(ENTRIES)) {
                if (!element.isJsonObject()) { continue; }
                Json.unknownKeys(element.getAsJsonObject(), ENTRIES, PICK_KEYS);
                String pool = Json.text(element.getAsJsonObject(), "pool", "");
                ResourceLocation entry = id(Json.text(element.getAsJsonObject(), "entry", ""));
                if (pool.isEmpty() || entry == null) {
                    ContentLog.LOGGER.error("Loot removal {} has an entry without a pool and a valid entry id, ignoring it", key);
                    continue;
                }
                removal.picks.add(new Pick(key, pool, entry));
                count[0]++;
            }
        }
        for (String item : Json.names(json, ITEMS)) {
            ResourceLocation id = id(item);
            if (id == null) {
                ContentLog.LOGGER.error("Loot removal {} names the item '{}', which is not a valid id, ignoring it", key, item);
                continue;
            }
            removal.items.putIfAbsent(id, key);
            count[0]++;
        }
    }

    private static ResourceLocation id(String value) { return ContentParser.location(value.startsWith("#") ? value.substring(1) : value); }

    private static synchronized Removal removalFor(ResourceLocation name) {
        if (GENERATION.stale()) { reload(); }
        return BY_TABLE.get(name);
    }

    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (Config.data.lootRemovalsOff()) { return; }
        Removal removal = removalFor(event.getName());
        LootTable table = event.getTable();
        if (removal == null || table == null) { return; }
        if (removal.clear) {
            table.pools.clear();
            return;
        }
        for (Pick pick : removal.picks) { removeEntry(event.getName(), table, pick); }
        if (!removal.items.isEmpty()) { removeItems(event.getName(), table, removal.items); }
    }

    private static void removeEntry(ResourceLocation name, LootTable table, Pick pick) {
        LootPool pool = resolve(table, pick.pool());
        if (pool == null) {
            ContentLog.LOGGER.warn("Loot removal {} for table {} names pool '{}', which the table does not have", pick.file(), name, pick.pool());
            return;
        }
        try {
            boolean[] hit = new boolean[1];
            pool.entries = filter(pool.entries, entry -> {
                boolean match = pick.entry().equals(yielded(entry));
                hit[0] |= match;
                return match;
            });
            if (!hit[0]) { ContentLog.LOGGER.warn("Loot removal {} for table {} names entry '{}', which pool '{}' does not have", pick.file(), name, pick.entry(), pick.pool()); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Could not remove entry '{}' from pool '{}' of loot table {}", pick.entry(), pick.pool(), name, ex);
        }
    }

    private static void removeItems(ResourceLocation name, LootTable table, Map<ResourceLocation, ResourceLocation> items) {
        Set<ResourceLocation> found = new HashSet<>();
        for (LootPool pool : table.pools) {
            try {
                pool.entries = filter(pool.entries, entry -> {
                    if (!(entry instanceof LootItem item)) { return false; }
                    ResourceLocation id = ForgeRegistries.ITEMS.getKey(item.item);
                    boolean match = id != null && items.containsKey(id);
                    if (match) { found.add(id); }
                    return match;
                });
            }
            catch (RuntimeException ex) {
                ContentLog.LOGGER.error("Could not remove items from pool '{}' of loot table {}", pool.getName(), name, ex);
            }
        }
        for (Map.Entry<ResourceLocation, ResourceLocation> item : items.entrySet()) {
            if (!found.contains(item.getKey())) { ContentLog.LOGGER.warn("Loot removal {} for table {} names item '{}', which no pool has", item.getValue(), name, item.getKey()); }
        }
    }

    private static ResourceLocation yielded(LootPoolEntryContainer entry) {
        if (entry instanceof LootItem item) { return ForgeRegistries.ITEMS.getKey(item.item); }
        if (entry instanceof TagEntry tag) { return tag.tag.location(); }
        if (entry instanceof LootTableReference reference) { return reference.name; }
        return null;
    }

    private static LootPoolEntryContainer[] filter(LootPoolEntryContainer[] source, Predicate<LootPoolEntryContainer> drop) {
        List<LootPoolEntryContainer> kept = new ArrayList<>();
        for (LootPoolEntryContainer entry : source) {
            if (drop.test(entry)) { continue; }
            if (entry instanceof CompositeEntryBase composite) {
                LootPoolEntryContainer[] children = filter(composite.children, drop);
                if (children.length == 0 && composite.children.length > 0) { continue; }
                if (children.length != composite.children.length) {
                    composite.children = children;
                    composite.composedChildren = composite.compose(children);
                }
            }
            kept.add(entry);
        }
        return kept.toArray(new LootPoolEntryContainer[0]);
    }

    private static LootPool resolve(LootTable table, String wanted) {
        for (LootPool pool : table.pools) {
            if (wanted.equals(pool.getName())) { return pool; }
        }
        if (MAIN.equals(wanted)) { return table.pools.isEmpty() ? null : table.pools.get(0); }
        if (!wanted.startsWith(POOL_PREFIX) || wanted.length() == POOL_PREFIX.length()) { return null; }
        try {
            int index = Integer.parseInt(wanted.substring(POOL_PREFIX.length()));
            return index >= 0 && index < table.pools.size() ? table.pools.get(index) : null;
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }
}
