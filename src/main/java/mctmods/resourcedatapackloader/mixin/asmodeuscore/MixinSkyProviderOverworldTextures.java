package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderOverworld;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SkyProviderOverworld.class, remap = false) public class MixinSkyProviderOverworldTextures {
    @WrapOperation(method = "rendererSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", remap = true)) private void rdpl$packMoon(TextureManager textures, ResourceLocation resource, Operation<Void> original) { original.call(textures, ContentDimensionRules.skyTexture(Minecraft.getMinecraft().world, resource)); }

    @ModifyReturnValue(method = "sunImage", at = @At("RETURN")) private ResourceLocation rdpl$packSun(ResourceLocation own) { return ContentDimensionRules.skyTexture(Minecraft.getMinecraft().world, own); }
}
