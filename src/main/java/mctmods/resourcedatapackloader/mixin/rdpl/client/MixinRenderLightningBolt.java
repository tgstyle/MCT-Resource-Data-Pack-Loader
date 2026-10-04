package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentSkyRenderers;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLightningBolt.class) public abstract class MixinRenderLightningBolt {
    @Redirect(method = "doRender(Lnet/minecraft/entity/effect/EntityLightningBolt;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;color(FFFF)Lnet/minecraft/client/renderer/BufferBuilder;"))
    private BufferBuilder rdpl$tintBolt(BufferBuilder buffer, float red, float green, float blue, float alpha) { return ContentSkyRenderers.bolt(buffer, red, green, blue, alpha); }
}
