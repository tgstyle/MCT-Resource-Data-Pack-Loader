package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

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
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentSkyRenderer {
    private static final Map<DimensionTraitsDef.Sky, ContentSkyRenderer> RENDERERS = new IdentityHashMap<>();
    private static final int PHASES = 8;
    @Nullable private static GpuBuffer quadBuffer;
    @Nullable private static GpuBuffer phaseBuffer;
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    private final DimensionTraitsDef.Sky sky;
    private final ContentStars stars;
    @Nullable private final Identifier moonTexture;
    private final float moonSize;

    private ContentSkyRenderer(DimensionTraitsDef.Sky sky, @Nullable DimensionTraitsDef.Moon moon) {
        this.sky = sky;
        this.stars = new ContentStars(sky.starCount(), sky.starSize());
        this.moonTexture = moon == null ? null : moon.texture();
        this.moonSize = moon == null ? DimensionTraitsDef.VANILLA_MOON : moon.size();
    }

    @Nullable public static ContentSkyRenderer of(@Nullable ClientLevel level) {
        DimensionDef def = level == null ? null : ContentDimensions.def(level);
        DimensionTraitsDef.Sky sky = def == null ? null : def.traits().sky();
        return sky == null ? null : RENDERERS.computeIfAbsent(sky, held -> new ContentSkyRenderer(held, def.traits().moon()));
    }

    public void render(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, int phase, float rainBrightness, float starBrightness, Consumer<PoseStack> moon) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(sunAngle));
        if (sky.sunSize() > 0.0F) { quad(poseStack, sky.sunTexture(), sky.sunSize(), rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level)); }
        poseStack.popPose();
        if (sky.bodies() == null) {
            if (moonSize > 0.0F) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotation(moonAngle));
                if (moonTexture != null) { phase(poseStack, moonTexture, phase, rainBrightness); }
                else {
                    poseStack.scale(moonSize / DimensionTraitsDef.VANILLA_MOON, 1.0F, moonSize / DimensionTraitsDef.VANILLA_MOON);
                    moon.accept(poseStack);
                }
                poseStack.popPose();
            }
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
        if (quadBuffer == null) { quadBuffer = corners(); }
        draw(poseStack, texture, size, rainBrightness, quadBuffer, 0);
    }

    private void phase(PoseStack poseStack, Identifier texture, int phase, float rainBrightness) {
        if (phaseBuffer == null) { phaseBuffer = phases(); }
        draw(poseStack, texture, moonSize, rainBrightness, phaseBuffer, Math.floorMod(phase, PHASES) * 4);
    }

    private void draw(PoseStack poseStack, Identifier texture, float size, float rainBrightness, GpuBuffer vertices, int baseVertex) {
        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (color == null) { return; }
        AbstractTexture image = Minecraft.getInstance().getTextureManager().getTexture(texture);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        modelView.translate(0.0F, ContentSkyShapes.SIZE, 0.0F);
        modelView.scale(size, 1.0F, size);
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1.0F, 1.0F, 1.0F, rainBrightness), new Vector3f(), new Matrix4f());
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL sky body", color, OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(RenderPipelines.CELESTIAL);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler0", image.getTextureView(), image.getSampler());
            pass.setVertexBuffer(0, vertices);
            pass.setIndexBuffer(quadIndices.getBuffer(6), quadIndices.type());
            pass.drawIndexed(baseVertex, 0, 6, 1);
        }
        modelView.popMatrix();
    }

    private static GpuBuffer phases() {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(PHASES * 4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (int phase = 0; phase < PHASES; phase++) {
                float u0 = (phase % 4) * 0.25F;
                float v0 = phase < 4 ? 0.0F : 0.5F;
                buffer.addVertex(-1.0F, 0.0F, -1.0F).setUv(u0 + 0.25F, v0 + 0.5F);
                buffer.addVertex(1.0F, 0.0F, -1.0F).setUv(u0, v0 + 0.5F);
                buffer.addVertex(1.0F, 0.0F, 1.0F).setUv(u0, v0);
                buffer.addVertex(-1.0F, 0.0F, 1.0F).setUv(u0 + 0.25F, v0);
            }
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL moon phases", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }

    private static GpuBuffer corners() {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.addVertex(-1.0F, 0.0F, -1.0F).setUv(0.0F, 0.0F);
            buffer.addVertex(1.0F, 0.0F, -1.0F).setUv(1.0F, 0.0F);
            buffer.addVertex(1.0F, 0.0F, 1.0F).setUv(1.0F, 1.0F);
            buffer.addVertex(-1.0F, 0.0F, 1.0F).setUv(0.0F, 1.0F);
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL sky body", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }
}
