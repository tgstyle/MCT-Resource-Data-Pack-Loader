package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.SavedTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import javax.annotation.Nullable;

public final class PregenMemory extends SavedData {
    public static final String NAME = "rdpl_pregen";
    private static final String RUN = "Run";
    private static final String MADE_TO = "MadeTo";
    private static final String MADE_IN = "MadeIn";
    private CompoundTag run = new CompoundTag();
    private CompoundTag madeTo = new CompoundTag();
    private CompoundTag madeIn = new CompoundTag();
    private static final SavedDataType<PregenMemory> TYPE = SavedTypes.of(NAME, PregenMemory::new, PregenMemory::read, PregenMemory::write);

    public static PregenMemory of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static PregenMemory read(CompoundTag tag) {
        PregenMemory held = new PregenMemory();
        held.run = tag.getCompoundOrEmpty(RUN).copy();
        held.madeTo = tag.getCompoundOrEmpty(MADE_TO).copy();
        held.madeIn = tag.getCompoundOrEmpty(MADE_IN).copy();
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.put(RUN, run.copy());
        tag.put(MADE_TO, madeTo.copy());
        tag.put(MADE_IN, madeIn.copy());
        return tag;
    }

    @Nullable public CompoundTag run() { return run.isEmpty() ? null : run.copy(); }

    public void setRun(@Nullable CompoundTag record) {
        run = record == null ? new CompoundTag() : record.copy();
        setDirty();
    }

    public int madeTo(String dimension) { return madeTo.getIntOr(dimension, 0); }

    public void setMadeTo(String dimension, int reach) {
        madeTo.putInt(dimension, reach);
        setDirty();
    }

    @Nullable public CompoundTag madeIn(String dimension) { return madeIn.contains(dimension) ? madeIn.getCompoundOrEmpty(dimension) : null; }

    public void setMadeIn(String dimension, @Nullable CompoundTag spot) {
        if (spot == null) { madeIn.remove(dimension); }
        else { madeIn.put(dimension, spot.copy()); }
        setDirty();
    }
}
