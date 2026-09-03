package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProviderServer.class) public abstract class MixinChunkProviderServer {
    @Shadow protected abstract void saveChunkData(Chunk chunkIn);
    @Shadow @Final public WorldServer world;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/ChunkProviderServer;saveChunkData(Lnet/minecraft/world/chunk/Chunk;)V"))
    private void rdpl$skipCleanWhileLighting(ChunkProviderServer provider, Chunk chunkIn) {
        if (ContentPregen.lightingOnly() && !chunkIn.needsSaving(false)) { return; }
        saveChunkData(chunkIn);
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

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;unloadQueued:Z", opcode = Opcodes.GETFIELD, remap = false))
    private boolean rdpl$keepWatched(Chunk chunk) {
        if (!chunk.unloadQueued) { return false; }
        PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(chunk.x, chunk.z);
        if (entry == null || entry.getChunk() != chunk) { return true; }
        chunk.unloadQueued = false;
        ContentLog.LOGGER.debug("Chunk {}, {} was queued for unloading while a player's map still held it, so it stays loaded", chunk.x, chunk.z);
        return false;
    }

    @Inject(method = "queueUnload", at = @At("TAIL")) private void rdpl$countReleased(Chunk chunkIn, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.released(); }
    }
}
