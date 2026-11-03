package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserWeights {
    public static final int UNSET = -1;

    private ContentParserWeights() {}

    public static int thousandths(JsonObject json, String member, Object owner) {
        if (!json.has(member)) { return UNSET; }
        double value = Json.asDouble(json.get(member), member, Double.NaN);
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0.0D || value > Integer.MAX_VALUE / 1000.0D) {
            ContentLog.LOGGER.error("{} sets {} to {}, which is not a number from 0 up, so it is ignored", owner, member, json.get(member));
            return UNSET;
        }
        return (int) Math.round(value * 1000.0D);
    }

    public static int weight(JsonObject json, Object owner) { return thousandths(json, "weight", owner); }

    public static boolean apply(WeightDef.Builder into, ResourceLocation key, JsonObject json) {
        boolean defines = false;
        if (json.has("unit")) { into.unit = Json.getString(json, "unit", "").trim(); }
        int fallback = thousandths(json, "default", key);
        if (fallback != UNSET) {
            into.defaultWeight = fallback;
            defines = true;
        }
        int limit = positive(thousandths(json, "limit", key), "limit", key);
        if (limit != UNSET) {
            into.limit = limit;
            defines = true;
        }
        if (json.has("bonuses")) { bonuses(into, key, Json.getJsonArray(json, "bonuses", new JsonArray())); }
        if (json.has("tiers")) { tiers(into, key, Json.getJsonArray(json, "tiers", new JsonArray())); }
        if (json.has("hardLimit")) {
            double hard = Json.asDouble(json.get("hardLimit"), "hardLimit", 0.0D);
            if (hard > 0.0D) { into.hardLimit = hard; }
            else { ContentLog.LOGGER.error("Weight file {} sets hardLimit to {}, which is not above 0, so it is ignored", key, json.get("hardLimit")); }
        }
        into.hardLimitMode = Json.choice(json, "hardLimitMode", into.hardLimitMode, key, Arrays.asList(WeightDef.HARD_PICKUP, WeightDef.HARD_TAKE, WeightDef.HARD_BOTH));
        int budget = positive(thousandths(json, "stackBudget", key), "stackBudget", key);
        if (budget != UNSET) { into.stackBudget = budget; }
        if (json.has("stackBudgetMin")) { into.stackBudgetMin = Math.max(1, Math.min(64, Json.getInt(json, "stackBudgetMin", into.stackBudgetMin))); }
        if (json.has("containerFactor")) { into.containerFactor = factor(json, "containerFactor", into.containerFactor, key); }
        into.countContents = Json.choice(json, "countContents", into.countContents, key, Arrays.asList(WeightDef.CONTENTS_NONE, WeightDef.CONTENTS_RDPL, WeightDef.CONTENTS_ALL));
        if (json.has("wornFactor")) { into.wornFactor = factor(json, "wornFactor", into.wornFactor, key); }
        into.creative = Json.getBoolean(json, "creative", into.creative);
        into.tooltip = Json.getBoolean(json, "tooltip", into.tooltip);
        if (json.has("hud")) {
            JsonObject hud = Json.getJsonObject(json, "hud", new JsonObject());
            into.hudShow = Json.choice(hud, "show", into.hudShow, key, Arrays.asList(WeightDef.HUD_NEVER, WeightDef.HUD_TIERS, WeightDef.HUD_ALWAYS));
            into.hudInventory = Json.getBoolean(hud, "inventory", into.hudInventory);
        }
        return defines;
    }

    private static int positive(int value, String member, Object owner) {
        if (value != 0) { return value; }
        ContentLog.LOGGER.error("Weight file {} sets {} to 0, which is not above 0, so it is ignored", owner, member);
        return UNSET;
    }

    private static double factor(JsonObject json, String member, double fallback, Object owner) {
        double value = Json.asDouble(json.get(member), member, Double.NaN);
        if (!Double.isNaN(value) && value >= 0.0D) { return value; }
        ContentLog.LOGGER.error("Weight file {} sets {} to {}, which is not a number from 0 up, so it is ignored", owner, member, json.get(member));
        return fallback;
    }

    private static void bonuses(WeightDef.Builder into, ResourceLocation key, JsonArray array) {
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A bonus in weight file {} is not an object, skipping it", key);
                continue;
            }
            JsonObject bonus = element.getAsJsonObject();
            if (!bonus.has("add") || Double.isNaN(Json.asDouble(bonus.get("add"), "add", Double.NaN))) {
                ContentLog.LOGGER.error("A bonus in weight file {} has no numeric add, skipping it", key);
                continue;
            }
            String advancement = Json.getString(bonus, "advancement", "").trim();
            List<String> worn = new ArrayList<>();
            for (String name : Json.strings(bonus, "worn")) { worn.add(Settings.lower(name)); }
            if (advancement.isEmpty() && worn.isEmpty()) {
                ContentLog.LOGGER.error("A bonus in weight file {} names neither an advancement nor worn items, skipping it", key);
                continue;
            }
            int add = (int) Math.round(Json.asDouble(bonus.get("add"), "add", 0.0D) * 1000.0D);
            into.bonuses.add(new WeightDef.Bonus(add, advancement.isEmpty() ? null : advancement, Collections.unmodifiableList(worn)));
        }
    }

    private static void tiers(WeightDef.Builder into, ResourceLocation key, JsonArray array) {
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A tier in weight file {} is not an object, skipping it", key);
                continue;
            }
            JsonObject tier = element.getAsJsonObject();
            double above = tier.has("above") ? Json.asDouble(tier.get("above"), "above", Double.NaN) : Double.NaN;
            if (Double.isNaN(above) || above <= 0.0D) {
                ContentLog.LOGGER.error("A tier in weight file {} has no above greater than 0, skipping it", key);
                continue;
            }
            List<PotionEffectDef> effects = Collections.unmodifiableList(ContentParser.effects(key, Json.getJsonArray(tier, "effects", new JsonArray()), 100, false));
            double speed = tier.has("speed") ? Json.asDouble(tier.get("speed"), "speed", 0.0D) : 0.0D;
            WeightDef.Tier made = new WeightDef.Tier(above,
                    Math.max(-1.0D, Math.min(0.0D, speed)),
                    Json.getBoolean(tier, "sprint", true),
                    Json.getBoolean(tier, "jump", true),
                    Math.max(0.0D, tier.has("exhaustion") ? Json.asDouble(tier.get("exhaustion"), "exhaustion", 0.0D) : 0.0D),
                    effects,
                    ContentParser.color(Json.getString(tier, "color", "FFFFFF"), key.toString()),
                    Json.getString(tier, "message", "").trim());
            into.tiers.removeIf(held -> held.above() == made.above());
            into.tiers.add(made);
        }
    }
}
