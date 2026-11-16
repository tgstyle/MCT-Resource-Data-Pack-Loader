package mctmods.resourcedatapackloader.core.registration;

import mctmods.resourcedatapackloader.common.villages.CityGrid;
import mctmods.resourcedatapackloader.common.villages.ContentPathIntersects;
import mctmods.resourcedatapackloader.common.villages.ContentPaths;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.villages.RecurrentVillages;
import mctmods.resourcedatapackloader.common.villages.VillagePieces;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructures;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.dress.ContentBeardSeat;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentCityMaps;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;

import net.minecraftforge.common.MinecraftForge;

public class StructureSetup {
    public static void villagePieces() {
        RecurrentVillages.register();
        VillagePieces.rails();
    }

    public static void loadMaps() {
        ContentPathIntersects.load();
        ContentStructureMaps.load();
        ContentDungeons.load();
        ContentCityMaps.load();
    }

    public static void terrainListeners() {
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentStructures.class);
    }

    public static void villages() {
        if (ContentVillages.load()) { ContentVillages.register(); }
    }

    public static void pathListeners() {
        MinecraftForge.EVENT_BUS.register(ContentPaths.class);
    }

    public static void cityGrid() {
        MinecraftForge.EVENT_BUS.register(CityGrid.class);
    }

    public static void reloadPlacement() {
        ContentStructurePlacement.reload();
    }

    public static void reloadVillages() {
        ContentVillages.reload();
        ContentBeardSeat.reload();
        ContentCityMaps.reload();
    }
}
