package mctmods.resourcedatapackloader.mixin.client.gui.screens;

import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.SplashSlate;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoadingOverlay.class) public abstract class LoadingOverlayMixin {
    @Shadow @Final private static int LOGO_BACKGROUND_COLOR;
    @Shadow @Final private static int LOGO_BACKGROUND_COLOR_DARK;

    @ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At(value = "INVOKE", target = "Ljava/util/function/IntSupplier;getAsInt()I")) private int rdpl$slate(int brand) { return (brand == LOGO_BACKGROUND_COLOR || brand == LOGO_BACKGROUND_COLOR_DARK) && !Config.tweaks.darkSplashOff() ? ARGB.opaque(SplashSlate.SLATE) : brand; }

    @ModifyArg(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"), index = 0) private RenderPipeline rdpl$logoBlend(RenderPipeline pipeline) { return pipeline == RenderPipelines.MOJANG_LOGO && !Config.tweaks.darkSplashOff() ? RenderPipelines.GUI_TEXTURED : pipeline; }
}
