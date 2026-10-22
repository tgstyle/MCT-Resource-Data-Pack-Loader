package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.SavedTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EnvironmentStorage extends SavedData {
    private static final String NAME = "rdpl_environments";
    private static final SavedDataType<EnvironmentStorage> TYPE = SavedTypes.of(NAME, EnvironmentStorage::new, EnvironmentStorage::read, EnvironmentStorage::write);
    private final Map<Identifier, List<String>> active = new LinkedHashMap<>();

    static EnvironmentStorage of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static EnvironmentStorage read(CompoundTag tag) {
        EnvironmentStorage held = new EnvironmentStorage();
        for (String key : tag.keySet()) {
            Identifier dimension = Identifier.tryParse(key);
            ListTag list = tag.getListOrEmpty(key);
            List<String> names = new ArrayList<>();
            for (int index = 0; index < list.size(); index++) { names.add(list.getStringOr(index, "")); }
            names.removeIf(String::isEmpty);
            if (dimension != null && !names.isEmpty()) { held.active.put(dimension, names); }
        }
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<Identifier, List<String>> entry : active.entrySet()) {
            ListTag list = new ListTag();
            for (String name : entry.getValue()) { list.add(StringTag.valueOf(name)); }
            tag.put(entry.getKey().toString(), list);
        }
        return tag;
    }

    Map<Identifier, List<String>> kept() { return Map.copyOf(active); }

    void keep(Map<Identifier, List<String>> now) {
        if (active.equals(now)) { return; }
        active.clear();
        active.putAll(now);
        setDirty();
    }
}
