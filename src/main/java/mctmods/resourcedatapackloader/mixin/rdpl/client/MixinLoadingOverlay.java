package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.mixin.SplashSlate;
import mctmods.resourcedatapackloader.util.Config;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LoadingOverlay.class) public abstract class MixinLoadingOverlay {
    @Shadow @Final private static int LOGO_BACKGROUND_COLOR;
    @Shadow @Final private static int LOGO_BACKGROUND_COLOR_DARK;

    @ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At(value = "INVOKE", target = "Ljava/util/function/IntSupplier;getAsInt()I"))
    private int rdpl$slate(int brand) { return (brand == LOGO_BACKGROUND_COLOR || brand == LOGO_BACKGROUND_COLOR_DARK) && !Config.tweaks.darkSplashOff() ? ARGB.opaque(SplashSlate.SLATE) : brand; }
}
