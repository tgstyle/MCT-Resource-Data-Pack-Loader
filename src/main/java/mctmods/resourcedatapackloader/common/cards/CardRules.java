package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.CardParser;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class CardRules {
    private static final Map<String, CardRule> BY_KEY = new LinkedHashMap<>();
    private static final Map<String, List<CardRule>> BY_TRIGGER = new HashMap<>();
    private static final List<CardRule> SCANNED = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private CardRules() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        BY_KEY.clear();
        BY_TRIGGER.clear();
        SCANNED.clear();
        if (!Config.content.load) { return; }
        Map<ResourceLocation, CardRule> read = new LinkedHashMap<>();
        Json.eachFile(PackManager.CARDS, "card rule", (key, contents) -> {
            CardRule rule = CardParser.parse(key, contents);
            if (rule != null) { read.put(key, rule); }
        });
        for (Map.Entry<ResourceLocation, CardRule> entry : read.entrySet()) {
            CardRule rule = entry.getValue();
            if (!ContentRegistry.available(rule.requires, entry.getKey())) { continue; }
            BY_KEY.put(rule.key, rule);
            BY_TRIGGER.computeIfAbsent(rule.trigger, k -> new ArrayList<>()).add(rule);
            if (CardRule.SCANNED.contains(rule.trigger)) { SCANNED.add(rule); }
        }
        if (!BY_KEY.isEmpty()) { Summary.info("cards", "Loaded " + BY_KEY.size() + " card rule(s)"); }
    }

    public static boolean unset(String key) { return !BY_KEY.containsKey(key); }

    @Nullable static CardRule builtin(String key) { return BY_KEY.get(key); }

    static List<CardRule> on(String trigger) { return BY_TRIGGER.getOrDefault(trigger, Collections.emptyList()); }

    static List<CardRule> scanned() { return SCANNED; }

    static boolean timed() { return BY_TRIGGER.containsKey(CardRule.TIME_OF_DAY) || BY_TRIGGER.containsKey(CardRule.DAY); }

    @Nullable public static CardRule find(String asked) {
        CardRule exact = BY_KEY.get(asked);
        if (exact != null) { return exact; }
        for (CardRule rule : BY_KEY.values()) {
            if (IdNames.path(rule.key).equals(asked)) { return rule; }
        }
        return null;
    }

    public static List<String> keys() { return new ArrayList<>(BY_KEY.keySet()); }
}
