package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class ContentParserWeights {
    public static final int UNSET = -1;
    private static final List<String> WEIGHT_KEYS = Collections.unmodifiableList(Arrays.asList("requires", "items", "unit", "default", "limit", "bonuses", "tiers", "hardLimit", "hardLimitMode", "stackBudget", "stackBudgetMin", "containerFactor", "countContents", "wornFactor", "creative", "tooltip", "hud"));
    private static final List<String> HUD_KEYS = Collections.unmodifiableList(Arrays.asList("show", "inventory", "anchor", "x", "y"));
    private static final List<String> BONUS_KEYS = Collections.unmodifiableList(Arrays.asList("add", "advancement", "worn"));
    private static final List<String> TIER_KEYS = Collections.unmodifiableList(Arrays.asList("above", "effects", "speed", "sprint", "jump", "exhaustion", "color", "message"));
    private static final double SCALE = 1000.0D;

    private ContentParserWeights() {}

    public static int thousandths(JsonObject json, String member, Object owner) {
        if (!json.has(member)) { return UNSET; }
        double value = decimal(json, member, Integer.MAX_VALUE / SCALE);
        if (Double.isNaN(value)) {
            ContentLog.LOGGER.error("{} sets {} to {}, which is not a number from 0 up, so it is ignored", owner, member, json.get(member));
            return UNSET;
        }
        return scaled(value);
    }

    private static int scaled(double value) { return (int) Math.round(value * SCALE); }

    private static double decimal(JsonObject json, String member, double high) {
        double value = Json.asDouble(json.get(member), member, Double.NaN);
        return Double.isNaN(value) || value < 0.0D || value > high ? Double.NaN : value;
    }

    public static int weight(JsonObject json, Object owner) { return thousandths(json, "weight", owner); }

    public static boolean apply(WeightDef.Builder into, ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", WEIGHT_KEYS);
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
            double hard = decimal(json, "hardLimit", Double.POSITIVE_INFINITY);
            if (hard > 0.0D) { into.hardLimit = hard; }
            else { ContentLog.LOGGER.error("Weight file {} sets hardLimit to {}, which is not above 0, so it is ignored", key, json.get("hardLimit")); }
        }
        into.hardLimitMode = Json.choice(json, "hardLimitMode", into.hardLimitMode, key, Arrays.asList(WeightDef.HARD_PICKUP, WeightDef.HARD_TAKE, WeightDef.HARD_BOTH));
        int budget = positive(thousandths(json, "stackBudget", key), "stackBudget", key);
        if (budget != UNSET) { into.stackBudget = budget; }
        if (json.has("stackBudgetMin")) { into.stackBudgetMin = Json.clamped(json, "stackBudgetMin", into.stackBudgetMin, 1, 64); }
        if (json.has("containerFactor")) { into.containerFactor = factor(json, "containerFactor", into.containerFactor, key); }
        into.countContents = Json.choice(json, "countContents", into.countContents, key, Arrays.asList(WeightDef.CONTENTS_NONE, WeightDef.CONTENTS_RDPL, WeightDef.CONTENTS_ALL));
        if (json.has("wornFactor")) { into.wornFactor = factor(json, "wornFactor", into.wornFactor, key); }
        into.creative = Json.getBoolean(json, "creative", into.creative);
        into.tooltip = Json.getBoolean(json, "tooltip", into.tooltip);
        if (json.has("hud")) {
            JsonObject hud = Json.getJsonObject(json, "hud", new JsonObject());
            Json.unknownKeys(hud, "hud", HUD_KEYS);
            into.hudShow = Json.choice(hud, "show", into.hudShow, key, Arrays.asList(WeightDef.HUD_NEVER, WeightDef.HUD_TIERS, WeightDef.HUD_ALWAYS));
            into.hudInventory = Json.getBoolean(hud, "inventory", into.hudInventory);
            into.hudAnchor = Json.choice(hud, "anchor", into.hudAnchor, key, Arrays.asList(WeightDef.HUD_HOTBAR, WeightDef.HUD_TOP_LEFT, WeightDef.HUD_TOP, WeightDef.HUD_TOP_RIGHT, WeightDef.HUD_LEFT, WeightDef.HUD_CENTER, WeightDef.HUD_RIGHT, WeightDef.HUD_BOTTOM_LEFT, WeightDef.HUD_BOTTOM, WeightDef.HUD_BOTTOM_RIGHT));
            into.hudX = Json.getInt(hud, "x", into.hudX);
            into.hudY = Json.getInt(hud, "y", into.hudY);
        }
        return defines;
    }

    private static int positive(int value, String member, Object owner) {
        if (value != 0) { return value; }
        ContentLog.LOGGER.error("Weight file {} sets {} to 0, which is not above 0, so it is ignored", owner, member);
        return UNSET;
    }

    private static double factor(JsonObject json, String member, double fallback, Object owner) {
        double value = decimal(json, member, Double.POSITIVE_INFINITY);
        if (!Double.isNaN(value)) { return value; }
        ContentLog.LOGGER.error("Weight file {} sets {} to {}, which is not a number from 0 up, so it is ignored", owner, member, json.get(member));
        return fallback;
    }

    private static void bonuses(WeightDef.Builder into, ResourceLocation key, JsonArray array) {
        for (JsonObject bonus : Json.objectsOrSkip(array, "A bonus in weight file {} is not an object, skipping it", key)) {
            Json.unknownKeys(bonus, "bonuses", BONUS_KEYS);
            double added = bonus.has("add") ? Json.asDouble(bonus.get("add"), "add", Double.NaN) : Double.NaN;
            if (Double.isNaN(added)) {
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
            int add = scaled(added);
            into.bonuses.add(new WeightDef.Bonus(add, advancement.isEmpty() ? null : advancement, Collections.unmodifiableList(worn)));
        }
    }

    private static void tiers(WeightDef.Builder into, ResourceLocation key, JsonArray array) {
        for (JsonObject tier : Json.objectsOrSkip(array, "A tier in weight file {} is not an object, skipping it", key)) {
            Json.unknownKeys(tier, "tiers", TIER_KEYS);
            double above = tier.has("above") ? decimal(tier, "above", Double.POSITIVE_INFINITY) : Double.NaN;
            if (Double.isNaN(above) || above <= 0.0D) {
                ContentLog.LOGGER.error("A tier in weight file {} has no above greater than 0, skipping it", key);
                continue;
            }
            List<PotionEffectDef> effects = Collections.unmodifiableList(ContentParser.effects(key, Json.getJsonArray(tier, "effects", new JsonArray()), 100, false));
            double speed = tier.has("speed") ? Json.asDouble(tier.get("speed"), "speed", 0.0D) : 0.0D;
            WeightDef.Tier made = new WeightDef.Tier(above,
                    MathHelper.clamp(speed, -1.0D, 0.0D),
                    Json.getBoolean(tier, "sprint", true),
                    Json.getBoolean(tier, "jump", true),
                    Math.max(0.0D, tier.has("exhaustion") ? Json.asDouble(tier.get("exhaustion"), "exhaustion", 0.0D) : 0.0D),
                    effects,
                    ContentTypes.color(Json.getString(tier, "color", "FFFFFF"), key.toString()),
                    Json.getString(tier, "message", "").trim());
            into.tiers.removeIf(held -> held.above == made.above);
            into.tiers.add(made);
        }
    }
}
