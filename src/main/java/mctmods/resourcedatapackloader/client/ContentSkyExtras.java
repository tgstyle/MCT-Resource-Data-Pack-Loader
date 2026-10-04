package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import javax.annotation.Nullable;

public final class ContentSkyExtras {
    private static final float SIZE = 100.0F;
    private static final float[][][] FACES = {
            {{-1, 1, 1}, {1, 1, 1}, {1, 1, -1}, {-1, 1, -1}},
            {{-1, -1, -1}, {1, -1, -1}, {1, -1, 1}, {-1, -1, 1}},
            {{-1, 1, -1}, {1, 1, -1}, {1, -1, -1}, {-1, -1, -1}},
            {{1, 1, -1}, {1, 1, 1}, {1, -1, 1}, {1, -1, -1}},
            {{1, 1, 1}, {-1, 1, 1}, {-1, -1, 1}, {1, -1, 1}},
            {{-1, 1, 1}, {-1, 1, -1}, {-1, -1, -1}, {-1, -1, 1}}};
    private static final int LONGITUDES = 32;
    private static final int LATITUDES = 16;
    private static final int AURORA_STEPS = 48;
    private static final float[] BOW_RADII = {40.0F, 40.6F, 41.0F, 41.4F, 41.8F, 42.2F, 42.6F, 43.2F};
    private static final int[] BOW_COLORS = {0x8B00FF, 0x8B00FF, 0x0040FF, 0x00C000, 0xFFFF00, 0xFF8000, 0xFF0000, 0xFF0000};
    private static final int BOW_STEPS = 64;
    private static final long RAINBOW_TICKS = 2400L;
    private static final float RAINBOW_FADE = 600.0F;
    private static int wetWorld;
    private static long wetAt = Long.MIN_VALUE;

    private ContentSkyExtras() {}

    public static void backdrop(@Nullable ClientLevel level, Matrix4f pose) {
        SkyLookDef look = ContentFogSampler.look(level);
        if (look == null || look.skybox() == null) { return; }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableCull();
        if (look.skybox().panorama() != null) {
            RenderSystem.setShaderTexture(0, look.skybox().panorama());
            panorama(pose);
        }
        else {
            for (int face = 0; face < FACES.length; ++face) {
                RenderSystem.setShaderTexture(0, look.skybox().faces().get(face));
                face(pose, FACES[face]);
            }
        }
        RenderSystem.enableCull();
    }

    public static void overlay(@Nullable ClientLevel level, Matrix4f pose, float partialTick) {
        SkyLookDef look = ContentFogSampler.look(level);
        if (level == null || look == null || look.aurora() == null && !look.rainbow()) { return; }
        float angle = level.getTimeOfDay(partialTick);
        float clear = 1.0F - level.getRainLevel(partialTick);
        float glow = look.aurora() == null ? 0.0F : night(angle) * clear;
        float bow = look.rainbow() ? rainbow(level, partialTick) * Mth.clamp(Mth.cos(angle * Mth.TWO_PI) * 4.0F, 0.0F, 1.0F) : 0.0F;
        if (glow <= 0.0F && bow <= 0.0F) { return; }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (glow > 0.0F) { aurora(pose, look.aurora(), glow, level.getGameTime() + partialTick); }
        if (bow > 0.0F) { bow(pose, angle * Mth.TWO_PI, bow); }
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private static float night(float angle) {
        float dark = Mth.clamp(1.0F - (Mth.cos(angle * Mth.TWO_PI) * 2.0F + 0.25F), 0.0F, 1.0F);
        return dark * dark;
    }

    private static float rainbow(ClientLevel level, float partialTick) {
        int id = System.identityHashCode(level);
        if (id != wetWorld) {
            wetWorld = id;
            wetAt = Long.MIN_VALUE;
        }
        float now = level.getRainLevel(1.0F);
        float before = level.getRainLevel(0.0F);
        long tick = level.getGameTime();
        if (now > before) {
            if (now > 0.2F) { wetAt = tick; }
            return 0.0F;
        }
        if (now >= 0.2F && now == before) { wetAt = tick; }
        if (wetAt == Long.MIN_VALUE || tick - wetAt > RAINBOW_TICKS) { return 0.0F; }
        return Math.min(1.0F, (RAINBOW_TICKS - (tick - wetAt)) / RAINBOW_FADE) * (1.0F - level.getRainLevel(partialTick));
    }

    private static void face(Matrix4f pose, float[][] corners) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(pose, corners[0][0] * SIZE, corners[0][1] * SIZE, corners[0][2] * SIZE).setUv(0.0F, 0.0F);
        buffer.addVertex(pose, corners[1][0] * SIZE, corners[1][1] * SIZE, corners[1][2] * SIZE).setUv(1.0F, 0.0F);
        buffer.addVertex(pose, corners[2][0] * SIZE, corners[2][1] * SIZE, corners[2][2] * SIZE).setUv(1.0F, 1.0F);
        buffer.addVertex(pose, corners[3][0] * SIZE, corners[3][1] * SIZE, corners[3][2] * SIZE).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void panorama(Matrix4f pose) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int row = 0; row < LATITUDES; ++row) {
            float top = (float) row / LATITUDES;
            float bottom = (float) (row + 1) / LATITUDES;
            for (int column = 0; column < LONGITUDES; ++column) {
                float left = (float) column / LONGITUDES;
                float right = (float) (column + 1) / LONGITUDES;
                sphere(buffer, pose, left, top);
                sphere(buffer, pose, right, top);
                sphere(buffer, pose, right, bottom);
                sphere(buffer, pose, left, bottom);
            }
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void sphere(BufferBuilder buffer, Matrix4f pose, float u, float v) {
        float around = u * Mth.TWO_PI;
        float down = v * Mth.PI;
        float ring = Mth.sin(down) * SIZE;
        buffer.addVertex(pose, Mth.sin(around) * ring, Mth.cos(down) * SIZE, -Mth.cos(around) * ring).setUv(u, v);
    }

    private static void aurora(Matrix4f pose, SkyLookDef.Aurora aurora, float strength, float time) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int band = 0; band < 2; ++band) {
            for (int step = 0; step < AURORA_STEPS; ++step) {
                curtain(buffer, pose, aurora, strength, time, band, step, false);
                curtain(buffer, pose, aurora, strength, time, band, step + 1, false);
                curtain(buffer, pose, aurora, strength, time, band, step + 1, true);
                curtain(buffer, pose, aurora, strength, time, band, step, true);
            }
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void curtain(BufferBuilder buffer, Matrix4f pose, SkyLookDef.Aurora aurora, float strength, float time, int band, int step, boolean top) {
        float along = (float) step / AURORA_STEPS;
        float around = (float) Math.toRadians(-70.0F + 140.0F * along + 4.0F * Mth.sin(time * 0.05F + step * 0.35F + band * 2.0F));
        float base = 18.0F + band * 6.0F + 3.0F * Mth.sin(time * 0.03F + step * 0.2F + band);
        float height = 18.0F + 10.0F * (0.5F + 0.5F * Mth.sin(time * 0.07F + step * 0.5F + band * 1.3F));
        float up = (float) Math.toRadians(top ? base + height : base);
        float flat = Mth.cos(up) * SIZE;
        int color = top ? aurora.topColor() : aurora.color();
        float alpha = top ? 0.0F : strength * 0.8F * Mth.sin(along * Mth.PI) * (0.6F + 0.4F * Mth.sin(time * 0.11F + step * 0.9F + band * 0.7F));
        buffer.addVertex(pose, Mth.sin(around) * flat, Mth.sin(up) * SIZE, -Mth.cos(around) * flat).setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha);
    }

    private static void bow(Matrix4f pose, float sunAngle, float strength) {
        float[] away = {Mth.sin(sunAngle), -Mth.cos(sunAngle)};
        float[] side = {-Mth.cos(sunAngle), -Mth.sin(sunAngle)};
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int ring = 0; ring < BOW_RADII.length - 1; ++ring) {
            for (int step = 0; step < BOW_STEPS; ++step) {
                arc(buffer, pose, away, side, ring, step, strength);
                arc(buffer, pose, away, side, ring, step + 1, strength);
                arc(buffer, pose, away, side, ring + 1, step + 1, strength);
                arc(buffer, pose, away, side, ring + 1, step, strength);
            }
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void arc(BufferBuilder buffer, Matrix4f pose, float[] away, float[] side, int ring, int step, float strength) {
        float radius = (float) Math.toRadians(BOW_RADII[ring]);
        float turn = step * Mth.TWO_PI / BOW_STEPS;
        float out = Mth.sin(radius);
        float in = Mth.cos(radius);
        float along = Mth.sin(turn) * out;
        int color = BOW_COLORS[ring];
        float alpha = ring == 0 || ring == BOW_RADII.length - 1 ? 0.0F : strength * 0.35F;
        buffer.addVertex(pose, (away[0] * in + side[0] * along) * SIZE, (away[1] * in + side[1] * along) * SIZE, Mth.cos(turn) * out * SIZE).setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha);
    }
}
