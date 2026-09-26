package mctmods.resourcedatapackloader.content.def;

import javax.annotation.Nullable;

public final class ExtraPlanetsDef {
    public final int pressure;
    public final int radiation;
    public final float temperatureDay;
    public final float temperatureNight;
    @Nullable public final String lander;

    public ExtraPlanetsDef(int pressure, int radiation, float temperatureDay, float temperatureNight, @Nullable String lander) {
        this.pressure = pressure;
        this.radiation = radiation;
        this.temperatureDay = temperatureDay;
        this.temperatureNight = temperatureNight;
        this.lander = lander;
    }
}
