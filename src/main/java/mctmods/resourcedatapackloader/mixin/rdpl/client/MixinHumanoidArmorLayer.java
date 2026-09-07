package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.EntityTint;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class) public abstract class MixinHumanoidArmorLayer<T extends LivingEntity> {
    @Unique private static final String RDPL_RENDER = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V";

    @Inject(method = RDPL_RENDER, at = @At("HEAD"), cancellable = true)
    private void rdpl$hidden(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float age, float headYaw, float headPitch, CallbackInfo ci) {
        if (ContentEntities.hidesArmor(entity)) { ci.cancel(); }
    }

    @ModifyVariable(method = RDPL_RENDER, at = @At("HEAD"), argsOnly = true)
    private MultiBufferSource rdpl$tinted(MultiBufferSource tinted, PoseStack pose, MultiBufferSource buffer, int light, T entity) {
        return EntityTint.wrap(tinted, ContentEntities.tint(entity, EntityVariantDef.ARMOR));
    }
}
