package mctmods.resourcedatapackloader.client;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nullable;

public final class ContentStars {
    public static final ContentStars VANILLA = new ContentStars(ContentStarField.VANILLA_COUNT, ContentStarField.VANILLA_SIZE, new SkyOccluder[0]);
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    private final int count;
    private final float size;
    private final int[] first = new int[ContentStarField.GROUPS];
    private final int[] quads = new int[ContentStarField.GROUPS];
    private final int[] movingFirst = new int[ContentStarField.GROUPS];
    private final int[] movingQuads = new int[ContentStarField.GROUPS];
    @Nullable private GpuBuffer buffer;
    @Nullable private GpuBuffer movingBuffer;
    private final SkyOccluder[] occluders;
    private final boolean dynamic;
    private float[][] catalog;
    private float[][] visible;
    private boolean[] moving;
    private float[] hidden = new float[0];
    private int built;
    private int total = -1;
    private int movingTotal;
    private boolean movingStale;

    public ContentStars(int count, float size, SkyOccluder[] occluders) {
        this.count = count;
        this.size = size;
        this.occluders = occluders;
        boolean fixed = false;
        for (SkyOccluder body : occluders) { fixed |= body.fixed(); }
        dynamic = fixed;
    }

    public void hide(float[] offsets) {
        boolean stale = built != SkyOccluder.current() || hidden.length != offsets.length;
        boolean moved = false;
        for (int at = 0; at < offsets.length && !stale; ++at) {
            if (hidden[at] == offsets[at]) { continue; }
            if (occluders[at].fixed()) { moved = true; }
            else { stale = true; }
        }
        if (!stale && !moved) { return; }
        built = SkyOccluder.current();
        hidden = offsets.clone();
        for (int at = 0; at < offsets.length; ++at) { occluders[at].turn(offsets[at]); }
        if (stale) {
            if (buffer != null) {
                buffer.close();
                buffer = null;
            }
            total = -1;
        }
        movingStale = dynamic;
    }

    private void retime() {
        if (!movingStale || total < 0) { return; }
        movingStale = false;
        if (movingBuffer != null) {
            movingBuffer.close();
            movingBuffer = null;
        }
        float[][] picked = new float[visible.length][];
        for (int star = 0; star < picked.length; ++star) {
            float[] quad = visible[star];
            if (quad != null && moving[star] && !covered(quad, true)) { picked[star] = quad; }
        }
        float[][] groups = ContentStarField.groups(picked);
        movingTotal = layout(groups, movingFirst, movingQuads);
        movingBuffer = upload(groups, movingTotal);
    }

    public void draw(PoseStack poseStack, float brightness) {
        if (total < 0) { build(); }
        retime();
        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (buffer == null && movingBuffer == null || color == null) { return; }
        Vector4f[] colors = ContentStarField.colors(Minecraft.getInstance().level, brightness);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(poseStack.last().pose());
        GpuBufferSlice[] transforms = new GpuBufferSlice[colors.length];
        for (int group = 0; group < colors.length; ++group) { transforms[group] = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(modelView), colors[group], new Vector3f(), new Matrix4f()); }
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "RDPL stars", color, OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(RenderPipelines.STARS);
            RenderSystem.bindDefaultUniforms(pass);
            if (buffer != null) { drawGroups(pass, buffer, total, first, quads, transforms); }
            if (movingBuffer != null) { drawGroups(pass, movingBuffer, movingTotal, movingFirst, movingQuads, transforms); }
        }
        modelView.popMatrix();
    }

    private void drawGroups(RenderPass pass, GpuBuffer vertices, int shown, int[] starts, int[] sizes, GpuBufferSlice[] transforms) {
        pass.setVertexBuffer(0, vertices);
        pass.setIndexBuffer(quadIndices.getBuffer(shown * 6), quadIndices.type());
        for (int group = 0; group < transforms.length; ++group) {
            if (sizes[group] == 0) { continue; }
            pass.setUniform("DynamicTransforms", transforms[group]);
            pass.drawIndexed(starts[group] * 4, 0, sizes[group] * 6, 1);
        }
    }

    private void build() {
        if (catalog == null) { catalog = ContentStarField.stars(count, size); }
        visible = new float[catalog.length][];
        moving = new boolean[catalog.length];
        for (int star = 0; star < catalog.length; ++star) {
            float[] quad = catalog[star];
            if (quad == null || covered(quad, false)) { continue; }
            visible[star] = quad;
            moving[star] = sweeps(quad);
        }
        float[][] picked = new float[visible.length][];
        for (int star = 0; star < picked.length; ++star) {
            if (!moving[star]) { picked[star] = visible[star]; }
        }
        float[][] groups = ContentStarField.groups(picked);
        total = layout(groups, first, quads);
        buffer = upload(groups, total);
    }

    private static int layout(float[][] groups, int[] starts, int[] sizes) {
        int shown = 0;
        for (int group = 0; group < groups.length; ++group) {
            starts[group] = shown;
            sizes[group] = groups[group].length / 12;
            shown += sizes[group];
        }
        return shown;
    }

    @Nullable private static GpuBuffer upload(float[][] groups, int shown) {
        if (shown == 0) { return null; }
        try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * shown * 4)) {
            BufferBuilder builder = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            for (float[] group : groups) {
                for (int at = 0; at < group.length; at += 3) { builder.addVertex(group[at], group[at + 1], group[at + 2]); }
            }
            try (MeshData mesh = builder.buildOrThrow()) { return RenderSystem.getDevice().createBuffer(() -> "RDPL stars", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer()); }
        }
    }

    private boolean covered(float[] star, boolean fixed) {
        for (SkyOccluder body : occluders) {
            if (body.fixed() == fixed && body.covers(star[12], star[13], star[14], star[15])) { return true; }
        }
        return false;
    }

    private boolean sweeps(float[] star) {
        for (SkyOccluder body : occluders) {
            if (body.fixed() && body.sweeps(star[12] / ContentSkyShapes.SIZE, star[15])) { return true; }
        }
        return false;
    }
}
