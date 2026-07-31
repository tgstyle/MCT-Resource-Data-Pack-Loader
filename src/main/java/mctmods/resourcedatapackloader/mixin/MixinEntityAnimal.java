package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAnimal.class)
public abstract class MixinEntityAnimal {
    @Inject(method = "getBlockPathWeight", at = @At("HEAD"), cancellable = true)
    private void rdpl$packGroundPath(BlockPos pos, CallbackInfoReturnable<Float> cir) {
        EntityAnimal animal = (EntityAnimal) (Object) this;
        if (ContentSpawning.sustainsAnimals(animal.world.getBlockState(pos.down()).getBlock())) { cir.setReturnValue(10.0F); }
    }

    @Inject(method = "getCanSpawnHere", at = @At("HEAD"), cancellable = true)
    private void rdpl$packGroundSpawns(CallbackInfoReturnable<Boolean> cir) {
        EntityAnimal animal = (EntityAnimal) (Object) this;
        BlockPos pos = new BlockPos(animal.posX, animal.getEntityBoundingBox().minY, animal.posZ);
        if (!ContentSpawning.sustainsAnimals(animal.world.getBlockState(pos.down()).getBlock())) { return; }
        if (animal.world.getLight(pos) <= 8) { return; }

        cir.setReturnValue(Boolean.TRUE);
    }
}
