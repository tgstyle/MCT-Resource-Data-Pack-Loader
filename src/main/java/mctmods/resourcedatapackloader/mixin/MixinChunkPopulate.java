package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunkPopulate {
    @Shadow private boolean isTerrainPopulated;

    @Inject(method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$dressNothingWhileLighting(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, CallbackInfo ci) {
        if (isTerrainPopulated || !ContentPregen.lightingOnly()) { return; }
        ContentChunkWatch.dressingHeldOff();
        ci.cancel();
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void rdpl$lightAfterDressing(Chunk chunk) {
        isTerrainPopulated = true;
        ContentChunkWatch.lightDeferred();
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;populate(II)V"))
    private void rdpl$timeDecoration(IChunkGenerator generator, int x, int z) {
        if (!ContentChunkWatch.watching()) {
            generator.populate(x, z);
            return;
        }

        long start = System.nanoTime();
        generator.populate(x, z);
        ContentChunkWatch.decorated(System.nanoTime() - start);
    }
}
