package mctmods.resourcedatapackloader.common.ownership.claims;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.util.world.SavedData;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

public final class ClaimIndex extends WorldSavedData {
    public static final String MODE_REMOVE = "remove";
    private static final String NAME = "rdpl_claims";
    private static final int VERSION = 1;

    public static final class Anchor {
        public final int id;
        public final int h;
        public final int x;
        public final int y;
        public final int z;
        public final int r;

        Anchor(int id, int h, int x, int y, int z, int r) {
            this.id = id;
            this.h = h;
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
        }

        NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("id", id);
            tag.setInteger("h", h);
            tag.setInteger("x", x);
            tag.setInteger("y", y);
            tag.setInteger("z", z);
            tag.setInteger("r", r);
            return tag;
        }

        static Anchor read(NBTTagCompound tag) {
            return new Anchor(tag.getInteger("id"), tag.getInteger("h"), tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), tag.getInteger("r"));
        }
    }

    public static final class Pending {
        public final long pos;
        public int h;
        public final String mode;
        private final IntArrayList ydone = new IntArrayList();

        Pending(long pos, int h, String mode) {
            this.pos = pos;
            this.h = h;
            this.mode = mode;
        }

        public boolean isDone(int index) { return ydone.contains(index); }

        public int[] doneIndexes() { return ydone.toIntArray(); }

        NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("pos", pos);
            tag.setInteger("h", h);
            tag.setString("mode", mode);
            tag.setIntArray("ydone", ydone.toIntArray());
            return tag;
        }

        static Pending read(NBTTagCompound tag) {
            Pending pending = new Pending(tag.getLong("pos"), tag.getInteger("h"), tag.getString("mode"));
            for (int index : tag.getIntArray("ydone")) { pending.ydone.add(index); }
            return pending;
        }
    }

    private final Long2IntOpenHashMap handleAt = new Long2IntOpenHashMap();
    private final Long2IntOpenHashMap viaAt = new Long2IntOpenHashMap();
    private final Map<Integer, LongLinkedOpenHashSet> chunks = new LinkedHashMap<>();
    private final Map<Integer, Anchor> anchors = new LinkedHashMap<>();
    private final Map<Long, Pending> pending = new LinkedHashMap<>();
    private int next = 1;
    @Nullable private OwnerTable table;

    public ClaimIndex(String name) {
        super(name);
        handleAt.defaultReturnValue(0);
        viaAt.defaultReturnValue(0);
    }

    @Nullable public static ClaimIndex obtain(@Nullable World world) {
        if (world == null) { return null; }
        MapStorage storage = world.getPerWorldStorage();
        ClaimIndex index = SavedData.get(storage, ClaimIndex.class, NAME, ClaimIndex::new);
        index.attach(OwnerTable.get(world.getMinecraftServer()));
        return index;
    }

    private void attach(@Nullable OwnerTable owners) {
        boolean fresh = table == null;
        table = owners;
        if (!fresh || owners == null) { return; }
        Set<Integer> handles = new LinkedHashSet<>(chunks.keySet());
        for (Pending one : pending.values()) { handles.add(one.h); }
        for (Anchor anchor : anchors.values()) { handles.add(anchor.h); }
        for (int handle : handles) {
            int now = owners.resolve(handle);
            if (now != 0 && now != handle) { moveHandle(handle, now); }
        }
    }

    private static ChunkPos chunkAt(long pos) { return new ChunkPos(Longs.low(pos), Longs.high(pos)); }

    public static void moveAll(int from, int to) {
        for (WorldServer world : DimensionManager.getWorlds()) {
            ClaimIndex index = obtain(world);
            if (index != null) { index.moveHandle(from, to); }
        }
    }

    public static long key(int chunkX, int chunkZ) { return ChunkPos.asLong(chunkX, chunkZ); }

    public int ownerAt(int chunkX, int chunkZ) { return handleAt.get(key(chunkX, chunkZ)); }

    public boolean claim(int chunkX, int chunkZ, int handle, int via) {
        long pos = key(chunkX, chunkZ);
        if (handle == 0 || handleAt.containsKey(pos)) { return false; }
        handleAt.put(pos, handle);
        if (via != 0) { viaAt.put(pos, via); }
        chunks.computeIfAbsent(handle, ignored -> new LongLinkedOpenHashSet()).add(pos);
        if (table != null) { table.claimAdded(handle); }
        markDirty();
        return true;
    }

    public int release(int chunkX, int chunkZ) {
        long pos = key(chunkX, chunkZ);
        if (!handleAt.containsKey(pos)) { return 0; }
        int handle = handleAt.remove(pos);
        viaAt.remove(pos);
        LongLinkedOpenHashSet group = chunks.get(handle);
        if (group != null) {
            group.remove(pos);
            if (group.isEmpty()) { chunks.remove(handle); }
        }
        if (table != null) { table.claimRemoved(handle); }
        markDirty();
        return handle;
    }

    public List<ChunkPos> chunksOf(int handle) {
        List<ChunkPos> listed = new ArrayList<>();
        LongLinkedOpenHashSet group = chunks.get(handle);
        if (group == null) { return listed; }
        for (long pos : group) { listed.add(chunkAt(pos)); }
        return listed;
    }

    public int claimCountOf(int handle) {
        LongLinkedOpenHashSet group = chunks.get(handle);
        return group == null ? 0 : group.size();
    }

    public boolean isEmpty() { return handleAt.isEmpty() && anchors.isEmpty() && pending.isEmpty(); }

    public void moveHandle(int from, int to) {
        if (from == 0 || to == 0 || from == to) { return; }
        int moved = 0;
        LongLinkedOpenHashSet group = chunks.remove(from);
        if (group != null) {
            LongLinkedOpenHashSet target = chunks.computeIfAbsent(to, ignored -> new LongLinkedOpenHashSet());
            for (long pos : group) {
                handleAt.put(pos, to);
                target.add(pos);
            }
            moved += group.size();
        }
        for (Pending one : pending.values()) {
            if (one.h == from) {
                one.h = to;
                moved++;
            }
        }
        Map<Integer, Anchor> rewritten = new LinkedHashMap<>();
        boolean anchorsMoved = false;
        for (Anchor anchor : anchors.values()) {
            if (anchor.h == from) {
                rewritten.put(anchor.id, new Anchor(anchor.id, to, anchor.x, anchor.y, anchor.z, anchor.r));
                anchorsMoved = true;
            } else {
                rewritten.put(anchor.id, anchor);
            }
        }
        if (anchorsMoved) {
            anchors.clear();
            anchors.putAll(rewritten);
        }
        if (moved == 0 && !anchorsMoved) { return; }
        if (table != null && moved != 0) { table.claimsMoved(from, to, moved); }
        markDirty();
    }

    public int addAnchor(int handle, int x, int y, int z, int radius) {
        Anchor anchor = new Anchor(next++, handle, x, y, z, radius);
        anchors.put(anchor.id, anchor);
        markDirty();
        return anchor.id;
    }

    public void removeAnchor(int id) {
        if (anchors.remove(id) != null) { markDirty(); }
    }

    @Nullable public Anchor anchor(int id) { return anchors.get(id); }

    public List<Anchor> anchorList() { return new ArrayList<>(anchors.values()); }

    public List<ChunkPos> chunksNamingAnchor(int id) {
        List<ChunkPos> listed = new ArrayList<>();
        if (id == 0) { return listed; }
        for (Long2IntOpenHashMap.Entry entry : viaAt.long2IntEntrySet()) {
            if (entry.getIntValue() == id) { listed.add(chunkAt(entry.getLongKey())); }
        }
        return listed;
    }

    public boolean addPending(int chunkX, int chunkZ, int handle, String mode) {
        long pos = key(chunkX, chunkZ);
        if (handle == 0 || pending.containsKey(pos)) { return false; }
        pending.put(pos, new Pending(pos, handle, mode));
        if (table != null) { table.claimAdded(handle); }
        markDirty();
        return true;
    }

    @Nullable public Pending pendingAt(int chunkX, int chunkZ) { return pending.get(key(chunkX, chunkZ)); }

    public void markDone(int chunkX, int chunkZ, int yIndex) {
        Pending one = pending.get(key(chunkX, chunkZ));
        if (one == null || one.ydone.contains(yIndex)) { return; }
        one.ydone.add(yIndex);
        markDirty();
    }

    public void removePending(int chunkX, int chunkZ) {
        Pending one = pending.remove(key(chunkX, chunkZ));
        if (one == null) { return; }
        if (table != null) { table.claimRemoved(one.h); }
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        handleAt.clear();
        viaAt.clear();
        chunks.clear();
        anchors.clear();
        pending.clear();
        next = Math.max(1, compound.getInteger("next"));
        NBTTagList owners = compound.getTagList("owners", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < owners.tagCount(); index++) {
            NBTTagCompound one = owners.getCompoundTagAt(index);
            int handle = one.getInteger("h");
            long[] positions = Longs.fromInts(one.getIntArray("pos"));
            int[] via = one.getIntArray("via");
            LongLinkedOpenHashSet group = chunks.computeIfAbsent(handle, ignored -> new LongLinkedOpenHashSet());
            for (int at = 0; at < positions.length; at++) {
                handleAt.put(positions[at], handle);
                group.add(positions[at]);
                if (at < via.length && via[at] != 0) { viaAt.put(positions[at], via[at]); }
            }
        }
        NBTTagList anchored = compound.getTagList("anchors", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < anchored.tagCount(); index++) {
            Anchor anchor = Anchor.read(anchored.getCompoundTagAt(index));
            anchors.put(anchor.id, anchor);
            if (anchor.id >= next) { next = anchor.id + 1; }
        }
        NBTTagList queued = compound.getTagList("pending", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < queued.tagCount(); index++) {
            Pending one = Pending.read(queued.getCompoundTagAt(index));
            pending.put(one.pos, one);
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList owners = new NBTTagList();
        for (Map.Entry<Integer, LongLinkedOpenHashSet> entry : chunks.entrySet()) {
            LongArrayList positions = new LongArrayList(entry.getValue());
            int[] via = new int[positions.size()];
            for (int at = 0; at < via.length; at++) { via[at] = viaAt.get(positions.getLong(at)); }
            NBTTagCompound one = new NBTTagCompound();
            one.setInteger("h", entry.getKey());
            one.setIntArray("pos", Longs.toInts(positions.toLongArray()));
            one.setIntArray("via", via);
            owners.appendTag(one);
        }
        NBTTagList anchored = new NBTTagList();
        for (Anchor anchor : anchors.values()) { anchored.appendTag(anchor.write()); }
        NBTTagList queued = new NBTTagList();
        for (Pending one : pending.values()) { queued.appendTag(one.write()); }
        compound.setInteger("v", VERSION);
        compound.setInteger("next", next);
        compound.setTag("owners", owners);
        compound.setTag("anchors", anchored);
        compound.setTag("pending", queued);
        return compound;
    }
}
