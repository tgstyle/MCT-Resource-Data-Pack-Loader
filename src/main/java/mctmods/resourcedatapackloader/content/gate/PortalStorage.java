package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentPortalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class PortalStorage extends SavedData {
    public static final String NAME = "rdpl_portals";
    private static final String TAG = "positions";
    private static final String EARLIER_TAG = "Positions";
    private final Map<Long, String> positions = new LinkedHashMap<>();
    private static final SavedDataType<PortalStorage> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, NAME), PortalStorage::new, CompoundTag.CODEC.xmap(PortalStorage::read, PortalStorage::write));

    private static PortalStorage of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }

    private static PortalStorage read(CompoundTag tag) {
        PortalStorage held = new PortalStorage();
        for (Tag entry : tag.getListOrEmpty(EARLIER_TAG)) {
            CompoundTag stored = (CompoundTag) entry;
            held.positions.put(stored.getLongOr("At", 0L), stored.getStringOr("Owner", ""));
        }
        for (Tag entry : tag.getListOrEmpty(TAG)) {
            CompoundTag stored = (CompoundTag) entry;
            held.positions.put(BlockPos.asLong(stored.getIntOr("x", 0), stored.getIntOr("y", 0), stored.getIntOr("z", 0)), stored.getStringOr("owner", ""));
        }
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (Map.Entry<Long, String> stored : positions.entrySet()) {
            BlockPos at = BlockPos.of(stored.getKey());
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", at.getX());
            entry.putInt("y", at.getY());
            entry.putInt("z", at.getZ());
            entry.putString("owner", stored.getValue());
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
