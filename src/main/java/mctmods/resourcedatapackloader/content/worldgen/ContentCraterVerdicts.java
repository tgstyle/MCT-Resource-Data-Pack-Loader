package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.SavedTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentCraterVerdicts extends SavedData {
    private static final String NAME = "rdpl_craters";
    private static final String CLAIMED = "Claimed";
    private static final String OPEN = "Open";
    private static final SavedDataType<ContentCraterVerdicts> TYPE = SavedTypes.of(NAME, ContentCraterVerdicts::new, ContentCraterVerdicts::read, ContentCraterVerdicts::write);
    private final Map<Long, Boolean> verdicts = new ConcurrentHashMap<>();

    static ContentCraterVerdicts of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }

    private static ContentCraterVerdicts read(CompoundTag tag) {
        ContentCraterVerdicts held = new ContentCraterVerdicts();
        for (long key : tag.getLongArray(CLAIMED).orElse(new long[0])) { held.verdicts.put(key, true); }
        for (long key : tag.getLongArray(OPEN).orElse(new long[0])) { held.verdicts.put(key, false); }
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putLongArray(CLAIMED, verdicts.entrySet().stream().filter(Map.Entry::getValue).mapToLong(Map.Entry::getKey).toArray());
        tag.putLongArray(OPEN, verdicts.entrySet().stream().filter(entry -> !entry.getValue()).mapToLong(Map.Entry::getKey).toArray());
        return tag;
    }

    @Nullable Boolean claimed(long key) { return verdicts.get(key); }

    boolean settle(long key, boolean claimed) {
        Boolean before = verdicts.putIfAbsent(key, claimed);
        if (before != null) { return before; }
        setDirty();
        return claimed;
    }
}
