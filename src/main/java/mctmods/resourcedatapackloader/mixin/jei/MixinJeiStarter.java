package mctmods.resourcedatapackloader.mixin.jei;

import mctmods.resourcedatapackloader.compat.JEIPluginOrder;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = JeiStarter.class, remap = false)
public abstract class MixinJeiStarter {

    @Inject(method = "registerPlugins", at = @At("HEAD"))
    private static void rdpl$reset(List<IModPlugin> plugins, ModRegistry modRegistry, CallbackInfo ci) { JEIPluginOrder.reset(); }

    @Redirect(method = "registerPlugins", at = @At(value = "INVOKE", target = "Lmezz/jei/api/IModPlugin;register(Lmezz/jei/api/IModRegistry;)V"))
    private static void rdpl$trackRegister(IModPlugin plugin, IModRegistry registry) {
        JEIPluginOrder.begin(plugin);
        try { plugin.register(registry); }
        finally { JEIPluginOrder.end(); }
    }

    @ModifyArg(
            method = "start",
            at = @At(value = "INVOKE", target = "Lmezz/jei/startup/JeiStarter;sendRuntime(Ljava/util/List;Lmezz/jei/api/IJeiRuntime;)V"),
            index = 0
    )
    private List<IModPlugin> rdpl$orderRuntime(List<IModPlugin> plugins) { return JEIPluginOrder.reorder(plugins); }
}
