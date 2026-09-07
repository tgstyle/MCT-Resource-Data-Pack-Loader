package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.EntityTint;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class) public abstract class MixinItemInHandLayer<T extends LivingEntity> {
    @Unique
    private static final String RDPL_RENDER = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V";

    @Inject(method = RDPL_RENDER, at = @At("HEAD"), cancellable = true)
    private void rdpl$hidden(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (ContentEntities.hidesHeld(livingEntity)) { ci.cancel(); }
    }

    @ModifyVariable(method = RDPL_RENDER, at = @At("HEAD"), argsOnly = true)
    private MultiBufferSource rdpl$tinted(MultiBufferSource tinted, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity) {
        return EntityTint.wrap(tinted, ContentEntities.tint(livingEntity, EntityVariantDef.HELD));
    }
}
