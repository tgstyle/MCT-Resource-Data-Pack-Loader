package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
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
import net.minecraft.util.RandomSource;
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
    @Nullable private VertexBuffer starBuffer;

    ContentSkyRenderer(DimensionTraitsDef.Sky sky) { this.sky = sky; }

    void render(ClientLevel level, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projection, boolean foggy, Runnable setupFog) {
        setupFog.run();
        if (foggy || hidden(camera)) { return; }
        if (skyBuffer == null || darkBuffer == null || starBuffer == null) {
            skyBuffer = upload(disc(16.0F));
            darkBuffer = upload(disc(-16.0F));
            starBuffer = upload(stars(sky.starCount(), sky.starSize()));
        }
        Vec3 color = level.getSkyColor(camera.getPosition(), partialTick);
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor((float) color.x, (float) color.y, (float) color.z, 1.0F);
        ShaderInstance shader = RenderSystem.getShader();
        draw(skyBuffer, poseStack, projection, shader);
        RenderSystem.enableBlend();
        sunrise(level, partialTick, poseStack);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        float clear = 1.0F - level.getRainLevel(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, clear);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        float turn = level.getTimeOfDay(partialTick) * 360.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(turn));
        if (sky.sunSize() > 0.0F) { quad(poseStack.last().pose(), sky.sunTexture(), sky.sunSize()); }
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
            RenderSystem.setShaderColor(bright, bright, bright, bright);
            FogRenderer.setupNoFog();
            draw(starBuffer, poseStack, projection, GameRenderer.getPositionShader());
            setupFog.run();
            poseStack.popPose();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
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
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(pose, 0.0F, 100.0F, 0.0F).color(glow[0], glow[1], glow[2], glow[3]).endVertex();
        for (int step = 0; step <= 16; ++step) {
            float at = step * ((float) Math.PI * 2.0F) / 16.0F;
            float sin = Mth.sin(at);
            float cos = Mth.cos(at);
            buffer.vertex(pose, sin * 120.0F, cos * 120.0F, -cos * 40.0F * glow[3]).color(glow[0], glow[1], glow[2], 0.0F).endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());
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
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pose, -size, -100.0F, size).uv(right, bottom).endVertex();
        buffer.vertex(pose, size, -100.0F, size).uv(left, bottom).endVertex();
        buffer.vertex(pose, size, -100.0F, -size).uv(left, top).endVertex();
        buffer.vertex(pose, -size, -100.0F, -size).uv(right, top).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void quad(Matrix4f pose, ResourceLocation texture, float size) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.setShaderTexture(0, texture);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pose, -size, 100.0F, -size).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(pose, size, 100.0F, -size).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(pose, size, 100.0F, size).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(pose, -size, 100.0F, size).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void draw(VertexBuffer buffer, PoseStack poseStack, Matrix4f projection, @Nullable ShaderInstance shader) {
        if (shader == null) { return; }
        buffer.bind();
        buffer.drawWithShader(poseStack.last().pose(), projection, shader);
        VertexBuffer.unbind();
    }

    private static VertexBuffer upload(BufferBuilder.RenderedBuffer built) {
        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(built);
        VertexBuffer.unbind();
        return buffer;
    }

    private static BufferBuilder.RenderedBuffer disc(float height) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        float reach = Math.signum(height) * 512.0F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        buffer.vertex(0.0D, height, 0.0D).endVertex();
        for (int degrees = -180; degrees <= 180; degrees += 45) {
            float at = degrees * ((float) Math.PI / 180.0F);
            buffer.vertex(reach * Mth.cos(at), height, 512.0F * Mth.sin(at)).endVertex();
        }
        return buffer.end();
    }

    private static BufferBuilder.RenderedBuffer stars(int count, float smallest) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RandomSource random = RandomSource.create(10842L);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        float spread = smallest * 2.0F / 3.0F;
        for (int star = 0; star < count; ++star) {
            double x = random.nextFloat() * 2.0F - 1.0F;
            double y = random.nextFloat() * 2.0F - 1.0F;
            double z = random.nextFloat() * 2.0F - 1.0F;
            double size = smallest + random.nextFloat() * spread;
            double length = x * x + y * y + z * z;
            if (length >= 1.0D || length <= 0.01D) { continue; }
            length = 1.0D / Math.sqrt(length);
            x *= length;
            y *= length;
            z *= length;
            double yaw = Math.atan2(x, z);
            double yawSin = Math.sin(yaw);
            double yawCos = Math.cos(yaw);
            double pitch = Math.atan2(Math.sqrt(x * x + z * z), y);
            double pitchSin = Math.sin(pitch);
            double pitchCos = Math.cos(pitch);
            double spin = random.nextDouble() * Math.PI * 2.0D;
            double spinSin = Math.sin(spin);
            double spinCos = Math.cos(spin);
            for (int corner = 0; corner < 4; ++corner) {
                double a = ((corner & 2) - 1) * size;
                double b = ((corner + 1 & 2) - 1) * size;
                double c = a * spinCos - b * spinSin;
                double d = b * spinCos + a * spinSin;
                double e = c * pitchSin;
                double f = -c * pitchCos;
                buffer.vertex(x * 100.0D + f * yawSin - d * yawCos, y * 100.0D + e, z * 100.0D + d * yawSin + f * yawCos).endVertex();
            }
        }
        return buffer.end();
    }
}
