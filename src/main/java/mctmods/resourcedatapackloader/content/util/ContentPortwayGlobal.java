package mctmods.resourcedatapackloader.content.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ContentPortwayGlobal extends SavedData {
    private static final String NAME = "rdpl_portway_global";
    private static final String STOPS = "stops";
    private static final Factory<ContentPortwayGlobal> FACTORY = new Factory<>(ContentPortwayGlobal::new, (tag, lookup) -> read(tag));
    private final List<PortwayStop> stops = new ArrayList<>();

    public static ContentPortwayGlobal get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static ContentPortwayGlobal read(CompoundTag compound) {
        ContentPortwayGlobal data = new ContentPortwayGlobal();
        ListTag tags = compound.getList(STOPS, Tag.TAG_COMPOUND);
        for (int index = 0; index < tags.size(); index++) {
            PortwayStop stop = PortwayStop.read(tags.getCompound(index));
            if (stop != null) { data.stops.add(stop); }
        }
        return data;
    }

    public List<PortwayStop> stops() { return List.copyOf(stops); }

    public void put(PortwayStop stop) {
        for (int index = 0; index < stops.size(); index++) {
            if (!stops.get(index).same(stop)) { continue; }
            if (stops.get(index).differs(stop)) {
                stops.set(index, stop.keeping(stops.get(index)));
                setDirty();
            }
            return;
        }
        stops.add(stop.fresh());
        setDirty();
    }

    public void remove(ResourceKey<Level> dimension, BlockPos pos) { remove(Collections.singleton(new PortwayStop.Key(dimension, pos))); }

    public void remove(Set<PortwayStop.Key> lost) {
        if (stops.removeIf(stop -> lost.contains(stop.key()))) { setDirty(); }
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound, @Nonnull HolderLookup.Provider lookup) {
        ListTag tags = new ListTag();
        for (PortwayStop stop : stops) { tags.add(stop.write()); }
        compound.put(STOPS, tags);
        return compound;
    }
}
