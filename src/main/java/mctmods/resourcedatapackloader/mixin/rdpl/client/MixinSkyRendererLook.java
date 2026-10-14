package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentFogSampler;
import mctmods.resourcedatapackloader.client.ContentWindowLook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkyRenderer.class) public abstract class MixinSkyRendererLook {
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
