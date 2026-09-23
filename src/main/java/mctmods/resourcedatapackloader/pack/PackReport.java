package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

final class PackReport {
    private PackReport() {}

    static void warnAboutDisabledFeatures(List<RDPLPack> packs) {
        if (packs.isEmpty()) { return; }
        List<String[]> off = new ArrayList<>();
        clientSide(off, Config.content.load, "content.load", PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS, PackManager.MATERIALS);
        clientSide(off, Config.content.sounds, "content.sounds", PackManager.SOUNDS);
        collect(off, Config.content.fuels, "content.fuels", PackManager.JSON, PackManager.FUELS);
        collect(off, Config.content.oreDictionary, "content.oreDictionary", PackManager.JSON, PackManager.OREDICT);
        clientSide(off, Config.content.potions, "content.potions", PackManager.POTIONS, PackManager.POTION_TYPES);
        collect(off, Config.content.brewing, "content.brewing", PackManager.JSON, PackManager.BREWING);
        clientSide(off, Config.content.villagers, "content.villagers", PackManager.VILLAGERS);
        collect(off, Config.content.villagers, "content.villagers", PackManager.JSON, PackManager.TRADES);
        clientSide(off, Config.content.biomes, "content.biomes", PackManager.BIOMES);
        clientSide(off, Config.content.dimensions, "content.dimensions", PackManager.DIMENSIONS);
        collect(off, Config.content.villages, "content.villages", PackManager.JSON, PackManager.VILLAGES);
        clientSide(off, Config.content.entities, "content.entities", PackManager.ENTITIES);
        collect(off, Config.content.hardness, "content.hardness", PackManager.JSON, PackManager.HARDNESS);
        collect(off, Config.recipes.furnace, "recipes.furnace", PackManager.JSON, PackManager.FURNACE);
        collect(off, Config.recipes.removals, "recipes.removals", PackManager.JSON, PackManager.RECIPE_REMOVALS);
        collect(off, Config.data.lootInjections, "data.lootInjections", PackManager.JSON, PackManager.LOOT_INJECTIONS);
        collect(off, Config.data.blockDrops, "data.blockDrops", PackManager.JSON, PackManager.BLOCK_DROPS);
        collect(off, Config.data.anvils, "data.anvils", PackManager.JSON, PackManager.ANVILS);
        collect(off, Config.data.playerLoot, "data.playerLoot", PackManager.JSON, PackManager.PLAYER_LOOT);
        collect(off, Config.data.registryRemaps, "data.registryRemaps", PackManager.JSON, PackManager.REGISTRY_REMAP);
        collect(off, Config.worldgen.load, "worldgen.load", PackManager.JSON, PackManager.WORLDGEN);
        collect(off, Config.data.functions, "data.functions", PackManager.MCFUNCTION, PackManager.FUNCTIONS);
        if (off.isEmpty()) { return; }
        for (RDPLPack pack : packs) {
            for (String[] entry : off) {
                int count = pack.count(entry[0], entry[1]);
                if (count == 0) { continue; }
                ContentLog.LOGGER.warn("Pack '{}' provides {} {} file(s), but {}, so they do nothing", pack.getName(), count, entry[0], entry[2]);
            }
        }
    }

    private static void collect(List<String[]> off, boolean enabled, String setting, String ext, String... types) {
        because(off, enabled, setting + " is off in the config", ext, types);
    }

    private static void clientSide(List<String[]> off, boolean enabled, String setting, String... types) {
        if (Config.content.vanillaClients) {
            because(off, false, "content.vanillaClients is on and they are the sort a client would need too", PackManager.JSON, types);
            return;
        }
        collect(off, enabled, setting, PackManager.JSON, types);
    }

    private static void because(List<String[]> off, boolean enabled, String reason, String ext, String... types) {
        if (enabled) { return; }
        for (String type : types) { off.add(new String[] { type, ext, reason }); }
    }

    static void report(List<RDPLPack> packs, @Nullable Path root) {
        if (packs.isEmpty()) {
            ContentLog.LOGGER.info("No packs found in {}", root);
            return;
        }
        int fromMods = 0;
        for (RDPLPack pack : packs) {
            if (pack.isFromMod()) { fromMods++; }
        }
        ContentLog.LOGGER.info("Loaded {} pack(s) from {}, lowest priority first{}", packs.size(), root,
                fromMods == 0 ? "" : ", " + fromMods + " of them shipped inside a mod jar and listed in config/mods.json");
        if (!Config.packs.logContents) { return; }
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " priority=" + pack.getPriority() : "";
            String tier = (pack.isOverriding() ? " overriding" : "") + (pack.isFromMod() ? " from a mod jar" : "");
            ContentLog.LOGGER.debug("  '{}'{}{}: files={} namespaces={} advancements={} loot_tables={} recipes={} functions={} remaps={} blocks={} items={} fluids={} furnace={} worldgen={} fuels={} oredict={} sounds={} recipe_removals={} materials={} loot_injections={} player_loot={} tabs={} potions={} potion_types={} brewing={} villagers={} trades={} biomes={} villages={} entities={} hardness={}",
                    pack.getName(), priority, tier, pack.getFileCount(), pack.getNamespaces(), pack.count(PackManager.ADVANCEMENTS, PackManager.JSON), pack.count(PackManager.LOOT_TABLES, PackManager.JSON), pack.count(PackManager.RECIPES, PackManager.JSON), pack.count(PackManager.FUNCTIONS, PackManager.MCFUNCTION), pack.count(PackManager.REGISTRY_REMAP, PackManager.JSON), pack.count(PackManager.BLOCKS, PackManager.JSON), pack.count(PackManager.ITEMS, PackManager.JSON), pack.count(PackManager.FLUIDS, PackManager.JSON), pack.count(PackManager.FURNACE, PackManager.JSON), pack.count(PackManager.WORLDGEN, PackManager.JSON), pack.count(PackManager.FUELS, PackManager.JSON), pack.count(PackManager.OREDICT, PackManager.JSON), pack.count(PackManager.SOUNDS, PackManager.JSON), pack.count(PackManager.RECIPE_REMOVALS, PackManager.JSON), pack.count(PackManager.MATERIALS, PackManager.JSON), pack.count(PackManager.LOOT_INJECTIONS, PackManager.JSON), pack.count(PackManager.PLAYER_LOOT, PackManager.JSON), pack.count(PackManager.TABS, PackManager.JSON), pack.count(PackManager.POTIONS, PackManager.JSON), pack.count(PackManager.POTION_TYPES, PackManager.JSON), pack.count(PackManager.BREWING, PackManager.JSON), pack.count(PackManager.VILLAGERS, PackManager.JSON), pack.count(PackManager.TRADES, PackManager.JSON), pack.count(PackManager.BIOMES, PackManager.JSON), pack.count(PackManager.VILLAGES, PackManager.JSON), pack.count(PackManager.ENTITIES, PackManager.JSON), pack.count(PackManager.HARDNESS, PackManager.JSON));
        }
    }
}
