package mctmods.resourcedatapackloader.core.integration.galaxyspace;

import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcWorldProvider;

public class GsWorldProvider extends GcWorldProvider implements IGsSpace {
    @Override public float getThermalLevelModifier() { return GsBodies.thermal(world, galaxySpace(), super.getThermalLevelModifier()); }
}
