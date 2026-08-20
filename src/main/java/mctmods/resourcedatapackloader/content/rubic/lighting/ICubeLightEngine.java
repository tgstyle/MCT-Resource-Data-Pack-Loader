package mctmods.resourcedatapackloader.content.rubic.lighting;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public interface ICubeLightEngine {
    void scheduleLightUpdate(EnumSkyBlock lightType, BlockPos pos);

    void processLightUpdates();

    void cubeStorageMade(ICube cube, ExtendedBlockStorage storage);

    void updateBetween(Chunk column, int localX, int y1, int y2, int localZ);

    void cubeLoaded(ICube cube);

    void firstLight(ICube cube);

    String getId();
}
