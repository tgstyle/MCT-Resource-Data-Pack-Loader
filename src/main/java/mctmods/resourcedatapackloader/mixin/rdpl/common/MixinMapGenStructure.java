package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(MapGenStructure.class) public abstract class MixinMapGenStructure {
    @Inject(method = "generateStructure", at = @At("HEAD"), cancellable = true) private void rdpl$skipStructure(World worldIn, Random randomIn, ChunkPos chunkCoord, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructures.blocks(worldIn, (MapGenBase) (Object) this)) { cir.setReturnValue(Boolean.FALSE); }
    }
}
