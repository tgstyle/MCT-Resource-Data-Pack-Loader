package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import javax.annotation.Nullable;

public final class ContentSkyExtras {
    private static final RenderPipeline BOX = RenderPipelines.CELESTIAL.toBuilder().withLocation(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "pipeline/sky_box")).withColorTargetState(ColorTargetState.DEFAULT).withCull(false).build();
    private static final RenderPipeline GLOW = RenderPipelines.SUNRISE_SUNSET.toBuilder().withLocation(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "pipeline/sky_glow")).withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY)).withPrimitiveTopology(PrimitiveTopology.QUADS).withCull(false).build();
    private static final RenderSystem.AutoStorageIndexBuffer QUAD_INDICES = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
    @Nullable private static GpuBuffer box;
    @Nullable private static GpuBuffer panorama;
    @Nullable private static GpuBuffer aurora;
    @Nullable private static GpuBuffer bow;
    @Nullable private static SkyLookDef.Skybox skybox;
    private static AbstractTexture[] images = new AbstractTexture[0];
    private static float glow;
    private static float arc;

    private ContentSkyExtras() {}

    public static void prepare(float sunAngle) {
        ClientLevel level = Minecraft.getInstance().level;
        SkyLookDef look = ContentFogSampler.look(level);
        skybox = look == null ? null : look.skybox();
        glow = 0.0F;
        arc = 0.0F;
        if (level == null || look == null) { return; }
        if (skybox != null) {
            images = ContentSkyShapes.images(skybox);
            if (skybox.panorama() != null && panorama == null) { panorama = textured("RDPL sky panorama", ContentSkyShapes.panorama()); }
            if (skybox.panorama() == null && box == null) { box = textured("RDPL skybox", ContentSkyShapes.box()); }
        }
        float partialTick = ContentSkyShapes.partialTick();
        glow = ContentSkyShapes.aurora(level, partialTick, sunAngle);
        arc = ContentSkyShapes.rainbow(level, partialTick, sunAngle);
        if (glow > 0.0F && look.aurora() != null) { aurora = colored(aurora, "RDPL aurora", ContentSkyShapes.aurora(look.aurora(), glow, level.getGameTime() + partialTick)); }
        if (arc > 0.0F) { bow = colored(bow, "RDPL rainbow", ContentSkyShapes.bow(sunAngle, arc)); }
    }

    public static void backdrop(RenderPass renderPass) {
        if (skybox == null) { return; }
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
        renderPass.pushDebugGroup(() -> "RDPL skybox");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(BOX));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", transforms);
        if (skybox.panorama() != null && panorama != null) {
            texture(renderPass, images[0]);
            renderPass.setVertexBuffer(0, panorama.slice());
            renderPass.setIndexBuffer(QUAD_INDICES.getBuffer(ContentSkyShapes.PANORAMA_VERTICES / 4 * 6), QUAD_INDICES.type());
            renderPass.drawIndexed(ContentSkyShapes.PANORAMA_VERTICES / 4 * 6, 1, 0, 0, 0);
        }
        else if (skybox.panorama() == null && box != null) {
            renderPass.setVertexBuffer(0, box.slice());
            renderPass.setIndexBuffer(QUAD_INDICES.getBuffer(6), QUAD_INDICES.type());
            for (int face = 0; face < ContentSkyShapes.FACE_COUNT; ++face) {
                texture(renderPass, images[face]);
                renderPass.drawIndexed(6, 1, 0, face * 4, 0);
            }
        }
        renderPass.popDebugGroup();
    }

    public static void overlay(RenderPass renderPass, PoseStack poseStack) {
        boolean showsAurora = glow > 0.0F && aurora != null;
        boolean showsBow = arc > 0.0F && bow != null;
        if (!showsAurora && !showsBow) { return; }
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelView), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
        modelView.popMatrix();
        renderPass.pushDebugGroup(() -> "RDPL sky glow");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(GLOW));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", transforms);
        renderPass.setIndexBuffer(QUAD_INDICES.getBuffer(ContentSkyShapes.BOW_VERTICES / 4 * 6), QUAD_INDICES.type());
        if (showsAurora) {
            renderPass.setVertexBuffer(0, aurora.slice());
            renderPass.drawIndexed(ContentSkyShapes.AURORA_VERTICES / 4 * 6, 1, 0, 0, 0);
        }
        if (showsBow) {
            renderPass.setVertexBuffer(0, bow.slice());
            renderPass.drawIndexed(ContentSkyShapes.BOW_VERTICES / 4 * 6, 1, 0, 0, 0);
        }
        renderPass.popDebugGroup();
    }

    private static void texture(RenderPass renderPass, AbstractTexture image) { renderPass.setUniform("Sampler0", image.getTextureView(), image.getSampler()); }

    private static GpuBuffer colored(@Nullable GpuBuffer target, String label, float[] vertices) {
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(vertices.length / ContentSkyShapes.COLORED * DefaultVertexFormat.POSITION_COLOR.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
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
            BufferBuilder buffer = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (int at = 0; at < vertices.length; at += ContentSkyShapes.TEXTURED) { buffer.addVertex(vertices[at], vertices[at + 1], vertices[at + 2]).setUv(vertices[at + 3], vertices[at + 4]); }
            try (MeshData mesh = buffer.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> label, GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }
}
