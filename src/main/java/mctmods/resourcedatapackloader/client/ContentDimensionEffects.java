package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.RainSplash;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import org.joml.Matrix4f;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentDimensionEffects extends DimensionSpecialEffects {
    @Nullable private final DimensionDef def;
    private final DimensionSpecialEffects base;
    @Nullable private final ContentSkyRenderer sky;
    @Nullable private final RainDef rain;
    @Nullable private final ContentWeatherRenderer weather;

    private ContentDimensionEffects(@Nullable DimensionDef def, DimensionSpecialEffects base, float cloudHeight, SkyType skyType) {
        super(cloudHeight, base.hasGround(), skyType, base.forceBrightLightmap(), base.constantAmbientLight());
        this.def = def;
        this.base = base;
        this.sky = def == null || def.traits().sky() == null ? null : new ContentSkyRenderer(def.traits().sky());
        this.rain = def == null ? null : def.traits().rain();
        this.weather = rain == null ? null : new ContentWeatherRenderer(rain);
    }

    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        for (DimensionDef def : ContentDimensions.all()) {
            if (!def.hasEffects()) { continue; }
            DimensionSpecialEffects base = new OverworldEffects();
            if (def.surfaceWorld()) { event.register(def.key(), new ContentDimensionEffects(def, base, def.cloudHeight() >= 0 ? def.cloudHeight() : base.getCloudHeight(), SkyType.NORMAL)); }
            else { event.register(def.key(), new ContentDimensionEffects(def, base, Float.NaN, SkyType.NONE)); }
        }
    }

    @Override @Nonnull public Vec3 getBrightnessDependentFogColor(@Nonnull Vec3 color, float brightness) {
        return base.getBrightnessDependentFogColor(def == null || def.fogColor() < 0 ? color : Vec3.fromRGB24(def.fogColor()), brightness);
    }

    @Override public boolean isFoggyAt(int x, int z) { return (def != null && def.showFog()) || base.isFoggyAt(x, z); }

    @Override @Nullable public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        if (def != null && !def.sunriseColors()) { return null; }
        return base.getSunriseColor(timeOfDay, partialTicks);
    }

    @Override public boolean renderSky(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull Matrix4f modelViewMatrix, @Nonnull Camera camera, @Nonnull Matrix4f projectionMatrix, boolean isFoggy, @Nonnull Runnable setupFog) {
        if (def != null && !def.renderSky()) { return true; }
        if (sky == null) { return false; }
        sky.render(level, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog);
        return true;
    }

    @Override public boolean renderClouds(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull PoseStack poseStack, double camX, double camY, double camZ, @Nonnull Matrix4f modelViewMatrix, @Nonnull Matrix4f projectionMatrix) {
        return def != null && !def.renderClouds();
    }

    @Override public boolean renderSnowAndRain(@Nonnull ClientLevel level, int ticks, float partialTick, @Nonnull LightTexture lightTexture, double camX, double camY, double camZ) {
        if (def == null) { return false; }
        if (!def.renderWeather()) { return true; }
        if (weather == null) { return false; }
        weather.render(level, ticks, partialTick, lightTexture, camX, camY, camZ);
        return true;
    }

    @Override public boolean tickRain(@Nonnull ClientLevel level, int ticks, @Nonnull Camera camera) {
        if (rain == null) { return false; }
        RainSplash.tick(level, rain, ticks, camera);
        return true;
    }
}
