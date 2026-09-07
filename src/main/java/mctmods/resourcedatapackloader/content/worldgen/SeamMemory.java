package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SeamMemory extends SavedData {
    private static final String NAME = "rdpl_world_seams";
    private static final String ENTRIES = "Entries";
    private static final String LANDINGS = "Landings";
    private final Set<Long> entries = new HashSet<>();
    private final Map<Long, BlockPos> landings = new HashMap<>();

    public static SeamMemory of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(SeamMemory::read, SeamMemory::new, NAME); }

    private static SeamMemory read(CompoundTag tag) {
        SeamMemory held = new SeamMemory();
        for (long column : tag.getLongArray(ENTRIES)) { held.entries.add(column); }
        for (Tag entry : tag.getList(LANDINGS, Tag.TAG_COMPOUND)) {
            CompoundTag stored = (CompoundTag) entry;
            held.landings.put(stored.getLong("Column"), BlockPos.of(stored.getLong("Spot")));
        }
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.putLongArray(ENTRIES, entries.stream().mapToLong(Long::longValue).toArray());
        ListTag spots = new ListTag();
        for (Map.Entry<Long, BlockPos> landing : landings.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("Column", landing.getKey());
            entry.putLong("Spot", landing.getValue().asLong());
            spots.add(entry);
        }
        tag.put(LANDINGS, spots);
        return tag;
    }

    public static long column(int x, int z) { return ((long) x << 32) | (z & 0xFFFFFFFFL); }

    private static int columnX(long column) { return (int) (column >> 32); }

    private static int columnZ(long column) { return (int) column; }

    public void noteEntry(int x, int z) {
        if (entries.add(column(x, z))) { setDirty(); }
    }

    @Nullable public BlockPos entryNear(double x, double z, int reach) {
        long best = 0L;
        double closest = (double) reach * reach;
        boolean found = false;
        for (long key : entries) {
            double dx = columnX(key) + 0.5D - x;
            double dz = columnZ(key) + 0.5D - z;
            double away = dx * dx + dz * dz;
            if (away > closest) { continue; }
            closest = away;
            best = key;
            found = true;
        }
        return found ? new BlockPos(columnX(best), 0, columnZ(best)) : null;
    }

    @Nullable public BlockPos landingFor(long key) { return landings.get(key); }

    public void rememberLanding(long key, BlockPos spot) {
        if (spot.equals(landings.get(key))) { return; }
        landings.put(key, spot);
        setDirty();
    }
}
