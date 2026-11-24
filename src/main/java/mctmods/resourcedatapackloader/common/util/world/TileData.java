package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public final class TileData {
    private TileData() {}

    public static NBTTagCompound at(NBTTagCompound saved, BlockPos pos) {
        NBTTagCompound copy = saved.copy();
        copy.setInteger("x", pos.getX());
        copy.setInteger("y", pos.getY());
        copy.setInteger("z", pos.getZ());
        return copy;
    }
}
