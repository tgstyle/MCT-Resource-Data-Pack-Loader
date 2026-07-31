package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk {
    @Shadow public abstract ChunkPos getPos();

    @Inject(method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At("HEAD"))
    private void rdpl$traceCascade(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, CallbackInfo ci) {
        ChunkPos parent = AccessorChunk.rdpl$getPopulating();
        if (parent != null) { ContentCascade.report(parent, getPos()); }
    }
}
