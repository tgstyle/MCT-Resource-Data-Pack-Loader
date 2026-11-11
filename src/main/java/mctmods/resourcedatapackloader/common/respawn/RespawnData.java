package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class RespawnData extends SavedData {
    public static final String NAME = "rdpl_respawn";
    public static final int MOST_INSTANCES = 4096;
    private final Map<String, RespawnInstance> held = new LinkedHashMap<>();
    private final PriorityQueue<RespawnInstance> queue = new PriorityQueue<>(Comparator.comparingLong(instance -> instance.due));
    private final Map<Long, List<RespawnInstance>> parked = new HashMap<>();
    private boolean refused;

    public static RespawnData of(ServerLevel level) {
        synchronized (RespawnData.class) { return level.getDataStorage().computeIfAbsent(RespawnData::read, RespawnData::new, NAME); }
    }

    private static RespawnData read(CompoundTag tag) {
        RespawnData data = new RespawnData();
        ListTag rows = tag.getList("Instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < rows.size() && data.held.size() < MOST_INSTANCES; i++) {
            RespawnInstance read = RespawnInstance.read(rows.getCompound(i));
            if (read != null) { data.held.put(read.id, read); }
        }
        return data;
    }

    @Override @Nonnull public synchronized CompoundTag save(@Nonnull CompoundTag tag) {
        ListTag rows = new ListTag();
        for (RespawnInstance instance : held.values()) { rows.add(instance.write()); }
        tag.put("Instances", rows);
        return tag;
    }

    public synchronized int size() { return held.size(); }

    public synchronized boolean admits(String id) {
        if (held.containsKey(id) || held.size() < MOST_INSTANCES) { return true; }
        if (!refused) {
            refused = true;
            ContentLog.LOGGER.warn("A dimension already holds {} respawning structure instances, so further ones are not recorded and never respawn", MOST_INSTANCES);
        }
        return false;
    }

    public synchronized void add(RespawnInstance fresh) {
        RespawnInstance known = held.get(fresh.id);
        if (known != null) { known.merge(fresh); }
        else {
            held.put(fresh.id, fresh);
            schedule(fresh);
        }
        setDirty();
    }

    @Nullable public synchronized RespawnInstance pollDue(long now) {
        RespawnInstance head = queue.peek();
        return head == null || head.due > now ? null : queue.poll();
    }

    public synchronized void requeue(RespawnInstance instance, long due) {
        instance.due = due;
        queue.add(instance);
    }

    public synchronized void park(RespawnInstance instance) { parked.computeIfAbsent(instance.centerChunk(), key -> new ArrayList<>()).add(instance); }

    public synchronized List<RespawnInstance> unpark(long chunkKey) {
        List<RespawnInstance> waiting = parked.remove(chunkKey);
        return waiting == null ? new ArrayList<>() : waiting;
    }

    public synchronized void remove(RespawnInstance instance) {
        held.remove(instance.id);
        queue.remove(instance);
        List<RespawnInstance> waiting = parked.get(instance.centerChunk());
        if (waiting != null) { waiting.remove(instance); }
        setDirty();
    }

    public static void prune(ServerLevel level) { of(level).prune(); }

    private synchronized void prune() {
        int dropped = 0;
        Iterator<RespawnInstance> each = held.values().iterator();
        while (each.hasNext()) {
            RespawnInstance instance = each.next();
            if (RespawnRecorder.resolve(instance.kind, instance.def) == null) {
                each.remove();
                dropped++;
            }
        }
        queue.clear();
        parked.clear();
        for (RespawnInstance instance : held.values()) { schedule(instance); }
        if (dropped > 0) {
            setDirty();
            ContentLog.LOGGER.info("Dropped {} saved respawning structure instance(s) whose definition no longer respawns", dropped);
        }
    }

    private void schedule(RespawnInstance instance) {
        RespawnDef respawn = RespawnRecorder.resolve(instance.kind, instance.def);
        if (respawn == null) { return; }
        instance.due = instance.last + respawn.after() * 20L;
        queue.add(instance);
    }
}
