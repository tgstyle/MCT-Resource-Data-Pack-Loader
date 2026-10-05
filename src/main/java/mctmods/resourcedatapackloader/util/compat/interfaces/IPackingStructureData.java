package mctmods.resourcedatapackloader.util.compat.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import javax.annotation.Nullable;

public interface IPackingStructureData {
    void rdpl$packFarStarts(long[] centers, int keep);

    @Nullable NBTTagCompound rdpl$recall(int chunkX, int chunkZ);

    int rdpl$startCount();

    boolean rdpl$startWithin(int chunkX, int chunkZ, int chunks);
}
