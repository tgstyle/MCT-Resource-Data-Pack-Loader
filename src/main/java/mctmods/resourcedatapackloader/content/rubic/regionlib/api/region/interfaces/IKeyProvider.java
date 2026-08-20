package mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces;


import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;

public interface IKeyProvider {
    int getKeyCount(RegionKey key);

    boolean isValid(RegionKey key);
}
