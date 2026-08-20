package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapGenBase.class) public abstract class MixinMapGenBase {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$skipCarving(World worldIn, int x, int z, ChunkPrimer primer, CallbackInfo ci) {
        if (ContentStructures.blocks(worldIn, (MapGenBase) (Object) this)) { ci.cancel(); }
    }
}
