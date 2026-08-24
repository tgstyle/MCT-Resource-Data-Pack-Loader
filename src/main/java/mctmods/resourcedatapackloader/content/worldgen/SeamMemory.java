package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class SeamMemory extends WorldSavedData {
    private static final String ID = "rdpl_world_seams";
    private final Set<Long> entries = new HashSet<>();
    private final Map<Long, BlockPos> landings = new HashMap<>();

    public SeamMemory(String name) { super(name); }

    public static SeamMemory of(WorldServer world) {
        SeamMemory held = (SeamMemory) world.getPerWorldStorage().getOrLoadData(SeamMemory.class, ID);
        if (held != null) { return held; }
        SeamMemory made = new SeamMemory(ID);
        world.getPerWorldStorage().setData(ID, made);
        return made;
    }

    public static long column(int x, int z) { return ((long) x << 32) | (z & 0xFFFFFFFFL); }

    private static int columnX(long key) { return (int) (key >> 32); }

    private static int columnZ(long key) { return (int) key; }

    public void noteEntry(int x, int z) {
        if (entries.add(column(x, z))) { markDirty(); }
    }

    @Nullable public BlockPos entryNear(double x, double z, int reach) {
        long best = 0L;
        double closest = (double) reach * reach;
        boolean found = false;
        for (long key : entries) {
            double dx = columnX(key) + 0.5 - x;
            double dz = columnZ(key) + 0.5 - z;
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
        markDirty();
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        entries.clear();
        landings.clear();
        NBTTagList holes = nbt.getTagList("entries", 10);
        for (int index = 0; index < holes.tagCount(); index++) { entries.add(holes.getCompoundTagAt(index).getLong("column")); }
        NBTTagList stored = nbt.getTagList("landings", 10);
        for (int index = 0; index < stored.tagCount(); index++) {
            NBTTagCompound entry = stored.getCompoundTagAt(index);
            landings.put(entry.getLong("column"), BlockPos.fromLong(entry.getLong("spot")));
        }
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList holes = new NBTTagList();
        for (long key : entries) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong("column", key);
            holes.appendTag(entry);
        }
        nbt.setTag("entries", holes);
        NBTTagList spots = new NBTTagList();
        for (Map.Entry<Long, BlockPos> landing : landings.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong("column", landing.getKey());
            entry.setLong("spot", landing.getValue().toLong());
            spots.appendTag(entry);
        }
        nbt.setTag("landings", spots);
        return nbt;
    }
}
