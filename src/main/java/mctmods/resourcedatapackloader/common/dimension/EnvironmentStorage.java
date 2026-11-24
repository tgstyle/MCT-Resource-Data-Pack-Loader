package mctmods.resourcedatapackloader.common.dimension;

import mctmods.resourcedatapackloader.common.util.NbtStrings;
import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class EnvironmentStorage extends WorldSavedData {
    private static final String NAME = "rdpl_environments";
    private final Map<Integer, List<String>> active = new LinkedHashMap<>();

    public EnvironmentStorage(String name) { super(name); }

    @Nullable static EnvironmentStorage get(MinecraftServer server) {
        return SavedData.overworld(server, EnvironmentStorage.class, NAME, EnvironmentStorage::new);
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
            List<String> names = new ArrayList<>();
            NbtStrings.read(tag.getTagList(key, Constants.NBT.TAG_STRING), names);
            if (names.isEmpty()) { continue; }
            try { active.put(Integer.parseInt(key), names); }
            catch (NumberFormatException ignored) { markDirty(); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        for (Map.Entry<Integer, List<String>> entry : active.entrySet()) {
            tag.setTag(String.valueOf(entry.getKey()), NbtStrings.write(entry.getValue()));
        }
        return tag;
    }
}
