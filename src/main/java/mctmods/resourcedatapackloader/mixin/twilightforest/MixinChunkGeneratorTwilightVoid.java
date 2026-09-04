package mctmods.resourcedatapackloader.mixin.twilightforest;

import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.world.ChunkGeneratorTFBase;
import twilightforest.world.ChunkGeneratorTwilightVoid;

@Mixin(value = ChunkGeneratorTwilightVoid.class, remap = false) public abstract class MixinChunkGeneratorTwilightVoid extends ChunkGeneratorTFBase {
    @SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) private MixinChunkGeneratorTwilightVoid() { super(null, 0L, false, false); }

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(world, x, z);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }
}
