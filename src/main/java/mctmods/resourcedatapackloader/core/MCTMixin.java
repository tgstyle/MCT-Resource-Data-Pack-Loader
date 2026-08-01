package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.content.ContentModels;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.core.util.ConfigLate;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mod(modid = MCTMixin.MIXIN_ID, name = "RDPL Mixin", version = "1.0", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*")
@IFMLLoadingPlugin.Name("RDPLCore")
@IFMLLoadingPlugin.SortingIndex(1001)
public class MCTMixin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final String MIXIN_ID = "resourcedatapackloader_mixin";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.sync(MIXIN_ID, Type.INSTANCE);
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
        LOGGER.info("Config compat: fixTinkersModelErrors={}", Config.compat.fixTinkersModelErrors);
        LOGGER.info("Config client: loadingScreenPercent={}", Config.client.loadingScreenPercent);
        LOGGER.info("Config tweaks: promptLeafDecay={} lenientPaths={}", Config.tweaks.promptLeafDecay, Config.tweaks.lenientPaths);
        PackManager.get().report();
        PackManager.get().warnAboutDisabledFeatures();
        if (Config.content.load) {
            ContentRegistry.registerFluids();
            MinecraftForge.EVENT_BUS.register(ContentRegistry.class);
            MinecraftForge.EVENT_BUS.register(ContentSounds.class);
            if (ContentPotions.load()) { MinecraftForge.EVENT_BUS.register(ContentPotions.class); }
            if (ContentVillagers.load()) { MinecraftForge.EVENT_BUS.register(ContentVillagers.class); }
            if (ContentBiomes.load()) { MinecraftForge.EVENT_BUS.register(ContentBiomes.class); }
            if (FMLCommonHandler.instance().getSide().isClient()) { MinecraftForge.EVENT_BUS.register(ContentModels.class); }
        }
    }

    @Override public String[] getASMTransformerClass() { return new String[0]; }

    @Override public String getModContainerClass() { return null; }

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

    @Override public List<String> getMixinConfigs() { return Arrays.asList("mixins.resourcedatapackloader.json", "mixins.resourcedatapackloader.groovyscript.json", "mixins.resourcedatapackloader.cofhemu.json"); }

}
