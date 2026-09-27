package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRules;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class GameRuleNames {
    private static final int PACK_DATA_VERSION = 3955;
    private static final String OLD_FIELD = "GameRules";
    private static final String NEW_FIELD = "game_rules";

    private GameRuleNames() {}

    public static Map<String, String> modernize(Map<String, String> named) {
        JsonObject rules = new JsonObject();
        for (Map.Entry<String, String> rule : named.entrySet()) { rules.addProperty(rule.getKey(), rule.getValue()); }
        JsonObject level = new JsonObject();
        level.add(OLD_FIELD, rules);
        Dynamic<JsonElement> fixed = DataFixers.getDataFixer().update(References.LEVEL, new Dynamic<>(JsonOps.INSTANCE, level), PACK_DATA_VERSION, SharedConstants.getCurrentVersion().dataVersion().version());
        Map<String, String> modern = new LinkedHashMap<>();
        JsonElement out = fixed.getValue();
        if (!(out instanceof JsonObject whole) || !(whole.get(NEW_FIELD) instanceof JsonObject renamed)) { return new LinkedHashMap<>(named); }
        for (Map.Entry<String, JsonElement> rule : renamed.entrySet()) {
            if (rule.getValue().isJsonPrimitive()) { modern.put(rule.getKey(), rule.getValue().getAsString()); }
        }
        return modern;
    }

    @Nullable public static GameRule<?> rule(String name) {
        Identifier id = Identifier.tryParse(name);
        return id == null ? null : BuiltInRegistries.GAME_RULE.getValue(id);
    }

    public static <T> void apply(GameRules rules, GameRule<T> rule, String value) {
        T parsed = rule.deserialize(value).result().orElseGet(() -> lenient(rule, value));
        if (parsed != null) { rules.set(rule, parsed, null); }
    }

    @Nullable private static <T> T lenient(GameRule<T> rule, String value) {
        if (rule.gameRuleType() == GameRuleType.BOOL) { return rule.valueClass().cast(Boolean.parseBoolean(value)); }
        if (rule.gameRuleType() == GameRuleType.INT) { return rule.valueClass().cast(safeParse(value)); }
        return null;
    }

    private static int safeParse(String value) {
        if (!value.isEmpty()) {
            try { return Integer.parseInt(value); }
            catch (NumberFormatException wrong) { ContentLog.LOGGER.warn("Failed to parse integer {}", value); }
        }
        return 0;
    }
}
