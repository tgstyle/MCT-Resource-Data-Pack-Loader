package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LootInjections {
    public static final String TARGET = "target";
    public static final String POOLS = "pools";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, List<JsonElement>> BY_TABLE = new LinkedHashMap<>();
    private static int generation = -1;

    private LootInjections() {}

    public static void reload() {
        BY_TABLE.clear();
        generation = PackManager.get().getGeneration();
        if (!Config.data.lootInjections()) { return; }
        int[] count = new int[1];
        PackManager.get().forEach(PackManager.LOOT_INJECTIONS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
            try { read(key, contents, count); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in loot injection {}, ignoring it", key, ex); }
        });
        if (count[0] > 0) { Summary.info("loot.injected", "Loaded " + count[0] + " loot pool injection(s) across " + BY_TABLE.size() + " table(s)"); }
    }

    private static void read(ResourceLocation key, String contents, int[] count) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Loot injection {} is empty, ignoring it", key);
            return;
        }
        String target = GsonHelper.getAsString(json, TARGET, "");
        if (target.isEmpty()) {
            ContentLog.LOGGER.error("Loot injection {} has no target table, ignoring it", key);
            return;
        }
        if (!json.has(POOLS)) {
            ContentLog.LOGGER.error("Loot injection {} has no pools, ignoring it", key);
            return;
        }
        List<JsonElement> pools = BY_TABLE.computeIfAbsent(ResourceLocation.parse(target), k -> new ArrayList<>());
        for (JsonElement element : GsonHelper.getAsJsonArray(json, POOLS)) {
            pools.add(element);
            count[0]++;
        }
    }

    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!Config.data.lootInjections()) { return; }
        if (generation != PackManager.get().getGeneration()) { reload(); }
        List<JsonElement> pools = BY_TABLE.get(event.getName());
        LootTable table = event.getTable();
        if (pools == null || table == null) { return; }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, event.getRegistries());
        for (JsonElement pool : pools) {
            try { table.addPool(LootPool.CODEC.parse(ops, pool).getOrThrow(JsonParseException::new)); }
            catch (RuntimeException ex) {
                ContentLog.LOGGER.error("Could not add an injected pool to loot table {}. A pool with the same name may already be there, give each injected pool its own name", event.getName(), ex);
            }
        }
    }
}
