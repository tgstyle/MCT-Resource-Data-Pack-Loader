package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.core.util.ConfigLate;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class MCTLateMixin implements ILateMixinLoader {
    private static Boolean tinkersFix;

    @Override public List<String> getMixinConfigs() {
        return Arrays.asList(
                "mixins.resourcedatapackloader.jei.json",
                "mixins.resourcedatapackloader.tconstruct.json",
                "mixins.resourcedatapackloader.conarm.json",
                "mixins.resourcedatapackloader.bop.json",
                "mixins.resourcedatapackloader.crafttweaker.json",
                "mixins.resourcedatapackloader.quark.json",
                "mixins.resourcedatapackloader.vanillaportals.json",
                "mixins.resourcedatapackloader.vanillagrowth.json");
    }

    @Override public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (mixinConfig.endsWith(".jei.json")) { return Loader.isModLoaded("jei"); }
        if (mixinConfig.endsWith(".tconstruct.json")) { return tinkersFixEnabled() && Loader.isModLoaded("tconstruct"); }
        if (mixinConfig.endsWith(".conarm.json")) { return tinkersFixEnabled() && Loader.isModLoaded("conarm"); }
        if (mixinConfig.endsWith(".bop.json")) { return Loader.isModLoaded("biomesoplenty"); }
        if (mixinConfig.endsWith(".crafttweaker.json")) { return Loader.isModLoaded("crafttweaker"); }
        if (mixinConfig.endsWith(".quark.json")) { return Loader.isModLoaded("quark"); }
        if (mixinConfig.endsWith(".vanillaportals.json")) { return !Loader.isModLoaded("universaltweaks"); }
        if (mixinConfig.endsWith(".vanillagrowth.json")) { return !Loader.isModLoaded("universaltweaks"); }
        return true;
    }

    private static boolean tinkersFixEnabled() {
        if (tinkersFix == null) { tinkersFix = ConfigCore.read(ConfigLate.COMPAT, "fixTinkersModelErrors"); }
        return tinkersFix;
    }

}
