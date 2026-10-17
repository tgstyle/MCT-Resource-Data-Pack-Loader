package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Longs;
import mctmods.resourcedatapackloader.util.world.SavedData;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentCraterVerdicts extends WorldSavedData {
    private static final String NAME = "RDPLCraters";
    private final Long2BooleanOpenHashMap held = new Long2BooleanOpenHashMap();

    public ContentCraterVerdicts(String name) { super(name); }

    public static ContentCraterVerdicts of(World world) { return SavedData.get(world.getPerWorldStorage(), ContentCraterVerdicts.class, NAME, ContentCraterVerdicts::new); }

    @Nullable public Boolean claimed(long crater) { return held.containsKey(crater) ? held.get(crater) : null; }

    public void put(long crater, boolean claimed) {
        held.put(crater, claimed);
        markDirty();
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        held.clear();
        for (long crater : Longs.fromInts(nbt.getIntArray("Claimed"))) { held.put(crater, true); }
        for (long crater : Longs.fromInts(nbt.getIntArray("Open"))) { held.put(crater, false); }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        LongArrayList claimed = new LongArrayList();
        LongArrayList open = new LongArrayList();
        for (Long2BooleanMap.Entry entry : held.long2BooleanEntrySet()) { (entry.getBooleanValue() ? claimed : open).add(entry.getLongKey()); }
        nbt.setIntArray("Claimed", Longs.toInts(claimed.toLongArray()));
        nbt.setIntArray("Open", Longs.toInts(open.toLongArray()));
        return nbt;
    }
}
