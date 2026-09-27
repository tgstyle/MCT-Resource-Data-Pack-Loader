package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.Collection;

public final class ContentStructureCounts extends SavedData {
    public static final String NAME = "rdpl_structure_most";
    private static final String FOUNDED = "Founded";
    private static final SavedDataType<ContentStructureCounts> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, NAME), ContentStructureCounts::new, CompoundTag.CODEC.xmap(ContentStructureCounts::read, ContentStructureCounts::write));
    private CompoundTag founded = new CompoundTag();

    private static ContentStructureCounts of(ServerLevel level) {
        synchronized (ContentStructureCounts.class) { return level.getDataStorage().computeIfAbsent(TYPE); }
    }

    private static ContentStructureCounts read(CompoundTag tag) {
        ContentStructureCounts held = new ContentStructureCounts();
        held.founded = tag.getCompoundOrEmpty(FOUNDED).copy();
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        synchronized (this) { tag.put(FOUNDED, founded.copy()); }
        return tag;
    }

    public static int count(ServerLevel level, Identifier structure) {
        ContentStructureCounts held = of(level);
        synchronized (held) { return held.founded.getIntOr(structure.toString(), 0); }
    }

    public static int total(ServerLevel level, Collection<Identifier> structures) {
        ContentStructureCounts held = of(level);
        synchronized (held) {
            int sum = 0;
            for (Identifier structure : structures) { sum += held.founded.getIntOr(structure.toString(), 0); }
            return sum;
        }
    }

    public static void add(ServerLevel level, Identifier structure) {
        ContentStructureCounts held = of(level);
        synchronized (held) { held.founded.putInt(structure.toString(), held.founded.getIntOr(structure.toString(), 0) + 1); }
        held.setDirty();
    }
}
