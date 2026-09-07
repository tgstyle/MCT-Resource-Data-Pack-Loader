package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;

public final class ContentStructureCounts extends SavedData {
    private static final String NAME = "rdpl_structure_most";
    private static final String FOUNDED = "Founded";
    private static final Factory<ContentStructureCounts> FACTORY = new Factory<>(ContentStructureCounts::new, (tag, lookup) -> read(tag));
    private CompoundTag founded = new CompoundTag();

    private static ContentStructureCounts of(ServerLevel level) {
        synchronized (ContentStructureCounts.class) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }
    }

    private static ContentStructureCounts read(CompoundTag tag) {
        ContentStructureCounts held = new ContentStructureCounts();
        held.founded = tag.getCompound(FOUNDED).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        synchronized (this) { tag.put(FOUNDED, founded.copy()); }
        return tag;
    }

    public static int count(ServerLevel level, ResourceLocation structure) {
        ContentStructureCounts held = of(level);
        synchronized (held) { return held.founded.getInt(structure.toString()); }
    }

    public static int add(ServerLevel level, ResourceLocation structure) {
        ContentStructureCounts held = of(level);
        int now;
        synchronized (held) {
            now = held.founded.getInt(structure.toString()) + 1;
            held.founded.putInt(structure.toString(), now);
        }
        held.setDirty();
        return now;
    }
}
