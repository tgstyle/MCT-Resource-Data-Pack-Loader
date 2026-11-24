package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.core.util.math.Longs;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public final class BaseLedger extends SavedData {
    private static final String NAME = "rdpl_bases";
    private static final int VERSION = 1;
    private static final int PLACED_CAP = 4000;
    private static final int BOX_CAP = 8;
    private static final int WHO_CAP = 4;
    private static final int BOX_SHARE = 20;
    private static final int TICKS_PER_SHARE = 200;
    private static final Factory<BaseLedger> FACTORY = new Factory<>(BaseLedger::new, (tag, lookup) -> read(tag));
    private static int cellCap = BaseDef.DEFAULT_CELL_CAP;
    private final Long2ObjectOpenHashMap<Cell> cells = new Long2ObjectOpenHashMap<>();
    private final Map<UUID, BlockPos> beds = new LinkedHashMap<>();
    private final Map<Integer, Long> raided = new LinkedHashMap<>();
    private long revision;

    public static BaseLedger get(ServerLevel level) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }

    public static long key(BlockPos pos) { return ChunkPos.asLong(pos); }

    public static void setCellCap(int cap) { cellCap = Math.max(BaseDef.MIN_CELL_CAP, cap); }

    public long revision() { return revision; }

    @Nullable public Cell cell(Level level, long key) {
        Cell cell = cells.get(key);
        if (cell != null && cell.normalize(level.getServer())) { changed(); }
        return cell;
    }

    public void placed(ServerPlayer player, BlockPos pos, int delta) {
        if (delta == 0) { return; }
        int handle = RaidOwner.of(player);
        Cell cell = delta > 0 ? credited(player, pos, handle, delta) : contributed(player.level(), pos, handle);
        if (cell == null) { return; }
        cell.placed = Mth.clamp(cell.placed + delta, 0, PLACED_CAP);
        stamp(player.level(), cell);
    }

    public void opened(ServerPlayer player, BlockPos container) {
        long packed = container.asLong();
        Cell found = cells.get(key(container));
        if (found != null && (found.boxCount >= BOX_CAP || found.hasBox(packed))) { return; }
        Cell cell = credited(player, container, RaidOwner.of(player), BOX_SHARE);
        if (cell == null || cell.boxCount >= BOX_CAP || cell.hasBox(packed)) { return; }
        cell.boxes[cell.boxCount++] = packed;
        stamp(player.level(), cell);
    }

    public void ticks(ServerPlayer player, BlockPos pos, int amount) {
        if (amount <= 0) { return; }
        Cell cell = credited(player, pos, RaidOwner.of(player), Math.max(1, amount / TICKS_PER_SHARE));
        if (cell == null) { return; }
        cell.ticks = (int) Math.min(Integer.MAX_VALUE, (long) cell.ticks + amount);
        stamp(player.level(), cell);
    }

    public void setBed(UUID player, BlockPos pos) {
        if (pos.equals(beds.get(player))) { return; }
        beds.put(player, pos.immutable());
        changed();
    }

    public void clearBed(UUID player) {
        if (beds.remove(player) != null) { changed(); }
    }

    @Nullable public BlockPos bed(UUID player) { return beds.get(player); }

    public Map<UUID, BlockPos> beds() { return Collections.unmodifiableMap(beds); }

    public boolean raidedWithin(@Nullable MinecraftServer server, int handle, long now, long span) {
        int owner = RaidOwner.of(server, handle);
        if (owner == 0) { return false; }
        for (Map.Entry<Integer, Long> entry : raided.entrySet()) {
            if (RaidOwner.of(server, entry.getKey()) == owner && now - entry.getValue() < span) { return true; }
        }
        return false;
    }

    public void markRaided(@Nullable MinecraftServer server, int handle, long time) {
        int owner = RaidOwner.of(server, handle);
        if (owner == 0) { return; }
        raided.keySet().removeIf(stored -> {
            int now = RaidOwner.of(server, stored);
            return now == 0 || now == owner;
        });
        raided.put(owner, time);
        changed();
    }

    @Nullable private Cell credited(ServerPlayer player, BlockPos pos, int handle, int share) {
        if (handle == 0) { return null; }
        long key = key(pos);
        Cell cell = cell(player.level(), key);
        if (cell != null && !admits(player, pos, cell, handle)) { return null; }
        if (cell == null) {
            evictAtCap();
            cell = new Cell(key);
            cells.put(key, cell);
        }
        cell.credit(handle, share);
        return cell;
    }

    @Nullable private Cell contributed(Level level, BlockPos pos, int handle) {
        Cell cell = cell(level, key(pos));
        return cell != null && cell.holds(handle) ? cell : null;
    }

    private static boolean admits(ServerPlayer player, BlockPos pos, Cell cell, int handle) {
        if (cell.contributors == 0 || cell.holds(handle) || ClaimOwners.isMember(player, cell.top())) { return true; }
        return ClaimGuard.covers(player.level(), pos) != 0 && ClaimGuard.passes(player, player.level(), pos);
    }

    private void evictAtCap() {
        if (cells.size() < cellCap) { return; }
        LongOpenHashSet bedded = new LongOpenHashSet();
        for (BlockPos bed : beds.values()) { bedded.add(key(bed)); }
        List<Cell> order = new ArrayList<>(cells.values());
        order.sort(Comparator.comparingDouble((Cell cell) -> cell.raw() + (bedded.contains(cell.key) ? BaseDef.BED_WEIGHT : 0)).thenComparingLong(Cell::last));
        int drop = Math.clamp(order.size() - cellCap + 1, order.size() / 20, order.size());
        for (int i = 0; i < drop; i++) { cells.remove(order.get(i).key); }
    }

    private void stamp(Level level, Cell cell) {
        cell.last = level.getGameTime();
        changed();
    }

    private void changed() {
        revision++;
        setDirty();
    }

    private static BaseLedger read(CompoundTag compound) {
        BaseLedger ledger = new BaseLedger();
        ListTag cellList = compound.getList("Cells", Tag.TAG_COMPOUND);
        for (int i = 0; i < cellList.size(); i++) {
            Cell cell = Cell.read(cellList.getCompound(i));
            ledger.cells.put(cell.key, cell);
        }
        ListTag bedList = compound.getList("Beds", Tag.TAG_COMPOUND);
        for (int i = 0; i < bedList.size(); i++) {
            CompoundTag one = bedList.getCompound(i);
            UUID id;
            try {
                id = UUID.fromString(one.getString("u"));
            } catch (IllegalArgumentException bad) {
                continue;
            }
            ledger.beds.put(id, BlockPos.of(one.getLong("p")));
        }
        ListTag raidList = compound.getList("Raided", Tag.TAG_COMPOUND);
        for (int i = 0; i < raidList.size(); i++) {
            CompoundTag one = raidList.getCompound(i);
            if (one.getInt("h") != 0) { ledger.raided.put(one.getInt("h"), one.getLong("t")); }
        }
        return ledger;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound, @Nonnull HolderLookup.Provider lookup) {
        compound.putInt("v", VERSION);
        ListTag cellList = new ListTag();
        for (Cell cell : cells.values()) { cellList.add(cell.write()); }
        compound.put("Cells", cellList);
        ListTag bedList = new ListTag();
        for (Map.Entry<UUID, BlockPos> entry : beds.entrySet()) {
            CompoundTag one = new CompoundTag();
            one.putString("u", entry.getKey().toString());
            one.putLong("p", entry.getValue().asLong());
            bedList.add(one);
        }
        compound.put("Beds", bedList);
        ListTag raidList = new ListTag();
        for (Map.Entry<Integer, Long> entry : raided.entrySet()) {
            CompoundTag one = new CompoundTag();
            one.putInt("h", entry.getKey());
            one.putLong("t", entry.getValue());
            raidList.add(one);
        }
        compound.put("Raided", raidList);
        return compound;
    }

    public static final class Cell {
        private final long key;
        private final int[] handles = new int[WHO_CAP + 1];
        private final int[] shares = new int[WHO_CAP + 1];
        private final long[] boxes = new long[BOX_CAP];
        private int contributors;
        private int boxCount;
        private int placed;
        private int ticks;
        private long last;

        private Cell(long key) { this.key = key; }

        public long key() { return key; }

        public int placed() { return placed; }

        public int ticks() { return ticks; }

        public long last() { return last; }

        public int chest() { return boxCount; }

        public boolean holds(int handle) { return handle != 0 && indexOf(handle) >= 0; }

        public int shareOf(int handle) {
            int at = handle == 0 ? -1 : indexOf(handle);
            return at < 0 ? 0 : shares[at];
        }

        public int top() {
            if (contributors == 0) { return 0; }
            int best = 0;
            for (int i = 1; i < contributors; i++) {
                if (shares[i] > shares[best]) { best = i; }
            }
            return handles[best];
        }

        private int indexOf(int handle) {
            for (int i = 0; i < contributors; i++) {
                if (handles[i] == handle) { return i; }
            }
            return -1;
        }

        private boolean hasBox(long packed) {
            for (int i = 0; i < boxCount; i++) {
                if (boxes[i] == packed) { return true; }
            }
            return false;
        }

        public double minutes() { return Math.min(ticks / BaseDef.TICKS_PER_MINUTE, BaseDef.MINUTE_CAP); }

        private double raw() { return boxCount * BaseDef.CHEST_WEIGHT + placed * BaseDef.PLACED_WEIGHT + minutes() * BaseDef.MINUTES_WEIGHT; }

        private void credit(int handle, int amount) {
            int at = indexOf(handle);
            if (at < 0) {
                at = contributors++;
                handles[at] = handle;
                shares[at] = 0;
            }
            shares[at] += Math.min(amount, Short.MAX_VALUE);
            if (shares[at] > Short.MAX_VALUE) { halve(); }
            if (contributors > WHO_CAP) { drop(lowest()); }
        }

        private void halve() {
            for (int i = 0; i < contributors; i++) { shares[i] = Math.max(1, shares[i] / 2); }
        }

        private int lowest() {
            int low = 0;
            for (int i = 1; i < contributors; i++) {
                if (shares[i] < shares[low]) { low = i; }
            }
            return low;
        }

        private void drop(int at) {
            System.arraycopy(handles, at + 1, handles, at, contributors - at - 1);
            System.arraycopy(shares, at + 1, shares, at, contributors - at - 1);
            contributors--;
        }

        private boolean normalize(@Nullable MinecraftServer server) {
            if (contributors == 0 || server == null) { return false; }
            boolean changed = false;
            for (int i = contributors - 1; i >= 0; i--) {
                int now = RaidOwner.of(server, handles[i]);
                if (now == handles[i]) { continue; }
                int share = shares[i];
                drop(i);
                if (now != 0) { credit(now, share); }
                changed = true;
            }
            return changed;
        }

        private CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("k", key);
            tag.putShort("placed", (short) placed);
            tag.putInt("ticks", ticks);
            tag.putLong("last", last);
            ListTag who = new ListTag();
            for (int i = 0; i < contributors; i++) {
                CompoundTag one = new CompoundTag();
                one.putInt("h", handles[i]);
                one.putShort("s", (short) shares[i]);
                who.add(one);
            }
            tag.put("who", who);
            tag.putIntArray("boxes", Longs.toInts(Arrays.copyOf(boxes, boxCount)));
            return tag;
        }

        private static Cell read(CompoundTag tag) {
            Cell cell = new Cell(tag.getLong("k"));
            cell.placed = Mth.clamp(tag.getShort("placed"), 0, PLACED_CAP);
            cell.ticks = Math.max(0, tag.getInt("ticks"));
            cell.last = tag.getLong("last");
            ListTag who = tag.getList("who", Tag.TAG_COMPOUND);
            for (int i = 0; i < who.size(); i++) {
                CompoundTag one = who.getCompound(i);
                if (one.getInt("h") != 0) { cell.credit(one.getInt("h"), Math.max(1, one.getShort("s"))); }
            }
            for (long box : Longs.fromInts(tag.getIntArray("boxes"))) {
                if (cell.boxCount < BOX_CAP && !cell.hasBox(box)) { cell.boxes[cell.boxCount++] = box; }
            }
            return cell;
        }
    }
}
