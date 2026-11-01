package mctmods.resourcedatapackloader.mixin.client.renderer.feature;

import mctmods.resourcedatapackloader.client.renderer.tint.TintedCollector;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemFeatureRenderer.class) public abstract class ItemFeatureRendererMixin {
    @ModifyArg(method = "prepareMainSubmit(Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/QuadInstance;setColor(I)V")) private int rdpl$heldTint(int color, @Local(argsOnly = true, name = "submit") ItemFeatureRenderer.Submit submit) { return TintedCollector.itemColor(color, submit.tintLayers()); }
}
