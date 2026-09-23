package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.Collection;
import javax.annotation.Nonnull;

public final class ContentStructureCounts extends SavedData {
    private static final String NAME = "rdpl_structure_most";
    private static final String FOUNDED = "Founded";
    private CompoundTag founded = new CompoundTag();

    private static ContentStructureCounts of(ServerLevel level) {
        synchronized (ContentStructureCounts.class) { return level.getDataStorage().computeIfAbsent(ContentStructureCounts::read, ContentStructureCounts::new, NAME); }
    }

    private static ContentStructureCounts read(CompoundTag tag) {
        ContentStructureCounts held = new ContentStructureCounts();
        held.founded = tag.getCompound(FOUNDED).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        synchronized (this) { tag.put(FOUNDED, founded.copy()); }
        return tag;
    }

    public static int count(ServerLevel level, ResourceLocation structure) {
        ContentStructureCounts held = of(level);
        synchronized (held) { return held.founded.getInt(structure.toString()); }
    }

    public static int total(ServerLevel level, Collection<ResourceLocation> structures) {
        ContentStructureCounts held = of(level);
        synchronized (held) {
            int sum = 0;
            for (ResourceLocation structure : structures) { sum += held.founded.getInt(structure.toString()); }
            return sum;
        }
    }

    public static void add(ServerLevel level, ResourceLocation structure) {
        ContentStructureCounts held = of(level);
        synchronized (held) { held.founded.putInt(structure.toString(), held.founded.getInt(structure.toString()) + 1); }
        held.setDirty();
    }
}
