package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.util.compat.interfaces.IGsSpace;

public class GsWorldProvider extends GcWorldProvider implements IGsSpace {
    @Override public float getThermalLevelModifier() { return GsBodies.thermal(world, galaxySpace(), super.getThermalLevelModifier()); }
}
