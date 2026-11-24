package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.event.LootTableLoadEvent;

public final class LootRemovals {
    private static final String TARGET = "target";
    private static final String CLEAR = "clear";
    private static final String ENTRIES = "entries";
    private static final String ITEMS = "items";
    private static final String CHILDREN = "children";
    private static final String MAIN = "main";
    private static final String POOL_PREFIX = "pool";
    private static final List<String> REMOVAL_KEYS = List.of(TARGET, CLEAR, ENTRIES, ITEMS);
    private static final List<String> PICK_KEYS = List.of("pool", "entry");
    private static final Map<ResourceLocation, Removal> BY_TABLE = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private LootRemovals() {}

    private record Pick(ResourceLocation file, String pool, String entry) {}

    private static final class Removal {
        boolean clear;
        final List<Pick> picks = new ArrayList<>();
        final Map<String, ResourceLocation> items = new LinkedHashMap<>();
    }

    private static void reload() {
        int[] count = new int[1];
        GENERATION.load(!Config.data.lootRemovalsOff(), BY_TABLE::clear, PackManager.LOOT_REMOVALS, "loot removal", (key, json) -> read(key, json, count), () -> {
            if (count[0] > 0) { Summary.info("loot.removed", "Loaded " + count[0] + " loot removal(s) across " + BY_TABLE.size() + " table(s)"); }
        });
    }

    private static void read(ResourceLocation key, JsonObject json, int[] count) {
        Json.unknownKeys(json, "the top level", REMOVAL_KEYS);
        String target = Json.required(json, TARGET, key, "Loot removal {} has no target table, ignoring it");
        if (target.isEmpty()) { return; }
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
                String entry = Json.text(element.getAsJsonObject(), "entry", "");
                if (pool.isEmpty() || entry.isEmpty()) {
                    ContentLog.LOGGER.error("Loot removal {} has an entry without a pool and an entry name, ignoring it", key);
                    continue;
                }
                removal.picks.add(new Pick(key, pool, IdNames.namespaced(entry)));
                count[0]++;
            }
        }
        for (String item : Json.names(json, ITEMS)) {
            removal.items.putIfAbsent(IdNames.namespaced(item), key);
            count[0]++;
        }
    }

    private static synchronized Removal removalFor(ResourceLocation name) {
        if (GENERATION.stale()) { reload(); }
        return BY_TABLE.get(name);
    }

    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (Config.data.lootRemovalsOff()) { return; }
        ResourceLocation name = event.getName();
        Removal removal = removalFor(name);
        if (removal == null) { return; }
        LootTable table = event.getTable();
        if (removal.clear) {
            table.pools.clear();
            return;
        }
        Map<Integer, List<Pick>> byPool = new TreeMap<>();
        for (Pick pick : removal.picks) {
            int index = resolve(table, pick.pool());
            if (index < 0) { ContentLog.LOGGER.warn("Loot removal {} for table {} names pool '{}', which the table does not have", pick.file(), name, pick.pool()); }
            else { byPool.computeIfAbsent(index, k -> new ArrayList<>()).add(pick); }
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, event.getRegistries());
        Set<String> foundItems = new HashSet<>();
        for (int index = 0; index < table.pools.size(); index++) {
            List<Pick> picks = byPool.getOrDefault(index, List.of());
            if (!picks.isEmpty() || !removal.items.isEmpty()) { rebuild(name, table, index, picks, removal.items, foundItems, ops); }
        }
        for (Map.Entry<String, ResourceLocation> item : removal.items.entrySet()) {
            if (!foundItems.contains(item.getKey())) { ContentLog.LOGGER.warn("Loot removal {} for table {} names item '{}', which no pool has", item.getValue(), name, item.getKey()); }
        }
    }

    private static void rebuild(ResourceLocation name, LootTable table, int index, List<Pick> picks, Map<String, ResourceLocation> items, Set<String> foundItems, RegistryOps<JsonElement> ops) {
        LootPool pool = table.pools.get(index);
        try {
            JsonElement json = LootPool.CODEC.encodeStart(ops, pool).getOrThrow(JsonParseException::new);
            Set<String> wanted = new HashSet<>();
            for (Pick pick : picks) { wanted.add(pick.entry()); }
            Set<String> foundEntries = new HashSet<>();
            int removed = strip(json.getAsJsonObject().getAsJsonArray(ENTRIES), entry -> matches(entry, wanted, items.keySet(), foundEntries, foundItems));
            for (Pick pick : picks) {
                if (!foundEntries.contains(pick.entry())) { ContentLog.LOGGER.warn("Loot removal {} for table {} names entry '{}', which pool '{}' does not have", pick.file(), name, pick.entry(), pick.pool()); }
            }
            if (removed > 0) { table.pools.set(index, LootPool.CODEC.parse(ops, json).getOrThrow(JsonParseException::new)); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Could not remove entries from pool {} of loot table {}", index, name, ex);
        }
    }

    private static boolean matches(JsonObject entry, Set<String> wanted, Set<String> items, Set<String> foundEntries, Set<String> foundItems) {
        String type = IdNames.bare(Json.text(entry, "type"));
        String yielded = "loot_table".equals(type) ? Json.text(entry, "value") : "item".equals(type) || "tag".equals(type) ? Json.text(entry, "name") : "";
        if (yielded.isEmpty()) { return false; }
        String id = IdNames.namespaced(yielded);
        boolean hit = wanted.contains(id);
        if (hit) { foundEntries.add(id); }
        if ("item".equals(type) && items.contains(id)) {
            foundItems.add(id);
            hit = true;
        }
        return hit;
    }

    public static int strip(JsonArray entries, Predicate<JsonObject> drop) {
        int removed = 0;
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (!entries.get(i).isJsonObject()) { continue; }
            JsonObject entry = entries.get(i).getAsJsonObject();
            if (drop.test(entry)) {
                entries.remove(i);
                removed++;
                continue;
            }
            JsonElement children = entry.get(CHILDREN);
            if (children == null || !children.isJsonArray()) { continue; }
            int inner = strip(children.getAsJsonArray(), drop);
            removed += inner;
            if (inner > 0 && children.getAsJsonArray().isEmpty()) { entries.remove(i); }
        }
        return removed;
    }

    private static int resolve(LootTable table, String wanted) {
        for (int i = 0; i < table.pools.size(); i++) {
            if (wanted.equals(table.pools.get(i).getName())) { return i; }
        }
        if (MAIN.equals(wanted)) { return table.pools.isEmpty() ? -1 : 0; }
        if (!wanted.startsWith(POOL_PREFIX) || wanted.length() == POOL_PREFIX.length()) { return -1; }
        try {
            int index = Integer.parseInt(wanted.substring(POOL_PREFIX.length()));
            return index >= 0 && index < table.pools.size() ? index : -1;
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }
}
