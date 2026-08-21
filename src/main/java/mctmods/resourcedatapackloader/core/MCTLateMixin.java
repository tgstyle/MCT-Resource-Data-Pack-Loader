package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.core.util.ConfigLate;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused") public class MCTLateMixin implements ILateMixinLoader {
    private static Boolean tinkersFix;

    @Override public List<String> getMixinConfigs() {
        return Arrays.asList(
                "mixins.resourcedatapackloader.jei.json",
                "mixins.resourcedatapackloader.tconstruct.json",
                "mixins.resourcedatapackloader.conarm.json",
                "mixins.resourcedatapackloader.bop.json",
                "mixins.resourcedatapackloader.crafttweaker.json",
                "mixins.resourcedatapackloader.dungeons2.json",
                "mixins.resourcedatapackloader.quark.json",
                "mixins.resourcedatapackloader.aether.json",
                "mixins.resourcedatapackloader.betweenlands.json",
                "mixins.resourcedatapackloader.lostcities.json",
                "mixins.resourcedatapackloader.neoterra.json",
                "mixins.resourcedatapackloader.otg.json",
                "mixins.resourcedatapackloader.rtg.json",
                "mixins.resourcedatapackloader.twilightforest.json",
                "mixins.resourcedatapackloader.reccomplex.json",
                "mixins.resourcedatapackloader.mca.json",
                "mixins.resourcedatapackloader.waystones.json",
                "mixins.resourcedatapackloader.paperfixes.json",
                "mixins.resourcedatapackloader.vanillaportals.json",
                "mixins.resourcedatapackloader.betterf3.json");
    }

    @Override public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (mixinConfig.endsWith(".jei.json")) { return Loader.isModLoaded("jei"); }
        if (mixinConfig.endsWith(".tconstruct.json")) { return tinkersFixEnabled() && Loader.isModLoaded("tconstruct"); }
        if (mixinConfig.endsWith(".conarm.json")) { return tinkersFixEnabled() && Loader.isModLoaded("conarm"); }
        if (mixinConfig.endsWith(".bop.json")) { return Loader.isModLoaded("biomesoplenty"); }
        if (mixinConfig.endsWith(".crafttweaker.json")) { return Loader.isModLoaded("crafttweaker"); }
        if (mixinConfig.endsWith(".dungeons2.json")) { return Loader.isModLoaded("dungeons2"); }
        if (mixinConfig.endsWith(".quark.json")) { return Loader.isModLoaded("quark"); }
        if (mixinConfig.endsWith(".aether.json")) { return Loader.isModLoaded("aether_legacy"); }
        if (mixinConfig.endsWith(".betweenlands.json")) { return Loader.isModLoaded("thebetweenlands"); }
        if (mixinConfig.endsWith(".lostcities.json")) { return Loader.isModLoaded("lostcities"); }
        if (mixinConfig.endsWith(".neoterra.json")) { return Loader.isModLoaded("neoterra"); }
        if (mixinConfig.endsWith(".otg.json")) { return Loader.isModLoaded("openterraingenerator"); }
        if (mixinConfig.endsWith(".rtg.json")) { return Loader.isModLoaded("rtg"); }
        if (mixinConfig.endsWith(".twilightforest.json")) { return Loader.isModLoaded("twilightforest"); }
        if (mixinConfig.endsWith(".reccomplex.json")) { return Loader.isModLoaded("reccomplex"); }
        if (mixinConfig.endsWith(".mca.json")) { return Loader.isModLoaded("mca"); }
        if (mixinConfig.endsWith(".waystones.json")) { return Loader.isModLoaded("waystones"); }
        if (mixinConfig.endsWith(".paperfixes.json")) { return Loader.isModLoaded("paperfixes"); }
        if (mixinConfig.endsWith(".vanillaportals.json")) { return !Loader.isModLoaded("universaltweaks"); }
        if (mixinConfig.endsWith(".betterf3.json")) { return Loader.isModLoaded("betterf3reborn"); }
        return true;
    }

    private static boolean tinkersFixEnabled() {
        if (tinkersFix == null) { tinkersFix = ConfigCore.read(ConfigLate.COMPAT, "fixTinkersModelErrors"); }
        return tinkersFix;
    }
}
