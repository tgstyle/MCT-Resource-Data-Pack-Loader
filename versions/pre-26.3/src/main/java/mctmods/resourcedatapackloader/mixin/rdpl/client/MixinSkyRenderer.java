package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.ContentSkyRenderer;
import mctmods.resourcedatapackloader.client.ContentStarField;
import mctmods.resourcedatapackloader.client.ContentStars;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class) public abstract class MixinSkyRenderer {
    @Shadow protected abstract void renderMoon(MoonPhase moonPhase, float rainBrightness, PoseStack poseStack);

    @Inject(method = "renderSkyDisc(I)V", at = @At("TAIL"))
    private void rdpl$skybox(int skyColor, CallbackInfo ci) { ContentSkyExtras.backdrop(); }

    @Inject(method = "renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$packSky(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        ContentSkyExtras.overlay(poseStack, sunAngle);
        ContentSkyRenderer sky = ContentSkyRenderer.of(Minecraft.getInstance().level);
        if (sky == null) { return; }
        sky.render(poseStack, sunAngle, moonAngle, starAngle, rainBrightness, starBrightness, turned -> renderMoon(moonPhase, rainBrightness, turned));
        ci.cancel();
    }

    @Inject(method = "renderStars(FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$tintStars(float starBrightness, PoseStack poseStack, CallbackInfo ci) {
        if (!ContentStarField.replacesVanilla(Minecraft.getInstance().level)) { return; }
        ContentStars.VANILLA.draw(poseStack, starBrightness);
        ci.cancel();
    }
}
