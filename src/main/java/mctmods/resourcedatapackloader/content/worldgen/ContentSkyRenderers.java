package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.Colors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT) public final class ContentSkyRenderers {
    private static final IRenderHandler NOTHING = new IRenderHandler() {
        @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {}
    };
    private static final Map<DimensionDef, ContentSkyRenderer> PACK_SKIES = new HashMap<>();
    private static final Map<DimensionDef, ContentWeatherRenderer> PACK_WEATHER = new HashMap<>();
    private static final Map<DimensionDef, ContentCloudRenderer> PACK_CLOUDS = new HashMap<>();

    private ContentSkyRenderers() {}

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
        if (!def.renderSky || fogHidesEndSky(provider, def)) { provider.setSkyRenderer(NOTHING); }
        else if (def.traits.sky != null && drawsWholeSky(def)) { provider.setSkyRenderer(PACK_SKIES.computeIfAbsent(def, held -> new ContentSkyRenderer(held.traits.sky, held.traits.moon))); }
        if (!def.renderClouds) { provider.setCloudRenderer(NOTHING); }
        else if (def.look != null && def.look.movesClouds()) { provider.setCloudRenderer(PACK_CLOUDS.computeIfAbsent(def, held -> new ContentCloudRenderer(held.look))); }
        if (!def.renderWeather) { provider.setWeatherRenderer(NOTHING); }
        else if (def.traits.rain != null) { provider.setWeatherRenderer(PACK_WEATHER.computeIfAbsent(def, held -> new ContentWeatherRenderer(held.traits.rain))); }
    }
}
