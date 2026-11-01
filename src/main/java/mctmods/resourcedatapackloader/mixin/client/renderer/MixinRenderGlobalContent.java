package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyExtras;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyRenderers;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentStars;
import mctmods.resourcedatapackloader.client.renderer.sky.SeamSkyRenderer;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class) public abstract class MixinRenderGlobalContent {
    @Shadow @Final private static ResourceLocation SUN_TEXTURES;
    @Shadow private WorldClient world;
    @Shadow @Final private Minecraft mc;

    @Inject(method = "renderSky(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;SUN_TEXTURES:Lnet/minecraft/util/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private void rdpl$sunHaze(float partialTicks, int pass, CallbackInfo ci) { ContentSkyRenderers.sunHaze(world, SUN_TEXTURES, (1.0F - world.getRainStrength(partialTicks)) * ContentSkyRenderers.sun(world), DimensionTraitsDef.VANILLA_SUN, false); }

    @Redirect(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1)) private void rdpl$tintStars(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
        if (ContentStars.keepsVanilla(world)) {
            GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
            return;
        }
        ContentStars.VANILLA.draw(world, colorAlpha, Minecraft.getMinecraft().getRenderPartialTicks(), 0.0F);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Inject(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V")) private void rdpl$skybox(float partialTicks, int pass, CallbackInfo ci) { ContentSkyExtras.backdrop(world, partialTicks); }

    @Inject(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V", ordinal = 1, shift = At.Shift.AFTER)) private void rdpl$skyOverlay(float partialTicks, int pass, CallbackInfo ci) { ContentSkyExtras.overlay(world, partialTicks); }

    @Inject(method = "renderSky(FI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;MOON_PHASES_TEXTURES:Lnet/minecraft/util/ResourceLocation;", opcode = Opcodes.GETSTATIC)) private void rdpl$dimMoon(float partialTicks, int pass, CallbackInfo ci) { GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - world.getRainStrength(partialTicks)) * ContentSkyRenderers.moon(world)); }

    @Inject(method = "renderSky(FI)V", at = @At("RETURN")) private void rdpl$seamSky(float partialTicks, int pass, CallbackInfo ci) { SeamSkyRenderer.render(world, mc, partialTicks); }

    @WrapOperation(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calcSunriseSunsetColors(FF)[F")) private float[] rdpl$existingSunrise(WorldProvider provider, float celestialAngle, float partialTicks, Operation<float[]> original) { return ContentDimensionRules.existingSunrise(world, original.call(provider, celestialAngle, partialTicks)); }

    @WrapOperation(method = {"renderSky(FI)V", "renderClouds"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;isSurfaceWorld()Z")) private boolean rdpl$existingSurface(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingSurface(provider, original.call(provider)); }

    @WrapOperation(method = "renderSky(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V")) private void rdpl$skyTexture(TextureManager textures, ResourceLocation resource, Operation<Void> original) { original.call(textures, ContentDimensionRules.skyTexture(world, resource)); }

    @WrapOperation(method = {"renderClouds", "renderCloudsFancy"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getCloudHeight()F")) private float rdpl$existingCloudHeight(WorldProvider provider, Operation<Float> original) { return ContentDimensionRules.existingCloudHeight(world, original.call(provider)); }
}
