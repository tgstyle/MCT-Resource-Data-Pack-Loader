package mctmods.resourcedatapackloader.mixin.integration.reccomplex;

import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentBeard;

import ivorius.reccomplex.world.gen.feature.villages.GenericVillageCreationHandler;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GenericVillageCreationHandler.class, remap = false) public abstract class MixinGenericVillageCreationHandler {
    @Shadow public abstract Class<? extends StructureVillagePieces.Village> getComponentClass();

    @Inject(method = "getVillagePieceWeight", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$standAside(Random random, int villageSize, CallbackInfoReturnable<StructureVillagePieces.PieceWeight> cir) {
        if (!ContentBeard.wanted()) { return; }
        cir.setReturnValue(new StructureVillagePieces.PieceWeight(getComponentClass(), 0, 0));
    }
}
