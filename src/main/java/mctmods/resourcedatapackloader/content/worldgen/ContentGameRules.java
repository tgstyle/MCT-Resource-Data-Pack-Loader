package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentGameRules {
    private static final Gson GSON = new Gson();
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
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
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
                Map<String, String> rules = BY_DIMENSION.computeIfAbsent(dimension, _ -> new LinkedHashMap<>());
                for (Map.Entry<String, JsonElement> rule : entry.getValue().getAsJsonObject().entrySet()) {
                    if (!rule.getValue().isJsonPrimitive()) { continue; }
                    rules.put(rule.getKey(), rule.getValue().getAsString());
                }
            }
        });
        for (DimensionDef def : ContentDimensions.all()) {
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
        held = new GameRules(level.enabledFeatures());
        for (Map.Entry<GameRule<?>, String> rule : wanted.entrySet()) { GameRuleNames.apply(held, rule.getKey(), rule.getValue()); }
        BUILT.put(level, held);
        if (level instanceof ServerLevel serverLevel && level.dimension() == Level.OVERWORLD && wanted.containsKey(GameRules.ADVANCE_TIME)) { serverLevel.getServer().getGameRules().set(GameRules.ADVANCE_TIME, held.get(GameRules.ADVANCE_TIME), serverLevel.getServer()); }
        ContentLog.LOGGER.debug("Dimension {} keeps its own game rules: {}", level.dimension().identifier(), wanted);
        return held;
    }
}
