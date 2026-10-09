package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
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
        rule.trigger = builtin ? CardRule.BUILTIN : Settings.lower(GsonHelper.getAsString(json, "trigger", ""));
        if (!builtin && !CardRule.TRIGGERS.contains(rule.trigger)) {
            ContentLog.LOGGER.error("Card rule {} has trigger '{}', which is not one of {}, so it is skipped", key, rule.trigger, CardRule.TRIGGERS);
            return null;
        }
        rule.dimension = CardPlace.dimensionId(GsonHelper.getAsString(json, "dimension", "").trim());
        rule.biomes = Json.strings(json, "biomes");
        rule.structures = Json.strings(json, "structures");
        rule.radius = Math.max(1, GsonHelper.getAsInt(json, "radius", DEFAULT_RADIUS));
        rule.advancement = GsonHelper.getAsString(json, "advancement", "").trim();
        rule.item = GsonHelper.getAsString(json, "item", "").trim();
        rule.entity = GsonHelper.getAsString(json, "entity", "").trim();
        rule.count = Math.max(1, GsonHelper.getAsInt(json, "count", 1));
        rule.below = json.has("below") ? GsonHelper.getAsInt(json, "below") : null;
        rule.above = json.has("above") ? GsonHelper.getAsInt(json, "above") : null;
        rule.time = Math.floorMod(GsonHelper.getAsInt(json, "time", 0), Level.TICKS_PER_DAY);
        rule.day = json.has("day") ? Math.max(0L, GsonHelper.getAsInt(json, "day")) : -1L;
        rule.minutes = Math.max(0, GsonHelper.getAsInt(json, "minutes", 0));
        rule.objective = GsonHelper.getAsString(json, "objective", "").trim();
        rule.score = GsonHelper.getAsInt(json, "score", 1);
        if (json.has("when")) { rule.when = when(GsonHelper.getAsJsonObject(json, "when")); }
        rule.title = json.has("title") ? GsonHelper.getAsString(json, "title") : null;
        rule.lines = json.has("lines") ? lines(key, json) : null;
        rule.icon = optional(json, "icon");
        rule.color = optional(json, "color");
        rule.image = optional(json, "image");
        rule.background = json.has("background") ? GsonHelper.getAsBoolean(json, "background") : null;
        rule.font = optional(json, "font");
        String owner = "Card rule " + key;
        rule.style = Json.choice(json, "style", null, owner, CardLook.STYLES);
        rule.ticks = Math.max(0, GsonHelper.getAsInt(json, "ticks", 0));
        rule.audience = Json.choice(json, "audience", CardRule.PLAYER, owner, CardRule.AUDIENCES);
        rule.repeat = Json.choice(json, "repeat", CardRule.ALWAYS, owner, CardRule.REPEATS);
        rule.cooldown = Math.max(0, GsonHelper.getAsInt(json, "cooldown", 0));
        rule.runs = GsonHelper.getAsString(json, "runs", "").trim();
        rule.requires = Json.strings(json, "requires");
        return missing(rule) ? null : rule;
    }

    private static CardWhen when(JsonObject json) {
        CardWhen when = new CardWhen();
        when.biomes = Json.strings(json, "biomes");
        when.timeFrom = json.has("timeFrom") ? Math.floorMod(GsonHelper.getAsInt(json, "timeFrom"), Level.TICKS_PER_DAY) : -1;
        when.timeTo = json.has("timeTo") ? Math.floorMod(GsonHelper.getAsInt(json, "timeTo"), Level.TICKS_PER_DAY) : -1;
        when.dayAtLeast = json.has("dayAtLeast") ? Math.max(0L, GsonHelper.getAsInt(json, "dayAtLeast")) : -1L;
        when.advancement = GsonHelper.getAsString(json, "advancement", "").trim();
        when.gameMode = GsonHelper.getAsString(json, "gameMode", "").trim();
        when.team = GsonHelper.getAsString(json, "team", "").trim();
        when.objective = GsonHelper.getAsString(json, "objective", "").trim();
        when.scoreAtLeast = json.has("scoreAtLeast") ? GsonHelper.getAsInt(json, "scoreAtLeast") : null;
        return when;
    }

    private static List<String> lines(String key, JsonObject json) {
        JsonArray array = GsonHelper.getAsJsonArray(json, "lines");
        List<String> lines = new ArrayList<>();
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) {
                ContentLog.LOGGER.error("Card rule {} has a line that is not text, skipping it", key);
                continue;
            }
            if (lines.size() < MOST_LINES) { lines.add(element.getAsString()); }
        }
        return Collections.unmodifiableList(lines);
    }

    @Nullable private static String optional(JsonObject json, String member) { return json.has(member) ? GsonHelper.getAsString(json, member).trim() : null; }

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
