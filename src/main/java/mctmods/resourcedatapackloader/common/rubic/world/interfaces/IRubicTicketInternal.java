package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

import mctmods.resourcedatapackloader.common.util.ticks.ITicket;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

public interface IRubicTicketInternal extends IRubicTicket, ITicket {
    void rdpl$setForcedChunkCubes(ChunkPos location, IntSet yCoords);

    void rdpl$clearForcedChunkCubes(ChunkPos location);

    void rdpl$setAllForcedChunkCubes(Map<ChunkPos, IntSet> cubePosMap);

    void rdpl$capForcedCubes(int cap);

    void setModData(NBTTagCompound modData);

    void setPlayer(String player);

    void setEntityChunkX(int chunkX);

    void rdpl$setEntityChunkY(int cubeY);

    void setEntityChunkZ(int chunkZ);

    int getEntityChunkX();

    int rdpl$getEntityChunkY();

    int getEntityChunkZ();

    @Override default boolean shouldTick() { return true; }
}
