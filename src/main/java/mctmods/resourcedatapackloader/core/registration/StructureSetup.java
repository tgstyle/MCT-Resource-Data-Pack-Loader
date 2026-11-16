package mctmods.resourcedatapackloader.core.registration;

import mctmods.resourcedatapackloader.common.villages.ContentPathIntersects;
import mctmods.resourcedatapackloader.common.villages.ContentPaths;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCity;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.CityBorder;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentCityMaps;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;

import net.minecraftforge.common.MinecraftForge;

public class StructureSetup {
    public static void loadMaps() {
        ContentStructureMaps.load();
        ContentDungeons.load();
        ContentVillages.load();
        ContentPathIntersects.load();
        ContentCityMaps.load();
    }

    public static void pathListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentPaths::onRightClick);
    }

    public static void cityListeners() {
        MinecraftForge.EVENT_BUS.addListener(CityBorder::onServerTick);
    }

    public static void reloadCityMaps() {
        ContentCityMaps.reload();
    }

    public static void beginCity() {
        ContentCity.begin();
    }

    public static void forgetCity() {
        CityBorder.forget();
    }
}
