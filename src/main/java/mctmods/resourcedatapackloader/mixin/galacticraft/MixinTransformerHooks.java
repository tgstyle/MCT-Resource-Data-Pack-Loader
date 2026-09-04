package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import micdoodle8.mods.galacticraft.core.TransformerHooks;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransformerHooks.class, remap = false) public class MixinTransformerHooks {
    @Inject(method = "getRenderPosY", at = @At("HEAD"), cancellable = true)
    private static void rdpl$honestCameraHeight(Entity viewEntity, double regular, CallbackInfoReturnable<Double> cir) {
        if (GenHeights.rubic(viewEntity.world)) { cir.setReturnValue(regular + viewEntity.getEyeHeight()); }
    }
}
