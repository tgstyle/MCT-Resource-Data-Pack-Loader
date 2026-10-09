package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentFogSampler;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningBoltRenderer.class) public abstract class MixinLightningBoltRenderer {
    @Redirect(method = "quad", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;")) private static VertexConsumer rdpl$tintBolt(VertexConsumer consumer, float r, float g, float b, float a) {
        int color = ContentFogSampler.lightning();
        if (color == SkyLookDef.UNSET) { return consumer.setColor(r, g, b, a); }
        return consumer.setColor(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), a);
    }
}
