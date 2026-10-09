package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.core.util.ConfigLate;
import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@SuppressWarnings("unused") public class MCTLateMixin implements ILateMixinLoader {
    private static Boolean tinkersFix;
    private static Boolean pulsar;

    private static final String PREFIX = "mixins.resourcedatapackloader.";
    private static final Map<String, BooleanSupplier> QUEUED = new LinkedHashMap<>();

    static {
        QUEUED.put("jei", () -> Loader.isModLoaded("jei"));
        QUEUED.put("tconstruct", () -> tinkersFixEnabled() && Loader.isModLoaded("tconstruct"));
        QUEUED.put("conarm", () -> tinkersFixEnabled() && Loader.isModLoaded("conarm"));
        QUEUED.put("bop", () -> Loader.isModLoaded("biomesoplenty"));
        QUEUED.put("crafttweaker", () -> Loader.isModLoaded("crafttweaker"));
        QUEUED.put("dungeons2", () -> Loader.isModLoaded("dungeons2"));
        QUEUED.put("quark", () -> Loader.isModLoaded("quark"));
        QUEUED.put("aether", () -> Loader.isModLoaded("aether_legacy"));
        QUEUED.put("betweenlands", () -> Loader.isModLoaded("thebetweenlands"));
        QUEUED.put("lostcities", () -> Loader.isModLoaded("lostcities"));
        QUEUED.put("neoterra", () -> Loader.isModLoaded("neoterra"));
        QUEUED.put("otg", () -> Loader.isModLoaded("openterraingenerator"));
        QUEUED.put("rtg", () -> Loader.isModLoaded("rtg"));
        QUEUED.put("twilightforest", () -> Loader.isModLoaded("twilightforest"));
        QUEUED.put("reccomplex", () -> Loader.isModLoaded("reccomplex"));
        QUEUED.put("mca", () -> Loader.isModLoaded("mca"));
        QUEUED.put("waystones", () -> Loader.isModLoaded("waystones"));
        QUEUED.put("paperfixes", () -> Loader.isModLoaded("paperfixes"));
        QUEUED.put("vanillaportals", () -> !Loader.isModLoaded("universaltweaks"));
        QUEUED.put("betterf3", () -> Loader.isModLoaded("betterf3reborn"));
        QUEUED.put("galacticraft", () -> Loader.isModLoaded("galacticraftcore"));
        QUEUED.put("asmodeuscore", () -> Loader.isModLoaded("galacticraftcore") && Loader.isModLoaded("asmodeuscore"));
        QUEUED.put("extraplanets", () -> Loader.isModLoaded("extraplanets"));
        QUEUED.put("pulsar", MCTLateMixin::pulsarPresent);
    }

    @Override public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>(QUEUED.size());
        for (String name : QUEUED.keySet()) { configs.add(PREFIX + name + PackManager.JSON_SUFFIX); }
        return configs;
    }

    @Override public boolean shouldMixinConfigQueue(String mixinConfig) {
        for (Map.Entry<String, BooleanSupplier> entry : QUEUED.entrySet()) {
            if (mixinConfig.endsWith("." + entry.getKey() + PackManager.JSON_SUFFIX)) { return entry.getValue().getAsBoolean(); }
        }
        return true;
    }

    private static boolean tinkersFixEnabled() {
        if (tinkersFix == null) { tinkersFix = ConfigCore.read(ConfigLate.COMPAT, "fixTinkersModelErrors"); }
        return tinkersFix;
    }

    private static boolean pulsarPresent() {
        if (pulsar == null) { pulsar = Launch.classLoader.getResource("com/sumirelabs/pulsar/proxy/IProxy.class") != null; }
        return pulsar;
    }
}
