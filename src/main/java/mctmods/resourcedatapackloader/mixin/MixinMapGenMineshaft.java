package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.gen.structure.MapGenMineshaft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenMineshaft.class)
public abstract class MixinMapGenMineshaft {
    @Shadow private double chance;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void rdpl$chance(CallbackInfo ci) { chance = ContentStructurePlacement.chance(ContentStructurePlacement.MINESHAFTS, chance); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void rdpl$pinned(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructurePlacement.pinned(ContentStructurePlacement.MINESHAFTS, chunkX, chunkZ)) { cir.setReturnValue(true); }
    }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.MINESHAFTS, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
