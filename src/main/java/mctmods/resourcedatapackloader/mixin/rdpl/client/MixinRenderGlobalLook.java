package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentSkyExtras;
import mctmods.resourcedatapackloader.content.worldgen.ContentSkyRenderers;
import mctmods.resourcedatapackloader.content.worldgen.ContentStars;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class) public abstract class MixinRenderGlobalLook {
    @Shadow private WorldClient world;

    @ModifyArg(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 0), index = 3)
    private float rdpl$dimSun(float alpha) { return alpha * ContentSkyRenderers.sun(world); }

    @Redirect(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1))
    private void rdpl$tintStars(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
        if (!ContentStars.replacesVanilla(world)) {
            GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
            return;
        }
        ContentStars.VANILLA.draw(world, colorAlpha, Minecraft.getMinecraft().getRenderPartialTicks());
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Inject(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V"))
    private void rdpl$skybox(float partialTicks, int pass, CallbackInfo ci) { ContentSkyExtras.backdrop(world); }

    @Inject(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V", ordinal = 1, shift = At.Shift.AFTER))
    private void rdpl$skyOverlay(float partialTicks, int pass, CallbackInfo ci) { ContentSkyExtras.overlay(world, partialTicks); }

    @Inject(method = "renderSky(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;MOON_PHASES_TEXTURES:Lnet/minecraft/util/ResourceLocation;", opcode = Opcodes.GETSTATIC))
    private void rdpl$dimMoon(float partialTicks, int pass, CallbackInfo ci) { GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - world.getRainStrength(partialTicks)) * ContentSkyRenderers.moon(world)); }
}
