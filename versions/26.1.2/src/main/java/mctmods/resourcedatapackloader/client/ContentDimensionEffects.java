package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.CustomCloudsRenderer;
import net.neoforged.neoforge.client.CustomSkyboxRenderer;
import net.neoforged.neoforge.client.CustomWeatherEffectRenderer;
import net.neoforged.neoforge.client.event.RegisterCustomEnvironmentEffectRendererEvent;
import org.joml.Matrix4fc;
import javax.annotation.Nonnull;

public final class ContentDimensionEffects implements CustomSkyboxRenderer, CustomCloudsRenderer, CustomWeatherEffectRenderer {
    public static final Identifier HIDDEN = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "hidden");

    private ContentDimensionEffects() {}

    public static void register(RegisterCustomEnvironmentEffectRendererEvent event) {
        ContentDimensionEffects hidden = new ContentDimensionEffects();
        event.registerSkyboxRenderer(HIDDEN, hidden);
        event.registerCloudRenderer(HIDDEN, hidden);
        event.registerWeatherEffectRenderer(HIDDEN, hidden);
    }

    @Override public boolean renderSky(@Nonnull LevelRenderState level, @Nonnull SkyRenderState sky, @Nonnull Matrix4fc modelView, @Nonnull Runnable setupFog) { return true; }

    @Override public boolean renderClouds(@Nonnull LevelRenderState level, @Nonnull Vec3 camera, @Nonnull CloudStatus status, int color, float height, int range, @Nonnull Matrix4fc modelView) { return true; }

    @Override public boolean renderSnowAndRain(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull MultiBufferSource buffers, @Nonnull Vec3 camera) { return true; }
}
