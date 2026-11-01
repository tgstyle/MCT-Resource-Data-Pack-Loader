package mctmods.resourcedatapackloader.mixin.client.renderer.entity.layers;

import mctmods.resourcedatapackloader.client.renderer.tint.TintedCollector;
import mctmods.resourcedatapackloader.client.util.EntityLook;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class) public abstract class HumanoidArmorLayerMixin {
    @Unique private static final String rdpl$SUBMIT = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V";

    @Inject(method = rdpl$SUBMIT, at = @At("HEAD"), cancellable = true) private void rdpl$hidden(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) { if (EntityLook.of(state).hidesArmor()) { ci.cancel(); } }

    @ModifyVariable(method = rdpl$SUBMIT, at = @At("HEAD"), name = "submitNodeCollector", argsOnly = true) private SubmitNodeCollector rdpl$tinted(SubmitNodeCollector submitNodeCollector, @Local(argsOnly = true, name = "state") HumanoidRenderState state) { return TintedCollector.wrap(submitNodeCollector, EntityLook.of(state).armor()); }
}
