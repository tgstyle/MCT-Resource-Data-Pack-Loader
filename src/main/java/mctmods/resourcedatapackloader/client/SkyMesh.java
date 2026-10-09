package mctmods.resourcedatapackloader.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class SkyMesh {
    public static final float SIZE = 100.0F;

    private SkyMesh() {}

    public static BufferBuilder begin(VertexFormat.Mode mode, VertexFormat format) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(mode, format);
        return buffer;
    }
}
