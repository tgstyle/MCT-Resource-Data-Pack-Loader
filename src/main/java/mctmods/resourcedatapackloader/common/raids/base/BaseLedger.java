package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.SavedData;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class BaseLedger extends WorldSavedData {
    private static final String NAME = "rdpl_bases";
    private static final int VERSION = 1;
    private static final int PLACED_CAP = 4000;
    private static final int BOX_CAP = 8;
    private static final int WHO_CAP = 4;
    private static final int BOX_SHARE = 20;
    private static final int TICKS_PER_SHARE = 200;
    private static int cellCap = BaseDef.DEFAULT_CELL_CAP;
    private final Long2ObjectOpenHashMap<Cell> cells = new Long2ObjectOpenHashMap<>();
    private final Map<UUID, BlockPos> beds = new LinkedHashMap<>();
    private final Map<Integer, Long> raided = new LinkedHashMap<>();
    private long revision;

    public BaseLedger(String name) { super(name); }

    public static BaseLedger get(World world) { return SavedData.get(world.getPerWorldStorage(), BaseLedger.class, NAME, BaseLedger::new); }

    public static long key(BlockPos pos) { return ChunkPos.asLong(Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getZ())); }

    public static void setCellCap(int cap) { cellCap = Math.max(BaseDef.MIN_CELL_CAP, cap); }

    public long revision() { return revision; }

    @Nullable public Cell cell(World world, long key) {
        Cell cell = cells.get(key);
        if (cell != null && cell.normalize(world.getMinecraftServer())) { changed(); }
        return cell;
    }

    public void placed(EntityPlayer player, BlockPos pos, int delta) {
        if (delta == 0) { return; }
        int handle = RaidOwner.of(player);
        Cell cell = delta > 0 ? credited(player, pos, handle, delta) : contributed(player.world, pos, handle);
        if (cell == null) { return; }
        cell.placed = MathHelper.clamp(cell.placed + delta, 0, PLACED_CAP);
        stamp(player.world, cell);
    }

    public void opened(EntityPlayer player, BlockPos container) {
        long packed = container.toLong();
        Cell found = cells.get(key(container));
        if (found != null && (found.boxCount >= BOX_CAP || found.hasBox(packed))) { return; }
        Cell cell = credited(player, container, RaidOwner.of(player), BOX_SHARE);
        if (cell == null || cell.boxCount >= BOX_CAP || cell.hasBox(packed)) { return; }
        cell.boxes[cell.boxCount++] = packed;
        stamp(player.world, cell);
    }

    public void ticks(EntityPlayer player, BlockPos pos, int amount) {
        if (amount <= 0) { return; }
        Cell cell = credited(player, pos, RaidOwner.of(player), Math.max(1, amount / TICKS_PER_SHARE));
        if (cell == null) { return; }
        cell.ticks = (int) Math.min(Integer.MAX_VALUE, (long) cell.ticks + amount);
        stamp(player.world, cell);
    }

    public void setBed(UUID player, BlockPos pos) {
        if (pos.equals(beds.get(player))) { return; }
        beds.put(player, pos.toImmutable());
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

    @Nullable private Cell credited(EntityPlayer player, BlockPos pos, int handle, int share) {
        if (handle == 0) { return null; }
        long key = key(pos);
        Cell cell = cell(player.world, key);
        if (cell != null && !admits(player, pos, cell, handle)) { return null; }
        if (cell == null) {
            evictAtCap();
            cell = new Cell(key);
            cells.put(key, cell);
        }
        cell.credit(handle, share);
        return cell;
    }

    @Nullable private Cell contributed(World world, BlockPos pos, int handle) {
        Cell cell = cell(world, key(pos));
        return cell != null && cell.holds(handle) ? cell : null;
    }

    private static boolean admits(EntityPlayer player, BlockPos pos, Cell cell, int handle) {
        if (cell.contributors == 0 || cell.holds(handle) || ClaimOwners.isMember(player, cell.top())) { return true; }
        return ClaimGuard.covers(player.world, pos) != 0 && ClaimGuard.passes(player, player.world, pos);
    }

    private void evictAtCap() {
        if (cells.size() < cellCap) { return; }
        LongOpenHashSet bedded = new LongOpenHashSet();
        for (BlockPos bed : beds.values()) { bedded.add(key(bed)); }
        List<Cell> order = new ArrayList<>(cells.values());
        order.sort(Comparator.comparingDouble((Cell cell) -> cell.raw() + (bedded.contains(cell.key) ? BaseDef.BED_WEIGHT : 0)).thenComparingLong(Cell::last));
        int drop = Math.min(order.size(), Math.max(order.size() - cellCap + 1, order.size() / 20));
        for (int i = 0; i < drop; i++) { cells.remove(order.get(i).key); }
    }

    private void stamp(World world, Cell cell) {
        cell.last = world.getTotalWorldTime();
        changed();
    }

    private void changed() {
        revision++;
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        cells.clear();
        beds.clear();
        raided.clear();
        NBTTagList cellList = compound.getTagList("Cells", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < cellList.tagCount(); i++) {
            Cell cell = Cell.read(cellList.getCompoundTagAt(i));
            cells.put(cell.key, cell);
        }
        NBTTagList bedList = compound.getTagList("Beds", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < bedList.tagCount(); i++) {
            NBTTagCompound one = bedList.getCompoundTagAt(i);
            UUID id;
            try {
                id = UUID.fromString(one.getString("u"));
            } catch (IllegalArgumentException bad) {
                continue;
            }
            beds.put(id, BlockPos.fromLong(one.getLong("p")));
        }
        NBTTagList raidList = compound.getTagList("Raided", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < raidList.tagCount(); i++) {
            NBTTagCompound one = raidList.getCompoundTagAt(i);
            if (one.getInteger("h") != 0) { raided.put(one.getInteger("h"), one.getLong("t")); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("v", VERSION);
        NBTTagList cellList = new NBTTagList();
        for (Cell cell : cells.values()) { cellList.appendTag(cell.write()); }
        compound.setTag("Cells", cellList);
        NBTTagList bedList = new NBTTagList();
        for (Map.Entry<UUID, BlockPos> entry : beds.entrySet()) {
            NBTTagCompound one = new NBTTagCompound();
            one.setString("u", entry.getKey().toString());
            one.setLong("p", entry.getValue().toLong());
            bedList.appendTag(one);
        }
        compound.setTag("Beds", bedList);
        NBTTagList raidList = new NBTTagList();
        for (Map.Entry<Integer, Long> entry : raided.entrySet()) {
            NBTTagCompound one = new NBTTagCompound();
            one.setInteger("h", entry.getKey());
            one.setLong("t", entry.getValue());
            raidList.appendTag(one);
        }
        compound.setTag("Raided", raidList);
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

        private double raw() { return boxCount * BaseDef.CHEST_WEIGHT + placed * BaseDef.PLACED_WEIGHT + Math.min(ticks / 1200, 60) * BaseDef.MINUTES_WEIGHT; }

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
            if (contributors == 0 || OwnerTable.get(server) == null) { return false; }
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

        private NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("k", key);
            tag.setShort("placed", (short) placed);
            tag.setInteger("ticks", ticks);
            tag.setLong("last", last);
            NBTTagList who = new NBTTagList();
            for (int i = 0; i < contributors; i++) {
                NBTTagCompound one = new NBTTagCompound();
                one.setInteger("h", handles[i]);
                one.setShort("s", (short) shares[i]);
                who.appendTag(one);
            }
            tag.setTag("who", who);
            tag.setIntArray("boxes", Longs.toInts(Arrays.copyOf(boxes, boxCount)));
            return tag;
        }

        private static Cell read(NBTTagCompound tag) {
            Cell cell = new Cell(tag.getLong("k"));
            cell.placed = MathHelper.clamp(tag.getShort("placed"), 0, PLACED_CAP);
            cell.ticks = Math.max(0, tag.getInteger("ticks"));
            cell.last = tag.getLong("last");
            NBTTagList who = tag.getTagList("who", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < who.tagCount(); i++) {
                NBTTagCompound one = who.getCompoundTagAt(i);
                if (one.getInteger("h") != 0) { cell.credit(one.getInteger("h"), Math.max(1, one.getShort("s"))); }
            }
            for (long box : Longs.fromInts(tag.getIntArray("boxes"))) {
                if (cell.boxCount < BOX_CAP && !cell.hasBox(box)) { cell.boxes[cell.boxCount++] = box; }
            }
            return cell;
        }
    }
}
