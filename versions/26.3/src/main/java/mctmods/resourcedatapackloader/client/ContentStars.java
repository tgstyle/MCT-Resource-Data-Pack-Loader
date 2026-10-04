package mctmods.resourcedatapackloader.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import javax.annotation.Nullable;

public final class ContentStars {
    public static final ContentStars VANILLA = new ContentStars(ContentStarField.VANILLA_COUNT, ContentStarField.VANILLA_SIZE);
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
    private final int count;
    private final float size;
    private final int[] first = new int[ContentStarField.GROUPS];
    private final int[] quads = new int[ContentStarField.GROUPS];
    @Nullable private GpuBuffer buffer;
    private int total = -1;

    public ContentStars(int count, float size) {
        this.count = count;
        this.size = size;
    }

    public void prepare() {
        if (total >= 0) { return; }
        float[][] groups = ContentStarField.groups(count, size);
        total = 0;
        for (int group = 0; group < groups.length; ++group) {
            first[group] = total;
            quads[group] = groups[group].length / 12;
            total += quads[group];
        }
        if (total == 0) { return; }
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * total * 4)) {
            BufferBuilder builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION);
            for (float[] group : groups) {
                for (int at = 0; at < group.length; at += 3) { builder.addVertex(group[at], group[at + 1], group[at + 2]); }
            }
            try (MeshData mesh = builder.buildOrThrow()) { buffer = RenderSystem.getDevice().createBuffer(() -> "RDPL stars", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }

    public void draw(RenderPass renderPass, PoseStack poseStack, float brightness) {
        if (buffer == null) { return; }
        Vector4f[] colors = ContentStarField.colors(Minecraft.getInstance().level, brightness);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        GpuBufferSlice[] transforms = new GpuBufferSlice[colors.length];
        for (int group = 0; group < colors.length; ++group) { transforms[group] = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelView), colors[group]); }
        modelView.popMatrix();
        renderPass.pushDebugGroup(() -> "RDPL stars");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.STARS));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setVertexBuffer(0, buffer.slice());
        renderPass.setIndexBuffer(quadIndices.getBuffer(total * 6), quadIndices.type());
        for (int group = 0; group < colors.length; ++group) {
            if (quads[group] == 0) { continue; }
            renderPass.setUniform("DynamicTransforms", transforms[group]);
            renderPass.drawIndexed(quads[group] * 6, 1, 0, first[group] * 4, 0);
        }
        renderPass.popDebugGroup();
    }
}
