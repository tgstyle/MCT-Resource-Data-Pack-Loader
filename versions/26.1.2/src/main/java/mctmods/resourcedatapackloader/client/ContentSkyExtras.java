package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nullable;

public final class ContentSkyExtras {
    private static final RenderPipeline BOX = RenderPipelines.CELESTIAL.toBuilder().withLocation(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "pipeline/sky_box")).withColorTargetState(ColorTargetState.DEFAULT).withCull(false).build();
    private static final RenderPipeline GLOW = RenderPipelines.SUNRISE_SUNSET.toBuilder().withLocation(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "pipeline/sky_glow")).withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY)).withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS).withCull(false).build();
    private static final RenderSystem.AutoStorageIndexBuffer QUAD_INDICES = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    @Nullable private static GpuBuffer box;
    @Nullable private static GpuBuffer panorama;
    @Nullable private static GpuBuffer aurora;
    @Nullable private static GpuBuffer bow;

    private ContentSkyExtras() {}

    public static void backdrop() {
        SkyLookDef.Skybox skybox = ContentSkyShapes.skybox(Minecraft.getInstance().level);
        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (skybox == null || color == null) { return; }
        AbstractTexture[] images = ContentSkyShapes.images(skybox);
        GpuBuffer mesh = skybox.panorama() != null ? panorama() : box();
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(RenderSystem.getModelViewStack()), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL skybox", color, OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(BOX);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setVertexBuffer(0, mesh);
            if (skybox.panorama() != null) {
                texture(pass, images[0]);
                pass.setIndexBuffer(QUAD_INDICES.getBuffer(ContentSkyShapes.PANORAMA_VERTICES / 4 * 6), QUAD_INDICES.type());
                pass.drawIndexed(0, 0, ContentSkyShapes.PANORAMA_VERTICES / 4 * 6, 1);
                return;
            }
            pass.setIndexBuffer(QUAD_INDICES.getBuffer(6), QUAD_INDICES.type());
            for (int face = 0; face < ContentSkyShapes.FACE_COUNT; ++face) {
                texture(pass, images[face]);
                pass.drawIndexed(face * 4, 0, 6, 1);
            }
        }
    }

    private static GpuBuffer panorama() {
        if (panorama == null) { panorama = textured("RDPL sky panorama", ContentSkyShapes.panorama()); }
        return panorama;
    }

    private static GpuBuffer box() {
        if (box == null) { box = textured("RDPL skybox", ContentSkyShapes.box()); }
        return box;
    }

    public static void overlay(PoseStack poseStack, float sunAngle) {
        ClientLevel level = Minecraft.getInstance().level;
        SkyLookDef look = ContentFogSampler.look(level);
        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (level == null || look == null || color == null) { return; }
        float partialTick = ContentSkyShapes.partialTick();
        float glow = ContentSkyShapes.aurora(level, partialTick, sunAngle);
        float arc = ContentSkyShapes.rainbow(level, partialTick, sunAngle);
        if (glow <= 0.0F && arc <= 0.0F) { return; }
        if (glow > 0.0F && look.aurora() != null) { aurora = colored(aurora, "RDPL aurora", ContentSkyShapes.aurora(look.aurora(), glow, level.getGameTime() + partialTick)); }
        if (arc > 0.0F) { bow = colored(bow, "RDPL rainbow", ContentSkyShapes.bow(sunAngle, arc)); }
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelView), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
        modelView.popMatrix();
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL sky glow", color, OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(GLOW);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setIndexBuffer(QUAD_INDICES.getBuffer(ContentSkyShapes.BOW_VERTICES / 4 * 6), QUAD_INDICES.type());
            if (glow > 0.0F && aurora != null) {
                pass.setVertexBuffer(0, aurora);
                pass.drawIndexed(0, 0, ContentSkyShapes.AURORA_VERTICES / 4 * 6, 1);
            }
            if (arc > 0.0F && bow != null) {
                pass.setVertexBuffer(0, bow);
                pass.drawIndexed(0, 0, ContentSkyShapes.BOW_VERTICES / 4 * 6, 1);
            }
        }
    }

    private static void texture(RenderPass pass, AbstractTexture image) { pass.bindTexture("Sampler0", image.getTextureView(), image.getSampler()); }

    private static GpuBuffer colored(@Nullable GpuBuffer target, String label, float[] vertices) {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(vertices.length / ContentSkyShapes.COLORED * DefaultVertexFormat.POSITION_COLOR.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (int at = 0; at < vertices.length; at += ContentSkyShapes.COLORED) { buffer.addVertex(vertices[at], vertices[at + 1], vertices[at + 2]).setColor(vertices[at + 3], vertices[at + 4], vertices[at + 5], vertices[at + 6]); }
            try (MeshData mesh = buffer.buildOrThrow()) {
                if (target == null) { return RenderSystem.getDevice().createBuffer(() -> label, GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, mesh.vertexBuffer()); }
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(target.slice(), mesh.vertexBuffer());
                return target;
            }
        }
    }

    private static GpuBuffer textured(String label, float[] vertices) {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(vertices.length / ContentSkyShapes.TEXTURED * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (int at = 0; at < vertices.length; at += ContentSkyShapes.TEXTURED) { buffer.addVertex(vertices[at], vertices[at + 1], vertices[at + 2]).setUv(vertices[at + 3], vertices[at + 4]); }
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> label, GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }
}
