package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.interfaces.ITicket;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import java.util.Map;

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
