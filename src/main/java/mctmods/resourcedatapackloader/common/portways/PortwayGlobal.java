package mctmods.resourcedatapackloader.common.portways;

import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class PortwayGlobal extends WorldSavedData {
    private static final String NAME = "rdpl_portway_global";
    private static final String STOPS = "stops";
    private final List<PortwayStop> stops = new ArrayList<>();

    public PortwayGlobal(String name) { super(name); }

    @Nullable private static PortwayGlobal get(@Nullable MinecraftServer server) {
        return SavedData.overworld(server, PortwayGlobal.class, NAME, PortwayGlobal::new);
    }

    public static List<PortwayStop> list(@Nullable MinecraftServer server) {
        PortwayGlobal data = get(server);
        return data == null ? new ArrayList<>() : new ArrayList<>(data.stops);
    }

    public static boolean contains(@Nullable MinecraftServer server, PortwayStop stop) {
        PortwayGlobal data = get(server);
        return data != null && data.stops.contains(stop);
    }

    public static void upsert(@Nullable MinecraftServer server, PortwayStop stop) {
        PortwayGlobal data = get(server);
        if (data == null) { return; }
        if (data.stops.contains(stop)) {
            if (stop.refreshIn(data.stops)) { data.markDirty(); }
            return;
        }
        data.stops.add(stop.fresh());
        data.markDirty();
    }

    public static void remove(@Nullable MinecraftServer server, int dimension, BlockPos pos) { remove(server, Collections.singleton(new PortwayStop(dimension, pos))); }

    public static void remove(@Nullable MinecraftServer server, Set<PortwayStop> lost) {
        PortwayGlobal data = get(server);
        if (data != null && data.stops.removeIf(lost::contains)) { data.markDirty(); }
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        stops.clear();
        stops.addAll(PortwayStop.readAll(compound.getTagList(STOPS, Constants.NBT.TAG_COMPOUND)));
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag(STOPS, PortwayStop.writeAll(stops));
        return compound;
    }
}
