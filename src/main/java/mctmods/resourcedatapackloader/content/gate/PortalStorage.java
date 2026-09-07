package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.block.ContentPortalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PortalStorage extends SavedData {
    private static final String NAME = "rdpl_portals";
    private static final String TAG = "Positions";
    private final Map<Long, String> positions = new LinkedHashMap<>();

    private static final Factory<PortalStorage> FACTORY = new Factory<>(PortalStorage::new, (tag, lookup) -> read(tag));

    private static PortalStorage of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static PortalStorage read(CompoundTag tag) {
        PortalStorage held = new PortalStorage();
        for (Tag entry : tag.getList(TAG, Tag.TAG_COMPOUND)) {
            CompoundTag stored = (CompoundTag) entry;
            held.positions.put(stored.getLong("At"), stored.getString("Owner"));
        }
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        ListTag list = new ListTag();
        for (Map.Entry<Long, String> stored : positions.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("At", stored.getKey());
            entry.putString("Owner", stored.getValue());
            list.add(entry);
        }
        tag.put(TAG, list);
        return tag;
    }

    public static void add(ServerLevel level, BlockPos pos, @Nullable UUID owner) {
        PortalStorage data = of(level);
        String stored = owner == null ? "" : owner.toString();
        if (stored.equals(data.positions.get(pos.asLong()))) { return; }
        data.positions.put(pos.asLong(), stored);
        data.setDirty();
    }

    @Nullable public static UUID owner(ServerLevel level, BlockPos pos) {
        String stored = of(level).positions.get(pos.asLong());
        if (stored == null || stored.isEmpty()) { return null; }
        try { return UUID.fromString(stored); }
        catch (IllegalArgumentException broken) { return null; }
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        PortalStorage data = of(level);
        if (data.positions.remove(pos.asLong()) == null) { return; }
        data.setDirty();
    }

    @Nullable public static BlockPos nearest(ServerLevel level, BlockPos around) {
        PortalStorage data = of(level);
        if (data.positions.isEmpty()) { return null; }
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        Set<Long> stale = new LinkedHashSet<>();
        for (long packed : data.positions.keySet()) {
            BlockPos at = BlockPos.of(packed);
            if (level.isLoaded(at) && !(level.getBlockState(at).getBlock() instanceof ContentPortalBlock)) {
                stale.add(packed);
                continue;
            }
            double distance = at.distSqr(around);
            if (distance >= closest) { continue; }
            closest = distance;
            best = at;
        }
        if (!stale.isEmpty()) {
            data.positions.keySet().removeAll(stale);
            data.setDirty();
        }
        return best;
    }
}
