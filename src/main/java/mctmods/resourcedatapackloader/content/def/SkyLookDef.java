package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record SkyLookDef(boolean sampleFog, float fogDensity, float fogGroundWeight, int lightSkyColor, int lightBlockColor, float skyFactor, float cloudSpeed, List<CloudLayer> cloudLayers, float sunBrightness, float moonBrightness, @Nullable Heat heat, int snowColor, int waterFogColor, int lavaFogColor, int starColor, float starTwinkle, int lightningColor, @Nullable Skybox skybox, @Nullable Aurora aurora, boolean rainbow) {
    public static final int UNSET = -1;

    public boolean tintsStars() { return starColor != UNSET || starTwinkle > 0.0F; }

    public boolean tintsLight() { return lightSkyColor != UNSET || lightBlockColor != UNSET || skyFactor < 1.0F; }

    public boolean movesClouds() { return cloudSpeed != 1.0F || !cloudLayers.isEmpty(); }

    public record CloudLayer(float height, float speed, int color) {}

    public record Skybox(@Nullable ResourceLocation panorama, List<ResourceLocation> faces) {}

    public record Aurora(int color, int topColor) {}

    public record Heat(float strength, float minTemperature, boolean dayOnly, boolean followsWorld, float startDistance) {}
}
