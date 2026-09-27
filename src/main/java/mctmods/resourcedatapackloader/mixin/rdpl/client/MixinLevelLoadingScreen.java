package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.SpawnProgress;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelLoadingScreen.class) public abstract class MixinLevelLoadingScreen {
    @Redirect(method = { "tick", "updateNarratedWidget" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/LevelLoadTracker;serverProgress()F"))
    private float rdpl$sitingCounted(LevelLoadTracker tracker) { return SpawnProgress.shown(Math.round(tracker.serverProgress() * 100.0F)) / 100.0F; }
}
