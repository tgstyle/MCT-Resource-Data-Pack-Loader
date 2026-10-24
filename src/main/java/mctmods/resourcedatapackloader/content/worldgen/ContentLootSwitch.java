package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class ContentLootSwitch {
    private static final Pattern ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9/._-]+");
    private static final ResourceLocation EMPTY = new ResourceLocation("minecraft:empty");

    private ContentLootSwitch() {}

    @Nullable static Map<ResourceLocation, ResourceLocation> parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives loot that is not an object of loot table ids, ignoring it", owner);
            return null;
        }
        Map<ResourceLocation, ResourceLocation> swaps = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives loot for '{}' that is not a loot table id, ignoring it", owner, entry.getKey());
                continue;
            }
            ResourceLocation from = id(entry.getKey());
            ResourceLocation to = id(entry.getValue().getAsString());
            if (from == null || to == null) {
                ContentLog.LOGGER.error("{} gives loot '{}' to '{}', which is not a pair of loot table ids, ignoring it", owner, entry.getKey(), entry.getValue().getAsString());
                continue;
            }
            if (from.equals(to)) {
                ContentLog.LOGGER.error("{} gives loot '{}' to itself, ignoring it", owner, from);
                continue;
            }
            swaps.put(from, to);
        }
        return swaps.isEmpty() ? null : Collections.unmodifiableMap(swaps);
    }

    @Nullable public static ResourceLocation id(String text) {
        String lower = Settings.lower(text);
        String full = lower.indexOf(':') < 0 ? "minecraft:" + lower : lower;
        return ID.matcher(full).matches() ? new ResourceLocation(full) : null;
    }

    public static ResourceLocation swap(World world, BlockPos pos, ResourceLocation table) {
        if (table == null || world.isRemote) { return table; }
        return ContentEnvironments.loot(world, pos).getOrDefault(table, table);
    }

    public static void serverStarted(MinecraftServer server) {
        LootTableManager manager = server.getWorld(0).getLootTableManager();
        Set<ResourceLocation> told = new HashSet<>();
        for (Map<ResourceLocation, ResourceLocation> swaps : ContentEnvironments.allLoot()) {
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : swaps.entrySet()) {
                if (missing(manager, told, entry.getKey())) { ContentLog.LOGGER.error("{} names no loot table, so an environment's loot swap that uses it drops nothing", entry.getKey()); }
                if (missing(manager, told, entry.getValue())) { ContentLog.LOGGER.error("{} names no loot table, so an environment's loot swap that uses it drops nothing", entry.getValue()); }
            }
        }
        for (StructureMapDef def : ContentStructureMaps.defs()) {
            if (def.loot != null && missing(manager, told, def.loot)) { ContentLog.LOGGER.error("{} names no loot table, so the chests structure map {} fills drop nothing", def.loot, def.key); }
        }
    }

    private static boolean missing(LootTableManager manager, Set<ResourceLocation> told, ResourceLocation id) { return !EMPTY.equals(id) && manager.getLootTableFromLocation(id) == LootTable.EMPTY_LOOT_TABLE && told.add(id); }
}
