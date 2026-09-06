package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class) public abstract class MixinWorldOpenFlows {
    @Inject(method = "confirmWorldCreation", at = @At("HEAD"), cancellable = true)
    private static void rdpl$skipCreationWarning(Minecraft minecraft, CreateWorldScreen screen, Lifecycle lifecycle, Runnable loadWorld, boolean skipWarnings, CallbackInfo ci) {
        if (Config.tweaks.experimentalWarning() || lifecycle != Lifecycle.experimental()) { return; }
        ContentLog.LOGGER.debug("Skipping the experimental settings warning for the new world, tweaks.experimentalWarning is off");
        loadWorld.run();
        ci.cancel();
    }

    @Inject(method = "askForBackup", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipLoadWarning(LevelStorageSource.LevelStorageAccess levelStorage, boolean customized, Runnable loadLevel, Runnable onCancel, CallbackInfo ci) {
        if (customized || Config.tweaks.experimentalWarning()) { return; }
        ContentLog.LOGGER.debug("Skipping the experimental settings warning while opening the world, tweaks.experimentalWarning is off");
        loadLevel.run();
        ci.cancel();
    }
}
