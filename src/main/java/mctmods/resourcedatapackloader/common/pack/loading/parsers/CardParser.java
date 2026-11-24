package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.board.ContentDice;
import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.cards.CardLook;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.cards.CardWhen;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.syncdata.DataCheck;
import mctmods.resourcedatapackloader.common.syncdata.DataSets;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.entity.WornCheck;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class CardParser {
    private static final List<String> CARD_KEYS = Collections.unmodifiableList(Arrays.asList("trigger", "dimension", "biomes", "structures", "radius", "advancement", "item", "entity", "count", "below", "above", "time", "day", "minutes", "objective", "score", "roll", "order", "exposure", "environment", "portway", "raid", "rank", "network", "reason", "level", "atLeast", "atMost", "when", "title", "lines", "icon", "color", "image", "background", "font", "style", "ticks", "sound", "audience", "repeat", "cooldown", "runs", "sets", "worldSets", "requires", "every"));
    private static final List<String> WHEN_KEYS = Collections.unmodifiableList(Arrays.asList("biomes", "environments", "season", "dimension", "timeFrom", "timeTo", "dayAtLeast", "advancement", "gameMode", "portway", "network", "scoreAtLeast", "dietAtLeast", "dietCount", "team", "objective", "worn", "each", "data"));
    private static final int MOST_LINES = 16;
    private static final int METER_TICKS = 40;
    private static final int METER_STEP = MathUtil.TICKS_PER_SECOND;
    private static final int TICKS_IN_DAY = (int) DimensionTraitsDef.VANILLA_DAY;

    private CardParser() {}

    @Nullable public static CardRule parse(ResourceLocation id, String contents) {
        JsonObject json = Json.object(id, contents, "card file");
        if (json == null) { return null; }
        Json.unknownKeys(json, "the top level", CARD_KEYS);
        String key = id.toString();
        CardRule rule = new CardRule(key);
        boolean builtin = CardIds.ALL.contains(key);
        rule.trigger = builtin ? CardRule.BUILTIN : Settings.lower(Json.getString(json, "trigger", ""));
        if (!builtin && !CardRule.TRIGGERS.contains(rule.trigger)) {
            ContentLog.LOGGER.error("Card rule {} has trigger '{}', which is not one of {}, so it is skipped", key, rule.trigger, CardRule.TRIGGERS);
            return null;
        }
        rule.dimension = Json.getString(json, "dimension", "").trim();
        rule.biomes = Json.strings(json, "biomes");
        rule.structures = Json.strings(json, "structures");
        rule.radius = Json.atLeast(json, "radius", CardPlace.NEAR, 1);
        rule.advancement = Json.getString(json, "advancement", "").trim();
        rule.item = Json.getString(json, "item", "").trim();
        rule.entity = Json.getString(json, "entity", "").trim();
        rule.count = Json.atLeast(json, "count", 1, 1);
        rule.below = Json.intOrNull(json, "below");
        rule.above = Json.intOrNull(json, "above");
        rule.time = Math.floorMod(Json.getInt(json, "time", 0), TICKS_IN_DAY);
        Integer day = Json.intOrNull(json, "day");
        rule.day = day == null ? -1L : Math.max(0L, day);
        rule.minutes = Json.atLeast(json, "minutes", 0, 0);
        rule.objective = Json.getString(json, "objective", "").trim();
        rule.score = Json.getInt(json, "score", 1);
        rule.roll = ContentDice.notation(Json.getString(json, "roll", "").trim());
        rule.order = Json.getString(json, "order", "").trim();
        rule.exposure = Json.getString(json, "exposure", "").trim();
        rule.environment = Json.getString(json, "environment", "").trim();
        rule.portway = Json.getString(json, "portway", "").trim();
        rule.raid = Json.getString(json, "raid", "").trim();
        rule.rank = IdNames.namespaced(Json.getString(json, "rank", "").trim(), id.getNamespace());
        rule.network = Json.getString(json, "network", "").trim();
        rule.reason = Json.getString(json, "reason", "").trim();
        rule.level = Json.intOrNull(json, "level");
        rule.atLeast = Json.intOrNull(json, "atLeast");
        rule.atMost = Json.intOrNull(json, "atMost");
        if (json.has("when")) { rule.when = when(Json.getJsonObject(json, "when"), id, "Card rule"); }
        rule.title = Json.stringOrNull(json, "title");
        rule.lines = json.has("lines") ? lines(key, json) : null;
        rule.icon = optional(json, "icon");
        rule.color = optional(json, "color");
        rule.image = optional(json, "image");
        rule.background = Json.flagOrNull(json, "background");
        rule.font = optional(json, "font");
        String owner = "Card rule " + key;
        rule.style = Json.choice(json, "style", null, owner, CardLook.STYLES);
        rule.ticks = Json.atLeast(json, "ticks", 0, 0);
        rule.sound = Json.getString(json, "sound", "").trim();
        rule.audience = Json.choice(json, "audience", CardRule.PLAYER, owner, CardRule.AUDIENCES);
        rule.repeat = Json.choice(json, "repeat", CardRule.ALWAYS, owner, CardRule.REPEATS);
        rule.cooldown = Json.atLeast(json, "cooldown", 0, 0);
        rule.runs = Json.getString(json, Json.RUNS, "").trim();
        rule.sets = DataSets.parse(json, "Card rule", key, id);
        if (CardRule.WORN.equals(rule.trigger)) { meter(rule, json); }
        rule.requires = Json.strings(json, "requires");
        return missing(rule) ? null : rule;
    }

    private static void meter(CardRule rule, JsonObject json) {
        int asked = Json.getInt(json, "every", METER_TICKS);
        rule.every = Math.max(METER_STEP, (asked + METER_STEP - 1) / METER_STEP * METER_STEP);
        if (rule.every != asked) { ContentLog.LOGGER.error("Card rule {} sets every {}, which is not a multiple of {} ticks, so it refreshes every {} ticks", rule.key, asked, METER_STEP, rule.every); }
        if (rule.cooldown > 0 || !CardRule.ALWAYS.equals(rule.repeat)) { ContentLog.LOGGER.warn("Card rule {} has trigger worn and sets cooldown or repeat, which a meter ignores", rule.key); }
    }

    public static CardWhen when(JsonObject json, ResourceLocation key, String kind) {
        Json.unknownKeys(json, "when", WHEN_KEYS);
        CardWhen when = new CardWhen();
        when.biomes = Json.strings(json, "biomes");
        when.environments = Json.strings(json, "environments");
        when.seasons = Json.strings(json, "season");
        when.dimension = Json.getString(json, "dimension", "").trim();
        when.timeFrom = CardPlace.tickOf(json, "timeFrom");
        when.timeTo = CardPlace.tickOf(json, "timeTo");
        when.dayAtLeast = json.has("dayAtLeast") ? Math.max(0L, Json.getInt(json, "dayAtLeast")) : -1L;
        when.advancement = Json.getString(json, "advancement", "").trim();
        when.gameMode = Json.getString(json, "gameMode", "").trim();
        when.portway = Json.getString(json, "portway", "").trim();
        when.network = Json.getString(json, "network", "").trim();
        when.standing = Standing.parse(json);
        when.worn = WornCheck.parse(json, key, kind);
        if (json.has("data")) { when.data = DataCheck.parse(json.get("data"), kind, "when", key); }
        return when;
    }

    private static List<String> lines(String key, JsonObject json) {
        JsonArray array = Json.getJsonArray(json, "lines");
        List<String> lines = new ArrayList<>();
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) {
                ContentLog.LOGGER.error("Card rule {} has a line that is not text, skipping it", key);
                continue;
            }
            if (lines.size() < MOST_LINES) { lines.add(Json.asString(element, "lines", "")); }
        }
        return Collections.unmodifiableList(lines);
    }

    @Nullable public static String optional(JsonObject json, String member) {
        String value = Json.stringOrNull(json, member);
        return value == null ? null : value.trim();
    }

    private static boolean missing(CardRule rule) {
        String needs = needs(rule);
        if (needs == null) { return false; }
        ContentLog.LOGGER.error("Card rule {} has trigger {} but no {}, so it is skipped", rule.key, rule.trigger, needs);
        return true;
    }

    @Nullable private static String needs(CardRule rule) {
        if (!CardRule.BUILTIN.equals(rule.trigger) && (rule.lines == null || rule.lines.isEmpty()) && (rule.title == null || rule.title.isEmpty())) { return "title or lines"; }
        switch (rule.trigger) {
            case CardRule.DIMENSION_ENTER: return rule.dimension.isEmpty() ? "dimension" : null;
            case CardRule.BIOME_ENTER: return rule.biomes.isEmpty() ? "biomes" : null;
            case CardRule.STRUCTURE_ENTER: return rule.structures.isEmpty() ? "structures" : null;
            case CardRule.ADVANCEMENT: return rule.advancement.isEmpty() ? "advancement" : null;
            case CardRule.CRAFT:
            case CardRule.PICKUP: return rule.item.isEmpty() ? "item" : null;
            case CardRule.KILL: return rule.entity.isEmpty() ? "entity" : null;
            case CardRule.Y_LEVEL: return rule.below == null && rule.above == null ? "below or above" : null;
            case CardRule.PLAY_TIME: return rule.minutes <= 0 ? "minutes" : null;
            case CardRule.SCORE: return rule.objective.isEmpty() ? "objective" : null;
            case CardRule.WORN: return rule.when == null || !rule.when.worn.present() ? "when.worn" : null;
            default: return null;
        }
    }
}
