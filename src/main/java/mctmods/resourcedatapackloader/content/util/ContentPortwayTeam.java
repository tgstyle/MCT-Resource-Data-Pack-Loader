package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.SavedTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentPortwayTeam extends SavedData {
    private static final String NAME = "rdpl_portway_team";
    private static final String TEAMS = "teams";
    private static final SavedDataType<ContentPortwayTeam> TYPE = SavedTypes.of(NAME, ContentPortwayTeam::new, ContentPortwayTeam::read, ContentPortwayTeam::write);
    private final Map<String, List<PortwayStop>> teams = new LinkedHashMap<>();

    private static ContentPortwayTeam get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static ContentPortwayTeam read(CompoundTag compound) {
        ContentPortwayTeam data = new ContentPortwayTeam();
        CompoundTag tags = compound.getCompoundOrEmpty(TEAMS);
        for (String team : tags.keySet()) {
            ListTag tagged = tags.getListOrEmpty(team);
            List<PortwayStop> stops = new ArrayList<>();
            for (int index = 0; index < tagged.size(); index++) {
                PortwayStop stop = PortwayStop.read(tagged.getCompoundOrEmpty(index));
                if (stop != null) { stops.add(stop); }
            }
            if (!stops.isEmpty()) { data.teams.put(team, stops); }
        }
        return data;
    }

    private CompoundTag write() {
        CompoundTag tags = new CompoundTag();
        for (Map.Entry<String, List<PortwayStop>> entry : teams.entrySet()) {
            if (entry.getValue().isEmpty()) { continue; }
            ListTag tagged = new ListTag();
            for (PortwayStop stop : entry.getValue()) { tagged.add(stop.write()); }
            tags.put(entry.getKey(), tagged);
        }
        CompoundTag compound = new CompoundTag();
        compound.put(TEAMS, tags);
        return compound;
    }

    public static List<PortwayStop> list(MinecraftServer server, String team) {
        List<PortwayStop> stops = team.isEmpty() ? null : get(server).teams.get(team);
        return stops == null ? new ArrayList<>() : new ArrayList<>(stops);
    }

    public static boolean learn(MinecraftServer server, String team, PortwayStop stop) {
        ContentPortwayTeam data = get(server);
        List<PortwayStop> stops = data.teams.computeIfAbsent(team, _ -> new ArrayList<>());
        for (int index = 0; index < stops.size(); index++) {
            if (!stops.get(index).same(stop)) { continue; }
            if (stops.get(index).differs(stop)) {
                stops.set(index, stop.keeping(stops.get(index)));
                data.setDirty();
            }
            return false;
        }
        stops.add(stop.fresh());
        data.setDirty();
        return true;
    }

    public static void refresh(MinecraftServer server, PortwayStop live) {
        ContentPortwayTeam data = get(server);
        boolean changed = false;
        for (List<PortwayStop> stops : data.teams.values()) {
            for (int index = 0; index < stops.size(); index++) {
                if (stops.get(index).same(live) && stops.get(index).differs(live)) {
                    stops.set(index, live.keeping(stops.get(index)));
                    changed = true;
                }
            }
        }
        if (changed) { data.setDirty(); }
    }

    public static void remove(MinecraftServer server, ResourceKey<Level> dimension, BlockPos pos) { remove(server, Set.of(new PortwayStop.Key(dimension, pos))); }

    public static void remove(MinecraftServer server, Set<PortwayStop.Key> lost) {
        ContentPortwayTeam data = get(server);
        boolean changed = false;
        for (List<PortwayStop> stops : data.teams.values()) { changed |= stops.removeIf(stop -> lost.contains(stop.key())); }
        data.teams.values().removeIf(List::isEmpty);
        if (changed) { data.setDirty(); }
    }
}
