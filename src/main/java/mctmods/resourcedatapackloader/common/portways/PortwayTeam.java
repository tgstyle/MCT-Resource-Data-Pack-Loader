package mctmods.resourcedatapackloader.common.portways;

import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class PortwayTeam extends WorldSavedData {
    private static final String NAME = "rdpl_portway_team";
    private static final String TEAMS = "teams";
    private final Map<String, List<PortwayStop>> teams = new LinkedHashMap<>();

    public PortwayTeam(String name) { super(name); }

    @Nullable private static PortwayTeam get(@Nullable MinecraftServer server) {
        return SavedData.overworld(server, PortwayTeam.class, NAME, PortwayTeam::new);
    }

    public static List<PortwayStop> list(@Nullable MinecraftServer server, String team) {
        PortwayTeam data = get(server);
        List<PortwayStop> stops = data == null || team.isEmpty() ? null : data.teams.get(team);
        return stops == null ? new ArrayList<>() : new ArrayList<>(stops);
    }

    public static boolean learn(@Nullable MinecraftServer server, String team, PortwayStop stop) {
        PortwayTeam data = get(server);
        if (data == null) { return false; }
        List<PortwayStop> stops = data.teams.computeIfAbsent(team, key -> new ArrayList<>());
        if (stops.contains(stop)) {
            if (stop.refreshIn(stops)) { data.markDirty(); }
            return false;
        }
        stops.add(stop.fresh());
        data.markDirty();
        return true;
    }

    public static void refresh(@Nullable MinecraftServer server, PortwayStop live) {
        PortwayTeam data = get(server);
        if (data == null) { return; }
        boolean changed = false;
        for (List<PortwayStop> stops : data.teams.values()) {
            if (live.refreshIn(stops)) { changed = true; }
        }
        if (changed) { data.markDirty(); }
    }

    public static void remove(@Nullable MinecraftServer server, int dimension, BlockPos pos) { remove(server, Collections.singleton(new PortwayStop(dimension, pos))); }

    public static void remove(@Nullable MinecraftServer server, Set<PortwayStop> lost) {
        PortwayTeam data = get(server);
        if (data == null) { return; }
        boolean changed = false;
        for (List<PortwayStop> stops : data.teams.values()) { changed |= stops.removeIf(lost::contains); }
        data.teams.values().removeIf(List::isEmpty);
        if (changed) { data.markDirty(); }
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        teams.clear();
        NBTTagCompound tags = compound.getCompoundTag(TEAMS);
        for (String team : tags.getKeySet()) {
            List<PortwayStop> stops = PortwayStop.readAll(tags.getTagList(team, Constants.NBT.TAG_COMPOUND));
            if (!stops.isEmpty()) { teams.put(team, stops); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound tags = new NBTTagCompound();
        for (Map.Entry<String, List<PortwayStop>> entry : teams.entrySet()) {
            if (entry.getValue().isEmpty()) { continue; }
            tags.setTag(entry.getKey(), PortwayStop.writeAll(entry.getValue()));
        }
        compound.setTag(TEAMS, tags);
        return compound;
    }
}
