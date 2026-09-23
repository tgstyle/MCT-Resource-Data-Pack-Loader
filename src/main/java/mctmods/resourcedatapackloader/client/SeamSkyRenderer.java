package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

public final class SeamSkyRenderer {
    private static final double NEAR = 32.0D;

    private SeamSkyRenderer() {}

    public static void onRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) { return; }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) { return; }
        String dimension = level.dimension().location().toString();
        ResourceLocation under = ContentSeams.below(dimension);
        ResourceLocation over = ContentSeams.above(dimension);
        if (under == null && over == null) { return; }
        double eyeY = event.getCamera().getPosition().y;
        Matrix4f pose = event.getModelViewMatrix();
        float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        if (under != null) { plane(pose, under, level.getMinBuildHeight() - 1 - eyeY, partialTicks); }
        if (over != null) { plane(pose, over, ContentSeams.ceiling(level) + 1 - eyeY, partialTicks); }
    }

    private static void plane(Matrix4f pose, ResourceLocation dimension, double height, float partialTicks) {
        if (Math.abs(height) > NEAR) { return; }
        Vec3 tint = skyOf(dimension, partialTicks);
        float extent = Math.max(64, Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
        float y = (float) height;
        int red = (int) (tint.x * 255.0D);
        int green = (int) (tint.y * 255.0D);
        int blue = (int) (tint.z * 255.0D);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(pose, -extent, y, -extent).setColor(red, green, blue, 255);
        buffer.addVertex(pose, -extent, y, extent).setColor(red, green, blue, 255);
        buffer.addVertex(pose, extent, y, extent).setColor(red, green, blue, 255);
        buffer.addVertex(pose, extent, y, -extent).setColor(red, green, blue, 255);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    private static Vec3 skyOf(ResourceLocation dimension, float partialTicks) {
        DimensionDef def = ContentDimensions.def(dimension);
        String base = def == null ? dimension.toString() : "minecraft:" + def.base();
        if (def != null && def.fogColor() >= 0) { return new Vec3(((def.fogColor() >> 16) & 255) / 255.0D, ((def.fogColor() >> 8) & 255) / 255.0D, (def.fogColor() & 255) / 255.0D); }
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        ServerLevel target = server == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
        if (target != null) {
            float brightness = Mth.clamp(Mth.cos(target.getTimeOfDay(partialTicks) * ((float) Math.PI * 2.0F)) * 2.0F + 0.5F, 0.0F, 1.0F);
            return DimensionSpecialEffects.forType(target.dimensionType()).getBrightnessDependentFogColor(Vec3.fromRGB24(fogOf(base)), brightness);
        }
        return switch (base) {
            case "minecraft:the_nether" -> new Vec3(0.2D, 0.03D, 0.03D);
            case "minecraft:the_end" -> new Vec3(0.06D, 0.06D, 0.09D);
            default -> new Vec3(0.5D, 0.66D, 1.0D);
        };
    }

    private static int fogOf(String base) {
        return switch (base) {
            case "minecraft:the_nether" -> 0x330808;
            case "minecraft:the_end" -> 0xA080A0;
            default -> 0xC0D8FF;
        };
    }
}
