package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.village.ContentVillages;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureVillagePieces.Village.class)
public abstract class MixinVillageBlocks {
    @Inject(method = "getBiomeSpecificBlockState", at = @At("RETURN"), cancellable = true)
    private void rdpl$packBlocks(IBlockState blockstateIn, CallbackInfoReturnable<IBlockState> cir) {
        IBlockState wanted = ContentVillages.swap(cir.getReturnValue());
        if (wanted == null) { wanted = ContentVillages.swap(blockstateIn); }
        if (wanted != null) { cir.setReturnValue(wanted); }
    }
}
