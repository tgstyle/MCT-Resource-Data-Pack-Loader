package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class SeamSkyRenderer {
    private static final double NEAR = 32.0D;

    private SeamSkyRenderer() {}

    public static void render(WorldClient world, Minecraft mc, float partialTicks) {
        if (world == null) { return; }
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) { return; }
        int dimension = world.provider.getDimension();
        Integer under = ContentSeams.below(dimension);
        Integer over = ContentSeams.above(dimension);
        if (under == null && over == null) { return; }
        int floor = GenHeights.floor(world, 0);
        double eyeY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks + viewer.getEyeHeight();
        if (under != null) { plane(mc, under, floor - 1 - eyeY, partialTicks); }
        if (over != null) { plane(mc, over, RubicWorldControl.generatedCeiling(world) + 1 - eyeY, partialTicks); }
    }

    private static boolean near(double height) { return Math.abs(height) <= NEAR; }

    private static void plane(Minecraft mc, int dimension, double height, float partialTicks) {
        if (!near(height)) { return; }
        Vec3d tint = skyOf(dimension, partialTicks);
        double extent = Math.max(64, mc.gameSettings.renderDistanceChunks * 16);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableFog();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.color((float) tint.x, (float) tint.y, (float) tint.z, 1.0F);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(-extent, height, -extent).endVertex();
        buffer.pos(-extent, height, extent).endVertex();
        buffer.pos(extent, height, extent).endVertex();
        buffer.pos(extent, height, -extent).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Nullable private static WorldServer loaded(int dimension) { return DimensionManager.getWorld(dimension); }

    private static Vec3d skyOf(int dimension, float partialTicks) {
        WorldServer target = loaded(dimension);
        if (target != null) { return target.provider.getFogColor(target.getCelestialAngle(partialTicks), partialTicks); }
        DimensionType type = typeOf(dimension);
        if (type == DimensionType.NETHER) { return new Vec3d(0.2D, 0.03D, 0.03D); }
        if (type == DimensionType.THE_END) { return new Vec3d(0.06D, 0.06D, 0.09D); }
        return new Vec3d(0.5D, 0.66D, 1.0D);
    }

    @Nullable private static DimensionType typeOf(int dimension) {
        try { return DimensionManager.getProviderType(dimension); }
        catch (IllegalArgumentException unknown) { return null; }
    }
}
