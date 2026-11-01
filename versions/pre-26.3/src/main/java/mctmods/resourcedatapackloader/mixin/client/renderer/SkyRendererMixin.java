package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyRenderer;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentStarField;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentStars;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentWindowLook;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkyRenderer.class) public abstract class SkyRendererMixin {
    @Shadow @Final private TextureAtlas celestialsAtlas;
    @Shadow @Final private GpuBuffer sunBuffer;

    @Shadow protected abstract void renderMoon(MoonPhase moonPhase, float rainBrightness, PoseStack poseStack);

    @Inject(method = "renderSun(FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD")) private void rdpl$underSun(float rainBrightness, PoseStack poseStack, CallbackInfo ci) { ContentSkyExtras.underSun(poseStack, DimensionTraitsDef.VANILLA_SUN, rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level), celestialsAtlas, sunBuffer); }

    @Inject(method = "renderSkyDisc(I)V", at = @At("TAIL")) private void rdpl$skybox(int skyColor, CallbackInfo ci) { ContentSkyExtras.backdrop(); }

    @Inject(method = "renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V", at = @At("HEAD"), cancellable = true) private void rdpl$packSky(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        ContentSkyExtras.overlay(poseStack, sunAngle);
        ContentSkyRenderer sky = ContentSkyRenderer.of(Minecraft.getInstance().level);
        if (sky == null) { return; }
        sky.render(poseStack, sunAngle, moonAngle, starAngle, moonPhase.index(), rainBrightness, starBrightness, turned -> renderMoon(moonPhase, rainBrightness, turned));
        ci.cancel();
    }

    @Inject(method = "renderStars(FLcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"), cancellable = true) private void rdpl$tintStars(float starBrightness, PoseStack poseStack, CallbackInfo ci) {
        if (!ContentStarField.replacesVanilla(Minecraft.getInstance().level)) { return; }
        ContentStars.VANILLA.draw(poseStack, starBrightness);
        ci.cancel();
    }

    @ModifyArgs(method = "renderSun", at = @At(value = "INVOKE", target = "Lorg/joml/Vector4f;<init>(FFFF)V")) private void rdpl$tintSun(Args args) {
        ClientLevel level = Minecraft.getInstance().level;
        float[] tint = ContentWindowLook.sunTint(level);
        if (tint != null) {
            args.set(0, tint[0]);
            args.set(1, tint[1]);
            args.set(2, tint[2]);
        }
        float alpha = args.get(3);
        args.set(3, alpha * ContentFogSampler.sun(level));
    }

    @ModifyVariable(method = "renderMoon", at = @At("HEAD"), argsOnly = true, name = "rainBrightness") private float rdpl$dimMoon(float rainBrightness) { return rainBrightness * ContentFogSampler.moon(Minecraft.getInstance().level); }
}
