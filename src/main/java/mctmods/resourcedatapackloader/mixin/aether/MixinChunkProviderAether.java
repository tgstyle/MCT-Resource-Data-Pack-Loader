package mctmods.resourcedatapackloader.mixin.aether;

import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import com.gildedgames.the_aether.world.ChunkProviderAether;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkProviderAether.class, remap = false) public abstract class MixinChunkProviderAether {
    @Shadow private World worldObj;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(worldObj, x, z);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$skipPopulate(int chunkX, int chunkZ, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(worldObj)) { ci.cancel(); }
    }
}
