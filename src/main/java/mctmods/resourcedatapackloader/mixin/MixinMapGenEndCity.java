package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.gen.structure.MapGenEndCity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenEndCity.class)
public abstract class MixinMapGenEndCity {
    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 20))
    private int rdpl$spacing(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.ENDCITIES, original); }

    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 9))
    private int rdpl$separation(int original) { return ContentStructurePlacement.separation(ContentStructurePlacement.ENDCITIES, original); }

    @ModifyConstant(method = "getNearestStructurePos", constant = @Constant(intValue = 20))
    private int rdpl$locateSpacing(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.ENDCITIES, original); }

    @ModifyConstant(method = "getNearestStructurePos", constant = @Constant(intValue = 11))
    private int rdpl$locateSeparation(int original) { return ContentStructurePlacement.separation(ContentStructurePlacement.ENDCITIES, original); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.ENDCITIES, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
