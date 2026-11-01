package mctmods.resourcedatapackloader.client.renderer.sky;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import javax.annotation.Nonnull;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.oit.OitRenderPassProvider;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.CustomCloudsRenderer;
import net.neoforged.neoforge.client.CustomSkyboxRenderer;
import net.neoforged.neoforge.client.CustomWeatherEffectRenderer;
import net.neoforged.neoforge.client.event.RegisterCustomEnvironmentEffectRendererEvent;
import org.joml.Matrix4fc;

public final class ContentDimensionEffects implements CustomSkyboxRenderer, CustomCloudsRenderer, CustomWeatherEffectRenderer {
    public static final Identifier HIDDEN = ResourceDataPackLoader.id("hidden");

    private ContentDimensionEffects() {}

    public static boolean cameraAboveCeiling() {
        Entity view = Minecraft.getInstance().getCameraEntity();
        return view != null && ContentWeather.above(view.level(), view.getBlockY());
    }

    public static void register(RegisterCustomEnvironmentEffectRendererEvent event) {
        ContentDimensionEffects hidden = new ContentDimensionEffects();
        event.registerSkyboxRenderer(HIDDEN, hidden);
        event.registerCloudRenderer(HIDDEN, hidden);
        event.registerWeatherEffectRenderer(HIDDEN, hidden);
        for (DimensionDef def : ContentDimensions.all()) {
            if (def.traits().rain() != null) { event.registerWeatherEffectRenderer(def.key(), new ContentWeatherRenderer(def.traits().rain())); }
        }
    }

    @Override public boolean renderSky(@Nonnull LevelRenderState level, @Nonnull SkyRenderState sky, @Nonnull Matrix4fc modelView, @Nonnull GpuBufferSlice skyFog) { return true; }

    @Override public boolean renderClouds(@Nonnull LevelRenderState level, @Nonnull CloudStatus status, @Nonnull Matrix4fc modelView, @Nonnull RenderPass pass) { return true; }

    @Override public boolean renderCloudsOit(@Nonnull LevelRenderState level, @Nonnull CloudStatus status, @Nonnull Matrix4fc modelView, @Nonnull OitStage stage, @Nonnull GpuTextureView depth, @Nonnull OitRenderPassProvider.Parameters params) { return true; }

    @Override public boolean renderSnowAndRain(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull Vec3 camera, @Nonnull RenderPass pass) { return true; }

    @Override public boolean renderSnowAndRainOit(@Nonnull LevelRenderState level, @Nonnull WeatherRenderState weather, @Nonnull Vec3 camera, @Nonnull OitStage stage, @Nonnull RenderPass pass) { return true; }
}
