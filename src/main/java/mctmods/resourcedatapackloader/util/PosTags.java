package mctmods.resourcedatapackloader.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public final class PosTags {
    private PosTags() {}

    public static CompoundTag write(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }

    public static BlockPos read(CompoundTag tag) { return new BlockPos(tag.getIntOr("X", 0), tag.getIntOr("Y", 0), tag.getIntOr("Z", 0)); }
}
