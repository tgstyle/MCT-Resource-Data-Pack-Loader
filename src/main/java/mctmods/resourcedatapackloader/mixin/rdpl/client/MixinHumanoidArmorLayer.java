package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.EntityLook;
import mctmods.resourcedatapackloader.client.TintedCollector;

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

@Mixin(HumanoidArmorLayer.class) public abstract class MixinHumanoidArmorLayer {
    @Unique private static final String RDPL_SUBMIT = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V";

    @Inject(method = RDPL_SUBMIT, at = @At("HEAD"), cancellable = true)
    private void rdpl$hidden(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (EntityLook.of(state).hidesArmor()) { ci.cancel(); }
    }

    @ModifyVariable(method = RDPL_SUBMIT, at = @At("HEAD"), argsOnly = true)
    private SubmitNodeCollector rdpl$tinted(SubmitNodeCollector tinted, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidRenderState state) {
        return TintedCollector.wrap(tinted, EntityLook.of(state).armor());
    }
}
