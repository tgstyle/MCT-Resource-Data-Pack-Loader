package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentFogSampler;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningBoltRenderer.class) public abstract class MixinLightningBoltRenderer {
    @Redirect(method = "quad", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer rdpl$tintBolt(VertexConsumer consumer, float red, float green, float blue, float alpha) {
        int color = ContentFogSampler.lightning();
        if (color == SkyLookDef.UNSET) { return consumer.setColor(red, green, blue, alpha); }
        return consumer.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha);
    }
}
