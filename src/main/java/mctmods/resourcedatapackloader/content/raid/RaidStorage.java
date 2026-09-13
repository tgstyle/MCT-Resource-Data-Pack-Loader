package mctmods.resourcedatapackloader.content.raid;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RaidStorage extends WorldSavedData {
    private static final String NAME = "rdpl_raids";
    private final List<ActiveRaid> raids = new ArrayList<>();

    public RaidStorage(String name) { super(name); }

    public static RaidStorage get(WorldServer world) { return SavedData.get(world.getPerWorldStorage(), RaidStorage.class, NAME, RaidStorage::new); }

    @Nullable public ActiveRaid near(BlockPos center, int within) {
        for (ActiveRaid raid : raids) {
            if (!raid.stopped() && raid.center().distanceSq(center) <= (double) within * within) { return raid; }
        }
        return null;
    }

    public boolean underway(BlockPos at) {
        for (ActiveRaid raid : raids) {
            if (raid.underway(at)) { return true; }
        }
        return false;
    }

    public void add(ActiveRaid raid) {
        raids.add(raid);
        markDirty();
    }

    public void tick(WorldServer world) {
        if (raids.isEmpty()) { return; }
        for (ActiveRaid raid : new ArrayList<>(raids)) { raid.tick(world); }
        raids.removeIf(ActiveRaid::stopped);
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        raids.clear();
        NBTTagList list = tag.getTagList("Raids", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound one = list.getCompoundTagAt(i);
            ActiveRaid raid = ActiveRaid.read(one, ContentRaids.def(new ResourceLocation(one.getString("Raid"))));
            if (raid != null) { raids.add(raid); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (ActiveRaid raid : raids) {
            if (!raid.stopped()) { list.appendTag(raid.write()); }
        }
        tag.setTag("Raids", list);
        return tag;
    }
}
