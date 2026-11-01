package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

public interface IRubicWorldServer extends IRubicWorld {
    ICubeProviderServer rdpl$getCubeCache();

    void rdpl$unloadOldCubes();
}
