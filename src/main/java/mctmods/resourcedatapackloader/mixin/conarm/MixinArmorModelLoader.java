package mctmods.resourcedatapackloader.mixin.conarm;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "c4.conarm.client.utils.ArmorModelLoader", remap = false)
public abstract class MixinArmorModelLoader {

    @Inject(method = "accepts", at = @At("HEAD"), cancellable = true)
    private void rdpl$declineVariantLocations(ResourceLocation modelLocation, CallbackInfoReturnable<Boolean> cir) {
        if (modelLocation instanceof ModelResourceLocation) { cir.setReturnValue(Boolean.FALSE); }
    }
}
