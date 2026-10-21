package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Standing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

final class CardParser {
    private static final int MOST_LINES = 16;
    private static final int DEFAULT_RADIUS = 32;

    private CardParser() {}

    @Nullable static CardRule parse(ResourceLocation id, String contents) {
        JsonObject json = Json.object(id, contents, "card file");
        if (json == null) { return null; }
        String key = id.toString();
        CardRule rule = new CardRule(key);
        boolean builtin = CardIds.ALL.contains(key);
        rule.trigger = builtin ? CardRule.BUILTIN : Settings.lower(Json.getString(json, "trigger", ""));
        if (!builtin && !CardRule.TRIGGERS.contains(rule.trigger)) {
            ContentLog.LOGGER.error("Card rule {} has trigger '{}', which is not one of {}, so it is skipped", key, rule.trigger, CardRule.TRIGGERS);
            return null;
        }
        rule.dimension = CardPlace.dimensionId(Json.getString(json, "dimension", "").trim());
        rule.biomes = Json.strings(json, "biomes");
        rule.structures = Json.strings(json, "structures");
        rule.radius = Math.max(1, Json.getInt(json, "radius", DEFAULT_RADIUS));
        rule.advancement = Json.getString(json, "advancement", "").trim();
        rule.item = Json.getString(json, "item", "").trim();
        rule.entity = Json.getString(json, "entity", "").trim();
        rule.count = Math.max(1, Json.getInt(json, "count", 1));
        rule.below = json.has("below") ? Json.getInt(json, "below") : null;
        rule.above = json.has("above") ? Json.getInt(json, "above") : null;
        rule.time = Math.floorMod(Json.getInt(json, "time", 0), Level.TICKS_PER_DAY);
        rule.day = json.has("day") ? Math.max(0L, Json.getInt(json, "day")) : -1L;
        rule.minutes = Math.max(0, Json.getInt(json, "minutes", 0));
        rule.objective = Json.getString(json, "objective", "").trim();
        rule.score = Json.getInt(json, "score", 1);
        rule.roll = ContentDice.notation(Json.getString(json, "roll", "").trim());
        rule.atLeast = json.has("atLeast") ? Json.getInt(json, "atLeast") : null;
        rule.atMost = json.has("atMost") ? Json.getInt(json, "atMost") : null;
        if (json.has("when")) { rule.when = when(Json.getJsonObject(json, "when")); }
        rule.title = json.has("title") ? Json.getString(json, "title") : null;
        rule.lines = json.has("lines") ? lines(key, json) : null;
        rule.icon = optional(json, "icon");
        rule.color = optional(json, "color");
        rule.image = optional(json, "image");
        rule.background = json.has("background") ? Json.getBoolean(json, "background") : null;
        rule.font = optional(json, "font");
        String owner = "Card rule " + key;
        rule.style = Json.choice(json, "style", null, owner, CardLook.STYLES);
        rule.ticks = Math.max(0, Json.getInt(json, "ticks", 0));
        rule.audience = Json.choice(json, "audience", CardRule.PLAYER, owner, CardRule.AUDIENCES);
        rule.repeat = Json.choice(json, "repeat", CardRule.ALWAYS, owner, CardRule.REPEATS);
        rule.cooldown = Math.max(0, Json.getInt(json, "cooldown", 0));
        rule.runs = Json.getString(json, "runs", "").trim();
        rule.requires = Json.strings(json, "requires");
        return missing(rule) ? null : rule;
    }

    private static CardWhen when(JsonObject json) {
        CardWhen when = new CardWhen();
        when.biomes = Json.strings(json, "biomes");
        when.timeFrom = json.has("timeFrom") ? Math.floorMod(Json.getInt(json, "timeFrom"), Level.TICKS_PER_DAY) : -1;
        when.timeTo = json.has("timeTo") ? Math.floorMod(Json.getInt(json, "timeTo"), Level.TICKS_PER_DAY) : -1;
        when.dayAtLeast = json.has("dayAtLeast") ? Math.max(0L, Json.getInt(json, "dayAtLeast")) : -1L;
        when.advancement = Json.getString(json, "advancement", "").trim();
        when.gameMode = Json.getString(json, "gameMode", "").trim();
        when.standing = Standing.parse(json);
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

    @Nullable private static String optional(JsonObject json, String member) { return json.has(member) ? Json.getString(json, member).trim() : null; }

    private static boolean missing(CardRule rule) {
        String needs = needs(rule);
        if (needs == null) { return false; }
        ContentLog.LOGGER.error("Card rule {} has trigger {} but no {}, so it is skipped", rule.key, rule.trigger, needs);
        return true;
    }

    @Nullable private static String needs(CardRule rule) {
        if (!CardRule.BUILTIN.equals(rule.trigger) && (rule.lines == null || rule.lines.isEmpty()) && (rule.title == null || rule.title.isEmpty())) { return "title or lines"; }
        return switch (rule.trigger) {
            case CardRule.DIMENSION_ENTER -> rule.dimension.isEmpty() ? "dimension" : null;
            case CardRule.BIOME_ENTER -> rule.biomes.isEmpty() ? "biomes" : null;
            case CardRule.STRUCTURE_ENTER -> rule.structures.isEmpty() ? "structures" : null;
            case CardRule.ADVANCEMENT -> rule.advancement.isEmpty() ? "advancement" : null;
            case CardRule.CRAFT, CardRule.PICKUP -> rule.item.isEmpty() ? "item" : null;
            case CardRule.KILL -> rule.entity.isEmpty() ? "entity" : null;
            case CardRule.Y_LEVEL -> rule.below == null && rule.above == null ? "below or above" : null;
            case CardRule.PLAY_TIME -> rule.minutes <= 0 ? "minutes" : null;
            case CardRule.SCORE -> rule.objective.isEmpty() ? "objective" : null;
            default -> null;
        };
    }
}
