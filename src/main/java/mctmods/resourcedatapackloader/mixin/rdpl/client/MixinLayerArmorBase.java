package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class) public abstract class MixinLayerArmorBase {
    @Shadow private float colorR;
    @Shadow private float colorG;
    @Shadow private float colorB;
    @Unique private int rdpl$tint;

    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true) private void rdpl$hideArmor(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        rdpl$tint = 0;
        if (ContentEntities.hidesArmor(entitylivingbaseIn)) {
            ci.cancel();
            return;
        }
        int tint = ContentEntities.tint(entitylivingbaseIn, EntityVariantDef.ARMOR);
        if (tint == 0) { return; }
        rdpl$tint = tint;
        colorR = ContentEntities.channel(tint, 16);
        colorG = ContentEntities.channel(tint, 8);
        colorB = ContentEntities.channel(tint, 0);
    }

    @Inject(method = "doRenderLayer", at = @At("RETURN")) private void rdpl$untintArmor(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (rdpl$tint == 0) { return; }
        colorR = 1.0F;
        colorG = 1.0F;
        colorB = 1.0F;
    }
}
