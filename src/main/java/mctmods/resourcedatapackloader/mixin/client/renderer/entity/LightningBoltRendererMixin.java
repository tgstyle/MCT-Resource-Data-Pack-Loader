package mctmods.resourcedatapackloader.mixin.client.renderer.entity;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.core.util.math.Rgb;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningBoltRenderer.class) public abstract class LightningBoltRendererMixin {
    @Redirect(method = "quad", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;")) private static VertexConsumer rdpl$tintBolt(VertexConsumer consumer, float red, float green, float blue, float alpha) {
        int color = ContentFogSampler.lightning();
        if (color == SkyLookDef.UNSET) { return consumer.color(red, green, blue, alpha); }
        return consumer.color(Rgb.red(color), Rgb.green(color), Rgb.blue(color), alpha);
    }
}
