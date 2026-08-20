package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.core.optifine.ChunkPos3;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("target") @Mixin(ViewFrustum.class) public class MixinViewFrustumOptifine {
    @Dynamic @Redirect(method = "updateVboRegion(Lnet/minecraft/client/renderer/chunk/RenderChunk;)V",
            at = @At(value = "NEW", target = "net/minecraft/util/math/ChunkPos"))
    private ChunkPos getChunkPos(int x, int z, RenderChunk renderChunk) { return new ChunkPos3(x, renderChunk.getPosition().getY() & ~255, z); }
}
