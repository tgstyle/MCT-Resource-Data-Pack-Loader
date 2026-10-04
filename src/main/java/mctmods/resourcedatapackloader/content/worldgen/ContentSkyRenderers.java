package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT) public final class ContentSkyRenderers {
    private static final IRenderHandler NOTHING = new IRenderHandler() {
        @Override public void render(float partialTicks, WorldClient world, Minecraft mc) {}
    };
    private static final Map<DimensionDef, ContentSkyRenderer> PACK_SKIES = new HashMap<>();
    private static final Map<DimensionDef, ContentWeatherRenderer> PACK_WEATHER = new HashMap<>();
    private static final Map<DimensionDef, ContentCloudRenderer> PACK_CLOUDS = new HashMap<>();

    private ContentSkyRenderers() {}

    public static float sun(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        return look == null ? 1.0F : look.sunBrightness;
    }

    public static float moon(World world) {
        SkyLookDef look = ContentFogSampler.look(world);
        return look == null ? 1.0F : look.moonBrightness;
    }

    public static void apply(WorldProvider provider, DimensionDef def) {
        if (!def.renderSky) { provider.setSkyRenderer(NOTHING); }
        else if (def.traits.sky != null) { provider.setSkyRenderer(PACK_SKIES.computeIfAbsent(def, held -> new ContentSkyRenderer(held.traits.sky))); }
        if (!def.renderClouds) { provider.setCloudRenderer(NOTHING); }
        else if (def.look != null && def.look.movesClouds() && provider instanceof ContentWorldProvider) { provider.setCloudRenderer(PACK_CLOUDS.computeIfAbsent(def, held -> new ContentCloudRenderer(held.look))); }
        if (!def.renderWeather) { provider.setWeatherRenderer(NOTHING); }
        else if (def.traits.rain != null) { provider.setWeatherRenderer(PACK_WEATHER.computeIfAbsent(def, held -> new ContentWeatherRenderer(held.traits.rain))); }
    }
}
