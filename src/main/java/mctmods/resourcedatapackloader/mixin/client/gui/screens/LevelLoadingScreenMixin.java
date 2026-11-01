package mctmods.resourcedatapackloader.mixin.client.gui.screens;

import mctmods.resourcedatapackloader.common.pregen.SpawnProgress;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelLoadingScreen.class) public abstract class LevelLoadingScreenMixin {
    @Redirect(method = { "tick", "updateNarratedWidget" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/LevelLoadTracker;serverProgress()F")) private float rdpl$sitingCounted(LevelLoadTracker tracker) { return SpawnProgress.shown(Math.round(tracker.serverProgress() * 100.0F)) / 100.0F; }
}
