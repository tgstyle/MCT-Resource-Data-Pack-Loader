package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.util.world.SavedTypes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class GameRuleStorage extends SavedData {
    private static final String NAME = "rdpl_gamerules";
    private static final SavedDataType<GameRuleStorage> TYPE = SavedTypes.of(NAME, GameRuleStorage::new, GameRuleStorage::read, GameRuleStorage::write);
    private final Map<String, Map<String, String>> kept = new LinkedHashMap<>();
    private final Map<String, GameRules> live = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> baselines = new LinkedHashMap<>();

    static GameRuleStorage of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static GameRuleStorage read(CompoundTag tag) {
        GameRuleStorage held = new GameRuleStorage();
        for (String dimension : tag.keySet()) {
            CompoundTag rules = tag.getCompoundOrEmpty(dimension);
            Map<String, String> values = new LinkedHashMap<>();
            for (String name : rules.keySet()) { values.put(name, rules.getStringOr(name, "")); }
            values.values().removeIf(String::isEmpty);
            if (!values.isEmpty()) { held.kept.put(dimension, values); }
        }
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, Map<String, String>> entry : kept.entrySet()) {
            CompoundTag rules = new CompoundTag();
            for (Map.Entry<String, String> rule : entry.getValue().entrySet()) { rules.putString(rule.getKey(), rule.getValue()); }
            tag.put(entry.getKey(), rules);
        }
        return tag;
    }

    static Map<String, String> values(GameRules rules) {
        Map<String, String> values = new LinkedHashMap<>();
        rules.availableRules().forEach(rule -> values.put(rule.id(), rules.getAsString(rule)));
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
            setDirty();
        }
        return super.isDirty();
    }
}
