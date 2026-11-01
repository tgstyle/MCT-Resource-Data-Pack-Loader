package mctmods.resourcedatapackloader.core.integration.galaxyspace;

import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcWeatherWorldProvider;

public class GsRainWorldProvider extends GcWeatherWorldProvider implements IGsSpace {
    @Override public float getThermalLevelModifier() { return GsBodies.thermal(world, galaxySpace(), super.getThermalLevelModifier()); }
}
