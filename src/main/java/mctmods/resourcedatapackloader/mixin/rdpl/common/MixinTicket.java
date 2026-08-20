package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicTicketInternal;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mixin(value = ForgeChunkManager.Ticket.class, remap = false) public abstract class MixinTicket implements IRubicTicketInternal {
    @Unique private Map<ChunkPos, IntSet> rdpl$cubePosMap = new HashMap<>();
    @Unique private int rdpl$entityChunkY;

    @Override @Accessor public abstract void setModData(NBTTagCompound modData);
    @Override @Accessor public abstract void setPlayer(String player);
    @Override @Accessor public abstract void setEntityChunkX(int chunkX);
    @Override @Accessor public abstract void setEntityChunkZ(int chunkZ);
    @Override @Accessor public abstract int getEntityChunkX();
    @Override @Accessor public abstract int getEntityChunkZ();

    @Override public int rdpl$getEntityChunkY() { return rdpl$entityChunkY; }
    @Override public void rdpl$setEntityChunkY(int cubeY) { this.rdpl$entityChunkY = cubeY; }

    @Override public void rdpl$setForcedChunkCubes(ChunkPos location, IntSet yCoords) { rdpl$cubePosMap.put(location, yCoords); }

    @Override public void rdpl$clearForcedChunkCubes(ChunkPos location) { rdpl$cubePosMap.remove(location); }

    @Override public Map<ChunkPos, IntSet> rdpl$getAllForcedChunkCubes() { return Collections.unmodifiableMap(rdpl$cubePosMap); }

    @Override public void rdpl$setAllForcedChunkCubes(Map<ChunkPos, IntSet> cubePosMap) { this.rdpl$cubePosMap = cubePosMap; }
}
