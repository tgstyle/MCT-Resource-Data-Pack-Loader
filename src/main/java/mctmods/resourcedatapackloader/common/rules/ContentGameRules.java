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
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public final class ContentGameRules {
    private static final Map<ResourceLocation, Map<String, String>> BY_DIMENSION = new LinkedHashMap<>();
    private static final Map<Level, GameRules> BUILT = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Set<String> KNOWN = new LinkedHashSet<>();
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
                ResourceLocation dimension = ResourceLocation.tryParse(ContentFormats.dimensionId(entry.getKey()));
                if (dimension == null) {
                    ContentLog.LOGGER.error("Game rule file {} names dimension '{}', which is not a dimension id, ignoring it", key, entry.getKey());
                    continue;
                }
                ContentDimensionParser.rules(dimension, entry.getValue().getAsJsonObject(), BY_DIMENSION.computeIfAbsent(dimension, id -> new LinkedHashMap<>()));
            }
        });
        for (DimensionDef def : ContentDimensions.available()) {
            if (def.gameRules().isEmpty()) { continue; }
            BY_DIMENSION.computeIfAbsent(def.key(), id -> new LinkedHashMap<>()).putAll(def.gameRules());
        }
        if (BY_DIMENSION.isEmpty()) { return; }
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override public <T extends GameRules.Value<T>> void visit(@Nonnull GameRules.Key<T> key, @Nonnull GameRules.Type<T> type) { KNOWN.add(key.getId()); }
        });
        for (Map.Entry<ResourceLocation, Map<String, String>> entry : BY_DIMENSION.entrySet()) {
            entry.getValue().keySet().removeIf(rule -> {
                if (KNOWN.contains(rule)) { return false; }
                ContentLog.LOGGER.error("Dimension {} sets game rule '{}', which does not exist, ignoring it", entry.getKey(), rule);
                return true;
            });
        }
        BY_DIMENSION.values().removeIf(Map::isEmpty);
        if (!BY_DIMENSION.isEmpty()) { Summary.info("gamerules", "Applying separate game rules in dimension(s) " + BY_DIMENSION.keySet()); }
    }

    @Nullable public static GameRules forLevel(Level level) {
        if (BY_DIMENSION.isEmpty()) { return null; }
        GameRules held = BUILT.get(level);
        if (held != null) { return held; }
        Map<String, String> wanted = BY_DIMENSION.get(level.dimension().location());
        if (wanted == null) { return null; }
        CompoundTag tag = new GameRules().createTag();
        for (Map.Entry<String, String> rule : wanted.entrySet()) { tag.putString(rule.getKey(), rule.getValue()); }
        held = new GameRules(new Dynamic<>(NbtOps.INSTANCE, tag));
        BUILT.put(level, held);
        if (level instanceof ServerLevel serverLevel && level.dimension() == Level.OVERWORLD && wanted.containsKey(GameRules.RULE_DAYLIGHT.getId())) { level.getLevelData().getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(held.getBoolean(GameRules.RULE_DAYLIGHT), serverLevel.getServer()); }
        ContentLog.LOGGER.debug("Dimension {} keeps its own game rules: {}", level.dimension().location(), wanted);
        return held;
    }
}
