package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase {
    @Inject(method = "preRenderCallback", at = @At("RETURN"))
    private void rdpl$scale(EntityLivingBase entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        float scale = ContentEntities.scale(entitylivingbaseIn);
        if (scale == 1.0F) { return; }

        GlStateManager.scale(scale, scale, scale);
    }
}
