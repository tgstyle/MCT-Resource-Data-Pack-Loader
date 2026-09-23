package mctmods.resourcedatapackloader.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public class RDPLMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("RDPL");
    private static final Set<String> LIGHT = Set.of("MixinDataLayerStorageMap", "MixinBlockLightStorageMap", "MixinSkyLightStorageMap");
    private static final List<String> LIGHT_ENGINES = List.of("starlight", "scalablelux", "moonrise");
    private static Boolean vanillaLight;

    @Override public void onLoad(String mixinPackage) { SplashSlate.paint(); }

    @Override public String getRefMapperConfig() { return null; }

    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return !LIGHT.contains(mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1)) || vanillaLight(); }

    private static boolean vanillaLight() {
        if (vanillaLight == null) {
            vanillaLight = true;
            for (String engine : LIGHT_ENGINES) {
                if (LoadingModList.get().getModFileById(engine) == null) { continue; }
                vanillaLight = false;
                LOGGER.info("The light snapshot mixins stand down, since {} replaces the light engine", engine);
                break;
            }
        }
        return vanillaLight;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override public List<String> getMixins() { return null; }

    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
