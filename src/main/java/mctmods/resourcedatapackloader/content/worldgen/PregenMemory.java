package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PregenMemory extends SavedData {
    private static final String NAME = "rdpl_pregen";
    private static final String RUN = "Run";
    private static final String MADE_TO = "MadeTo";
    private static final String MADE_AT = "MadeAt";
    private static final String MADE_IN = "MadeIn";
    private CompoundTag run = new CompoundTag();
    private CompoundTag madeTo = new CompoundTag();
    private CompoundTag madeAt = new CompoundTag();
    private CompoundTag madeIn = new CompoundTag();

    private static final Factory<PregenMemory> FACTORY = new Factory<>(PregenMemory::new, (tag, lookup) -> read(tag));

    public static PregenMemory of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static PregenMemory read(CompoundTag tag) {
        PregenMemory held = new PregenMemory();
        held.run = tag.getCompound(RUN).copy();
        held.madeTo = tag.getCompound(MADE_TO).copy();
        held.madeAt = tag.getCompound(MADE_AT).copy();
        held.madeIn = tag.getCompound(MADE_IN).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        tag.put(RUN, run.copy());
        tag.put(MADE_TO, madeTo.copy());
        tag.put(MADE_AT, madeAt.copy());
        tag.put(MADE_IN, madeIn.copy());
        return tag;
    }

    @Nullable public CompoundTag run() { return run.isEmpty() ? null : run.copy(); }

    public void setRun(@Nullable CompoundTag record) {
        run = record == null ? new CompoundTag() : record.copy();
        setDirty();
    }

    public int madeTo(String dimension) { return madeTo.getInt(dimension); }

    public void setMadeTo(String dimension, int reach) {
        madeTo.putInt(dimension, reach);
        setDirty();
    }

    public int madeAt(String dimension) { return madeAt.getInt(dimension); }

    @Nullable public CompoundTag madeIn(String dimension) { return madeIn.contains(dimension) ? madeIn.getCompound(dimension) : null; }

    public void setMadeIn(String dimension, @Nullable CompoundTag spot) {
        madeAt.remove(dimension);
        if (spot == null) { madeIn.remove(dimension); }
        else { madeIn.put(dimension, spot.copy()); }
        setDirty();
    }
}
