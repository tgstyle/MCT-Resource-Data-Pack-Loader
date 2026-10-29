package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.core.util.SplashLogo;

import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unresolvable-target") @Mixin(targets = "net.minecraftforge.fml.client.SplashProgress$2", remap = false) public class MixinSplashProgressLogo {
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress$Texture;getWidth()I", remap = false), remap = false) private int rdpl$fitWidth(@Coerce Object texture) {
        ISplashTexture logo = (ISplashTexture) texture;
        return SplashLogo.fit(logo.rdpl$width(), logo.rdpl$width(), logo.rdpl$height(), Display.getWidth(), Display.getHeight());
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress$Texture;getHeight()I", remap = false), remap = false) private int rdpl$fitHeight(@Coerce Object texture) {
        ISplashTexture logo = (ISplashTexture) texture;
        return SplashLogo.fit(logo.rdpl$height(), logo.rdpl$width(), logo.rdpl$height(), Display.getWidth(), Display.getHeight());
    }
}
