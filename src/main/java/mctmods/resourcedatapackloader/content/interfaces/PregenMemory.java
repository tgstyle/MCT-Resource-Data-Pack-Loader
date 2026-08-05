package mctmods.resourcedatapackloader.content.interfaces;

import net.minecraft.nbt.NBTTagCompound;

public interface PregenMemory {
    NBTTagCompound rdpl$pregenRun();

    void rdpl$setPregenRun(NBTTagCompound run);

    int rdpl$landMadeTo(int dimension);

    void rdpl$setLandMadeTo(int dimension, int radius);

    int rdpl$landMadeAt(int dimension);

    void rdpl$setLandMadeAt(int dimension, int reached);
}
