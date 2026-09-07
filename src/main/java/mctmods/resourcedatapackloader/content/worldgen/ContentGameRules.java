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
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentGameRules {
    private static final Gson GSON = new Gson();
    private static final Map<String, Map<String, String>> BY_DIMENSION = new LinkedHashMap<>();
    private static final Map<Level, GameRules> BUILT = new WeakHashMap<>();
    private static final Set<String> KNOWN = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentGameRules() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff()) { return; }
        Json.eachFile(PackManager.GAMERULES, "game rule file", (key, contents) -> {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            if (json == null) { return; }
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    ContentLog.LOGGER.error("Game rule file {} maps dimension {} to something that is not a set of rules, ignoring it", key, entry.getKey());
                    continue;
                }
                String dimension = ContentFormats.dimensionId(entry.getKey());
                Map<String, String> rules = BY_DIMENSION.computeIfAbsent(dimension, id -> new LinkedHashMap<>());
                for (Map.Entry<String, JsonElement> rule : entry.getValue().getAsJsonObject().entrySet()) {
                    if (!rule.getValue().isJsonPrimitive()) { continue; }
                    rules.put(rule.getKey(), rule.getValue().getAsString());
                }
            }
        });
        for (DimensionDef def : ContentDimensions.all()) {
            if (def.gameRules().isEmpty()) { continue; }
            BY_DIMENSION.computeIfAbsent(def.key().toString(), id -> new LinkedHashMap<>()).putAll(def.gameRules());
        }
        if (BY_DIMENSION.isEmpty()) { return; }
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override public <T extends GameRules.Value<T>> void visit(@Nonnull GameRules.Key<T> key, @Nonnull GameRules.Type<T> type) { KNOWN.add(key.getId()); }
        });
        for (Map.Entry<String, Map<String, String>> entry : BY_DIMENSION.entrySet()) {
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
        if (BY_DIMENSION.isEmpty() || !(level instanceof ServerLevel)) { return null; }
        Map<String, String> wanted = BY_DIMENSION.get(level.dimension().location().toString());
        if (wanted == null) { return null; }
        GameRules held = BUILT.get(level);
        if (held != null) { return held; }
        CompoundTag tag = level.getLevelData().getGameRules().createTag();
        for (Map.Entry<String, String> rule : wanted.entrySet()) { tag.putString(rule.getKey(), rule.getValue()); }
        held = new GameRules(new Dynamic<>(NbtOps.INSTANCE, tag));
        BUILT.put(level, held);
        ContentLog.LOGGER.debug("Dimension {} keeps its own game rules: {}", level.dimension().location(), wanted);
        return held;
    }
}
