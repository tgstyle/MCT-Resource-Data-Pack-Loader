package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.util.world.SavedTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class RaidStorage extends SavedData {
    public static final String NAME = "rdpl_raids";
    private static final String RAIDS = "Raids";
    private static final SavedDataType<RaidStorage> TYPE = SavedTypes.of(NAME, RaidStorage::new, RaidStorage::read, RaidStorage::write);
    private final List<ActiveRaid> raids = new ArrayList<>();

    public static RaidStorage get(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }

    private static RaidStorage read(CompoundTag tag) {
        RaidStorage held = new RaidStorage();
        for (Tag element : tag.getListOrEmpty(RAIDS)) {
            CompoundTag one = (CompoundTag) element;
            ActiveRaid raid = ActiveRaid.read(one, ContentRaids.def(Identifier.tryParse(one.getStringOr(ActiveRaid.DEF_KEY, ""))));
            if (raid != null) { held.raids.add(raid); }
        }
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ActiveRaid raid : raids) {
            if (!raid.stopped()) { list.add(raid.write()); }
        }
        tag.put(RAIDS, list);
        return tag;
    }

    @Nullable private ActiveRaid first(Predicate<ActiveRaid> match) {
        for (ActiveRaid raid : raids) {
            if (!raid.stopped() && match.test(raid)) { return raid; }
        }
        return null;
    }

    public boolean raided(BlockPos center, int within) { return first(raid -> raid.center().distSqr(center) <= (double) within * within) != null; }

    @Nullable public ActiveRaid ownerRaid(int handle) {
        if (handle == 0) { return null; }
        return first(raid -> raid.owner() == handle);
    }

    @Nullable public ActiveRaid raidOf(BlockPos center) { return first(raid -> raid.center().equals(center)); }

    boolean crowds(ActiveRaid raid, int within) { return first(other -> other != raid && other.center().distSqr(raid.center()) <= (double) within * within) != null; }

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
