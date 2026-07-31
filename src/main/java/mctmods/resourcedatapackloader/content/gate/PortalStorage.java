package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PortalStorage extends WorldSavedData {
    private static final String NAME = "rdpl_portals";
    private static final String TAG = "positions";
    private final Map<Long, String> positions = new LinkedHashMap<>();

    public PortalStorage(String name) { super(name); }

    public static void add(World world, BlockPos pos, @Nullable UUID owner) {
        PortalStorage data = get(world);
        String stored = owner == null ? "" : owner.toString();
        if (stored.equals(data.positions.get(pos.toLong()))) { return; }

        data.positions.put(pos.toLong(), stored);
        data.markDirty();
    }

    @Nullable public static UUID owner(World world, BlockPos pos) {
        String stored = get(world).positions.get(pos.toLong());
        if (stored == null || stored.isEmpty()) { return null; }

        try { return UUID.fromString(stored); }
        catch (IllegalArgumentException broken) { return null; }
    }

    public static void remove(World world, BlockPos pos) {
        PortalStorage data = get(world);
        if (data.positions.remove(pos.toLong()) == null) { return; }

        data.markDirty();
    }

    @Nullable public static BlockPos nearest(World world, BlockPos around) {
        PortalStorage data = get(world);
        if (data.positions.isEmpty()) { return null; }

        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        Set<Long> stale = new LinkedHashSet<>();
        for (long packed : data.positions.keySet()) {
            BlockPos at = BlockPos.fromLong(packed);
            if (world.isBlockLoaded(at) && !(world.getBlockState(at).getBlock() instanceof ContentBlockPortal)) {
                stale.add(packed);
                continue;
            }

            double distance = at.distanceSq(around);
            if (distance >= closest) { continue; }

            closest = distance;
            best = at;
        }
        if (!stale.isEmpty()) {
            data.positions.keySet().removeAll(stale);
            data.markDirty();
        }
        return best;
    }

    private static PortalStorage get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        PortalStorage data = (PortalStorage) storage.getOrLoadData(PortalStorage.class, NAME);
        if (data == null) {
            data = new PortalStorage(NAME);
            storage.setData(NAME, data);
        }
        return data;
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        positions.clear();
        NBTTagList list = compound.getTagList(TAG, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            BlockPos at = new BlockPos(entry.getInteger("x"), entry.getInteger("y"), entry.getInteger("z"));
            positions.put(at.toLong(), entry.getString("owner"));
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Long, String> stored : positions.entrySet()) {
            BlockPos at = BlockPos.fromLong(stored.getKey());
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("x", at.getX());
            entry.setInteger("y", at.getY());
            entry.setInteger("z", at.getZ());
            entry.setString("owner", stored.getValue());
            list.appendTag(entry);
        }
        compound.setTag(TAG, list);
        return compound;
    }
}
