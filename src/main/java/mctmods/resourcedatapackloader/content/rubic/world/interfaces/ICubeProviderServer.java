package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import net.minecraft.world.chunk.Chunk;
import javax.annotation.Nullable;

public interface ICubeProviderServer extends ICubeProvider {
    @Nullable Chunk getColumn(int columnX, int columnZ, Requirement req);

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
