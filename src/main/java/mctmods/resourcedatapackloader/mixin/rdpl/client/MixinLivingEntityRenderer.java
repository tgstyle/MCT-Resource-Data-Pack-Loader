package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.EntityTint;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class) public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow protected M model;

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void rdpl$packTexture(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        ResourceLocation texture = ContentEntities.texture(livingEntity);
        if (texture == null) { return; }
        cir.setReturnValue(translucent ? RenderType.itemEntityTranslucentCull(texture) : bodyVisible ? model.renderType(texture) : glowing ? RenderType.outline(texture) : null);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;scale(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private void rdpl$packScale(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        float scale = ContentEntities.scale(entity);
        if (scale != 1.0F) { poseStack.scale(scale, scale, scale); }
    }

    @Inject(method = "setupRotations", at = @At("TAIL")) private void rdpl$lying(T entityLiving, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, CallbackInfo ci) {
        if (entityLiving.deathTime > 0 || !ContentEntities.lyingAsleep(entityLiving)) { return; }
        poseStack.translate(0.0F, entityLiving.getBbWidth() / 2.0F, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void rdpl$tinted(EntityModel<T> model, PoseStack pose, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha, T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        model.renderToBuffer(pose, EntityTint.wrap(consumer, ContentEntities.tint(entity, EntityVariantDef.BODY)), light, overlay, red, green, blue, alpha);
    }
}
