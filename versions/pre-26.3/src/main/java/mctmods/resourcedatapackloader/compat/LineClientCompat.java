package mctmods.resourcedatapackloader.compat;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import javax.annotation.Nullable;

public final class LineClientCompat {
    public static final String SUBMIT_MODEL = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V";

    private LineClientCompat() {}

    public static RenderType translucentBody(Identifier texture) { return RenderTypes.entityTranslucentCullItemTarget(texture); }

    public static InputConstants.Key keyV() { return InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_V); }

    public static <S> void submitModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack pose, RenderType type, int light, int overlay, int outline, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbling) { collector.submitModel(model, state, pose, type, light, overlay, outline, crumbling); }

    public static <S> void submitModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack pose, Identifier texture, int light, int overlay, int outline) { collector.submitModel(model, state, pose, texture, light, overlay, outline, null); }

    public abstract static class ForwardingVertices implements VertexConsumer {
        protected final VertexConsumer inner;

        protected ForwardingVertices(VertexConsumer inner) { this.inner = inner; }
    }
}
