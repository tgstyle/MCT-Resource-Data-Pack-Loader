package mctmods.resourcedatapackloader.util.compat;

public class GsRainWorldProvider extends GcWeatherWorldProvider implements IGsSpace {
    @Override public float getThermalLevelModifier() { return GsBodies.thermal(world, galaxySpace(), super.getThermalLevelModifier()); }
}
