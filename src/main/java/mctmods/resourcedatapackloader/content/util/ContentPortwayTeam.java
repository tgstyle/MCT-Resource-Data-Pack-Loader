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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ContentPortwayTeam extends SavedData {
    private static final String NAME = "rdpl_portway_team";
    private static final String TEAMS = "teams";
    private static final Factory<ContentPortwayTeam> FACTORY = new Factory<>(ContentPortwayTeam::new, (tag, lookup) -> read(tag));
    private final Map<String, List<PortwayStop>> teams = new LinkedHashMap<>();

    public static ContentPortwayTeam get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static ContentPortwayTeam read(CompoundTag compound) {
        ContentPortwayTeam data = new ContentPortwayTeam();
        CompoundTag tags = compound.getCompound(TEAMS);
        for (String team : tags.getAllKeys()) {
            ListTag tagged = tags.getList(team, Tag.TAG_COMPOUND);
            List<PortwayStop> stops = new ArrayList<>();
            for (int index = 0; index < tagged.size(); index++) {
                PortwayStop stop = PortwayStop.read(tagged.getCompound(index));
                if (stop != null) { stops.add(stop); }
            }
            if (!stops.isEmpty()) { data.teams.put(team, stops); }
        }
        return data;
    }

    private static int indexOf(List<PortwayStop> stops, PortwayStop stop) {
        for (int index = 0; index < stops.size(); index++) {
            if (stops.get(index).same(stop)) { return index; }
        }
        return -1;
    }

    public List<PortwayStop> list(String team) {
        List<PortwayStop> stops = team.isEmpty() ? null : teams.get(team);
        return stops == null ? new ArrayList<>() : new ArrayList<>(stops);
    }

    public boolean learn(String team, PortwayStop stop) {
        List<PortwayStop> stops = teams.computeIfAbsent(team, key -> new ArrayList<>());
        int at = indexOf(stops, stop);
        if (at >= 0) {
            if (stops.get(at).differs(stop)) {
                stops.set(at, stop.keeping(stops.get(at)));
                setDirty();
            }
            return false;
        }
        stops.add(stop.fresh());
        setDirty();
        return true;
    }

    public void refresh(PortwayStop live) {
        boolean changed = false;
        for (List<PortwayStop> stops : teams.values()) {
            int at = indexOf(stops, live);
            if (at >= 0 && stops.get(at).differs(live)) {
                stops.set(at, live.keeping(stops.get(at)));
                changed = true;
            }
        }
        if (changed) { setDirty(); }
    }

    public void remove(ResourceKey<Level> dimension, BlockPos pos) { remove(Collections.singleton(new PortwayStop.Key(dimension, pos))); }

    public void remove(Set<PortwayStop.Key> lost) {
        boolean changed = false;
        for (List<PortwayStop> stops : teams.values()) { changed |= stops.removeIf(stop -> lost.contains(stop.key())); }
        teams.values().removeIf(List::isEmpty);
        if (changed) { setDirty(); }
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound, @Nonnull HolderLookup.Provider lookup) {
        CompoundTag tags = new CompoundTag();
        for (Map.Entry<String, List<PortwayStop>> entry : teams.entrySet()) {
            if (entry.getValue().isEmpty()) { continue; }
            ListTag tagged = new ListTag();
            for (PortwayStop stop : entry.getValue()) { tagged.add(stop.write()); }
            tags.put(entry.getKey(), tagged);
        }
        compound.put(TEAMS, tags);
        return compound;
    }
}
