package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class RaidStorage extends WorldSavedData {
    private static final String NAME = "rdpl_raids";
    private final List<ActiveRaid> raids = new ArrayList<>();

    public RaidStorage(String name) { super(name); }

    public static RaidStorage get(WorldServer world) { return SavedData.get(world.getPerWorldStorage(), RaidStorage.class, NAME, RaidStorage::new); }

    @Nullable private ActiveRaid first(Predicate<ActiveRaid> match) {
        for (ActiveRaid raid : raids) {
            if (!raid.stopped() && match.test(raid)) { return raid; }
        }
        return null;
    }

    @Nullable public ActiveRaid near(BlockPos center, int within) { return first(raid -> raid.center().distanceSq(center) <= (double) within * within); }

    @Nullable public ActiveRaid ownerRaid(int handle) {
        if (handle == 0) { return null; }
        return first(raid -> raid.owner() == handle);
    }

    @Nullable public ActiveRaid raidOf(BlockPos center) { return first(raid -> raid.center().equals(center)); }

    boolean crowds(ActiveRaid raid, int within) { return first(other -> other != raid && other.center().distanceSq(raid.center()) <= (double) within * within) != null; }

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
        List<ActiveRaid> live = new ArrayList<>(raids);
        if (world.getTotalWorldTime() % ActiveRaid.SUPERVISE == 0) {
            for (ActiveRaid raid : live) { raid.supervise(world); }
        }
        for (ActiveRaid raid : live) { raid.tick(world); }
        raids.removeIf(ActiveRaid::stopped);
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        raids.clear();
        NBTTagList list = tag.getTagList("Raids", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound one = list.getCompoundTagAt(i);
            ActiveRaid raid = ActiveRaid.read(one, ContentRaids.def(new ResourceLocation(one.getString(ActiveRaid.DEF_KEY))));
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
