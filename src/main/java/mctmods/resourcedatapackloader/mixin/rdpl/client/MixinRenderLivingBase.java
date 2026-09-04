package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLivingBase.class) public abstract class MixinRenderLivingBase {
    @Unique private int rdpl$tint;

    @Inject(method = "prepareScale", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderLivingBase;preRenderCallback(Lnet/minecraft/entity/EntityLivingBase;F)V", shift = At.Shift.AFTER))
    private void rdpl$scale(EntityLivingBase entitylivingbaseIn, float partialTicks, CallbackInfoReturnable<Float> cir) {
        float scale = ContentEntities.scale(entitylivingbaseIn);
        if (scale == 1.0F) { return; }
        GlStateManager.scale(scale, scale, scale);
    }

    @Inject(method = "renderModel", at = @At("HEAD")) private void rdpl$tintBody(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        int tint = ContentEntities.tint(entitylivingbaseIn, EntityVariantDef.BODY);
        rdpl$tint = tint;
        if (tint == 0) { return; }
        GlStateManager.color(ContentEntities.channel(tint, 16), ContentEntities.channel(tint, 8), ContentEntities.channel(tint, 0), 1.0F);
    }

    @Inject(method = "renderModel", at = @At("RETURN")) private void rdpl$untintBody(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (rdpl$tint == 0) { return; }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
