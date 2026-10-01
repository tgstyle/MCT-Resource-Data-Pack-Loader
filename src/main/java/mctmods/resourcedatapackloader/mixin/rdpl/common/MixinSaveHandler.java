package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.CubicChunksWorld;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import java.io.File;

@Mixin(SaveHandler.class) public abstract class MixinSaveHandler {
    @Shadow @Final private File worldDirectory;

    @Inject(method = "loadWorldInfo", at = @At("HEAD")) private void rdpl$offerCubicChunksConversion(CallbackInfoReturnable<WorldInfo> cir) { CubicChunksWorld.offer(worldDirectory); }
}
