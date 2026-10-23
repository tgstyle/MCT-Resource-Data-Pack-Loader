package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.client.render.Blends;
import mctmods.resourcedatapackloader.client.render.RingQuads;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.util.BodyRings;

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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentSkyRenderer extends IRenderHandler {
    private static final ResourceLocation MOON_PHASES = new ResourceLocation("textures/environment/moon_phases.png");
    private final DimensionTraitsDef.Sky sky;
    private final ResourceLocation moonTexture;
    private final float moonSize;
    private final ContentStars starField;
    private int lists = -1;
    private boolean floor = true;

    ContentSkyRenderer(DimensionTraitsDef.Sky sky, @Nullable DimensionTraitsDef.Moon moon) {
        this.sky = sky;
        moonTexture = moon == null ? MOON_PHASES : moon.texture;
        moonSize = moon == null ? DimensionTraitsDef.VANILLA_MOON : moon.size;
        starField = new ContentStars(sky.starCount, sky.starSize, occluders(sky, moonTexture, moonSize));
    }

    private static SkyOccluder[] occluders(DimensionTraitsDef.Sky sky, ResourceLocation moonTexture, float moonSize) {
        List<SkyOccluder> bodies = new ArrayList<>();
        if (sky.sunSize > 0.0F) { bodies.add(new SkyOccluder(0.0F, 0.0F, sky.sunSize, false, false, new ResourceLocation[] {sky.sunTexture}, 1, 1)); }
        if (sky.bodies == null) {
            if (moonSize > 0.0F) { bodies.add(new SkyOccluder((float) Math.PI, 0.0F, moonSize, true, false, new ResourceLocation[] {moonTexture}, 4, 2)); }
        }
        else {
            for (DimensionTraitsDef.Body body : sky.bodies) {
                bodies.add(new SkyOccluder((float) Math.toRadians(body.angle), (float) Math.toRadians(body.tilt), body.size, false, !body.followsTime, new ResourceLocation[] {body.texture}, 1, 1));
            }
        }
        return bodies.toArray(new SkyOccluder[0]);
    }

    public ContentSkyRenderer withoutFloor() {
        floor = false;
        return this;
    }

    @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) { return; }
        if (lists < 0) { build(); }
        GlStateManager.disableTexture2D();
        Vec3d color = world.getSkyColor(viewer, partialTicks);
        float[] tint = anaglyph(mc, (float) color.x, (float) color.y, (float) color.z);
        float red = tint[0];
        float green = tint[1];
        float blue = tint[2];
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(red, green, blue);
        GlStateManager.callList(lists);
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Blends.translucent();
        ContentSkyExtras.backdrop(world);
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
            ContentSkyRenderers.sunHaze(world, sky.sunTexture, clear * ContentSkyRenderers.sun(world), sky.sunSize, false);
            mc.renderEngine.bindTexture(sky.sunTexture);
            ContentSkyRenderers.quad(sky.sunSize);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, clear * ContentSkyRenderers.moon(world));
        if (sky.bodies == null && moonSize > 0.0F) { moon(world, mc, buffer, tessellator); }
        GlStateManager.popMatrix();
        if (sky.bodies != null) {
            for (DimensionTraitsDef.Body body : sky.bodies) {
                GlStateManager.pushMatrix();
                if (body.followsTime) { GlStateManager.rotate(turn, 1.0F, 0.0F, 0.0F); }
                GlStateManager.rotate(body.angle, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(body.tilt, 0.0F, 0.0F, 1.0F);
                BodyRings.Ring ring = body.ringBody == null ? null : BodyRings.find(body.ringBody);
                if (ring != null) {
                    Blends.translucent();
                    RingQuads.sky(ring, body.size, false);
                }
                mc.renderEngine.bindTexture(body.texture);
                ContentSkyRenderers.quad(body.size);
                if (ring != null) {
                    RingQuads.sky(ring, body.size, true);
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.disableTexture2D();
        float stars = world.getStarBrightness(partialTicks) * clear;
        if (stars > 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(turn, 1.0F, 0.0F, 0.0F);
            starField.draw(world, stars, partialTicks, (float) Math.toRadians(turn));
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        ContentSkyExtras.overlay(world, partialTicks);
        GlStateManager.disableTexture2D();
        if (floor) {
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double below = viewer.getPositionEyes(partialTicks).y - world.getHorizon();
            if (below < 0.0D) { voidBox(buffer, tessellator, lists, below); }
            if (world.provider.isSkyColored()) { GlStateManager.color(red * 0.2F + 0.04F, green * 0.2F + 0.04F, blue * 0.6F + 0.1F); }
            else { GlStateManager.color(red, green, blue); }
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float) (below - 16.0D)), 0.0F);
            GlStateManager.callList(lists + 1);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    private static float[] anaglyph(Minecraft mc, float red, float green, float blue) {
        if (!mc.gameSettings.anaglyph) { return new float[] { red, green, blue }; }
        return new float[] { (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F, (red * 30.0F + green * 70.0F) / 100.0F, (red * 30.0F + blue * 70.0F) / 100.0F };
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
        float[] tint = anaglyph(mc, glow[0], glow[1], glow[2]);
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 100.0D, 0.0D).color(tint[0], tint[1], tint[2], glow[3]).endVertex();
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

    private void moon(WorldClient world, Minecraft mc, BufferBuilder buffer, Tessellator tessellator) {
        float size = moonSize;
        mc.renderEngine.bindTexture(moonTexture);
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

    void release() {
        starField.release();
        if (lists < 0) { return; }
        GLAllocation.deleteDisplayLists(lists, 2);
        lists = -1;
    }

    private void build() {
        lists = GLAllocation.generateDisplayLists(2);
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
}
