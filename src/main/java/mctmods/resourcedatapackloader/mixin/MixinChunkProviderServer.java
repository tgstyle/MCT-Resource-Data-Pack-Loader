package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
    @Shadow protected abstract void saveChunkData(Chunk chunkIn);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/ChunkProviderServer;saveChunkData(Lnet/minecraft/world/chunk/Chunk;)V"))
    private void rdpl$skipCleanWhileLighting(ChunkProviderServer provider, Chunk chunk) {
        if (ContentPregen.lightingOnly() && !chunk.needsSaving(false)) { return; }

        saveChunkData(chunk);
    }

    @Redirect(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;generateChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk rdpl$countMade(IChunkGenerator generator, int x, int z) {
        if (!ContentChunkWatch.watching()) { return generator.generateChunk(x, z); }

        ContentChunkWatch.made();
        long start = System.nanoTime();
        Chunk chunk = generator.generateChunk(x, z);
        ContentChunkWatch.terrain(System.nanoTime() - start);

        return chunk;
    }

    @Inject(method = "queueUnload", at = @At("TAIL"))
    private void rdpl$countReleased(Chunk chunkIn, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.released(); }
    }
}
