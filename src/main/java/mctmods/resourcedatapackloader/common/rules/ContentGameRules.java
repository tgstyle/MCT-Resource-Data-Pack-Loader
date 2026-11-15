package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.generated.ContentFormats;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentDimensionParser;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

public final class ContentGameRules {
    private static final Map<Identifier, Map<String, String>> BY_DIMENSION = new LinkedHashMap<>();
    private static final Map<Level, GameRules> BUILT = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Identifier, Map<GameRule<?>, String>> WANTED = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentGameRules() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.GAMERULES, "game rule file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "game rule file");
            if (json == null) { return; }
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    ContentLog.LOGGER.error("Game rule file {} maps dimension {} to something that is not a set of rules, ignoring it", key, entry.getKey());
                    continue;
                }
                Identifier dimension = Identifier.tryParse(ContentFormats.dimensionId(entry.getKey()));
                if (dimension == null) {
                    ContentLog.LOGGER.error("Game rule file {} names dimension '{}', which is not a dimension id, ignoring it", key, entry.getKey());
                    continue;
                }
                ContentDimensionParser.rules(dimension, entry.getValue().getAsJsonObject(), BY_DIMENSION.computeIfAbsent(dimension, _ -> new LinkedHashMap<>()));
            }
        });
        for (DimensionDef def : ContentDimensions.available()) {
            if (def.gameRules().isEmpty()) { continue; }
            BY_DIMENSION.computeIfAbsent(def.key(), _ -> new LinkedHashMap<>()).putAll(def.gameRules());
        }
        if (BY_DIMENSION.isEmpty()) { return; }
        for (Map.Entry<Identifier, Map<String, String>> entry : BY_DIMENSION.entrySet()) {
            Map<GameRule<?>, String> rules = new LinkedHashMap<>();
            for (Map.Entry<String, String> rule : GameRuleNames.modernize(entry.getValue()).entrySet()) {
                GameRule<?> known = GameRuleNames.rule(rule.getKey());
                if (known == null) {
                    ContentLog.LOGGER.error("Dimension {} sets game rule '{}', which does not exist, ignoring it", entry.getKey(), rule.getKey());
                    continue;
                }
                rules.put(known, rule.getValue());
            }
            if (!rules.isEmpty()) { WANTED.put(entry.getKey(), rules); }
        }
        if (!WANTED.isEmpty()) { Summary.info("gamerules", "Applying separate game rules in dimension(s) " + WANTED.keySet()); }
    }

    @Nullable public static GameRules forLevel(Level level) {
        if (WANTED.isEmpty()) { return null; }
        GameRules held = BUILT.get(level);
        if (held != null) { return held; }
        Map<GameRule<?>, String> wanted = WANTED.get(level.dimension().identifier());
        if (wanted == null) { return null; }
        held = build(level, wanted);
        BUILT.put(level, held);
        if (level instanceof ServerLevel serverLevel && level.dimension() == Level.OVERWORLD && wanted.containsKey(GameRules.ADVANCE_TIME)) { daylight(serverLevel.getServer(), held); }
        ContentLog.LOGGER.debug("Dimension {} keeps its own game rules: {}", level.dimension().identifier(), wanted);
        return held;
    }

    public static void serverStarted(MinecraftServer server) {
        if (WANTED.isEmpty()) { return; }
        GameRuleStorage storage = GameRuleStorage.of(server);
        for (ServerLevel level : server.getAllLevels()) {
            GameRules held = forLevel(level);
            if (held == null) { continue; }
            String dimension = level.dimension().identifier().toString();
            Map<String, String> saved = storage.saved(dimension);
            for (Map.Entry<String, String> rule : saved.entrySet()) {
                GameRule<?> known = GameRuleNames.rule(rule.getKey());
                if (known != null) { GameRuleNames.apply(held, known, rule.getValue()); }
            }
            if (level.dimension() == Level.OVERWORLD && saved.containsKey(GameRules.ADVANCE_TIME.id())) { daylight(server, held); }
            storage.watch(dimension, held, GameRuleStorage.values(build(level, WANTED.get(level.dimension().identifier()))));
        }
        for (String dimension : storage.unwatched()) { ContentLog.LOGGER.warn("Dimension {} was saved with its own game rules, but no pack gives it any now, so they are dropped", dimension); }
    }

    private static GameRules build(Level level, Map<GameRule<?>, String> wanted) {
        GameRules rules = new GameRules(level.enabledFeatures());
        for (Map.Entry<GameRule<?>, String> rule : wanted.entrySet()) { GameRuleNames.apply(rules, rule.getKey(), rule.getValue()); }
        return rules;
    }

    private static void daylight(MinecraftServer server, GameRules held) { server.getGameRules().set(GameRules.ADVANCE_TIME, held.get(GameRules.ADVANCE_TIME), server); }

    public static boolean names(Level level, GameRule<?> rule) {
        Map<GameRule<?>, String> wanted = WANTED.get(level.dimension().identifier());
        return wanted != null && wanted.containsKey(rule);
    }
}
