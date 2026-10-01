package mctmods.resourcedatapackloader.util.compat;

import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Map;

@SideOnly(Side.CLIENT) public final class GcStationLabels {
    private static final String UNNAMED = "Station: ";

    private GcStationLabels() {}

    public static void apply(Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> stations) {
        for (GcStations.Station held : GcStations.stations()) {
            Map<String, GuiCelestialSelection.StationDataGUI> built = stations.get(held.home.getDimensionID());
            if (built == null || held.def.galacticraft == null || !held.def.galacticraft.showName) { continue; }
            String name = held.body.getTranslatedName();
            for (Map.Entry<String, GuiCelestialSelection.StationDataGUI> entry : built.entrySet()) {
                if ((UNNAMED + entry.getKey()).equalsIgnoreCase(entry.getValue().getStationName())) { entry.getValue().setStationName(name); }
            }
        }
    }
}
