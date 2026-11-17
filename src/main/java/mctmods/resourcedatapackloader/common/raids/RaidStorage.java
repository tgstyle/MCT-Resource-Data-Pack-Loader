package mctmods.resourcedatapackloader.common.raids;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class RaidStorage extends SavedData {
    private static final String NAME = "rdpl_raids";
    private static final String RAIDS = "Raids";
    private static final Factory<RaidStorage> FACTORY = new Factory<>(RaidStorage::new, (tag, lookup) -> read(tag));
    private final List<ActiveRaid> raids = new ArrayList<>();

    public static RaidStorage get(ServerLevel level) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static RaidStorage read(CompoundTag tag) {
        RaidStorage held = new RaidStorage();
        for (Tag element : tag.getList(RAIDS, Tag.TAG_COMPOUND)) {
            CompoundTag one = (CompoundTag) element;
            ActiveRaid raid = ActiveRaid.read(one, ContentRaids.def(ResourceLocation.tryParse(one.getString("Raid"))));
            if (raid != null) { held.raids.add(raid); }
        }
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        ListTag list = new ListTag();
        for (ActiveRaid raid : raids) {
            if (!raid.stopped()) { list.add(raid.write()); }
        }
        tag.put(RAIDS, list);
        return tag;
    }

    public boolean raided(BlockPos center, int within) {
        for (ActiveRaid raid : raids) {
            if (!raid.stopped() && raid.center().distSqr(center) <= (double) within * within) { return true; }
        }
        return false;
    }

    @Nullable public ActiveRaid ownerRaid(int handle) {
        if (handle == 0) { return null; }
        for (ActiveRaid raid : raids) {
            if (!raid.stopped() && raid.owner() == handle) { return raid; }
        }
        return null;
    }

    @Nullable public ActiveRaid raidOf(BlockPos center) {
        for (ActiveRaid raid : raids) {
            if (!raid.stopped() && raid.center().equals(center)) { return raid; }
        }
        return null;
    }

    boolean crowds(ActiveRaid raid, int within) {
        for (ActiveRaid other : raids) {
            if (other != raid && !other.stopped() && other.center().distSqr(raid.center()) <= (double) within * within) { return true; }
        }
        return false;
    }

    public boolean underway(BlockPos at) {
        for (ActiveRaid raid : raids) {
            if (raid.underway(at)) { return true; }
        }
        return false;
    }

    public void add(ActiveRaid raid) {
        raids.add(raid);
        setDirty();
    }

    public void tick(ServerLevel level) {
        if (raids.isEmpty()) { return; }
        List<ActiveRaid> live = new ArrayList<>(raids);
        if (level.getGameTime() % ActiveRaid.SUPERVISE == 0) {
            for (ActiveRaid raid : live) { raid.supervise(level); }
        }
        for (ActiveRaid raid : live) { raid.tick(level); }
        raids.removeIf(ActiveRaid::stopped);
        setDirty();
    }
}
