package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.content.worldgen.ContentSkyRenderers;

import asmodeuscore.core.astronomy.sky.SkyProviderBase;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SkyProviderBase.class, remap = false) public abstract class MixinSkyProviderBaseSun {
    @Shadow protected abstract float sunSize();

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", ordinal = 0, remap = true)) private void rdpl$sunHaze(TextureManager textures, ResourceLocation resource, Operation<Void> original, float partialTicks, WorldClient world, Minecraft mc) {
        ContentSkyRenderers.sunHaze(world, resource, 1.0F - world.getRainStrength(partialTicks), sunSize(), true);
        original.call(textures, resource);
    }
}
