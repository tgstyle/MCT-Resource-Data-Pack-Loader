package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.core.util.SplashSlate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public class RDPLMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("RDPL");
    private static final String GATED_CONFIG = "mixins.resourcedatapackloader.rdpl.json";
    private static final List<String> SIDES = List.of("mixins", "client", "server");
    private static final Set<String> LIGHT = Set.of("DataLayerStorageMapMixin", "BlockLightStorageMapMixin", "SkyLightStorageMapMixin");
    private static final List<String> LIGHT_ENGINES = List.of("starlight", "scalablelux", "moonrise");
    private static Boolean vanillaLight;

    @Override public void onLoad(String mixinPackage) {
        checkGateNames();
        SplashSlate.paint();
    }

    private static void checkGateNames() {
        InputStream in = MixinService.getService().getResourceAsStream(GATED_CONFIG);
        if (in == null) { throw gateError("The mixin plugin could not find " + GATED_CONFIG); }
        Set<String> stale = new TreeSet<>(LIGHT);
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            for (String side : SIDES) {
                if (!json.has(side)) { continue; }
                for (JsonElement entry : json.getAsJsonArray(side)) {
                    String name = entry.getAsString();
                    stale.remove(name.substring(name.lastIndexOf('.') + 1));
                }
            }
        }
        catch (IOException failed) { throw gateError("The mixin plugin could not read " + GATED_CONFIG); }
        if (!stale.isEmpty()) { throw gateError("The mixin plugin gates names that no mixin config lists: " + stale); }
    }

    private static Error gateError(String message) {
        LOGGER.fatal(message);
        return new Error(message);
    }

    @Override public String getRefMapperConfig() { return null; }

    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return !LIGHT.contains(mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1)) || vanillaLight(); }

    private static boolean vanillaLight() {
        if (vanillaLight == null) {
            vanillaLight = true;
            for (String engine : LIGHT_ENGINES) {
                if (FMLLoader.getCurrent().getLoadingModList().getModFileById(engine) == null) { continue; }
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
