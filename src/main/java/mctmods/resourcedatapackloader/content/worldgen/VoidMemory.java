package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class VoidMemory extends SavedData {
    private static final String NAME = "rdpl_void_world";
    private static final String ENABLED = "enabled";
    private static final String DIMENSIONS = "dimensions";
    private static final String ARE_BLACKLIST = "areBlacklist";
    private boolean recorded;
    private boolean enabled;
    private List<String> dimensions = List.of();
    private boolean areBlacklist;

    private static final Factory<VoidMemory> FACTORY = new Factory<>(VoidMemory::new, (tag, lookup) -> read(tag));

    public static VoidMemory of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static VoidMemory read(CompoundTag tag) {
        VoidMemory held = new VoidMemory();
        if (!tag.contains(ENABLED)) { return held; }
        held.recorded = true;
        held.enabled = tag.getBoolean(ENABLED);
        List<String> named = new ArrayList<>();
        ListTag list = tag.getList(DIMENSIONS, Tag.TAG_STRING);
        for (int index = 0; index < list.size(); index++) { named.add(list.getString(index)); }
        held.dimensions = List.copyOf(named);
        held.areBlacklist = tag.getBoolean(ARE_BLACKLIST);
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        if (!recorded) { return tag; }
        tag.putBoolean(ENABLED, enabled);
        ListTag list = new ListTag();
        for (String dimension : dimensions) { list.add(StringTag.valueOf(dimension)); }
        tag.put(DIMENSIONS, list);
        tag.putBoolean(ARE_BLACKLIST, areBlacklist);
        return tag;
    }

    public boolean recorded() { return recorded; }

    public boolean enabled() { return enabled; }

    public List<String> dimensions() { return dimensions; }

    public boolean areBlacklist() { return areBlacklist; }

    public void record(boolean asked, List<String> wanted, boolean blacklist) {
        recorded = true;
        enabled = asked;
        dimensions = List.copyOf(wanted);
        areBlacklist = blacklist;
        setDirty();
    }
}
