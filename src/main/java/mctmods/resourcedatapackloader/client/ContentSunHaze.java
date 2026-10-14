package mctmods.resourcedatapackloader.client;

import static mctmods.resourcedatapackloader.client.SkyMesh.SIZE;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import javax.annotation.Nullable;

public final class ContentSunHaze {
    private static final float AURA_ALPHA = 0.5F;
    private static final float AURA_REACH = 1.5F;
    private static final int AURA_STEPS = 32;

    private ContentSunHaze() {}

    public static Matrix4f sunPose(ClientLevel level, Matrix4f frustum, float partialTick) { return new Matrix4f(frustum).rotate(Axis.YP.rotationDegrees(-90.0F)).rotate(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F)); }

    public static void aura(@Nullable ClientLevel level, Matrix4f pose, float alpha, float size) {
        float[] aura = ContentWindowLook.aura(ContentDimensions.def(level));
        if (aura == null || aura[3] <= 0.0F) { return; }
        float center = alpha * aura[3] * AURA_ALPHA;
        float wide = size * AURA_REACH;
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(pose, 0.0F, SIZE, 0.0F).setColor(aura[0], aura[1], aura[2], center);
        for (int step = 0; step <= AURA_STEPS; step++) {
            float at = step * Mth.TWO_PI / AURA_STEPS;
            buffer.addVertex(pose, Mth.cos(at) * wide, SIZE, Mth.sin(at) * wide).setColor(aura[0], aura[1], aura[2], 0.0F);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        additive();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
    }

    public static void sun(@Nullable ClientLevel level, Matrix4f pose, float alpha, float size, Runnable plain) {
        float[] tint = ContentWindowLook.sunTint(ContentDimensions.def(level));
        if (tint == null) {
            plain.run();
            return;
        }
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
        quad(pose, size);
        additive();
        RenderSystem.setShaderColor(tint[0], tint[1], tint[2], alpha);
        plain.run();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
    }

    static void quad(Matrix4f pose, float size) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(pose, -size, SIZE, -size).setUv(0.0F, 0.0F);
        buffer.addVertex(pose, size, SIZE, -size).setUv(1.0F, 0.0F);
        buffer.addVertex(pose, size, SIZE, size).setUv(1.0F, 1.0F);
        buffer.addVertex(pose, -size, SIZE, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void additive() { RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO); }
}
