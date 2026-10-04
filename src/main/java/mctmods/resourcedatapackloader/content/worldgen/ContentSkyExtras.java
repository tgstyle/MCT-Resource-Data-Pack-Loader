package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT) public final class ContentSkyExtras {
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

    public static void backdrop(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        if (look == null || look.skybox == null) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (look.skybox.panorama != null) {
            mc.renderEngine.bindTexture(look.skybox.panorama);
            panorama(buffer, tessellator);
        }
        else {
            for (int face = 0; face < FACES.length; ++face) {
                mc.renderEngine.bindTexture(look.skybox.faces.get(face));
                face(buffer, tessellator, FACES[face]);
            }
        }
        GlStateManager.enableCull();
        GlStateManager.disableTexture2D();
    }

    public static void overlay(World world, float partialTicks) {
        SkyLookDef look = ContentFogSampler.look(world);
        if (look == null || look.aurora == null && !look.rainbow) { return; }
        float angle = world.getCelestialAngle(partialTicks);
        float clear = 1.0F - world.getRainStrength(partialTicks);
        float glow = look.aurora == null ? 0.0F : night(angle) * clear;
        float bow = look.rainbow ? rainbow(world, partialTicks) * MathHelper.clamp(MathHelper.cos(angle * (float) Math.PI * 2.0F) * 4.0F, 0.0F, 1.0F) : 0.0F;
        if (glow <= 0.0F && bow <= 0.0F) { return; }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        if (glow > 0.0F) { aurora(buffer, tessellator, look.aurora, glow, world.getTotalWorldTime() + partialTicks); }
        if (bow > 0.0F) { bow(buffer, tessellator, angle * (float) Math.PI * 2.0F, bow); }
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
    }

    private static float night(float angle) {
        float dark = MathHelper.clamp(1.0F - (MathHelper.cos(angle * (float) Math.PI * 2.0F) * 2.0F + 0.25F), 0.0F, 1.0F);
        return dark * dark;
    }

    private static float rainbow(World world, float partialTicks) {
        int id = System.identityHashCode(world);
        if (id != wetWorld) {
            wetWorld = id;
            wetAt = Long.MIN_VALUE;
        }
        float now = world.getRainStrength(1.0F);
        float before = world.getRainStrength(0.0F);
        long tick = world.getTotalWorldTime();
        if (now > before) {
            if (now > 0.2F) { wetAt = tick; }
            return 0.0F;
        }
        if (now >= 0.2F && now == before) { wetAt = tick; }
        if (wetAt == Long.MIN_VALUE || tick - wetAt > RAINBOW_TICKS) { return 0.0F; }
        return Math.min(1.0F, (RAINBOW_TICKS - (tick - wetAt)) / RAINBOW_FADE) * (1.0F - world.getRainStrength(partialTicks));
    }

    private static void face(BufferBuilder buffer, Tessellator tessellator, float[][] corners) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(corners[0][0] * SIZE, corners[0][1] * SIZE, corners[0][2] * SIZE).tex(0.0D, 0.0D).endVertex();
        buffer.pos(corners[1][0] * SIZE, corners[1][1] * SIZE, corners[1][2] * SIZE).tex(1.0D, 0.0D).endVertex();
        buffer.pos(corners[2][0] * SIZE, corners[2][1] * SIZE, corners[2][2] * SIZE).tex(1.0D, 1.0D).endVertex();
        buffer.pos(corners[3][0] * SIZE, corners[3][1] * SIZE, corners[3][2] * SIZE).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }

    private static void panorama(BufferBuilder buffer, Tessellator tessellator) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        for (int row = 0; row < LATITUDES; ++row) {
            float top = (float) row / LATITUDES;
            float bottom = (float) (row + 1) / LATITUDES;
            for (int column = 0; column < LONGITUDES; ++column) {
                float left = (float) column / LONGITUDES;
                float right = (float) (column + 1) / LONGITUDES;
                sphere(buffer, left, top);
                sphere(buffer, right, top);
                sphere(buffer, right, bottom);
                sphere(buffer, left, bottom);
            }
        }
        tessellator.draw();
    }

    private static void sphere(BufferBuilder buffer, float u, float v) {
        float around = u * (float) Math.PI * 2.0F;
        float down = v * (float) Math.PI;
        float ring = MathHelper.sin(down) * SIZE;
        buffer.pos(MathHelper.sin(around) * ring, MathHelper.cos(down) * SIZE, -MathHelper.cos(around) * ring).tex(u, v).endVertex();
    }

    private static void aurora(BufferBuilder buffer, Tessellator tessellator, SkyLookDef.Aurora aurora, float strength, float time) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int band = 0; band < 2; ++band) {
            for (int step = 0; step < AURORA_STEPS; ++step) {
                curtain(buffer, aurora, strength, time, band, step, false);
                curtain(buffer, aurora, strength, time, band, step + 1, false);
                curtain(buffer, aurora, strength, time, band, step + 1, true);
                curtain(buffer, aurora, strength, time, band, step, true);
            }
        }
        tessellator.draw();
    }

    private static void curtain(BufferBuilder buffer, SkyLookDef.Aurora aurora, float strength, float time, int band, int step, boolean top) {
        float along = (float) step / AURORA_STEPS;
        float around = (float) Math.toRadians(-70.0F + 140.0F * along + 4.0F * MathHelper.sin(time * 0.05F + step * 0.35F + band * 2.0F));
        float base = 18.0F + band * 6.0F + 3.0F * MathHelper.sin(time * 0.03F + step * 0.2F + band);
        float height = 18.0F + 10.0F * (0.5F + 0.5F * MathHelper.sin(time * 0.07F + step * 0.5F + band * 1.3F));
        float up = (float) Math.toRadians(top ? base + height : base);
        float flat = MathHelper.cos(up) * SIZE;
        int color = top ? aurora.topColor : aurora.color;
        float alpha = top ? 0.0F : strength * 0.8F * MathHelper.sin(along * (float) Math.PI) * (0.6F + 0.4F * MathHelper.sin(time * 0.11F + step * 0.9F + band * 0.7F));
        buffer.pos(MathHelper.sin(around) * flat, MathHelper.sin(up) * SIZE, -MathHelper.cos(around) * flat).color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha).endVertex();
    }

    private static void bow(BufferBuilder buffer, Tessellator tessellator, float sunAngle, float strength) {
        float[] away = {MathHelper.sin(sunAngle), -MathHelper.cos(sunAngle), 0.0F};
        float[] side = {-MathHelper.cos(sunAngle), -MathHelper.sin(sunAngle), 0.0F};
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int ring = 0; ring < BOW_RADII.length - 1; ++ring) {
            for (int step = 0; step < BOW_STEPS; ++step) {
                arc(buffer, away, side, ring, step, strength);
                arc(buffer, away, side, ring, step + 1, strength);
                arc(buffer, away, side, ring + 1, step + 1, strength);
                arc(buffer, away, side, ring + 1, step, strength);
            }
        }
        tessellator.draw();
    }

    private static void arc(BufferBuilder buffer, float[] away, float[] side, int ring, int step, float strength) {
        float radius = (float) Math.toRadians(BOW_RADII[ring]);
        float turn = step * (float) Math.PI * 2.0F / BOW_STEPS;
        float out = MathHelper.sin(radius);
        float in = MathHelper.cos(radius);
        float across = MathHelper.cos(turn) * out;
        float along = MathHelper.sin(turn) * out;
        int color = BOW_COLORS[ring];
        float alpha = ring == 0 || ring == BOW_RADII.length - 1 ? 0.0F : strength * 0.35F;
        buffer.pos((away[0] * in + side[0] * along) * SIZE, (away[1] * in + side[1] * along) * SIZE, across * SIZE).color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha).endVertex();
    }
}
