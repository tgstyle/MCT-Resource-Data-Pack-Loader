package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
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
        Matrix4f pose = event.getPoseStack().last().pose();
        if (under != null) { plane(pose, under, level.getMinBuildHeight() - 1 - eyeY); }
        if (over != null) { plane(pose, over, level.getMaxBuildHeight() + 1 - eyeY); }
    }

    private static void plane(Matrix4f pose, ResourceLocation dimension, double height) {
        if (Math.abs(height) > NEAR) { return; }
        Vec3 tint = skyOf(dimension);
        float extent = Math.max(64, Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
        float y = (float) height;
        int red = (int) (tint.x * 255.0D);
        int green = (int) (tint.y * 255.0D);
        int blue = (int) (tint.z * 255.0D);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(pose, -extent, y, -extent).color(red, green, blue, 255).endVertex();
        buffer.vertex(pose, -extent, y, extent).color(red, green, blue, 255).endVertex();
        buffer.vertex(pose, extent, y, extent).color(red, green, blue, 255).endVertex();
        buffer.vertex(pose, extent, y, -extent).color(red, green, blue, 255).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    private static Vec3 skyOf(ResourceLocation dimension) {
        DimensionDef def = ContentDimensions.def(dimension);
        if (def != null && def.fogColor() >= 0) { return new Vec3(((def.fogColor() >> 16) & 255) / 255.0D, ((def.fogColor() >> 8) & 255) / 255.0D, (def.fogColor() & 255) / 255.0D); }
        String base = def == null ? dimension.toString() : "minecraft:" + def.base();
        return switch (base) {
            case "minecraft:the_nether" -> new Vec3(0.2D, 0.03D, 0.03D);
            case "minecraft:the_end" -> new Vec3(0.06D, 0.06D, 0.09D);
            default -> new Vec3(0.5D, 0.66D, 1.0D);
        };
    }
}
