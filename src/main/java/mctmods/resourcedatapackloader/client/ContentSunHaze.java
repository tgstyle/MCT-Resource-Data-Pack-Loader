package mctmods.resourcedatapackloader.client;

import static mctmods.resourcedatapackloader.client.SkyMesh.SIZE;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.Rgb;

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
import org.joml.Vector3f;
import javax.annotation.Nullable;

public final class ContentSunHaze {
    private static final float AURA_ALPHA = 0.5F;
    private static final int AURA_STEPS = 32;

    private ContentSunHaze() {}

    public static Matrix4f sunPose(ClientLevel level, Matrix4f frustum, float partialTick) { return new Matrix4f(frustum).rotate(Axis.YP.rotationDegrees(-90.0F)).rotate(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F)); }

    public static void aura(@Nullable ClientLevel level, Matrix4f pose, float alpha, float size, Matrix4f view) {
        DimensionDef def = ContentDimensions.def(level);
        float[] aura = ContentWindowLook.aura(def);
        if (aura == null || aura[3] <= 0.0F) { return; }
        SkyLookDef look = def == null ? null : def.look();
        float reach = look == null ? SkyLookDef.AURA_REACH : look.sunAuraSize();
        if (reach <= 0.0F) { return; }
        float center = alpha * aura[3] * AURA_ALPHA * lowSun(look, pose, view);
        if (center <= 0.0F) { return; }
        float wide = size * reach;
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

    public static void lines(@Nullable ClientLevel level, Matrix4f pose, float alpha, float size) {
        DimensionDef def = ContentDimensions.def(level);
        SkyLookDef look = def == null ? null : def.look();
        if (look == null || look.sunLineCount() <= 0 || look.sunLineLength() <= 0.0F || look.sunLineWidth() <= 0.0F) { return; }
        float near = alpha * (look.sunLineColor() >>> 24) / 255.0F;
        if (near <= 0.0F) { return; }
        float red = Rgb.red(look.sunLineColor());
        float green = Rgb.green(look.sunLineColor());
        float blue = Rgb.blue(look.sunLineColor());
        float half = size * look.sunLineWidth() / 2.0F;
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int line = 0; line < look.sunLineCount(); line++) {
            float at = line * Mth.TWO_PI / look.sunLineCount();
            float dx = Mth.cos(at);
            float dz = Mth.sin(at);
            float start = size * look.sunLineStart() / Math.max(Math.abs(dx), Math.abs(dz));
            float end = start + size * look.sunLineLength();
            float sideX = -dz * half;
            float sideZ = dx * half;
            buffer.addVertex(pose, dx * start + sideX, SIZE, dz * start + sideZ).setColor(red, green, blue, near);
            buffer.addVertex(pose, dx * start - sideX, SIZE, dz * start - sideZ).setColor(red, green, blue, near);
            buffer.addVertex(pose, dx * end - sideX, SIZE, dz * end - sideZ).setColor(red, green, blue, 0.0F);
            buffer.addVertex(pose, dx * end + sideX, SIZE, dz * end + sideZ).setColor(red, green, blue, 0.0F);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        additive();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
    }

    private static float lowSun(@Nullable SkyLookDef look, Matrix4f pose, Matrix4f view) {
        if (look == null || look.sunAuraHorizon() <= 0.0F) { return 1.0F; }
        Vector3f up = pose.transformDirection(new Vector3f(0.0F, 1.0F, 0.0F));
        new Matrix4f(view).invert().transformDirection(up).normalize();
        float elevation = (float) Math.toDegrees(Math.asin(Mth.clamp(up.y, -1.0F, 1.0F)));
        return 1.0F - Mth.clamp(Math.abs(elevation) / look.sunAuraHorizon(), 0.0F, 1.0F);
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
