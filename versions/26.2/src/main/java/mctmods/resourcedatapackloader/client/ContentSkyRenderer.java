package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentSkyRenderer {
    private static final Map<DimensionTraitsDef.Sky, ContentSkyRenderer> RENDERERS = new IdentityHashMap<>();
    @Nullable private static GpuBuffer quadBuffer;
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
    private final DimensionTraitsDef.Sky sky;
    private final ContentStars stars;

    private ContentSkyRenderer(DimensionTraitsDef.Sky sky) {
        this.sky = sky;
        this.stars = new ContentStars(sky.starCount(), sky.starSize());
    }

    @Nullable public static ContentSkyRenderer of(@Nullable ClientLevel level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        DimensionTraitsDef.Sky sky = def == null ? null : def.traits().sky();
        return sky == null ? null : RENDERERS.computeIfAbsent(sky, ContentSkyRenderer::new);
    }

    public void render(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, float rainBrightness, float starBrightness, Consumer<PoseStack> moon) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(sunAngle));
        if (sky.sunSize() > 0.0F) { quad(poseStack, sky.sunTexture(), sky.sunSize(), rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level)); }
        poseStack.popPose();
        if (sky.bodies() == null) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation(moonAngle));
            moon.accept(poseStack);
            poseStack.popPose();
        }
        else {
            for (DimensionTraitsDef.Body body : sky.bodies()) {
                poseStack.pushPose();
                if (body.followsTime()) { poseStack.mulPose(Axis.XP.rotation(sunAngle)); }
                poseStack.mulPose(Axis.XP.rotationDegrees(body.angle()));
                poseStack.mulPose(Axis.ZP.rotationDegrees(body.tilt()));
                quad(poseStack, body.texture(), body.size(), rainBrightness * ContentFogSampler.moon(Minecraft.getInstance().level));
                poseStack.popPose();
            }
        }
        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation(starAngle));
            stars.draw(poseStack, starBrightness);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void quad(PoseStack poseStack, Identifier texture, float size, float rainBrightness) {
        RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (color == null) { return; }
        if (quadBuffer == null) { quadBuffer = corners(); }
        AbstractTexture image = Minecraft.getInstance().getTextureManager().getTexture(texture);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        modelView.translate(0.0F, 100.0F, 0.0F);
        modelView.scale(size, 1.0F, size);
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelView), new Vector4f(1.0F, 1.0F, 1.0F, rainBrightness));
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL sky body", color, Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(RenderPipelines.CELESTIAL);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler0", image.getTextureView(), image.getSampler());
            pass.setVertexBuffer(0, quadBuffer.slice());
            pass.setIndexBuffer(quadIndices.getBuffer(6), quadIndices.type());
            pass.drawIndexed(6, 1, 0, 0, 0);
        }
        modelView.popMatrix();
    }

    private static GpuBuffer corners() {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.addVertex(-1.0F, 0.0F, -1.0F).setUv(0.0F, 0.0F);
            buffer.addVertex(1.0F, 0.0F, -1.0F).setUv(1.0F, 0.0F);
            buffer.addVertex(1.0F, 0.0F, 1.0F).setUv(1.0F, 1.0F);
            buffer.addVertex(-1.0F, 0.0F, 1.0F).setUv(0.0F, 1.0F);
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL sky body", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }
}
