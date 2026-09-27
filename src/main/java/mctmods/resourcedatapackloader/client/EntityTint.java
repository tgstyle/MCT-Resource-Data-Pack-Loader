package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.compat.LineClientCompat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nonnull;

public final class EntityTint {
    private EntityTint() {}

    public static VertexConsumer wrap(VertexConsumer consumer, int tint) { return tint == 0 ? consumer : new Tinted(consumer, tint); }

    private static final class Tinted extends LineClientCompat.ForwardingVertices {
        private final int red;
        private final int green;
        private final int blue;

        Tinted(VertexConsumer inner, int tint) {
            super(inner);
            this.red = tint >> 16 & 255;
            this.green = tint >> 8 & 255;
            this.blue = tint & 255;
        }

        @Override @Nonnull public VertexConsumer addVertex(float x, float y, float z) { inner.addVertex(x, y, z); return this; }

        @Override @Nonnull public VertexConsumer setColor(int r, int g, int b, int a) { inner.setColor(r * red / 255, g * green / 255, b * blue / 255, a); return this; }

        @Override @Nonnull public VertexConsumer setColor(int argb) { return setColor(argb >> 16 & 255, argb >> 8 & 255, argb & 255, argb >>> 24); }

        @Override @Nonnull public VertexConsumer setUv(float u, float v) { inner.setUv(u, v); return this; }

        @Override @Nonnull public VertexConsumer setUv1(int u, int v) { inner.setUv1(u, v); return this; }

        @Override @Nonnull public VertexConsumer setUv2(int u, int v) { inner.setUv2(u, v); return this; }

        @Override @Nonnull public VertexConsumer setNormal(float x, float y, float z) { inner.setNormal(x, y, z); return this; }

        @Override @Nonnull public VertexConsumer setLineWidth(float width) { inner.setLineWidth(width); return this; }
    }
}
