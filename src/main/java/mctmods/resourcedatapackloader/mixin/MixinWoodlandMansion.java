package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;

import net.minecraft.world.gen.structure.WoodlandMansion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WoodlandMansion.class)
public abstract class MixinWoodlandMansion {
    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 80))
    private int rdpl$spacing(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.MANSIONS, original); }

    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 79))
    private int rdpl$offset(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.MANSIONS, original + 1) - 1; }

    @ModifyConstant(method = "canSpawnStructureAtCoords", constant = @Constant(intValue = 60))
    private int rdpl$separation(int original) { return ContentStructurePlacement.separation(ContentStructurePlacement.MANSIONS, original); }

    @ModifyConstant(method = "getNearestStructurePos", constant = @Constant(intValue = 80))
    private int rdpl$locateSpacing(int original) { return ContentStructurePlacement.spacing(ContentStructurePlacement.MANSIONS, original); }

    @ModifyConstant(method = "getNearestStructurePos", constant = @Constant(intValue = 20))
    private int rdpl$locateSeparation(int original) { return ContentStructurePlacement.separation(ContentStructurePlacement.MANSIONS, original); }

    @Inject(method = "canSpawnStructureAtCoords", at = @At("RETURN"), cancellable = true)
    private void rdpl$placement(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) { return; }
        if (ContentStructurePlacement.allows(ContentStructurePlacement.MANSIONS, ((AccessorMapGenBase) this).rdpl$getWorld(), chunkX, chunkZ)) { return; }

        cir.setReturnValue(false);
    }
}
