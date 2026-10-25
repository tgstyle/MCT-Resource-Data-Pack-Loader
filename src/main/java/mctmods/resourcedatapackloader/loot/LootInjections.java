package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.LootTableLoadEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LootInjections {
    private static final String TARGET = "target";
    private static final String POOLS = "pools";
    private static final Gson LOOT = Deserializers.createLootTableSerializer().create();
    private static final Map<ResourceLocation, List<JsonElement>> BY_TABLE = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private LootInjections() {}

    private static void reload() {
        BY_TABLE.clear();
        GENERATION.stale();
        if (Config.data.lootInjectionsOff()) { return; }
        int[] count = new int[1];
        Json.eachObject(PackManager.LOOT_INJECTIONS, "loot injection", (key, json) -> read(key, json, count));
        if (count[0] > 0) { Summary.info("loot.injected", "Loaded " + count[0] + " loot pool injection(s) across " + BY_TABLE.size() + " table(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json, int[] count) {
        String target = Json.getString(json, TARGET, "");
        if (target.isEmpty()) {
            ContentLog.LOGGER.error("Loot injection {} has no target table, ignoring it", key);
            return;
        }
        ResourceLocation table = ContentParser.location(target);
        if (table == null) {
            ContentLog.LOGGER.error("Loot injection {} names the table '{}', which is not a valid id, ignoring it", key, target);
            return;
        }
        if (!json.has(POOLS)) {
            ContentLog.LOGGER.error("Loot injection {} has no pools, ignoring it", key);
            return;
        }
        List<JsonElement> pools = BY_TABLE.computeIfAbsent(table, k -> new ArrayList<>());
        for (JsonElement element : Json.getJsonArray(json, POOLS)) {
            pools.add(element);
            count[0]++;
        }
    }

    private static synchronized List<JsonElement> poolsFor(ResourceLocation name) {
        if (GENERATION.stale()) { reload(); }
        List<JsonElement> pools = BY_TABLE.get(name);
        return pools == null ? null : List.copyOf(pools);
    }

    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (Config.data.lootInjectionsOff()) { return; }
        List<JsonElement> pools = poolsFor(event.getName());
        if (pools == null) { return; }
        LootTable table = event.getTable();
        ResourceLocation synthetic = ResourceLocation.fromNamespaceAndPath(event.getName().getNamespace(), event.getName().getPath() + "_rdpl_injection");
        for (JsonElement pool : pools) {
            try {
                LootTable parsed = ForgeHooks.loadLootTable(LOOT, synthetic, wrap(pool), true);
                if (parsed == null) { continue; }
                for (LootPool each : parsed.pools) { table.addPool(each); }
            }
            catch (RuntimeException ex) {
                ContentLog.LOGGER.error("Could not add an injected pool to loot table {}. A pool with the same name may already be there, give each injected pool its own name", event.getName(), ex);
            }
        }
    }

    private static JsonObject wrap(JsonElement pool) {
        JsonArray array = new JsonArray();
        array.add(pool);
        JsonObject table = new JsonObject();
        table.add(POOLS, array);
        return table;
    }
}
