package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public record SkyLookDef(boolean sampleFog, float fogDensity, float fogGroundWeight, int lightSkyColor, int lightBlockColor, float skyFactor, float cloudSpeed, List<CloudLayer> cloudLayers, float sunBrightness, float moonBrightness, @Nullable Heat heat, int snowColor, int waterFogColor, int lavaFogColor, int starColor, float starTwinkle, int lightningColor) {
    public static final int UNSET = -1;

    public boolean tintsStars() { return starColor != UNSET || starTwinkle > 0.0F; }

    public record CloudLayer(float height, float speed, int color) {}

    public record Heat(float strength, float minTemperature, boolean dayOnly, boolean followsWorld, float startDistance) {}
}
