package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EnvironmentStorage extends WorldSavedData {
    private static final String NAME = "rdpl_environments";
    private final Map<Integer, List<String>> active = new LinkedHashMap<>();

    public EnvironmentStorage(String name) { super(name); }

    @Nullable static EnvironmentStorage get(MinecraftServer server) {
        MapStorage storage = server.getWorld(0).getMapStorage();
        if (storage == null) { return null; }
        return SavedData.get(storage, EnvironmentStorage.class, NAME, EnvironmentStorage::new);
    }

    Map<Integer, List<String>> kept() { return Collections.unmodifiableMap(new LinkedHashMap<>(active)); }

    void keep(Map<Integer, List<String>> now) {
        if (active.equals(now)) { return; }
        active.clear();
        active.putAll(now);
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        active.clear();
        for (String key : tag.getKeySet()) {
            NBTTagList list = tag.getTagList(key, Constants.NBT.TAG_STRING);
            List<String> names = new ArrayList<>();
            for (int index = 0; index < list.tagCount(); index++) { names.add(list.getStringTagAt(index)); }
            if (names.isEmpty()) { continue; }
            try { active.put(Integer.parseInt(key), names); }
            catch (NumberFormatException ignored) { markDirty(); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        for (Map.Entry<Integer, List<String>> entry : active.entrySet()) {
            NBTTagList list = new NBTTagList();
            for (String name : entry.getValue()) { list.appendTag(new NBTTagString(name)); }
            tag.setTag(String.valueOf(entry.getKey()), list);
        }
        return tag;
    }
}
