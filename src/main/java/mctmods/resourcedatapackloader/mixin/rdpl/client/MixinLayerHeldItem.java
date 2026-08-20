package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerHeldItem.class) public abstract class MixinLayerHeldItem {
    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true) private void rdpl$hideHeld(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (ContentEntities.hidesHeld(entitylivingbaseIn)) {
            ci.cancel();
            return;
        }

        int tint = ContentEntities.tint(entitylivingbaseIn, EntityVariantDef.HELD);
        if (tint == 0) { return; }

        GlStateManager.color((tint >> 16 & 255) / 255.0F, (tint >> 8 & 255) / 255.0F, (tint & 255) / 255.0F, 1.0F);
    }

    @Inject(method = "doRenderLayer", at = @At("RETURN")) private void rdpl$untintHeld(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (ContentEntities.tint(entitylivingbaseIn, EntityVariantDef.HELD) == 0) { return; }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
