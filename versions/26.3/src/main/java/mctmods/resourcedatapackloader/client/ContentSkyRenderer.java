package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentSkyRenderer {
    private static final Map<DimensionTraitsDef.Sky, ContentSkyRenderer> RENDERERS = new IdentityHashMap<>();
    private static final int PHASES = 8;
    private static final float[] UNTINTED = {1.0F, 1.0F, 1.0F};
    @Nullable private static GpuBuffer quadBuffer;
    @Nullable private static GpuBuffer phaseBuffer;
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
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

    public void prepare() {
        if (quadBuffer == null) { quadBuffer = corners(); }
        if (phaseBuffer == null) { phaseBuffer = phases(); }
        stars.prepare();
        TextureManager textures = Minecraft.getInstance().getTextureManager();
        if (sky.sunSize() > 0.0F) { textures.getTexture(sky.sunTexture()); }
        if (moonTexture != null) { textures.getTexture(moonTexture); }
        if (sky.bodies() == null) { return; }
        for (DimensionTraitsDef.Body body : sky.bodies()) { textures.getTexture(body.texture()); }
    }

    public void render(RenderPass renderPass, PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, int phase, float rainBrightness, float starBrightness, Consumer<PoseStack> moon) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, -90.0F);
        poseStack.pushPose();
        poseStack.rotate(Axis.XP, sunAngle);
        if (sky.sunSize() > 0.0F) { sun(renderPass, poseStack, rainBrightness * ContentFogSampler.sun(Minecraft.getInstance().level)); }
        poseStack.popPose();
        if (sky.bodies() == null) {
            if (moonSize > 0.0F) {
                poseStack.pushPose();
                poseStack.rotate(Axis.XP, moonAngle);
                if (moonTexture != null) { draw(renderPass, poseStack, moonTexture, moonSize, UNTINTED, rainBrightness, phaseBuffer, Math.floorMod(phase, PHASES) * 4); }
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
                if (body.followsTime()) { poseStack.rotate(Axis.XP, sunAngle); }
                poseStack.rotateDegrees(Axis.XP, body.angle());
                poseStack.rotateDegrees(Axis.ZP, body.tilt());
                quad(renderPass, poseStack, body.texture(), body.size(), rainBrightness * ContentFogSampler.moon(Minecraft.getInstance().level));
                poseStack.popPose();
            }
        }
        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.rotate(Axis.XP, starAngle);
            stars.draw(renderPass, poseStack, starBrightness);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void sun(RenderPass renderPass, PoseStack poseStack, float rainBrightness) {
        float[] tint = ContentSkyExtras.underSun(renderPass, poseStack, sky.sunSize(), rainBrightness, Minecraft.getInstance().getTextureManager().getTexture(sky.sunTexture()), quadBuffer);
        draw(renderPass, poseStack, sky.sunTexture(), sky.sunSize(), tint == null ? UNTINTED : tint, rainBrightness, quadBuffer, 0);
    }

    private void quad(RenderPass renderPass, PoseStack poseStack, Identifier texture, float size, float rainBrightness) { draw(renderPass, poseStack, texture, size, UNTINTED, rainBrightness, quadBuffer, 0); }

    private void draw(RenderPass renderPass, PoseStack poseStack, Identifier texture, float size, float[] tint, float rainBrightness, @Nullable GpuBuffer vertices, int baseVertex) {
        if (vertices == null) { return; }
        AbstractTexture image = Minecraft.getInstance().getTextureManager().getTexture(texture);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        modelView.translate(0.0F, ContentSkyShapes.SIZE, 0.0F);
        modelView.scale(size, 1.0F, size);
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelView), new Vector4f(tint[0], tint[1], tint[2], rainBrightness));
        modelView.popMatrix();
        renderPass.pushDebugGroup(() -> "RDPL sky body");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.CELESTIAL));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", transforms);
        renderPass.setUniform("Sampler0", image.getTextureView(), image.getSampler());
        renderPass.setVertexBuffer(0, vertices.slice());
        renderPass.setIndexBuffer(quadIndices.getBuffer(6), quadIndices.type());
        renderPass.drawIndexed(6, 1, 0, baseVertex, 0);
        renderPass.popDebugGroup();
    }

    private static GpuBuffer phases() {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(PHASES * 4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
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
            BufferBuilder buffer = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.addVertex(-1.0F, 0.0F, -1.0F).setUv(0.0F, 0.0F);
            buffer.addVertex(1.0F, 0.0F, -1.0F).setUv(1.0F, 0.0F);
            buffer.addVertex(1.0F, 0.0F, 1.0F).setUv(1.0F, 1.0F);
            buffer.addVertex(-1.0F, 0.0F, 1.0F).setUv(0.0F, 1.0F);
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL sky body", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }
}
