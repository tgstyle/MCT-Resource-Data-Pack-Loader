package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.content.ContentModels;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.portal.PortalEvents;
import mctmods.resourcedatapackloader.content.village.ContentVillageDecor;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.core.util.ConfigLate;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.ModJars;

import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.IEarlyMixinLoader;
import java.io.File;
import java.nio.file.Path;
import java.util.zip.ZipFile;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mod(modid = MCTMixin.MIXIN_ID, name = "RDPL Mixin", version = "1.0", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*") @IFMLLoadingPlugin.Name("RDPLCore") @IFMLLoadingPlugin.MCVersion("1.12.2") @IFMLLoadingPlugin.SortingIndex(1001) public class MCTMixin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final String MIXIN_ID = "resourcedatapackloader_mixin";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.sync(MIXIN_ID, Type.INSTANCE);
        Lang.load();
        if (Config.content.vanillaClients) {
            LOGGER.info("vanillaClients is on: nothing from any pack is registered, so clients without the mod can join. Blocks, items, fluids, materials, sounds, potions, villagers and their trades, biomes and dimensions in packs are all skipped and named below; everything that lives on the server alone, such as ore veins, gates, world templates and recipes, still applies");
        }
        ContentLog.LOGGER.setDebug(Config.worldgen.worldgenDebug);
        LOGGER.info("Config packs: rootDirectory={} overrideResourcePacks={} warnOnCaseMismatch={} logContents={} traceUnresolvedVariables={}",
                Config.packs.rootDirectory, Config.packs.overrideResourcePacks, Config.packs.warnOnCaseMismatch, Config.packs.logContents, Config.packs.traceUnresolvedVariables);
        LOGGER.info("Config content: load={} sounds={} fuels={} oreDictionary={} potions={} brewing={} villagers={} biomes={}",
                Config.content.load, Config.content.sounds, Config.content.fuels, Config.content.oreDictionary, Config.content.potions, Config.content.brewing, Config.content.villagers, Config.content.biomes);
        LOGGER.info("Config recipes: furnace={} removals={} disableOverrides={} skipMissingItems={} tolerateMissingInAdvancements={} blockRecipes={} recipeMatch={} blockFurnaceRecipes={}",
                Config.recipes.furnace, Config.recipes.removals, Config.recipes.disableOverrides, Config.recipes.skipMissingItems, Config.recipes.tolerateMissingInAdvancements, Config.recipes.blockRecipes, Config.recipes.recipeMatch, Config.recipes.blockFurnaceRecipes);
        LOGGER.info("Config data: lootInjections={} functions={} registryRemaps={}",
                Config.data.lootInjections, Config.data.functions, Config.data.registryRemaps);
        LOGGER.info("Config worldgen: load={} retrogen={} adoptExistingChunks={} retrogenKey='{}' retrogenChunksPerTick={} blockOres={} oreWhitelist={} oreTypes={} oreTypesAreBlacklist={} flatBedrock={} flatBedrockRetrogen={} blockBiomes={} biomeNames={} biomeNamesAreBlacklist={} worldgenDebug={} readCofhWorldFiles={}",
                Config.worldgen.load, Config.worldgen.retrogen, Config.worldgen.adoptExistingChunks, Config.worldgen.retrogenKey, Config.worldgen.retrogenChunksPerTick, Config.worldgen.blockOres, Arrays.toString(Config.worldgen.oreWhitelist), Arrays.toString(Config.worldgen.oreTypes), Config.worldgen.oreTypesAreBlacklist, Config.worldgen.flatBedrock, Config.worldgen.flatBedrockRetrogen, Config.worldgen.blockBiomes, Arrays.toString(Config.worldgen.biomeNames), Config.worldgen.biomeNamesAreBlacklist, Config.worldgen.worldgenDebug, Config.worldgen.readCofhWorldFiles);
        LOGGER.info("Config terrain: generatorOptions='{}'", Config.worldgen.generatorOptions);
        LOGGER.info("Config chunks: spawnChunkRadius={} spawnChunkRadii={} hurryWritesAbove={} pregenKeepLoaded={} pregenMillisPerRound={} pregenPauseAbove={}",
                Config.chunks.spawnChunkRadius, Arrays.toString(Config.chunks.spawnChunkRadii), Config.chunks.hurryWritesAbove, Config.chunks.pregenKeepLoaded, Config.chunks.pregenMillisPerRound, Config.chunks.pregenPauseAbove);
        LOGGER.info("Config compat: fixTinkersModelErrors={}", Config.compat.fixTinkersModelErrors);
        LOGGER.info("Config client: loadingScreenPercent={}", Config.client.loadingScreenPercent);
        LOGGER.info("Config tweaks: promptLeafDecay={} lenientPaths={}", Config.tweaks.promptLeafDecay, Config.tweaks.lenientPaths);
        PackManager.get().report();
        PackManager.get().warnAboutDisabledFeatures();
        if (Config.registersToClients()) {
            ContentRegistry.registerFluids();
            MinecraftForge.EVENT_BUS.register(ContentRegistry.class);
            MinecraftForge.EVENT_BUS.register(ContentSounds.class);
            if (ContentPotions.load()) { MinecraftForge.EVENT_BUS.register(ContentPotions.class); }
            if (ContentVillagers.load()) { MinecraftForge.EVENT_BUS.register(ContentVillagers.class); }
            if (ContentBiomes.load()) { MinecraftForge.EVENT_BUS.register(ContentBiomes.class); }
            if (FMLCommonHandler.instance().getSide().isClient()) { MinecraftForge.EVENT_BUS.register(ContentModels.class); }
        }
        MinecraftForge.EVENT_BUS.register(ContentBeard.class);
        MinecraftForge.EVENT_BUS.register(ContentVillageDecor.class);
        MinecraftForge.EVENT_BUS.register(PortalEvents.class);
    }

    @Override public String[] getASMTransformerClass() { return new String[]{
            "mctmods.resourcedatapackloader.core.transformer.RubicWorldEditTransformer"}; }

    @Override public String getModContainerClass() {
        if (cofhWorldPresent()) { return null; }
        LOGGER.info("CoFH World is not installed, providing an emulated container so mods that require it can load");
        return "mctmods.resourcedatapackloader.core.CofhWorldContainer";
    }

    private static boolean cofhWorldPresent() { return inModJars(); }

    private static boolean inModJars() {
        if (MCTMixin.class.getClassLoader().getResource("cofh/cofhworld/CoFHWorld.class") != null) { return true; }
        try {
            for (File jar : ModJars.list()) {
                try (ZipFile zip = new ZipFile(jar)) {
                    if (zip.getEntry("cofh/cofhworld/CoFHWorld.class") != null) { return true; }
                }
                catch (Exception unreadable) { ContentLog.LOGGER.debug("Could not open {} while looking for {}", jar.getName(), "CoFH World"); }
            }
            return false;
        }
        catch (Exception failed) {
            LOGGER.warn("Could not scan the mods folder for " + "CoFH World" + ", assuming it is absent", failed);
            return false;
        }
    }

    @Override public String getSetupClass() { return null; }

    @Override public void injectData(Map<String, Object> data) {
        Object location = data.get("mcLocation");
        if (!(location instanceof File)) {
            LOGGER.error("No mcLocation supplied, packs will not be scanned until the server starts");
            return;
        }
        File mcDir = (File) location;
        ConfigCore.at(mcDir);
        Path root = mcDir.toPath().resolve(rootDirectory());
        LOGGER.info("Pack root: {}", root);
        PackManager.get().scan(root);
    }

    private static String rootDirectory() { return ConfigCore.text(ConfigLate.PACKS, "rootDirectory", PackManager.ROOT_DIRECTORY); }

    @Override public String getAccessTransformerClass() { return null; }

    @Override public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.resourcedatapackloader.rdpl.json", "mixins.resourcedatapackloader.fml.json", "mixins.resourcedatapackloader.groovyscript.json",
                "mixins.resourcedatapackloader.vanillagrowth.json", "mixins.resourcedatapackloader.vanillatweaks.json");
    }

    @Override public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (mixinConfig.endsWith(".fml.json")) { return !cleanroom(); }
        return true;
    }

    private static Boolean cleanroomLoader;

    private static boolean cleanroom() {
        if (cleanroomLoader == null) {
            try {
                Class.forName("top.outlands.foundation.boot.Foundation", false, MCTMixin.class.getClassLoader());
                cleanroomLoader = Boolean.TRUE;
                LOGGER.warn("Running under Cleanroom, which loads FML before mixins can reach it. Pack 'requires' entries will not stop the game from starting");
            }
            catch (ClassNotFoundException absent) { cleanroomLoader = Boolean.FALSE; }
        }
        return cleanroomLoader;
    }
}
