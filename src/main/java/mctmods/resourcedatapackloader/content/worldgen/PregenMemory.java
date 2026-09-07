package mctmods.resourcedatapackloader.content.worldgen;

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
    private CompoundTag run = new CompoundTag();
    private CompoundTag madeTo = new CompoundTag();
    private CompoundTag madeAt = new CompoundTag();

    public static PregenMemory of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(PregenMemory::read, PregenMemory::new, NAME); }

    private static PregenMemory read(CompoundTag tag) {
        PregenMemory held = new PregenMemory();
        held.run = tag.getCompound(RUN).copy();
        held.madeTo = tag.getCompound(MADE_TO).copy();
        held.madeAt = tag.getCompound(MADE_AT).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.put(RUN, run.copy());
        tag.put(MADE_TO, madeTo.copy());
        tag.put(MADE_AT, madeAt.copy());
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

    public void setMadeAt(String dimension, int count) {
        if (count <= 0) { madeAt.remove(dimension); }
        else { madeAt.putInt(dimension, count); }
        setDirty();
    }
}
