package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGeneratorEnd.class)
public abstract class MixinChunkGeneratorEnd {
    @Shadow @Final
    private World world;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true)
    private void rdpl$emptyChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (!ContentVoidWorld.appliesTo(world)) { return; }

        Chunk chunk = new Chunk(world, new ChunkPrimer(), x, z);
        chunk.generateSkylightMap();
        cir.setReturnValue(chunk);
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }
}
