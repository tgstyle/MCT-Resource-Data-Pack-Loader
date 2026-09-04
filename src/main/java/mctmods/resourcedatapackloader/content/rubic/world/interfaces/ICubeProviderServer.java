package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import javax.annotation.Nullable;

public interface ICubeProviderServer extends ICubeProvider {

    @Nullable ICube getCube(int cubeX, int cubeY, int cubeZ, Requirement req);

    @Nullable ICube getCubeNow(int cubeX, int cubeY, int cubeZ, Requirement req);

    enum Requirement {
        GET_CACHED,
        LOAD,
        GENERATE,
        POPULATE,
        LIGHT
    }
}
