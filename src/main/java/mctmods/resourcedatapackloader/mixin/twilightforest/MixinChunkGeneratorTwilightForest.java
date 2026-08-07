package mctmods.resourcedatapackloader.mixin.twilightforest;

import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.world.ChunkGeneratorTFBase;
import twilightforest.world.ChunkGeneratorTwilightForest;

@Mixin(value = ChunkGeneratorTwilightForest.class, remap = false)
public abstract class MixinChunkGeneratorTwilightForest extends ChunkGeneratorTFBase {
    @SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) private MixinChunkGeneratorTwilightForest() { super(null, 0L, false, false); }

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (!ContentVoidWorld.appliesTo(world)) { return; }

        Chunk chunk = new Chunk(world, new ChunkPrimer(), x, z);
        chunk.generateSkylightMap();
        cir.setReturnValue(chunk);
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }
}
