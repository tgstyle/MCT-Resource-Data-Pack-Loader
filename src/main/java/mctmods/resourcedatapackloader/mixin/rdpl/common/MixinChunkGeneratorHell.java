package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorHell;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGeneratorHell.class) public abstract class MixinChunkGeneratorHell {
    @Shadow @Final private World world;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true) private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(world, x, z);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true) private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }
}
