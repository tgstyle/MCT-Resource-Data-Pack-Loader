package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.village.ContentVillagePlacement;

import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenVillage.class)
public abstract class MixinMapGenVillage {
    @Shadow private int distance;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$spacing(CallbackInfo ci) { distance = ContentVillagePlacement.spacing(distance); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$spawnDistance(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentVillagePlacement.farEnoughFromSpawn(((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
