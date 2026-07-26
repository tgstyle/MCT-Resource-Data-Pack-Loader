package mctmods.resourcedatapackloader.mixin.tconstruct;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "slimeknights.tconstruct.library.client.model.MaterialModelLoader", remap = false)
public abstract class MixinMaterialModelLoader {

    @Inject(method = "accepts", at = @At("HEAD"), cancellable = true)
    private void rdpl$declineVariantLocations(ResourceLocation modelLocation, CallbackInfoReturnable<Boolean> cir) {
        if (modelLocation instanceof ModelResourceLocation) { cir.setReturnValue(Boolean.FALSE); }
    }
}
