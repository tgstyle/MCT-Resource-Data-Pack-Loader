package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class SeamSkyRenderer {
    private static final double NEAR = 32.0D;

    private SeamSkyRenderer() {}

    public static void onRenderStage(RenderLevelStageEvent.AfterSky event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) { return; }
        String dimension = level.dimension().identifier().toString();
        Identifier under = ContentSeams.below(dimension);
        Identifier over = ContentSeams.above(dimension);
        if (under == null && over == null) { return; }
        double eyeY = event.getLevelRenderState().cameraRenderState.pos.y;
        if (under != null) { plane(under, level.getMinY() - 1 - eyeY); }
        if (over != null) { plane(over, ContentSeams.ceiling(level) + 1 - eyeY); }
    }

    private static void plane(Identifier dimension, double height) {
        if (Math.abs(height) > NEAR) { return; }
        Vec3 tint = skyOf(dimension);
        float extent = Math.max(64, Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
        float y = (float) height;
        int red = (int) (tint.x * 255.0D);
        int green = (int) (tint.y * 255.0D);
        int blue = (int) (tint.z * 255.0D);
        RenderType type = RenderTypes.debugQuads();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(type);
        buffer.addVertex(-extent, y, -extent).setColor(red, green, blue, 255);
        buffer.addVertex(-extent, y, extent).setColor(red, green, blue, 255);
        buffer.addVertex(extent, y, extent).setColor(red, green, blue, 255);
        buffer.addVertex(extent, y, -extent).setColor(red, green, blue, 255);
        buffers.endBatch(type);
    }

    private static Vec3 skyOf(Identifier dimension) {
        DimensionDef def = ContentDimensions.def(dimension);
        String base = def == null ? dimension.toString() : "minecraft:" + def.base();
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        ServerLevel target = server == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
        if (target != null) {
            float brightness = Mth.clamp(Mth.cos(target.environmentAttributes().getDimensionValue(EnvironmentAttributes.SUN_ANGLE) * Mth.DEG_TO_RAD) * 2.0F + 0.5F, 0.0F, 1.0F);
            return shaded(def != null && def.hasEffects() ? "minecraft:overworld" : base, rgb(def != null && def.fogColor() >= 0 ? def.fogColor() : fogOf(base)), brightness);
        }
        return switch (base) {
            case "minecraft:the_nether" -> new Vec3(0.2D, 0.03D, 0.03D);
            case "minecraft:the_end" -> new Vec3(0.06D, 0.06D, 0.09D);
            default -> new Vec3(0.5D, 0.66D, 1.0D);
        };
    }

    private static Vec3 shaded(String effects, Vec3 color, float brightness) {
        return switch (effects) {
            case "minecraft:the_nether" -> color;
            case "minecraft:the_end" -> color.scale(0.15F);
            default -> color.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
        };
    }

    private static Vec3 rgb(int rgb) { return new Vec3(((rgb >> 16) & 255) / 255.0D, ((rgb >> 8) & 255) / 255.0D, (rgb & 255) / 255.0D); }

    private static int fogOf(String base) {
        return switch (base) {
            case "minecraft:the_nether" -> 0x330808;
            case "minecraft:the_end" -> 0xA080A0;
            default -> 0xC0D8FF;
        };
    }
}
