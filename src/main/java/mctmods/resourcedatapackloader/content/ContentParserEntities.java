package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.entity.ContentTasks;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentParserEntities {
    private ContentParserEntities() {}

    @Nullable public static EntityVariantDef entityVariant(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Entity file {} is empty, ignoring it", key);
            return null;
        }
        String base = JsonUtils.getString(json, "entity", "");
        if (base.isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} names no entity to copy, ignoring it", key);
            return null;
        }
        Map<String, Double> attributes = new LinkedHashMap<>();
        if (json.has("attributes")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "attributes").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Entity variant {} sets attribute '{}' to something that is not a number, ignoring it", key, entry.getKey());
                    continue;
                }
                attributes.put(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
        List<String> tintParts = new ArrayList<>();
        for (String part : strings(json, "tintParts")) { tintParts.add(part.trim().toLowerCase(Locale.ROOT)); }
        if (tintParts.isEmpty()) { tintParts.add(EntityVariantDef.BODY); }
        Map<String, String> equipment = new LinkedHashMap<>();
        if (json.has("equipment")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "equipment").entrySet()) {
                if (!entry.getValue().isJsonPrimitive()) {
                    ContentLog.LOGGER.error("Entity variant {} sets slot '{}' to something that is not an item name, ignoring it", key, entry.getKey());
                    continue;
                }
                equipment.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (json.has("spawns")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "spawns")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                spawns.add(new SpawnEntryDef(JsonUtils.getString(entry, "creatureType", "creature"), "",
                        Math.max(1, JsonUtils.getInt(entry, "weight", 8)),
                        Math.max(1, JsonUtils.getInt(entry, "min", 1)),
                        Math.max(1, JsonUtils.getInt(entry, "max", 4))));
            }
        }
        JsonObject sounds = JsonUtils.getJsonObject(json, "sounds", new JsonObject());
        Map<String, Integer> effects = new LinkedHashMap<>();
        if (json.has("effects")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "effects")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject effect = element.getAsJsonObject();
                effects.put(JsonUtils.getString(effect, "potion", ""), Math.max(0, JsonUtils.getInt(effect, "amplifier", 0)));
            }
        }
        Map<String, Float> priorities = new LinkedHashMap<>();
        if (json.has("pathPriorities")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "pathPriorities").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Entity variant {} sets path priority '{}' to something that is not a number, ignoring it", key, entry.getKey());
                    continue;
                }
                priorities.put(entry.getKey(), entry.getValue().getAsFloat());
            }
        }
        JsonObject egg = json.has("egg") && json.get("egg").isJsonObject() ? JsonUtils.getJsonObject(json, "egg") : null;
        boolean wantsEgg = !json.has("egg") || egg != null || JsonUtils.getBoolean(json, "egg", true);
        return new EntityVariantDef(key, new ResourceLocation(base),
                JsonUtils.getString(json, "name", ""),
                JsonUtils.getString(json, "texture", ""),
                JsonUtils.getString(json, "lootTable", ""),
                JsonUtils.getString(json, "profession", ""),
                Math.max(0, JsonUtils.getInt(json, "career", 0)),
                baby(json),
                JsonUtils.getBoolean(json, "keepsBaseBaby", false),
                ContentParser.picks(json, "becomes", "variant"),
                JsonUtils.getString(sounds, "ambient", ""),
                JsonUtils.getString(sounds, "hurt", ""),
                JsonUtils.getString(sounds, "death", ""),
                strings(json, "immuneTo"),
                Math.max(0.1F, JsonUtils.getFloat(json, "jumpMultiplier", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "fallDamage", 1.0F)),
                JsonUtils.getBoolean(json, "hitEffects", true),
                strings(json, "ignoresEffects"),
                Math.max(0.0F, JsonUtils.getFloat(json, "soundVolume", 1.0F)),
                Math.max(0.1F, JsonUtils.getFloat(json, "soundPitch", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "waterSlowdown", 0.8F)),
                Math.max(-1, JsonUtils.getInt(json, "experience", -1)),
                Math.max(-1, JsonUtils.getInt(json, "maxFallHeight", -1)),
                Math.max(0.0F, JsonUtils.getFloat(json, "absorption", 0.0F)),
                JsonUtils.getString(json, "creatureAttribute", ""),
                JsonUtils.getBoolean(json, "breathesUnderwater", false),
                JsonUtils.getBoolean(json, "swims", false),
                JsonUtils.getBoolean(json, "amphibious", false),
                JsonUtils.getBoolean(json, "despawns", true),
                Math.max(0, JsonUtils.getInt(json, "despawnAfter", 0)) * 20,
                JsonUtils.getBoolean(json, "noAI", false),
                JsonUtils.getBoolean(json, "leftHanded", false),
                JsonUtils.getBoolean(json, "fireproof", false),
                JsonUtils.getBoolean(json, "invulnerable", false),
                JsonUtils.getBoolean(json, "glowing", false),
                JsonUtils.getBoolean(json, "invisible", false),
                MathHelper.clamp(JsonUtils.getFloat(json, "dropChance", 0.0F), 0.0F, 1.0F),
                Math.max(0.05F, JsonUtils.getFloat(json, "scale", 1.0F)),
                Math.max(0.05F, JsonUtils.getFloat(json, "angryScale", JsonUtils.getFloat(json, "scale", 1.0F))),
                JsonUtils.getBoolean(json, "leashable", false),
                JsonUtils.getBoolean(json, "steerable", false),
                Math.max(0.0F, JsonUtils.getFloat(json, "width", 0.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "height", 0.0F)),
                effects, priorities,
                wantsEgg,
                egg == null || !egg.has("primary") ? -1 : ContentTypes.color(JsonUtils.getString(egg, "primary", ""), key + " egg primary"),
                egg == null || !egg.has("secondary") ? -1 : ContentTypes.color(JsonUtils.getString(egg, "secondary", ""), key + " egg secondary"),
                Math.max(1, JsonUtils.getInt(json, "trackingRange", 80)),
                Math.max(1, JsonUtils.getInt(json, "trackingFrequency", 3)),
                JsonUtils.getBoolean(json, "trackVelocity", true),
                attributes,
                JsonUtils.getBoolean(json, "hostile", false),
                JsonUtils.getBoolean(json, "passive", false),
                JsonUtils.getBoolean(json, "ignoresSpawnRules", false),
                strings(json, "targets"),
                JsonUtils.getBoolean(json, "persistent", false),
                JsonUtils.getBoolean(json, "silent", false),
                JsonUtils.getBoolean(json, "picksUpLoot", false),
                JsonUtils.getBoolean(json, "hideArmor", false),
                JsonUtils.getBoolean(json, "hideHeld", false),
                json.has("tint") ? ContentTypes.color(JsonUtils.getString(json, "tint", ""), key + " tint") : 0,
                tintParts,
                JsonUtils.getBoolean(json, "showName", false),
                JsonUtils.getBoolean(json, "explodes", false),
                JsonUtils.getBoolean(json, "throws", false),
                Math.max(0, JsonUtils.getInt(json, "throwReload", 0)) * 20,
                Math.max(0, JsonUtils.getInt(json, "throwRetreat", 0)) * 20,
                Math.max(0, JsonUtils.getInt(json, "throwAmmo", 0)),
                Math.max(0.0F, JsonUtils.getFloat(json, "throwPower", 1.0F)),
                JsonUtils.getFloat(json, "throwArc", 0.35F),
                Math.max(0.0F, JsonUtils.getFloat(json, "explosionPower", 3.0F)),
                Math.max(1, JsonUtils.getInt(json, "explosionFuse", 30)),
                JsonUtils.getBoolean(json, "explosionFire", false),
                equipment, spawns,
                strings(json, "biomes"), strings(json, "biomeTypes"), strings(json, "requires"),
                JsonUtils.getBoolean(json, "charges", false),
                JsonUtils.getBoolean(json, "pounces", false),
                Math.max(0, JsonUtils.getInt(json, "sniffs", 0)),
                JsonUtils.getBoolean(json, "sleepsByDay", false),
                Math.max(0, JsonUtils.getInt(json, "home", 0)),
                MathHelper.clamp(JsonUtils.getFloat(json, "fleesWhenHurt", 0.0F), 0.0F, 1.0F),
                JsonUtils.getBoolean(json, "patrols", false),
                JsonUtils.getBoolean(json, "swoops", false),
                JsonUtils.getBoolean(json, "gusts", false),
                Math.max(0.1F, JsonUtils.getFloat(json, "gustPower", 1.5F)),
                Math.max(0, JsonUtils.getInt(json, "threatLeast", 0)),
                Math.max(0, JsonUtils.getInt(json, "threatHostile", 0)),
                ContentTasks.parse(key, json),
                Math.max(0.0F, JsonUtils.getFloat(json, "attackReach", 0.0F)),
                Math.max(-1, JsonUtils.getInt(json, "hurtResistance", -1)),
                json.has("stepHeight") ? Math.max(0.0F, JsonUtils.getFloat(json, "stepHeight", 0.0F)) : -1.0F,
                JsonUtils.getBoolean(json, "hitFire", true),
                json.has("climbs") ? JsonUtils.getBoolean(json, "climbs", false) : null,
                JsonUtils.getBoolean(json, "teleports", true),
                json.has("knockback") ? Math.max(0.0F, JsonUtils.getFloat(json, "knockback", 0.4F)) : -1.0F,
                json.has("explosionPower") || json.has("explosionFuse"),
                JsonUtils.getBoolean(json, "digs", false),
                JsonUtils.getString(sounds, "target", ""),
                JsonUtils.getString(sounds, "explode", ""),
                Math.max(0.0F, JsonUtils.getFloat(sounds, "targetVaries", 0.0F)),
                JsonUtils.getBoolean(json, "bright", false),
                JsonUtils.getBoolean(json, "collectsExperience", false),
                JsonUtils.getBoolean(json, "walks", false),
                JsonUtils.getBoolean(json, "throwReturns", false),
                JsonUtils.getString(sounds, "throw", ""));
    }

    private static float baby(JsonObject json) {
        if (!json.has("baby")) { return 0.0F; }
        JsonElement held = json.get("baby");
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { return held.getAsBoolean() ? 1.0F : 0.0F; }
        return MathHelper.clamp(JsonUtils.getFloat(json, "baby", 0.0F), 0.0F, 1.0F);
    }
}
