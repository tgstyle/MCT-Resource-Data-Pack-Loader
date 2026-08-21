package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.content.rubic.world.ClientHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.util.AddressTools;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.ChunkPrimer;

public interface IColumnInternal extends IColumn {
    ChunkPrimer getCompatGenerationPrimer();

    void removeFromStagingHeightmap(ICube cube);

    void addToStagingHeightmap(ICube cube);

    int getTopYWithStaging(int localX, int localZ);

    boolean isRubicColumn();

    default boolean pregenDone() { return false; }

    default void markPregenDone() {}

    default void writeHeightmapDataForClient(PacketBuffer out) {
        for (int i = 0; i < Cube.SIZE * Cube.SIZE; i++) { out.writeInt(getTopYWithStaging(AddressTools.getLocalX(i), AddressTools.getLocalZ(i))); }
    }

    default void loadClientHeightmapData(PacketBuffer in) { ((ClientHeightMap) getOpacityIndex()).loadData(in); }
}
