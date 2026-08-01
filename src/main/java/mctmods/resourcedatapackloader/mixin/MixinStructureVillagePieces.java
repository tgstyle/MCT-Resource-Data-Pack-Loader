package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.village.ContentVillages;

import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(StructureVillagePieces.class)
public abstract class MixinStructureVillagePieces {
    @Inject(method = "getStructureVillageWeightedPieceList", at = @At("RETURN"))
    private static void rdpl$filterPieces(Random random, int size, CallbackInfoReturnable<List<StructureVillagePieces.PieceWeight>> cir) {
        List<StructureVillagePieces.PieceWeight> list = cir.getReturnValue();
        if (list == null || !ContentVillages.filtering()) { return; }

        list.removeIf(weight -> ContentVillages.blocked(weight.villagePieceClass));
    }
}
