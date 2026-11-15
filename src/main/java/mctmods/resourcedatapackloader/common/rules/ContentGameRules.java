package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public final class ContentGameRules {
    private static final Map<Integer, GameRules> BY_DIMENSION = new HashMap<>();
    private static final Map<Integer, Map<String, String>> PACK = new HashMap<>();

    private ContentGameRules() {}

    public static void load() {
        BY_DIMENSION.clear();
        PACK.clear();
        Map<Integer, Map<String, String>> wanted = new LinkedHashMap<>();
        Json.eachFile(PackManager.GAMERULES, "game rule file", (key, contents) -> {
            for (Map.Entry<Integer, Map<String, String>> entry : ContentParserWorlds.gameRuleFile(key, contents).entrySet()) { wanted.computeIfAbsent(entry.getKey(), id -> new LinkedHashMap<>()).putAll(entry.getValue()); }
        });
        for (DimensionDef def : ContentDimensions.available().values()) {
            if (def.gameRules.isEmpty()) { continue; }
            wanted.computeIfAbsent(def.id, id -> new LinkedHashMap<>()).putAll(def.gameRules);
        }
        GameRules blank = new GameRules();
        for (Map.Entry<Integer, Map<String, String>> entry : wanted.entrySet()) {
            Map<String, String> known = new LinkedHashMap<>();
            for (Map.Entry<String, String> rule : entry.getValue().entrySet()) {
                if (!blank.hasRule(rule.getKey())) {
                    ContentLog.LOGGER.error("Dimension {} sets game rule '{}', which does not exist, ignoring it", entry.getKey(), rule.getKey());
                    continue;
                }
                known.put(rule.getKey(), rule.getValue());
            }
            PACK.put(entry.getKey(), known);
            BY_DIMENSION.put(entry.getKey(), build(known));
        }
        if (!BY_DIMENSION.isEmpty()) { Summary.info("gamerules", "Applying separate game rules in dimension(s) " + BY_DIMENSION.keySet()); }
    }

    @Nullable public static GameRules forWorld(World world) {
        if (BY_DIMENSION.isEmpty() || world == null || world.provider == null) { return null; }
        return BY_DIMENSION.get(world.provider.getDimension());
    }

    public static void serverStarted(MinecraftServer server) {
        if (BY_DIMENSION.isEmpty()) { return; }
        GameRuleStorage storage = GameRuleStorage.get(server);
        if (storage == null) { return; }
        for (Map.Entry<Integer, GameRules> entry : BY_DIMENSION.entrySet()) {
            String dimension = String.valueOf(entry.getKey());
            for (Map.Entry<String, String> rule : storage.saved(dimension).entrySet()) { entry.getValue().setOrCreateGameRule(rule.getKey(), rule.getValue()); }
            storage.watch(dimension, entry.getValue(), GameRuleStorage.values(build(PACK.get(entry.getKey()))));
        }
        for (String dimension : storage.unwatched()) { ContentLog.LOGGER.warn("Dimension {} was saved with its own game rules, but no pack gives it any now, so they are dropped", dimension); }
    }

    public static void serverStopped() {
        for (Map.Entry<Integer, Map<String, String>> entry : PACK.entrySet()) { BY_DIMENSION.put(entry.getKey(), build(entry.getValue())); }
    }

    private static GameRules build(Map<String, String> pack) {
        GameRules rules = new GameRules();
        for (Map.Entry<String, String> rule : pack.entrySet()) { rules.setOrCreateGameRule(rule.getKey(), rule.getValue()); }
        return rules;
    }
}
