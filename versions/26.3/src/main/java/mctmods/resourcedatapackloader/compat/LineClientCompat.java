package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.mixin.rdpl.client.IPostPass;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LineClientCompat {
    public static final String SUBMIT_MODEL = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/UvMapping;I)V";

    private LineClientCompat() {}

    public static RenderType translucentBody(Identifier texture) { return RenderTypes.entityTranslucentCull(texture); }

    public static Vec3 skyColor(Camera camera, float partialTick) {
        Vector3fc tint = camera.attributeProbe().getValue(EnvironmentAttributes.SKY_COLOR, partialTick);
        return new Vec3(tint.x(), tint.y(), tint.z());
    }

    public static InputConstants.Key keyV() { return InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_V); }

    public static void writeUniform(PostPass pass, String group, float... values) {
        Map<String, GpuBuffer> uniforms = ((IPostPass) pass).rdpl$getCustomUniforms();
        GpuBuffer buffer = uniforms.get(group);
        if (buffer == null) { return; }
        if ((buffer.usage() & GpuBuffer.USAGE_COPY_DST) == 0) {
            GpuBuffer writable = RenderSystem.getDevice().createBuffer(() -> group, GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, buffer.size());
            buffer.close();
            uniforms.put(group, writable);
            buffer = writable;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Std140Builder builder = Std140Builder.onStack(stack, (int) buffer.size());
            for (float value : values) { builder.putFloat(value); }
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(buffer.slice(), builder.get());
        }
    }

    public static <S> void submitModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack pose, RenderType type, int light, int overlay, int outline, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbling) {
        collector.submitModel(model, state, pose, type, light, overlay, outline);
        if (crumbling != null) { collector.submitCrumblingOverlay(model, state, pose, type, light, overlay, -1, crumbling); }
    }

    public static <S> void submitModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack pose, Identifier texture, int light, int overlay, int outline) { collector.submitModel(model, state, pose, texture, light, overlay, outline); }

    public abstract static class ForwardingVertices implements VertexConsumer {
        protected final VertexConsumer inner;

        protected ForwardingVertices(VertexConsumer inner) { this.inner = inner; }

        @Override @Nonnull public VertexConsumer setUv3(float u, float v) {
            inner.setUv3(u, v);
            return this;
        }
    }
}
