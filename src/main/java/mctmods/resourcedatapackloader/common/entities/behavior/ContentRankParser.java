package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankRandomDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankScalingDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ContentRankParser {
    private static final List<String> KEYS = List.of("entities", "weight", "name", "health", "damage", "effects", "loot", "dimensions", "equipment", "dropChance", "picksUpLoot", "digs", "digItem", "breaksDoors", "random", "steals", "scaling");
    private static final List<String> RANDOM_KEYS = List.of("armor", "armorPiece", "weapon", "offhand", "enchant", "exclude");
    private static final List<String> SCALING_KEYS = List.of("difficulty", "daysPerStep", "distancePerStep", "health", "damage", "armor", "weapon", "enchant");
    private static final String FRACTION = "a number from 0 to 1";
    private static final String STEP = "a number of 0 or more";

    private ContentRankParser() {}

    @Nullable static RankDef parse(ResourceLocation key, JsonObject json) {
        String owner = "Rank " + key;
        if (stray(owner, json, KEYS)) { return null; }
        List<String> entities = List.of(MobStats.MONSTER);
        if (json.has("entities")) {
            List<String> listed = MobStats.entities(owner, "entities", json.get("entities"));
            if (listed == null || listed.size() != json.get("entities").getAsJsonArray().size()) {
                ContentLog.LOGGER.error("{} gives entities that are not all entity ids and kinds, ignoring the rank", owner);
                return null;
            }
            entities = listed;
        }
        Float weight = number(owner, json, "weight", 1.0F, 1.0F, Float.POSITIVE_INFINITY, true, "a whole number of 1 or more");
        Float health = number(owner, json, "health", 1.0F, Float.MIN_VALUE, Float.POSITIVE_INFINITY, false, "a number above 0");
        Float damage = number(owner, json, "damage", 1.0F, 0.0F, Float.POSITIVE_INFINITY, false, "a number of 0 or more");
        Float loot = number(owner, json, "loot", 1.0F, 1.0F, Float.POSITIVE_INFINITY, false, "a number of 1 or more");
        Float dropChance = number(owner, json, "dropChance", 0.0F, 0.0F, 1.0F, false, FRACTION);
        String label = text(owner, json, "name");
        String digItem = text(owner, json, "digItem");
        Boolean digs = flag(owner, json, "digs");
        Boolean breaksDoors = flag(owner, json, "breaksDoors");
        Boolean steals = flag(owner, json, "steals");
        Boolean picksUpLoot = json.has("picksUpLoot") ? flag(owner, json, "picksUpLoot") : null;
        if (json.has("picksUpLoot") && picksUpLoot == null) { return null; }
        if (weight == null || health == null || damage == null || loot == null || dropChance == null) { return null; }
        if (label == null || digItem == null || digs == null || breaksDoors == null || steals == null) { return null; }
        if (json.has("equipment") && !json.get("equipment").isJsonObject()) {
            ContentLog.LOGGER.error("{} gives equipment as {}, which is not an object of slot and item, ignoring the rank", owner, json.get("equipment"));
            return null;
        }
        Map<String, Integer> effects = ContentEntityParser.effects(key, json);
        if (json.has("effects") && effects.size() != json.get("effects").getAsJsonArray().size()) {
            ContentLog.LOGGER.error("{} gives effects that are not all objects with a different potion each, ignoring the rank", owner);
            return null;
        }
        RankRandomDef random = random(owner, json);
        RankScalingDef scaling = json.has("scaling") ? scaling(owner, json) : null;
        if (random == null || json.has("scaling") && scaling == null) { return null; }
        List<String> dimensions = new ArrayList<>();
        for (String named : Json.strings(json, "dimensions")) { dimensions.add(CardPlace.dimensionId(named)); }
        return new RankDef(key, List.copyOf(entities), weight.intValue(), label, health, damage, Collections.unmodifiableMap(new LinkedHashMap<>(effects)), loot, List.copyOf(dimensions),
                Collections.unmodifiableMap(new LinkedHashMap<>(ContentEntityParser.equipment(key, json))), dropChance, picksUpLoot, digs, digItem, breaksDoors, steals, random, scaling);
    }

    @Nullable private static RankRandomDef random(String owner, JsonObject json) {
        if (!json.has("random")) { return RankRandomDef.NONE; }
        JsonObject random = section(owner, json, "random");
        if (random == null || stray(owner, random, RANDOM_KEYS)) { return null; }
        Float armor = number(owner, random, "armor", 0.0F, 0.0F, 1.0F, false, FRACTION);
        Float armorPiece = number(owner, random, "armorPiece", 0.0F, 0.0F, 1.0F, false, FRACTION);
        Float weapon = number(owner, random, "weapon", 0.0F, 0.0F, 1.0F, false, FRACTION);
        Float offhand = number(owner, random, "offhand", 0.0F, 0.0F, 1.0F, false, FRACTION);
        Float enchant = number(owner, random, "enchant", 0.0F, 0.0F, 1.0F, false, FRACTION);
        if (random.has("exclude") && !random.get("exclude").isJsonArray()) {
            ContentLog.LOGGER.error("{} gives exclude as {}, which is not a list of item ids or mod ids, ignoring the rank", owner, random.get("exclude"));
            return null;
        }
        if (armor == null || armorPiece == null || weapon == null || offhand == null || enchant == null) { return null; }
        return new RankRandomDef(armor, armorPiece, weapon, offhand, enchant, List.copyOf(Json.strings(random, "exclude")));
    }

    @Nullable private static RankScalingDef scaling(String owner, JsonObject json) {
        JsonObject scaling = section(owner, json, "scaling");
        if (scaling == null || stray(owner, scaling, SCALING_KEYS)) { return null; }
        Float difficulty = number(owner, scaling, "difficulty", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float days = number(owner, scaling, "daysPerStep", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float distance = number(owner, scaling, "distancePerStep", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float health = number(owner, scaling, "health", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float damage = number(owner, scaling, "damage", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float armor = number(owner, scaling, "armor", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float weapon = number(owner, scaling, "weapon", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        Float enchant = number(owner, scaling, "enchant", 0.0F, 0.0F, Float.POSITIVE_INFINITY, false, STEP);
        if (difficulty == null || days == null || distance == null || health == null || damage == null || armor == null || weapon == null || enchant == null) { return null; }
        return new RankScalingDef(difficulty, days, distance, health, damage, armor, weapon, enchant);
    }

    @Nullable private static JsonObject section(String owner, JsonObject json, String key) {
        JsonElement value = json.get(key);
        if (value.isJsonObject()) { return value.getAsJsonObject(); }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not an object, ignoring the rank", owner, key, value);
        return null;
    }

    private static boolean stray(String owner, JsonObject json, List<String> keys) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!keys.contains(entry.getKey())) {
                ContentLog.LOGGER.error("{} gives {}, which is not a rank key, ignoring the rank", owner, entry.getKey());
                return true;
            }
        }
        return false;
    }

    @Nullable private static String text(String owner, JsonObject json, String key) {
        if (!json.has(key)) { return ""; }
        JsonElement value = json.get(key);
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) { return value.getAsString(); }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not text, ignoring the rank", owner, key, value);
        return null;
    }

    @Nullable private static Boolean flag(String owner, JsonObject json, String key) {
        if (!json.has(key)) { return false; }
        JsonElement value = json.get(key);
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not true or false, ignoring the rank", owner, key, value);
        return null;
    }

    @Nullable private static Float number(String owner, JsonObject json, String key, float fallback, float low, float high, boolean whole, String need) {
        if (!json.has(key)) { return fallback; }
        JsonElement value = json.get(key);
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            float number = value.getAsFloat();
            if (number >= low && number <= high && number < Float.POSITIVE_INFINITY && (!whole || number == Math.floor(number))) { return number; }
        }
        ContentLog.LOGGER.error("{} gives {} as {}, which is not {}, ignoring the rank", owner, key, value, need);
        return null;
    }
}
