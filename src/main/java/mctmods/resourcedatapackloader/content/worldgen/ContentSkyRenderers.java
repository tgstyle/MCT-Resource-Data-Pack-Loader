package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
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

    private ContentSkyRenderers() {}

    public static void apply(WorldProvider provider, DimensionDef def) {
        if (!def.renderSky) { provider.setSkyRenderer(NOTHING); }
        else if (def.traits.sky != null) { provider.setSkyRenderer(PACK_SKIES.computeIfAbsent(def, held -> new ContentSkyRenderer(held.traits.sky))); }
        if (!def.renderClouds) { provider.setCloudRenderer(NOTHING); }
        if (!def.renderWeather) { provider.setWeatherRenderer(NOTHING); }
    }
}
