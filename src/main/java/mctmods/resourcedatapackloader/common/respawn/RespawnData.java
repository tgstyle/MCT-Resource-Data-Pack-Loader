package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.util.world.SavedData;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class RespawnData extends WorldSavedData {
    public static final String NAME = "RDPLRespawn";
    public static final int MOST_INSTANCES = 4096;
    private final Map<String, RespawnInstance> held = new LinkedHashMap<>();
    private final PriorityQueue<RespawnInstance> queue = new PriorityQueue<>(Comparator.comparingLong(instance -> instance.due));
    private final Map<Long, List<RespawnInstance>> parked = new HashMap<>();
    private boolean refused;

    public RespawnData(String name) { super(name); }

    public static RespawnData of(World world) { return SavedData.get(world.getPerWorldStorage(), RespawnData.class, NAME, RespawnData::new); }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        held.clear();
        NBTTagList rows = nbt.getTagList("Instances", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < rows.tagCount() && held.size() < MOST_INSTANCES; i++) {
            RespawnInstance read = RespawnInstance.read(rows.getCompoundTagAt(i));
            if (read != null) { held.put(read.id, read); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList rows = new NBTTagList();
        for (RespawnInstance instance : held.values()) { rows.appendTag(instance.write()); }
        nbt.setTag("Instances", rows);
        return nbt;
    }

    public int size() { return held.size(); }

    public boolean admits(String id) {
        if (held.containsKey(id) || held.size() < MOST_INSTANCES) { return true; }
        if (!refused) {
            refused = true;
            ContentLog.LOGGER.warn("A dimension already holds {} respawning structure instances, so further ones are not recorded and never respawn", MOST_INSTANCES);
        }
        return false;
    }

    public void add(RespawnInstance fresh) {
        RespawnInstance known = held.get(fresh.id);
        if (known != null) { known.merge(fresh); }
        else {
            held.put(fresh.id, fresh);
            schedule(fresh);
        }
        markDirty();
    }

    @Nullable public RespawnInstance pollDue(long now) {
        RespawnInstance head = queue.peek();
        return head == null || head.due > now ? null : queue.poll();
    }

    public void requeue(RespawnInstance instance, long due) {
        instance.due = due;
        queue.add(instance);
    }

    public void park(RespawnInstance instance) { parked.computeIfAbsent(instance.centerChunk(), key -> new ArrayList<>()).add(instance); }

    public List<RespawnInstance> unpark(long chunkKey) {
        List<RespawnInstance> waiting = parked.remove(chunkKey);
        return waiting == null ? new ArrayList<>() : waiting;
    }

    public void remove(RespawnInstance instance) {
        held.remove(instance.id);
        queue.remove(instance);
        List<RespawnInstance> waiting = parked.get(instance.centerChunk());
        if (waiting != null) { waiting.remove(instance); }
        markDirty();
    }

    public static void prune(World world) { of(world).prune(); }

    private void prune() {
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
            markDirty();
            ContentLog.LOGGER.info("Dropped {} saved respawning structure instance(s) whose definition no longer respawns", dropped);
        }
    }

    private void schedule(RespawnInstance instance) {
        RespawnDef respawn = RespawnRecorder.resolve(instance.kind, instance.def);
        if (respawn == null) { return; }
        instance.due = instance.last + respawn.after * 20L;
        queue.add(instance);
    }
}
