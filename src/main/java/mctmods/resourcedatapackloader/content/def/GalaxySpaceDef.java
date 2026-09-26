package mctmods.resourcedatapackloader.content.def;

import javax.annotation.Nullable;

public final class GalaxySpaceDef {
    public final float pressure;
    public final boolean radiation;
    @Nullable public final String bodyClass;
    public final float eccentricityX;
    public final float eccentricityY;
    public final float offsetX;
    public final float offsetY;
    public final boolean freezeBlocks;
    public final float thermalVariation;
    public final double solarWind;
    @Nullable public final String weather;
    public final double weatherFrequency;
    @Nullable public final String starType;
    @Nullable public final String starColor;
    public final float zoneDistance;
    public final float zoneSize;

    public GalaxySpaceDef(float pressure, boolean radiation, @Nullable String bodyClass, float eccentricityX, float eccentricityY, float offsetX, float offsetY, boolean freezeBlocks, float thermalVariation, double solarWind, @Nullable String weather, double weatherFrequency, @Nullable String starType, @Nullable String starColor, float zoneDistance, float zoneSize) {
        this.pressure = pressure;
        this.radiation = radiation;
        this.bodyClass = bodyClass;
        this.eccentricityX = eccentricityX;
        this.eccentricityY = eccentricityY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.freezeBlocks = freezeBlocks;
        this.thermalVariation = thermalVariation;
        this.solarWind = solarWind;
        this.weather = weather;
        this.weatherFrequency = weatherFrequency;
        this.starType = starType;
        this.starColor = starColor;
        this.zoneDistance = zoneDistance;
        this.zoneSize = zoneSize;
    }
}
