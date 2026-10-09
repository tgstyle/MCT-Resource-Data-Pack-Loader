package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Longs;
import mctmods.resourcedatapackloader.util.SavedTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class SeamMemory extends SavedData {
    public static final String NAME = "rdpl_world_seams";
    private static final String ENTRIES = "entries";
    private static final String LANDINGS = "landings";
    private static final String COLUMN = "column";
    private static final String SPOT = "spot";
    private static final String OLD_ENTRIES = "Entries";
    private static final String OLD_LANDINGS = "Landings";
    private final Set<Long> entries = new HashSet<>();
    private final Map<Long, BlockPos> landings = new HashMap<>();
    private static final SavedDataType<SeamMemory> TYPE = SavedTypes.of(NAME, SeamMemory::new, SeamMemory::read, SeamMemory::write);

    public static SeamMemory of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }

    private static SeamMemory read(CompoundTag tag) {
        SeamMemory held = new SeamMemory();
        for (Tag entry : tag.getListOrEmpty(ENTRIES)) { held.entries.add(((CompoundTag) entry).getLongOr(COLUMN, 0L)); }
        for (long column : tag.getLongArray(OLD_ENTRIES).orElse(new long[0])) { held.entries.add(column); }
        held.readLandings(tag.getListOrEmpty(LANDINGS), COLUMN, SPOT);
        held.readLandings(tag.getListOrEmpty(OLD_LANDINGS), "Column", "Spot");
        return held;
    }

    private void readLandings(ListTag stored, String column, String spot) {
        for (Tag entry : stored) {
            CompoundTag held = (CompoundTag) entry;
            landings.put(held.getLongOr(column, 0L), BlockPos.of(held.getLongOr(spot, 0L)));
        }
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag holes = new ListTag();
        for (long key : entries) {
            CompoundTag entry = new CompoundTag();
            entry.putLong(COLUMN, key);
            holes.add(entry);
        }
        tag.put(ENTRIES, holes);
        ListTag spots = new ListTag();
        for (Map.Entry<Long, BlockPos> landing : landings.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong(COLUMN, landing.getKey());
            entry.putLong(SPOT, landing.getValue().asLong());
            spots.add(entry);
        }
        tag.put(LANDINGS, spots);
        return tag;
    }

    public static long column(int x, int z) { return Longs.pack(x, z); }

    public void noteEntry(int x, int z) { if (entries.add(column(x, z))) { setDirty(); } }

    @Nullable public BlockPos entryNear(double x, double z, int reach) {
        long best = 0L;
        double closest = (double) reach * reach;
        boolean found = false;
        for (long key : entries) {
            double dx = Longs.high(key) + 0.5D - x;
            double dz = Longs.low(key) + 0.5D - z;
            double away = dx * dx + dz * dz;
            if (away > closest) { continue; }
            closest = away;
            best = key;
            found = true;
        }
        return found ? new BlockPos(Longs.high(best), 0, Longs.low(best)) : null;
    }

    @Nullable public BlockPos landingFor(long key) { return landings.get(key); }

    public void rememberLanding(long key, BlockPos spot) {
        if (spot.equals(landings.get(key))) { return; }
        landings.put(key, spot);
        setDirty();
    }
}
