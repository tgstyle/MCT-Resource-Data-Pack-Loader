package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import javax.annotation.Nullable;

final class ContentSkyRenderer {
    private static final ResourceLocation MOON_PHASES = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    private final DimensionTraitsDef.Sky sky;
    @Nullable private VertexBuffer skyBuffer;
    @Nullable private VertexBuffer darkBuffer;
    private final ContentStars stars;

    ContentSkyRenderer(DimensionTraitsDef.Sky sky) {
        this.sky = sky;
        stars = new ContentStars(sky.starCount(), sky.starSize());
    }

    void render(ClientLevel level, float partialTick, Matrix4f modelView, Camera camera, Matrix4f projection, boolean foggy, Runnable setupFog) {
        setupFog.run();
        if (foggy || hidden(camera)) { return; }
        if (skyBuffer == null || darkBuffer == null) {
            skyBuffer = upload(disc(16.0F));
            darkBuffer = upload(disc(-16.0F));
        }
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelView);
        Vec3 color = level.getSkyColor(camera.getPosition(), partialTick);
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor((float) color.x, (float) color.y, (float) color.z, 1.0F);
        ShaderInstance shader = RenderSystem.getShader();
        draw(skyBuffer, poseStack, projection, shader);
        ContentSkyExtras.backdrop(level, poseStack.last().pose());
        RenderSystem.enableBlend();
        sunrise(level, partialTick, poseStack);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        float clear = 1.0F - level.getRainLevel(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, clear * ContentDimensionEffects.sun(level));
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        float turn = level.getTimeOfDay(partialTick) * 360.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(turn));
        if (sky.sunSize() > 0.0F) { quad(poseStack.last().pose(), sky.sunTexture(), sky.sunSize()); }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, clear * ContentDimensionEffects.moon(level));
        if (sky.bodies() == null) { moon(poseStack.last().pose(), level.getMoonPhase()); }
        poseStack.popPose();
        if (sky.bodies() != null) {
            for (DimensionTraitsDef.Body body : sky.bodies()) {
                poseStack.pushPose();
                if (body.followsTime()) { poseStack.mulPose(Axis.XP.rotationDegrees(turn)); }
                poseStack.mulPose(Axis.XP.rotationDegrees(body.angle()));
                poseStack.mulPose(Axis.ZP.rotationDegrees(body.tilt()));
                quad(poseStack.last().pose(), body.texture(), body.size());
                poseStack.popPose();
            }
        }
        float bright = level.getStarBrightness(partialTick) * clear;
        if (bright > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(turn));
            FogRenderer.setupNoFog();
            stars.draw(level, bright, poseStack.last().pose(), projection, partialTick);
            setupFog.run();
            poseStack.popPose();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
        ContentSkyExtras.overlay(level, poseStack.last().pose(), partialTick);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        if (camera.getEntity().getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level) < 0.0D) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 12.0F, 0.0F);
            draw(darkBuffer, poseStack, projection, shader);
            poseStack.popPose();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    private static boolean hidden(Camera camera) {
        FogType fluid = camera.getFluidInCamera();
        if (fluid == FogType.POWDER_SNOW || fluid == FogType.LAVA) { return true; }
        return camera.getEntity() instanceof LivingEntity living && (living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS));
    }

    private static void sunrise(ClientLevel level, float partialTick, PoseStack poseStack) {
        float[] glow = level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
        if (glow == null) { return; }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(level.getSunAngle(partialTick)) < 0.0F ? 180.0F : 0.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        Matrix4f pose = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(pose, 0.0F, 100.0F, 0.0F).setColor(glow[0], glow[1], glow[2], glow[3]);
        for (int step = 0; step <= 16; ++step) {
            float at = step * ((float) Math.PI * 2.0F) / 16.0F;
            float sin = Mth.sin(at);
            float cos = Mth.cos(at);
            buffer.addVertex(pose, sin * 120.0F, cos * 120.0F, -cos * 40.0F * glow[3]).setColor(glow[0], glow[1], glow[2], 0.0F);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        poseStack.popPose();
    }

    private static void moon(Matrix4f pose, int phase) {
        float size = 20.0F;
        int column = phase % 4;
        int row = phase / 4 % 2;
        float left = column / 4.0F;
        float top = row / 2.0F;
        float right = (column + 1) / 4.0F;
        float bottom = (row + 1) / 2.0F;
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(pose, -size, -100.0F, size).setUv(right, bottom);
        buffer.addVertex(pose, size, -100.0F, size).setUv(left, bottom);
        buffer.addVertex(pose, size, -100.0F, -size).setUv(left, top);
        buffer.addVertex(pose, -size, -100.0F, -size).setUv(right, top);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void quad(Matrix4f pose, ResourceLocation texture, float size) {
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(pose, -size, 100.0F, -size).setUv(0.0F, 0.0F);
        buffer.addVertex(pose, size, 100.0F, -size).setUv(1.0F, 0.0F);
        buffer.addVertex(pose, size, 100.0F, size).setUv(1.0F, 1.0F);
        buffer.addVertex(pose, -size, 100.0F, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void draw(VertexBuffer buffer, PoseStack poseStack, Matrix4f projection, @Nullable ShaderInstance shader) {
        if (shader == null) { return; }
        buffer.bind();
        buffer.drawWithShader(poseStack.last().pose(), projection, shader);
        VertexBuffer.unbind();
    }

    static VertexBuffer upload(MeshData built) {
        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(built);
        VertexBuffer.unbind();
        return buffer;
    }

    private static MeshData disc(float height) {
        float reach = Math.signum(height) * 512.0F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        buffer.addVertex(0.0F, height, 0.0F);
        for (int degrees = -180; degrees <= 180; degrees += 45) {
            float at = degrees * ((float) Math.PI / 180.0F);
            buffer.addVertex(reach * Mth.cos(at), height, 512.0F * Mth.sin(at));
        }
        return buffer.buildOrThrow();
    }
}
