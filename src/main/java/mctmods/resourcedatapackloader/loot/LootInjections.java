package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LootInjections {
    public static final String TARGET = "target";
    public static final String POOLS = "pools";
    private static final Map<ResourceLocation, List<String>> BY_TABLE = new LinkedHashMap<>();
    private static int generation = -1;

    private LootInjections() {}

    public static void reload() {
        BY_TABLE.clear();
        generation = PackManager.get().getGeneration();
        if (!Config.data.lootInjections) { return; }

        int[] count = new int[1];
        PackManager.get().forEach(PackManager.LOOT_INJECTIONS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents, count); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in loot injection {}, ignoring it", key, ex); }
        });

        if (count[0] > 0) { Summary.info("loot.injected", "Loaded " + count[0] + " loot pool injection(s) across " + BY_TABLE.size() + " table(s)"); }
    }

    private static void read(ResourceLocation key, String contents, int[] count) {
        JsonObject json = new Gson().fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Loot injection {} is empty, ignoring it", key);
            return;
        }

        String target = JsonUtils.getString(json, TARGET, "");
        if (target.isEmpty()) {
            ContentLog.LOGGER.error("Loot injection {} has no target table, ignoring it", key);
            return;
        }

        if (!json.has(POOLS)) {
            ContentLog.LOGGER.error("Loot injection {} has no pools, ignoring it", key);
            return;
        }

        List<String> pools = BY_TABLE.computeIfAbsent(new ResourceLocation(target), k -> new ArrayList<>());
        for (com.google.gson.JsonElement element : JsonUtils.getJsonArray(json, POOLS)) {
            pools.add(element.toString());
            count[0]++;
        }
    }

    public static void apply(ResourceLocation name, LootTable table, Gson gson) {
        if (!Config.data.lootInjections) { return; }
        if (generation != PackManager.get().getGeneration()) { reload(); }

        List<String> pools = BY_TABLE.get(name);
        if (pools == null || table.isFrozen()) { return; }

        for (String pool : pools) {
            try {
                LootPool parsed = gson.fromJson(pool, LootPool.class);
                if (parsed == null) { continue; }
                table.addPool(parsed);
            }
            catch (RuntimeException ex) {
                ContentLog.LOGGER.error("Could not add an injected pool to loot table {}. A pool with the same name may already be there, give each injected pool its own name", name, ex);
            }
        }
    }
}
