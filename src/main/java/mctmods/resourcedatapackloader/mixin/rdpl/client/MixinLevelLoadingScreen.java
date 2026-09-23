package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.SpawnProgress;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelLoadingScreen.class) public abstract class MixinLevelLoadingScreen {
    @Redirect(method = "getFormattedProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/progress/StoringChunkProgressListener;getProgress()I"))
    private int rdpl$sitingCounted(StoringChunkProgressListener progressListener) { return SpawnProgress.shown(progressListener.getProgress()); }
}
