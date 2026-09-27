package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.EntityLook;
import mctmods.resourcedatapackloader.compat.LineClientCompat;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class) public abstract class MixinLivingEntityRenderer<M extends EntityModel<?>> {
    @Shadow protected M model;

    @Inject(method = "getRenderType(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/renderer/rendertype/RenderType;", at = @At("HEAD"), cancellable = true)
    private void rdpl$packTexture(LivingEntityRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing, CallbackInfoReturnable<RenderType> cir) {
        Identifier texture = EntityLook.of(state).texture();
        if (texture == null) { return; }
        cir.setReturnValue(forceTransparent ? LineClientCompat.translucentBody(texture) : isBodyVisible ? model.renderType(texture) : appearGlowing ? RenderTypes.outline(texture) : null);
    }

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("TAIL"))
    private void rdpl$lying(LivingEntityRenderState state, PoseStack poseStack, float bodyRot, float entityScale, CallbackInfo ci) {
        if (state.deathTime > 0.0F || !EntityLook.of(state).lying()) { return; }
        poseStack.translate(0.0F, state.boundingBoxWidth / 2.0F, 0.0F);
        poseStack.last().rotate(Axis.ZP.rotationDegrees(90.0F));
    }

    @ModifyArg(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = LineClientCompat.SUBMIT_MODEL), index = 6)
    private int rdpl$tinted(int color, @Local(argsOnly = true) LivingEntityRenderState state) {
        int tint = EntityLook.of(state).body();
        return tint == 0 ? color : ARGB.multiply(color, 0xFF000000 | tint);
    }
}
