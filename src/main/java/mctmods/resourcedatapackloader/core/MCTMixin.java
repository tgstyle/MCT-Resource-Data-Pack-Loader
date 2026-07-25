package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.Config;
import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
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
        LOGGER.info("Loaded config: rootDirectory={} overrideResourcePacks={} warnOnCaseMismatch={} logPackContents={} disableRecipeOverrides={} tolerateMissingRecipes={}", Config.settings.rootDirectory, Config.settings.overrideResourcePacks, Config.settings.warnOnCaseMismatch, Config.settings.logPackContents, Config.settings.disableRecipeOverrides, Config.settings.tolerateMissingRecipes);
        PackManager.get().report();
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
        Path root = mcDir.toPath().resolve(rootDirectory(mcDir));
        LOGGER.info("Pack root: {}", root);
        PackManager.get().scan(root);
    }

    private static String rootDirectory(File mcDir) {
        try {
            Configuration cfg = new Configuration(new File(mcDir, "config/mct_resourcedatapackloader_mixin.cfg"));
            cfg.load();
            Property prop = cfg.getCategory("settings").get("rootDirectory");
            if (prop != null) { return prop.getString(); }
        }
        catch (RuntimeException ex) {
            LOGGER.error("Could not read rootDirectory from config, using {}", PackManager.ROOT_DIRECTORY, ex);
        }
        return PackManager.ROOT_DIRECTORY;
    }

    @Override public String getAccessTransformerClass() { return null; }

    @Override public List<String> getMixinConfigs() { return Collections.singletonList("mixins.resourcedatapackloader.json"); }
}
