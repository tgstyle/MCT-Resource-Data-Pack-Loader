package mctmods.resourcedatapackloader.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class) public class ScreenEffectRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private MultiBufferSource bufferSource;

    @Inject(method = "renderScreenEffect", at = @At("HEAD")) private void rdpl$fluidOverlay(boolean isFirstPerson, boolean isSleeping, float partialTicks, SubmitNodeCollector submitNodeCollector, boolean hideGui, CallbackInfo ci) {
        Player player = minecraft.player;
        if (player == null || !isFirstPerson || isSleeping || player.noPhysics || player.isSpectator() || player.isEyeInFluid(NeoForgeMod.WATER_TYPE.value())) {
            return;
        }
        FluidType type = player.getFluidInteraction().getFirstEyeInFluid();
        if (!type.isAir()) {
            IClientFluidTypeExtensions.of(type).renderOverlay(minecraft, new PoseStack(), bufferSource);
        }
    }
}
