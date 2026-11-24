package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.WorldSavedData;

public final class GameRuleStorage extends WorldSavedData {
    private static final String NAME = "rdpl_gamerules";
    private final Map<String, Map<String, String>> kept = new LinkedHashMap<>();
    private final Map<String, GameRules> live = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> baselines = new LinkedHashMap<>();

    public GameRuleStorage(String name) { super(name); }

    @Nullable static GameRuleStorage get(MinecraftServer server) {
        return SavedData.overworld(server, GameRuleStorage.class, NAME, GameRuleStorage::new);
    }

    static Map<String, String> values(GameRules rules) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String name : rules.getRules()) { values.put(name, rules.getString(name)); }
        return values;
    }

    Map<String, String> saved(String dimension) { return Collections.unmodifiableMap(kept.getOrDefault(dimension, Collections.emptyMap())); }

    void watch(String dimension, GameRules rules, Map<String, String> baseline) {
        live.put(dimension, rules);
        baselines.put(dimension, baseline);
    }

    Set<String> unwatched() {
        Set<String> left = new LinkedHashSet<>(kept.keySet());
        left.removeAll(live.keySet());
        return left;
    }

    private Map<String, Map<String, String>> changed() {
        Map<String, Map<String, String>> now = new LinkedHashMap<>();
        for (Map.Entry<String, GameRules> entry : live.entrySet()) {
            Map<String, String> baseline = baselines.get(entry.getKey());
            Map<String, String> differs = new LinkedHashMap<>();
            for (Map.Entry<String, String> rule : values(entry.getValue()).entrySet()) {
                if (!rule.getValue().equals(baseline.get(rule.getKey()))) { differs.put(rule.getKey(), rule.getValue()); }
            }
            if (!differs.isEmpty()) { now.put(entry.getKey(), differs); }
        }
        return now;
    }

    @Override public boolean isDirty() {
        Map<String, Map<String, String>> now = changed();
        if (!now.equals(kept)) {
            kept.clear();
            kept.putAll(now);
            markDirty();
        }
        return super.isDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        kept.clear();
        for (String dimension : tag.getKeySet()) {
            NBTTagCompound rules = tag.getCompoundTag(dimension);
            Map<String, String> values = new LinkedHashMap<>();
            for (String name : rules.getKeySet()) { values.put(name, rules.getString(name)); }
            if (!values.isEmpty()) { kept.put(dimension, values); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        for (Map.Entry<String, Map<String, String>> entry : kept.entrySet()) {
            NBTTagCompound rules = new NBTTagCompound();
            for (Map.Entry<String, String> rule : entry.getValue().entrySet()) { rules.setString(rule.getKey(), rule.getValue()); }
            tag.setTag(entry.getKey(), rules);
        }
        return tag;
    }
}
