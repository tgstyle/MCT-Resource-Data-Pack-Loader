package mctmods.resourcedatapackloader.content.rubic.lighting;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public interface ILightingManager {
    default void doOnBlockSetLightUpdates(Chunk column, int localX, int y1, int y2, int localZ) { updateLightBetween(column, localX, y1, y2, localZ); }

    void updateLightBetween(Chunk column, int localX, int y1, int y2, int localZ);

    default void onSendCubes() { processUpdates(); }

    void onCubeLoad(ICube cube);

    default void onCubeUnload() { processUpdatesOnAccess(); }

    default void onGetLight() { processUpdatesOnAccess(); }

    default void onGetLightSubtracted() { processUpdatesOnAccess(); }

    void onCreateCubeStorage(ICube cube, ExtendedBlockStorage storage);

    default void onTick() { processUpdates(); }

    boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos);

    void processUpdates();

    void processUpdatesOnAccess();

    String getId();

    void writeToNbt(ICube cube, NBTTagCompound lightingInfo);

    void readFromNbt(ICube cube, NBTTagCompound lightingInfo);

    Cube.ICubeLightTrackingInfo createLightData();

    void onHeightUpdate(BlockPos pos);

    void onTrackCubeSurface(ICube cube);

    void doFirstLight(ICube cube);
}
