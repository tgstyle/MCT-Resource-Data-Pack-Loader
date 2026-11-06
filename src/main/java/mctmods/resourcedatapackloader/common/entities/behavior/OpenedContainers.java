package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.util.world.SavedData;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public final class OpenedContainers extends WorldSavedData {
    private static final String NAME = "rdpl_opened";
    private static final String OPENED = "Opened";
    private final Set<Long> opened = new HashSet<>();

    public OpenedContainers(String name) { super(name); }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        opened.clear();
        for (long packed : Longs.fromInts(nbt.getIntArray(OPENED))) { opened.add(packed); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setIntArray(OPENED, Longs.toInts(opened.stream().mapToLong(Long::longValue).toArray()));
        return nbt;
    }

    private static OpenedContainers of(World world) { return SavedData.get(world.getPerWorldStorage(), OpenedContainers.class, NAME, OpenedContainers::new); }

    public static void mark(World world, BlockPos pos) {
        OpenedContainers held = of(world);
        if (held.opened.add(pos.toLong())) { held.markDirty(); }
    }

    public static boolean has(World world, BlockPos pos) { return of(world).opened.contains(pos.toLong()); }

    public static void forget(World world, BlockPos pos) {
        OpenedContainers held = of(world);
        if (held.opened.remove(pos.toLong())) { held.markDirty(); }
    }
}
