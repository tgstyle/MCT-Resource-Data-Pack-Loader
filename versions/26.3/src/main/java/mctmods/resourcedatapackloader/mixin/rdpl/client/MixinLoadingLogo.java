package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Config;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoadingOverlay.class) public abstract class MixinLoadingLogo {
    @ModifyArg(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"), index = 0)
    private RenderPipeline rdpl$logoBlend(RenderPipeline pipeline) { return pipeline == RenderPipelines.MOJANG_LOGO && !Config.tweaks.darkSplashOff() ? RenderPipelines.GUI_TEXTURED : pipeline; }
}
