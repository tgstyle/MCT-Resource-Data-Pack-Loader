package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCraterVerdicts extends SavedData {
    private static final String NAME = "rdpl_craters";
    private static final String CLAIMED = "Claimed";
    private static final String OPEN = "Open";
    private static final Factory<ContentCraterVerdicts> FACTORY = new Factory<>(ContentCraterVerdicts::new, (tag, lookup) -> read(tag));
    private final Map<Long, Boolean> verdicts = new ConcurrentHashMap<>();

    static ContentCraterVerdicts of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static ContentCraterVerdicts read(CompoundTag tag) {
        ContentCraterVerdicts held = new ContentCraterVerdicts();
        for (long key : tag.getLongArray(CLAIMED)) { held.verdicts.put(key, true); }
        for (long key : tag.getLongArray(OPEN)) { held.verdicts.put(key, false); }
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
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
