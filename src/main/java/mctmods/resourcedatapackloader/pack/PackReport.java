package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.packs.PackType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

final class PackReport {
    private PackReport() {}

    static void warnAboutDisabledFeatures(List<RDPLPack> packs) {
        if (packs.isEmpty()) { return; }
        List<String[]> off = new ArrayList<>();
        clientSide(off, !Config.content.loadOff(), "content.load", PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS, PackManager.MATERIALS);
        clientSide(off, Config.content.sounds(), "content.sounds", PackManager.SOUNDS);
        collect(off, Config.content.fuels(), "content.fuels", PackManager.JSON, PackManager.FUELS);
        clientSide(off, Config.content.potions(), "content.potions", PackManager.POTIONS, PackManager.POTION_TYPES);
        collect(off, Config.content.brewing(), "content.brewing", PackManager.JSON, PackManager.BREWING);
        clientSide(off, Config.content.villagers(), "content.villagers", PackManager.VILLAGERS);
        collect(off, Config.content.villagers(), "content.villagers", PackManager.JSON, PackManager.TRADES);
        clientSide(off, Config.content.biomes(), "content.biomes", PackManager.BIOMES);
        clientSide(off, Config.content.dimensions(), "content.dimensions", PackManager.DIMENSIONS);
        collect(off, Config.content.villages(), "content.villages", PackManager.JSON, PackManager.VILLAGES);
        clientSide(off, Config.content.entities(), "content.entities", PackManager.ENTITIES);
        collect(off, Config.content.hardness(), "content.hardness", PackManager.JSON, PackManager.HARDNESS);
        collect(off, Config.content.disabled(), "content.disabled", PackManager.JSON, PackManager.DISABLED);
        collect(off, Config.recipes.furnace(), "recipes.furnace", PackManager.JSON, PackManager.FURNACE);
        collect(off, Config.recipes.removals(), "recipes.removals", PackManager.JSON, PackManager.RECIPE_REMOVALS);
        collect(off, !Config.data.lootInjectionsOff(), "data.lootInjections", PackManager.JSON, PackManager.LOOT_INJECTIONS);
        collect(off, !Config.data.blockDropsOff(), "data.blockDrops", PackManager.JSON, PackManager.BLOCK_DROPS);
        collect(off, !Config.data.anvilsOff(), "data.anvils", PackManager.JSON, PackManager.ANVILS);
        collect(off, !Config.data.playerLootOff(), "data.playerLoot", PackManager.JSON, PackManager.PLAYER_LOOT);
        collect(off, !Config.data.registryRemapsOff(), "data.registryRemaps", PackManager.JSON, PackManager.REGISTRY_REMAP);
        collect(off, !Config.worldgen.loadOff(), "worldgen.load", PackManager.JSON, PackManager.WORLDGEN);
        collect(off, !Config.data.functionsOff(), "data.functions", PackManager.MCFUNCTION, PackManager.FUNCTIONS);
        if (off.isEmpty()) { return; }
        for (RDPLPack pack : packs) {
            for (String[] entry : off) {
                int count = pack.files(PackManager.CONTENT, entry[0], entry[1]).size();
                if (count == 0) { continue; }
                ContentLog.LOGGER.warn("Pack '{}' provides {} {} file(s), but {}, so they do nothing", pack.getName(), count, entry[0], entry[2]);
            }
        }
    }

    private static void collect(List<String[]> off, boolean enabled, String setting, String ext, String... folders) { because(off, enabled, setting + " is off in the config", ext, folders); }

    private static void clientSide(List<String[]> off, boolean enabled, String setting, String... folders) {
        if (Config.content.vanillaClients()) {
            because(off, false, "content.vanillaClients is on and they are the sort a client would need too", PackManager.JSON, folders);
            return;
        }
        collect(off, enabled, setting, PackManager.JSON, folders);
    }

    private static void because(List<String[]> off, boolean enabled, String reason, String ext, String... folders) {
        if (enabled) { return; }
        for (String folder : folders) { off.add(new String[] { folder, ext, reason }); }
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
                fromMods == 0 ? "" : ", " + fromMods + " of them shipped inside a mod jar and listed in " + PackManager.CONFIG + "/" + ModPacks.CONTROL_FILE);
        if (Config.content.vanillaClients()) {
            ContentLog.LOGGER.info("vanillaClients is on: nothing a client would have to know is registered from any pack, so clients without the mod can join. Pack blocks, items, fluids, materials, creative tabs, sounds, potion effects and types, villager professions, entity variants and exposures are skipped and named below; everything that lives on the server alone still applies");
            for (RDPLPack pack : packs) {
                for (String folder : List.of(PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS, PackManager.MATERIALS, PackManager.TABS, PackManager.SOUNDS, PackManager.POTIONS, PackManager.POTION_TYPES, PackManager.VILLAGERS, PackManager.ENTITIES, PackManager.EXPOSURES)) {
                    List<String> skipped = pack.files(PackManager.CONTENT, folder, PackManager.JSON);
                    if (skipped.isEmpty()) { continue; }
                    ContentLog.LOGGER.warn("Pack '{}' provides {} {} file(s), skipped because content.vanillaClients is on: {}", pack.getName(), skipped.size(), folder, String.join(", ", skipped));
                }
            }
        }
        if (!Config.packs.logContents()) { return; }
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " priority=" + pack.getPriority() : "";
            String tier = (pack.isOverriding() ? " overriding" : "") + (pack.isFromMod() ? " from a mod jar" : "");
            ContentLog.LOGGER.debug("  '{}'{}{}: files={} assets={} {} data={} {}", pack.getName(), priority, tier, pack.getFileCount(),
                    pack.getFileCount(PackType.CLIENT_RESOURCES), pack.getNamespaces(PackType.CLIENT_RESOURCES), pack.getFileCount(PackType.SERVER_DATA), pack.getNamespaces(PackType.SERVER_DATA));
        }
    }
}
