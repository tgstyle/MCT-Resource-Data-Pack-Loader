package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PortwayGlobal extends WorldSavedData {
    private static final String NAME = "rdpl_portway_global";
    private static final String STOPS = "stops";
    private final List<PortwayStop> stops = new ArrayList<>();

    public PortwayGlobal(String name) { super(name); }

    @Nullable private static PortwayGlobal get(@Nullable MinecraftServer server) {
        if (server == null) { return null; }
        MapStorage storage = server.getWorld(0).getMapStorage();
        return storage == null ? null : SavedData.get(storage, PortwayGlobal.class, NAME, PortwayGlobal::new);
    }

    public static List<PortwayStop> list(@Nullable MinecraftServer server) {
        PortwayGlobal data = get(server);
        return data == null ? new ArrayList<>() : new ArrayList<>(data.stops);
    }

    public static void upsert(@Nullable MinecraftServer server, PortwayStop stop) {
        PortwayGlobal data = get(server);
        if (data == null) { return; }
        int at = data.stops.indexOf(stop);
        if (at >= 0 && data.stops.get(at).name.equals(stop.name) && data.stops.get(at).block.equals(stop.block)) { return; }
        if (at >= 0) { data.stops.set(at, stop.keeping(data.stops.get(at))); }
        else { data.stops.add(stop.fresh()); }
        data.markDirty();
    }

    public static void remove(@Nullable MinecraftServer server, int dimension, BlockPos pos) { remove(server, Collections.singleton(new PortwayStop(dimension, pos))); }

    public static void remove(@Nullable MinecraftServer server, Set<PortwayStop> lost) {
        PortwayGlobal data = get(server);
        if (data != null && data.stops.removeIf(lost::contains)) { data.markDirty(); }
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        stops.clear();
        NBTTagList tags = compound.getTagList(STOPS, Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < tags.tagCount(); index++) { stops.add(PortwayStop.read(tags.getCompoundTagAt(index))); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList tags = new NBTTagList();
        for (PortwayStop stop : stops) { tags.appendTag(stop.write()); }
        compound.setTag(STOPS, tags);
        return compound;
    }
}
