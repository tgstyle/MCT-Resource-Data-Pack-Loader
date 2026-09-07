package mctmods.resourcedatapackloader.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nonnull;

public final class EntityTint {
    private EntityTint() {}

    public static VertexConsumer wrap(VertexConsumer consumer, int tint) { return tint == 0 ? consumer : new Tinted(consumer, tint); }

    public static MultiBufferSource wrap(MultiBufferSource buffer, int tint) { return tint == 0 ? buffer : type -> wrap(buffer.getBuffer(type), tint); }

    private static final class Tinted implements VertexConsumer {
        private final VertexConsumer inner;
        private final int red;
        private final int green;
        private final int blue;

        Tinted(VertexConsumer inner, int tint) {
            this.inner = inner;
            this.red = tint >> 16 & 255;
            this.green = tint >> 8 & 255;
            this.blue = tint & 255;
        }

        @Override @Nonnull public VertexConsumer vertex(double x, double y, double z) { inner.vertex(x, y, z); return this; }

        @Override @Nonnull public VertexConsumer color(int r, int g, int b, int a) { inner.color(r * red / 255, g * green / 255, b * blue / 255, a); return this; }

        @Override @Nonnull public VertexConsumer uv(float u, float v) { inner.uv(u, v); return this; }

        @Override @Nonnull public VertexConsumer overlayCoords(int u, int v) { inner.overlayCoords(u, v); return this; }

        @Override @Nonnull public VertexConsumer uv2(int u, int v) { inner.uv2(u, v); return this; }

        @Override @Nonnull public VertexConsumer normal(float x, float y, float z) { inner.normal(x, y, z); return this; }

        @Override public void endVertex() { inner.endVertex(); }

        @Override public void defaultColor(int r, int g, int b, int a) { inner.defaultColor(r * red / 255, g * green / 255, b * blue / 255, a); }

        @Override public void unsetDefaultColor() { inner.unsetDefaultColor(); }
    }
}
