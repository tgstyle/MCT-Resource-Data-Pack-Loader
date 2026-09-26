package mctmods.resourcedatapackloader.util.compat;

public class GsWorldProvider extends GcWorldProvider implements IGsSpace {
    @Override public float getThermalLevelModifier() { return GsBodies.thermal(world, galaxySpace(), super.getThermalLevelModifier()); }
}
