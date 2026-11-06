package mctmods.resourcedatapackloader.common.loot;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class LootRemovals {
    private static final String TARGET = "target";
    private static final String CLEAR = "clear";
    private static final String ENTRIES = "entries";
    private static final String ITEMS = "items";
    private static final String MAIN = "main";
    private static final String POOL_PREFIX = "pool";
    private static final Map<ResourceLocation, Removal> BY_TABLE = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private LootRemovals() {}

    private static final class Pick {
        final ResourceLocation file;
        final String pool;
        final String entry;

        Pick(ResourceLocation file, String pool, String entry) {
            this.file = file;
            this.pool = pool;
            this.entry = entry;
        }
    }

    private static final class Removal {
        boolean clear;
        final List<Pick> picks = new ArrayList<>();
        final Map<String, ResourceLocation> items = new LinkedHashMap<>();
    }

    private static void reload() {
        BY_TABLE.clear();
        GENERATION.stale();
        if (!Config.data.lootRemovals) { return; }
        int[] count = new int[1];
        Json.eachObject(PackManager.LOOT_REMOVALS, "loot removal", (key, json) -> read(key, json, count));
        if (count[0] > 0) { Summary.info("loot.removed", "Loaded " + count[0] + " loot removal(s) across " + BY_TABLE.size() + " table(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json, int[] count) {
        String target = Json.getString(json, TARGET, "");
        if (target.isEmpty()) {
            ContentLog.LOGGER.error("Loot removal {} has no target table, ignoring it", key);
            return;
        }
        if (!json.has(CLEAR) && !json.has(ENTRIES) && !json.has(ITEMS)) {
            ContentLog.LOGGER.error("Loot removal {} has none of clear, entries or items, ignoring it", key);
            return;
        }
        Removal removal = BY_TABLE.computeIfAbsent(new ResourceLocation(target), k -> new Removal());
        if (Json.getBoolean(json, CLEAR, false)) {
            removal.clear = true;
            count[0]++;
            return;
        }
        if (JsonUtils.isJsonArray(json, ENTRIES)) {
            for (JsonElement element : json.getAsJsonArray(ENTRIES)) {
                if (!element.isJsonObject()) { continue; }
                String pool = Json.text(element.getAsJsonObject(), "pool", "");
                String entry = Json.text(element.getAsJsonObject(), "entry", "");
                if (pool.isEmpty() || entry.isEmpty()) {
                    ContentLog.LOGGER.error("Loot removal {} has an entry without a pool and an entry name, ignoring it", key);
                    continue;
                }
                removal.picks.add(new Pick(key, pool, entry));
                count[0]++;
            }
        }
        for (String item : Json.names(json, ITEMS)) {
            removal.items.putIfAbsent(new ResourceLocation(item).toString(), key);
            count[0]++;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!Config.data.lootRemovals) { return; }
        if (GENERATION.stale()) { reload(); }
        Removal removal = BY_TABLE.get(event.getName());
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
        LootPool pool = resolve(table, pick.pool);
        if (pool == null) {
            ContentLog.LOGGER.warn("Loot removal {} for table {} names pool '{}', which the table does not have", pick.file, name, pick.pool);
            return;
        }
        try {
            if (!has(pool, pick.entry)) {
                ContentLog.LOGGER.warn("Loot removal {} for table {} names entry '{}', which pool '{}' does not have", pick.file, name, pick.entry, pick.pool);
                return;
            }
            pool.removeEntry(pick.entry);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Could not remove entry '{}' from pool '{}' of loot table {}", pick.entry, pick.pool, name, ex);
        }
    }

    private static void removeItems(ResourceLocation name, LootTable table, Map<String, ResourceLocation> items) {
        List<String> found = new ArrayList<>();
        for (LootPool pool : table.pools) {
            try {
                List<String> names = new ArrayList<>();
                for (LootEntry entry : pool.lootEntries) {
                    if (!(entry instanceof LootEntryItem) || ((LootEntryItem) entry).item.getRegistryName() == null) { continue; }
                    String id = ((LootEntryItem) entry).item.getRegistryName().toString();
                    if (!items.containsKey(id)) { continue; }
                    names.add(entry.getEntryName());
                    found.add(id);
                }
                for (String entryName : names) { pool.removeEntry(entryName); }
            }
            catch (RuntimeException ex) {
                ContentLog.LOGGER.error("Could not remove items from pool '{}' of loot table {}", pool.getName(), name, ex);
            }
        }
        for (Map.Entry<String, ResourceLocation> item : items.entrySet()) {
            if (!found.contains(item.getKey())) { ContentLog.LOGGER.warn("Loot removal {} for table {} names item '{}', which no pool has", item.getValue(), name, item.getKey()); }
        }
    }

    private static boolean has(LootPool pool, String entryName) {
        for (LootEntry entry : pool.lootEntries) {
            if (entryName.equals(entry.getEntryName())) { return true; }
        }
        return false;
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
