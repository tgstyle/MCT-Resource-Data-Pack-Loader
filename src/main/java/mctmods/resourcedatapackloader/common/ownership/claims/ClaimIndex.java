package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.util.world.SavedTypes;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class ClaimIndex extends SavedData {
    public static final String MODE_REMOVE = "remove";
    private static final String NAME = "rdpl_claims";
    private static final int VERSION = 1;
    private static final SavedDataType<ClaimIndex> TYPE = SavedTypes.of(NAME, ClaimIndex::new, ClaimIndex::read, ClaimIndex::write);

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

        CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("id", id);
            tag.putInt("h", h);
            tag.putInt("x", x);
            tag.putInt("y", y);
            tag.putInt("z", z);
            tag.putInt("r", r);
            return tag;
        }

        static Anchor read(CompoundTag tag) {
            return new Anchor(tag.getIntOr("id", 0), tag.getIntOr("h", 0), tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0), tag.getIntOr("r", 0));
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

        CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("pos", pos);
            tag.putInt("h", h);
            tag.putString("mode", mode);
            tag.putIntArray("ydone", ydone.toIntArray());
            return tag;
        }

        static Pending read(CompoundTag tag) {
            Pending pending = new Pending(tag.getLongOr("pos", 0L), tag.getIntOr("h", 0), tag.getStringOr("mode", ""));
            for (int index : tag.getIntArray("ydone").orElse(new int[0])) { pending.ydone.add(index); }
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

    private ClaimIndex() {
        handleAt.defaultReturnValue(0);
        viaAt.defaultReturnValue(0);
    }

    public static ClaimIndex obtain(ServerLevel level) {
        ClaimIndex index = level.getDataStorage().computeIfAbsent(TYPE);
        index.attach(OwnerTable.get(level.getServer()));
        return index;
    }

    private void attach(OwnerTable owners) {
        boolean fresh = table == null;
        table = owners;
        if (!fresh) { return; }
        Set<Integer> handles = new LinkedHashSet<>(chunks.keySet());
        for (Pending one : pending.values()) { handles.add(one.h); }
        for (Anchor anchor : anchors.values()) { handles.add(anchor.h); }
        for (int handle : handles) {
            int now = owners.resolve(handle);
            if (now != 0 && now != handle) { moveHandle(handle, now); }
        }
    }

    public static void moveAll(int from, int to) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) { return; }
        for (ServerLevel level : server.getAllLevels()) { obtain(level).moveHandle(from, to); }
    }

    public static long key(int chunkX, int chunkZ) { return ChunkPos.pack(chunkX, chunkZ); }

    public int ownerAt(int chunkX, int chunkZ) { return handleAt.get(key(chunkX, chunkZ)); }

    public boolean claim(int chunkX, int chunkZ, int handle, int via) {
        long pos = key(chunkX, chunkZ);
        if (handle == 0 || handleAt.containsKey(pos)) { return false; }
        handleAt.put(pos, handle);
        if (via != 0) { viaAt.put(pos, via); }
        chunks.computeIfAbsent(handle, _ -> new LongLinkedOpenHashSet()).add(pos);
        if (table != null) { table.claimAdded(handle); }
        setDirty();
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
        setDirty();
        return handle;
    }

    public List<ChunkPos> chunksOf(int handle) {
        List<ChunkPos> listed = new ArrayList<>();
        LongLinkedOpenHashSet group = chunks.get(handle);
        if (group == null) { return listed; }
        for (long pos : group) { listed.add(ChunkPos.unpack(pos)); }
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
            LongLinkedOpenHashSet target = chunks.computeIfAbsent(to, _ -> new LongLinkedOpenHashSet());
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
        setDirty();
    }

    public int addAnchor(int handle, int x, int y, int z, int radius) {
        Anchor anchor = new Anchor(next++, handle, x, y, z, radius);
        anchors.put(anchor.id, anchor);
        setDirty();
        return anchor.id;
    }

    public void removeAnchor(int id) {
        if (anchors.remove(id) != null) { setDirty(); }
    }

    @Nullable public Anchor anchor(int id) { return anchors.get(id); }

    public List<Anchor> anchorList() { return new ArrayList<>(anchors.values()); }

    public List<ChunkPos> chunksNamingAnchor(int id) {
        List<ChunkPos> listed = new ArrayList<>();
        if (id == 0) { return listed; }
        for (Long2IntOpenHashMap.Entry entry : viaAt.long2IntEntrySet()) {
            if (entry.getIntValue() == id) { listed.add(ChunkPos.unpack(entry.getLongKey())); }
        }
        return listed;
    }

    public boolean addPending(int chunkX, int chunkZ, int handle, String mode) {
        long pos = key(chunkX, chunkZ);
        if (handle == 0 || pending.containsKey(pos)) { return false; }
        pending.put(pos, new Pending(pos, handle, mode));
        if (table != null) { table.claimAdded(handle); }
        setDirty();
        return true;
    }

    @Nullable public Pending pendingAt(int chunkX, int chunkZ) { return pending.get(key(chunkX, chunkZ)); }

    public void markDone(int chunkX, int chunkZ, int yIndex) {
        Pending one = pending.get(key(chunkX, chunkZ));
        if (one == null || one.ydone.contains(yIndex)) { return; }
        one.ydone.add(yIndex);
        setDirty();
    }

    public void removePending(int chunkX, int chunkZ) {
        Pending one = pending.remove(key(chunkX, chunkZ));
        if (one == null) { return; }
        if (table != null) { table.claimRemoved(one.h); }
        setDirty();
    }

    private static ClaimIndex read(CompoundTag compound) {
        ClaimIndex data = new ClaimIndex();
        data.next = Math.max(1, compound.getIntOr("next", 0));
        ListTag owners = compound.getListOrEmpty("owners");
        for (int index = 0; index < owners.size(); index++) {
            CompoundTag one = owners.getCompoundOrEmpty(index);
            int handle = one.getIntOr("h", 0);
            long[] positions = Longs.fromInts(one.getIntArray("pos").orElse(new int[0]));
            int[] via = one.getIntArray("via").orElse(new int[0]);
            LongLinkedOpenHashSet group = data.chunks.computeIfAbsent(handle, _ -> new LongLinkedOpenHashSet());
            for (int at = 0; at < positions.length; at++) {
                data.handleAt.put(positions[at], handle);
                group.add(positions[at]);
                if (at < via.length && via[at] != 0) { data.viaAt.put(positions[at], via[at]); }
            }
        }
        ListTag anchored = compound.getListOrEmpty("anchors");
        for (int index = 0; index < anchored.size(); index++) {
            Anchor anchor = Anchor.read(anchored.getCompoundOrEmpty(index));
            data.anchors.put(anchor.id, anchor);
            if (anchor.id >= data.next) { data.next = anchor.id + 1; }
        }
        ListTag queued = compound.getListOrEmpty("pending");
        for (int index = 0; index < queued.size(); index++) {
            Pending one = Pending.read(queued.getCompoundOrEmpty(index));
            data.pending.put(one.pos, one);
        }
        return data;
    }

    private CompoundTag write() {
        ListTag owners = new ListTag();
        for (Map.Entry<Integer, LongLinkedOpenHashSet> entry : chunks.entrySet()) {
            LongArrayList positions = new LongArrayList(entry.getValue());
            int[] via = new int[positions.size()];
            for (int at = 0; at < via.length; at++) { via[at] = viaAt.get(positions.getLong(at)); }
            CompoundTag one = new CompoundTag();
            one.putInt("h", entry.getKey());
            one.putIntArray("pos", Longs.toInts(positions.toLongArray()));
            one.putIntArray("via", via);
            owners.add(one);
        }
        ListTag anchored = new ListTag();
        for (Anchor anchor : anchors.values()) { anchored.add(anchor.write()); }
        ListTag queued = new ListTag();
        for (Pending one : pending.values()) { queued.add(one.write()); }
        CompoundTag compound = new CompoundTag();
        compound.putInt("v", VERSION);
        compound.putInt("next", next);
        compound.put("owners", owners);
        compound.put("anchors", anchored);
        compound.put("pending", queued);
        return compound;
    }
}
