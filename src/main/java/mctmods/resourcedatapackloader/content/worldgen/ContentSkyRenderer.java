package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import java.util.Random;

@SideOnly(Side.CLIENT) final class ContentSkyRenderer extends IRenderHandler {
    private static final ResourceLocation MOON_PHASES = new ResourceLocation("textures/environment/moon_phases.png");
    private final DimensionTraitsDef.Sky sky;
    private int lists = -1;

    ContentSkyRenderer(DimensionTraitsDef.Sky sky) { this.sky = sky; }

    @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) { return; }
        if (lists < 0) { build(); }
        GlStateManager.disableTexture2D();
        Vec3d color = world.getSkyColor(viewer, partialTicks);
        float red = (float) color.x;
        float green = (float) color.y;
        float blue = (float) color.z;
        if (mc.gameSettings.anaglyph) {
            float r = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            float g = (red * 30.0F + green * 70.0F) / 100.0F;
            blue = (red * 30.0F + blue * 70.0F) / 100.0F;
            red = r;
            green = g;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(red, green, blue);
        GlStateManager.callList(lists);
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        sunrise(world, mc, buffer, tessellator, partialTicks);
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        float clear = 1.0F - world.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, clear);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        float turn = world.getCelestialAngle(partialTicks) * 360.0F;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(turn, 1.0F, 0.0F, 0.0F);
        if (sky.sunSize > 0.0F) {
            mc.renderEngine.bindTexture(sky.sunTexture);
            quad(buffer, tessellator, sky.sunSize);
        }
        if (sky.bodies == null) { moon(world, mc, buffer, tessellator); }
        GlStateManager.popMatrix();
        if (sky.bodies != null) {
            for (DimensionTraitsDef.Body body : sky.bodies) {
                GlStateManager.pushMatrix();
                if (body.followsTime) { GlStateManager.rotate(turn, 1.0F, 0.0F, 0.0F); }
                GlStateManager.rotate(body.angle, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(body.tilt, 0.0F, 0.0F, 1.0F);
                mc.renderEngine.bindTexture(body.texture);
                quad(buffer, tessellator, body.size);
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.disableTexture2D();
        float stars = world.getStarBrightness(partialTicks) * clear;
        if (stars > 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(turn, 1.0F, 0.0F, 0.0F);
            GlStateManager.color(stars, stars, stars, stars);
            GlStateManager.callList(lists + 2);
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        double below = viewer.getPositionEyes(partialTicks).y - world.getHorizon();
        if (below < 0.0D) { voidBox(buffer, tessellator, lists, below); }
        if (world.provider.isSkyColored()) { GlStateManager.color(red * 0.2F + 0.04F, green * 0.2F + 0.04F, blue * 0.6F + 0.1F); }
        else { GlStateManager.color(red, green, blue); }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float) (below - 16.0D)), 0.0F);
        GlStateManager.callList(lists + 1);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    private static void sunrise(WorldClient world, Minecraft mc, BufferBuilder buffer, Tessellator tessellator, float partialTicks) {
        float[] glow = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
        if (glow == null) { return; }
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        float red = glow[0];
        float green = glow[1];
        float blue = glow[2];
        if (mc.gameSettings.anaglyph) {
            float r = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            float g = (red * 30.0F + green * 70.0F) / 100.0F;
            blue = (red * 30.0F + blue * 70.0F) / 100.0F;
            red = r;
            green = g;
        }
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 100.0D, 0.0D).color(red, green, blue, glow[3]).endVertex();
        for (int step = 0; step <= 16; ++step) {
            float at = step * ((float) Math.PI * 2.0F) / 16.0F;
            float sin = MathHelper.sin(at);
            float cos = MathHelper.cos(at);
            buffer.pos(sin * 120.0F, cos * 120.0F, -cos * 40.0F * glow[3]).color(glow[0], glow[1], glow[2], 0.0F).endVertex();
        }
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    private static void moon(WorldClient world, Minecraft mc, BufferBuilder buffer, Tessellator tessellator) {
        float size = 20.0F;
        mc.renderEngine.bindTexture(MOON_PHASES);
        int phase = world.getMoonPhase();
        int column = phase % 4;
        int row = phase / 4 % 2;
        float left = column / 4.0F;
        float top = row / 2.0F;
        float right = (column + 1) / 4.0F;
        float bottom = (row + 1) / 2.0F;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-size, -100.0D, size).tex(right, bottom).endVertex();
        buffer.pos(size, -100.0D, size).tex(left, bottom).endVertex();
        buffer.pos(size, -100.0D, -size).tex(left, top).endVertex();
        buffer.pos(-size, -100.0D, -size).tex(right, top).endVertex();
        tessellator.draw();
    }

    private static void quad(BufferBuilder buffer, Tessellator tessellator, float size) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-size, 100.0D, -size).tex(0.0D, 0.0D).endVertex();
        buffer.pos(size, 100.0D, -size).tex(1.0D, 0.0D).endVertex();
        buffer.pos(size, 100.0D, size).tex(1.0D, 1.0D).endVertex();
        buffer.pos(-size, 100.0D, size).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }

    private static void voidBox(BufferBuilder buffer, Tessellator tessellator, int lists, double below) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 12.0F, 0.0F);
        GlStateManager.callList(lists + 1);
        GlStateManager.popMatrix();
        double bottom = -(below + 65.0D);
        double[][] corners = {{-1, bottom, 1}, {1, bottom, 1}, {1, -1, 1}, {-1, -1, 1}, {-1, -1, -1}, {1, -1, -1}, {1, bottom, -1}, {-1, bottom, -1}, {1, -1, -1}, {1, -1, 1}, {1, bottom, 1}, {1, bottom, -1}, {-1, bottom, -1}, {-1, bottom, 1}, {-1, -1, 1}, {-1, -1, -1}, {-1, -1, -1}, {-1, -1, 1}, {1, -1, 1}, {1, -1, -1}};
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (double[] corner : corners) { buffer.pos(corner[0], corner[1], corner[2]).color(0, 0, 0, 255).endVertex(); }
        tessellator.draw();
    }

    private void build() {
        lists = GLAllocation.generateDisplayLists(3);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.glNewList(lists, GL11.GL_COMPILE);
        plane(buffer, 16.0F, false);
        tessellator.draw();
        GlStateManager.glEndList();
        GlStateManager.glNewList(lists + 1, GL11.GL_COMPILE);
        plane(buffer, -16.0F, true);
        tessellator.draw();
        GlStateManager.glEndList();
        GlStateManager.pushMatrix();
        GlStateManager.glNewList(lists + 2, GL11.GL_COMPILE);
        stars(buffer);
        tessellator.draw();
        GlStateManager.glEndList();
        GlStateManager.popMatrix();
    }

    private static void plane(BufferBuilder buffer, float height, boolean reverse) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        for (int x = -384; x <= 384; x += 64) {
            for (int z = -384; z <= 384; z += 64) {
                float near = reverse ? x + 64 : x;
                float far = reverse ? x : x + 64;
                buffer.pos(near, height, z).endVertex();
                buffer.pos(far, height, z).endVertex();
                buffer.pos(far, height, z + 64).endVertex();
                buffer.pos(near, height, z + 64).endVertex();
            }
        }
    }

    private void stars(BufferBuilder buffer) {
        Random random = new Random(10842L);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        float spread = sky.starSize * 2.0F / 3.0F;
        for (int star = 0; star < sky.starCount; ++star) {
            double x = random.nextFloat() * 2.0F - 1.0F;
            double y = random.nextFloat() * 2.0F - 1.0F;
            double z = random.nextFloat() * 2.0F - 1.0F;
            double size = sky.starSize + random.nextFloat() * spread;
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
                buffer.pos(x * 100.0D + f * yawSin - d * yawCos, y * 100.0D + e, z * 100.0D + d * yawSin + f * yawCos).endVertex();
            }
        }
    }
}
