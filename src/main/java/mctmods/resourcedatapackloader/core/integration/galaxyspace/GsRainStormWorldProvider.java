package mctmods.resourcedatapackloader.core.integration.galaxyspace;

import asmodeuscore.api.dimension.IProviderWeather;
import asmodeuscore.core.astronomy.WeatherData;
import javax.annotation.Nullable;

public class GsRainStormWorldProvider extends GsRainWorldProvider implements IProviderWeather {
    @Override @Nullable public WeatherData getWeather() { return GsBodies.weather(galaxySpace()); }
}
