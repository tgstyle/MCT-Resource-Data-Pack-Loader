package mctmods.resourcedatapackloader.content.rubic.world.interfaces;


public interface IRubicWorldServer extends IRubicWorld {
    ICubeProviderServer rdpl$getCubeCache();

    void rdpl$unloadOldCubes();
}
