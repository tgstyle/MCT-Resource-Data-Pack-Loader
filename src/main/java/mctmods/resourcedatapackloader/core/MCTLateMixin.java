package mctmods.resourcedatapackloader.core;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class MCTLateMixin implements ILateMixinLoader {
    private static Boolean tinkersFix;

    @Override public List<String> getMixinConfigs() {
        return Arrays.asList(
                "mixins.resourcedatapackloader.jei.json",
                "mixins.resourcedatapackloader.tconstruct.json",
                "mixins.resourcedatapackloader.conarm.json");
    }

    @Override public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (mixinConfig.endsWith(".jei.json")) { return Loader.isModLoaded("jei"); }
        if (mixinConfig.endsWith(".tconstruct.json")) { return tinkersFixEnabled() && Loader.isModLoaded("tconstruct"); }
        if (mixinConfig.endsWith(".conarm.json")) { return tinkersFixEnabled() && Loader.isModLoaded("conarm"); }
        return true;
    }

    private static boolean tinkersFixEnabled() {
        if (tinkersFix == null) { tinkersFix = readFlag(); }
        return tinkersFix;
    }

    private static boolean readFlag() {
        try {
            Configuration cfg = new Configuration(new File(Loader.instance().getConfigDir(), ConfigPath.FILE));
            cfg.load();
            String category = ConfigPath.SETTINGS;
            if (!cfg.hasCategory(category)) {
                MCTMixin.LOGGER.warn("Config has no '{}' category yet, leaving fixTinkersModelErrors off until the next start", category);
                return false;
            }
            Property prop = cfg.getCategory(category).get("fixTinkersModelErrors");
            if (prop != null) { return prop.getBoolean(); }
        }
        catch (RuntimeException ex) {
            MCTMixin.LOGGER.error("Could not read {} from config, leaving it off", "fixTinkersModelErrors", ex);
        }
        return false;
    }
}
