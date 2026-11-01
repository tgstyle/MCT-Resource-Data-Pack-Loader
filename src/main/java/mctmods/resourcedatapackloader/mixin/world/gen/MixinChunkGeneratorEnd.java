package mctmods.resourcedatapackloader.mixin.world.gen;

import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGeneratorEnd.class) public abstract class MixinChunkGeneratorEnd {
    @Inject(method = "generateChunk(II)Lnet/minecraft/world/chunk/Chunk;", at = @At("HEAD"), cancellable = true) private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(rdpl$world(), x, z);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "populate(II)V", at = @At("HEAD"), cancellable = true) private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(rdpl$world())) { ci.cancel(); }
    }

    @Unique private World rdpl$world() { return ((ChunkGeneratorEnd) (Object) this).world; }
}
