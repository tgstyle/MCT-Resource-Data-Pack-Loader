package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SavedTypes {
    private SavedTypes() {}

    public static <T extends SavedData> SavedDataType<T> of(String name, Supplier<T> factory, Function<CompoundTag, T> read, Function<T, CompoundTag> write) {
        return new SavedDataType<>(ResourceDataPackLoader.id(name), factory, CompoundTag.CODEC.xmap(read, write));
    }
}
