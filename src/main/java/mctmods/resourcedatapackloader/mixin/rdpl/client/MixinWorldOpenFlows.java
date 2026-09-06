package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class) public abstract class MixinWorldOpenFlows {
    @Inject(method = "confirmWorldCreation", at = @At("HEAD"), cancellable = true)
    private static void rdpl$skipCreationWarning(Minecraft minecraft, CreateWorldScreen screen, Lifecycle lifecycle, Runnable loadWorld, boolean skipWarnings, CallbackInfo ci) {
        if (Config.tweaks.experimentalWarning() || lifecycle == Lifecycle.stable()) { return; }
        ContentLog.LOGGER.debug("Skipping the experimental settings warning for the new world, tweaks.experimentalWarning is off");
        loadWorld.run();
        ci.cancel();
    }

    @Redirect(method = "doLoadLevel(Lnet/minecraft/client/gui/screens/Screen;Ljava/lang/String;ZZZ)V", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;", remap = true))
    private Lifecycle rdpl$skipLoadWarning(WorldData data) {
        Lifecycle held = data.worldGenSettingsLifecycle();
        if (Config.tweaks.experimentalWarning() || held == Lifecycle.stable()) { return held; }
        ContentLog.LOGGER.debug("Skipping the experimental settings warning while opening the world, tweaks.experimentalWarning is off");
        return Lifecycle.stable();
    }
}
