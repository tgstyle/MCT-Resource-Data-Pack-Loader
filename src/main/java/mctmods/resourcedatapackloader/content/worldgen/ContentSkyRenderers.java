package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.Colors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT) public final class ContentSkyRenderers {
    private static final IRenderHandler NOTHING = new IRenderHandler() {
        @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {}
    };
    private static final float AURA_ALPHA = 0.5F;
    private static final float AURA_REACH = 1.5F;
    private static final int AURA_SEGMENTS = 32;
    private static final Map<DimensionDef, ContentSkyRenderer> PACK_SKIES = new HashMap<>();
    private static final Map<DimensionDef, ContentWeatherRenderer> PACK_WEATHER = new HashMap<>();
    private static final Map<DimensionDef, ContentCloudRenderer> PACK_CLOUDS = new HashMap<>();
    private static final Map<WorldProvider, IRenderHandler[]> OWN = new WeakHashMap<>();

    private ContentSkyRenderers() {}

    public static void listen() {
        IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
        if (resources instanceof IReloadableResourceManager) { ((IReloadableResourceManager) resources).registerReloadListener(reloaded -> SkyOccluder.invalidate()); }
    }

    public static void reload() {
        List<ContentSkyRenderer> retired = new ArrayList<>(PACK_SKIES.values());
        PACK_SKIES.clear();
        PACK_WEATHER.clear();
        PACK_CLOUDS.clear();
        if (retired.isEmpty()) { return; }
        Minecraft.getMinecraft().addScheduledTask(() -> retired.forEach(ContentSkyRenderer::release));
    }

    private static boolean drawsWholeSky(DimensionDef def) {
        if (ContentDimensions.overlay(def.id) == null) { return true; }
        return def.gives("sky.bodies") || def.gives("sky.stars") || def.gives("sky.sun.size") || def.gives("sky.moon.size");
    }

    private static boolean fogHidesEndSky(WorldProvider provider, DimensionDef def) { return def.showFog && provider.getDimensionType() == DimensionType.THE_END; }

    public static float sun(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        return look == null ? 1.0F : look.sunBrightness;
    }

    public static void sunHaze(World world, ResourceLocation texture, float alpha, float size, boolean rawColor) {
        float[] aura = ContentWindowLook.aura(world);
        float[] tint = ContentWindowLook.sunTint(world);
        boolean hazed = aura != null && aura[3] > 0.0F;
        if (hazed || tint != null) {
            int sourceRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
            int destRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
            int sourceAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
            int destAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
            if (hazed) {
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                aura(aura, alpha * aura[3] * AURA_ALPHA, size);
            }
            if (tint != null) {
                GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
                color(alpha, alpha, alpha, 1.0F, rawColor);
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
                quad(size);
            }
            GL14.glBlendFuncSeparate(sourceRgb, destRgb, sourceAlpha, destAlpha);
        }
        if (tint == null) { color(1.0F, 1.0F, 1.0F, alpha, rawColor); }
        else { color(tint[0], tint[1], tint[2], alpha, rawColor); }
    }

    private static void color(float red, float green, float blue, float alpha, boolean raw) {
        if (raw) { GL11.glColor4f(red, green, blue, alpha); }
        else { GlStateManager.color(red, green, blue, alpha); }
    }

    static void quad(float size) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-size, 100.0D, -size).tex(0.0D, 0.0D).endVertex();
        buffer.pos(size, 100.0D, -size).tex(1.0D, 0.0D).endVertex();
        buffer.pos(size, 100.0D, size).tex(1.0D, 1.0D).endVertex();
        buffer.pos(-size, 100.0D, size).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }

    private static void aura(float[] aura, float alpha, float size) {
        boolean alphaTest = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        if (alphaTest) { GL11.glDisable(GL11.GL_ALPHA_TEST); }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        float wide = size * AURA_REACH;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 100.0D, 0.0D).color(aura[0], aura[1], aura[2], alpha).endVertex();
        for (int step = 0; step <= AURA_SEGMENTS; step++) {
            float at = step * (float) Math.PI * 2.0F / AURA_SEGMENTS;
            buffer.pos(MathHelper.cos(at) * wide, 100.0D, MathHelper.sin(at) * wide).color(aura[0], aura[1], aura[2], 0.0F).endVertex();
        }
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (alphaTest) { GL11.glEnable(GL11.GL_ALPHA_TEST); }
    }

    public static float moon(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        return look == null ? 1.0F : look.moonBrightness;
    }

    public static BufferBuilder bolt(BufferBuilder buffer, float red, float green, float blue, float alpha) {
        SkyLookDef look = ContentFogSampler.look(Minecraft.getMinecraft().world);
        if (look == null || look.lightningColor == SkyLookDef.UNSET) { return buffer.color(red, green, blue, alpha); }
        return buffer.color(Colors.red(look.lightningColor) / 255.0F, Colors.green(look.lightningColor) / 255.0F, Colors.blue(look.lightningColor) / 255.0F, alpha);
    }

    public static void apply(WorldProvider provider, DimensionDef def) {
        IRenderHandler[] own = OWN.computeIfAbsent(provider, held -> new IRenderHandler[] {held.getSkyRenderer(), held.getCloudRenderer(), held.getWeatherRenderer()});
        provider.setSkyRenderer(own[0]);
        provider.setCloudRenderer(own[1]);
        provider.setWeatherRenderer(own[2]);
        if (!def.renderSky || fogHidesEndSky(provider, def)) { provider.setSkyRenderer(NOTHING); }
        else if (def.traits.sky != null && drawsWholeSky(def)) { provider.setSkyRenderer(PACK_SKIES.computeIfAbsent(def, held -> new ContentSkyRenderer(held.traits.sky, held.traits.moon))); }
        if (!def.renderClouds) { provider.setCloudRenderer(NOTHING); }
        else if (def.look != null && def.look.movesClouds()) { provider.setCloudRenderer(PACK_CLOUDS.computeIfAbsent(def, held -> new ContentCloudRenderer(held.look))); }
        if (!def.renderWeather) { provider.setWeatherRenderer(NOTHING); }
        else if (def.traits.rain != null) { provider.setWeatherRenderer(PACK_WEATHER.computeIfAbsent(def, held -> new ContentWeatherRenderer(held.traits.rain))); }
    }
}
