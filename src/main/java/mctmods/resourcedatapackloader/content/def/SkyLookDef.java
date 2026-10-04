package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class SkyLookDef {
    public static final int UNSET = -1;
    public final boolean sampleFog;
    public final float fogDensity;
    public final float fogGroundWeight;
    public final int lightSkyColor;
    public final int lightBlockColor;
    public final float skyFactor;
    public final float cloudSpeed;
    public final List<CloudLayer> cloudLayers;
    public final float sunBrightness;
    public final float moonBrightness;
    @Nullable public final Heat heat;
    public final int snowColor;
    public final int waterFogColor;
    public final int lavaFogColor;
    public final int starColor;
    public final float starTwinkle;
    public final int lightningColor;
    @Nullable public final Skybox skybox;
    @Nullable public final Aurora aurora;
    public final boolean rainbow;

    public SkyLookDef(boolean sampleFog, float fogDensity, float fogGroundWeight, int lightSkyColor, int lightBlockColor, float skyFactor, float cloudSpeed, List<CloudLayer> cloudLayers, float sunBrightness, float moonBrightness, @Nullable Heat heat, int snowColor, int waterFogColor, int lavaFogColor, int starColor, float starTwinkle, int lightningColor, @Nullable Skybox skybox, @Nullable Aurora aurora, boolean rainbow) {
        this.sampleFog = sampleFog;
        this.fogDensity = fogDensity;
        this.fogGroundWeight = fogGroundWeight;
        this.lightSkyColor = lightSkyColor;
        this.lightBlockColor = lightBlockColor;
        this.skyFactor = skyFactor;
        this.cloudSpeed = cloudSpeed;
        this.cloudLayers = cloudLayers;
        this.sunBrightness = sunBrightness;
        this.moonBrightness = moonBrightness;
        this.heat = heat;
        this.snowColor = snowColor;
        this.waterFogColor = waterFogColor;
        this.lavaFogColor = lavaFogColor;
        this.starColor = starColor;
        this.starTwinkle = starTwinkle;
        this.lightningColor = lightningColor;
        this.skybox = skybox;
        this.aurora = aurora;
        this.rainbow = rainbow;
    }

    public boolean tintsStars() { return starColor != UNSET || starTwinkle > 0.0F; }

    public boolean tintsLight() { return lightSkyColor != UNSET || lightBlockColor != UNSET || skyFactor < 1.0F; }

    public boolean movesClouds() { return cloudSpeed != 1.0F || !cloudLayers.isEmpty(); }

    public static final class CloudLayer {
        public final float height;
        public final float speed;
        public final int color;

        public CloudLayer(float height, float speed, int color) {
            this.height = height;
            this.speed = speed;
            this.color = color;
        }
    }

    public static final class Skybox {
        @Nullable public final ResourceLocation panorama;
        public final List<ResourceLocation> faces;

        public Skybox(@Nullable ResourceLocation panorama, List<ResourceLocation> faces) {
            this.panorama = panorama;
            this.faces = faces;
        }
    }

    public static final class Aurora {
        public final int color;
        public final int topColor;

        public Aurora(int color, int topColor) {
            this.color = color;
            this.topColor = topColor;
        }
    }

    public static final class Heat {
        public final float strength;
        public final float minTemperature;
        public final boolean dayOnly;
        public final boolean followsWorld;
        public final float startDistance;

        public Heat(float strength, float minTemperature, boolean dayOnly, boolean followsWorld, float startDistance) {
            this.strength = strength;
            this.minTemperature = minTemperature;
            this.dayOnly = dayOnly;
            this.followsWorld = followsWorld;
            this.startDistance = startDistance;
        }
    }
}
