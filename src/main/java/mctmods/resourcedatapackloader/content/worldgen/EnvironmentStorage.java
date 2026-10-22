package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public final class EnvironmentStorage extends SavedData {
    private static final String NAME = "rdpl_environments";
    private static final Factory<EnvironmentStorage> FACTORY = new Factory<>(EnvironmentStorage::new, (tag, lookup) -> read(tag));
    private final Map<ResourceLocation, List<String>> active = new LinkedHashMap<>();

    static EnvironmentStorage of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static EnvironmentStorage read(CompoundTag tag) {
        EnvironmentStorage held = new EnvironmentStorage();
        for (String key : tag.getAllKeys()) {
            ResourceLocation dimension = ResourceLocation.tryParse(key);
            ListTag list = tag.getList(key, Tag.TAG_STRING);
            List<String> names = new ArrayList<>();
            for (int index = 0; index < list.size(); index++) { names.add(list.getString(index)); }
            if (dimension != null && !names.isEmpty()) { held.active.put(dimension, names); }
        }
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        for (Map.Entry<ResourceLocation, List<String>> entry : active.entrySet()) {
            ListTag list = new ListTag();
            for (String name : entry.getValue()) { list.add(StringTag.valueOf(name)); }
            tag.put(entry.getKey().toString(), list);
        }
        return tag;
    }

    Map<ResourceLocation, List<String>> kept() { return Map.copyOf(active); }

    void keep(Map<ResourceLocation, List<String>> now) {
        if (active.equals(now)) { return; }
        active.clear();
        active.putAll(now);
        setDirty();
    }
}
