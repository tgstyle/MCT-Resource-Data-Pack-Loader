package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentEntityParser {
    private static final Gson GSON = new Gson();

    private ContentEntityParser() {}

    @Nullable public static EntityVariantDef parse(ResourceLocation key, String contents) {
        JsonObject json;
        try { json = GSON.fromJson(contents, JsonObject.class); }
        catch (JsonParseException ex) {
            ContentLog.LOGGER.error("Entity file {} could not be read, ignoring it", key, ex);
            return null;
        }
        if (json == null) {
            ContentLog.LOGGER.error("Entity file {} is empty, ignoring it", key);
            return null;
        }
        ResourceLocation base = ContentParser.location(GsonHelper.getAsString(json, "entity", ""));
        if (base == null) {
            ContentLog.LOGGER.error("Entity variant {} names no entity to copy, ignoring it", key);
            return null;
        }
        List<String> tintParts = new ArrayList<>();
        for (String part : Json.strings(json, "tintParts")) {
            String named = part.trim().toLowerCase(Locale.ROOT);
            if (EntityVariantDef.PARTS.contains(named)) { tintParts.add(named); }
            else { ContentLog.LOGGER.error("Entity variant {} tints part '{}', which is not one of {}, leaving it out", key, part, EntityVariantDef.PARTS); }
        }
        if (tintParts.isEmpty()) { tintParts.add(EntityVariantDef.BODY); }
        JsonObject sounds = GsonHelper.getAsJsonObject(json, "sounds", new JsonObject());
        JsonObject egg = json.has("egg") && json.get("egg").isJsonObject() ? GsonHelper.getAsJsonObject(json, "egg") : null;
        boolean wantsEgg = !json.has("egg") || egg != null || GsonHelper.getAsBoolean(json, "egg", true);
        float scale = Math.max(0.05F, GsonHelper.getAsFloat(json, "scale", 1.0F));
        return new EntityVariantDef(key, base,
                GsonHelper.getAsString(json, "name", ""),
                GsonHelper.getAsBoolean(json, "showName", false),
                GsonHelper.getAsString(json, "texture", "").trim(),
                GsonHelper.getAsString(json, "lootTable", "").trim(),
                GsonHelper.getAsString(json, "profession", "").trim(),
                Math.max(0, GsonHelper.getAsInt(json, "career", 0)),
                baby(json),
                picks(key, json),
                new EntityVariantDef.Sounds(GsonHelper.getAsString(sounds, "ambient", "").trim(), GsonHelper.getAsString(sounds, "hurt", "").trim(), GsonHelper.getAsString(sounds, "death", "").trim(),
                        GsonHelper.getAsString(sounds, "target", "").trim(), GsonHelper.getAsString(sounds, "explode", "").trim(), Math.max(0.0F, GsonHelper.getAsFloat(sounds, "targetVaries", 0.0F)),
                        Math.max(0.0F, GsonHelper.getAsFloat(json, "soundVolume", 1.0F)), Math.max(0.1F, GsonHelper.getAsFloat(json, "soundPitch", 1.0F))),
                lowered(Json.strings(json, "immuneTo")),
                new EntityVariantDef.Physics(Math.max(0.1F, GsonHelper.getAsFloat(json, "jumpMultiplier", 1.0F)), Math.max(0.0F, GsonHelper.getAsFloat(json, "fallDamage", 1.0F)),
                        Math.max(-1, GsonHelper.getAsInt(json, "maxFallHeight", -1)), Math.max(0.0F, GsonHelper.getAsFloat(json, "waterSlowdown", 0.8F)),
                        GsonHelper.getAsBoolean(json, "breathesUnderwater", false), GsonHelper.getAsBoolean(json, "swims", false), GsonHelper.getAsBoolean(json, "amphibious", false)),
                Math.max(-1, GsonHelper.getAsInt(json, "experience", -1)),
                Math.max(0.0F, GsonHelper.getAsFloat(json, "absorption", 0.0F)),
                GsonHelper.getAsString(json, "creatureAttribute", "").trim().toLowerCase(Locale.ROOT),
                effects(key, json),
                GsonHelper.getAsBoolean(json, "despawns", true),
                Math.max(0, GsonHelper.getAsInt(json, "despawnAfter", 0)) * 20,
                new EntityVariantDef.Flags(GsonHelper.getAsBoolean(json, "noAI", false), GsonHelper.getAsBoolean(json, "leftHanded", false), GsonHelper.getAsBoolean(json, "fireproof", false),
                        GsonHelper.getAsBoolean(json, "invulnerable", false), GsonHelper.getAsBoolean(json, "glowing", false), GsonHelper.getAsBoolean(json, "invisible", false),
                        GsonHelper.getAsBoolean(json, "persistent", false), GsonHelper.getAsBoolean(json, "silent", false), GsonHelper.getAsBoolean(json, "picksUpLoot", false),
                        GsonHelper.getAsBoolean(json, "hideArmor", false), GsonHelper.getAsBoolean(json, "hideHeld", false), GsonHelper.getAsBoolean(json, "leashable", false),
                        GsonHelper.getAsBoolean(json, "steerable", false), GsonHelper.getAsBoolean(json, "ignoresSpawnRules", false)),
                Mth.clamp(GsonHelper.getAsFloat(json, "dropChance", 0.0F), 0.0F, 1.0F),
                scale,
                Math.max(0.05F, GsonHelper.getAsFloat(json, "angryScale", scale)),
                Math.max(0.0F, GsonHelper.getAsFloat(json, "width", 0.0F)),
                Math.max(0.0F, GsonHelper.getAsFloat(json, "height", 0.0F)),
                pathPriorities(key, json),
                new EntityVariantDef.Egg(wantsEgg, egg == null || !egg.has("primary") ? -1 : ContentParser.color(GsonHelper.getAsString(egg, "primary", ""), key + " egg primary"),
                        egg == null || !egg.has("secondary") ? -1 : ContentParser.color(GsonHelper.getAsString(egg, "secondary", ""), key + " egg secondary")),
                new EntityVariantDef.Tracking(Math.max(1, GsonHelper.getAsInt(json, "trackingRange", 80)), Math.max(1, GsonHelper.getAsInt(json, "trackingFrequency", 3)), GsonHelper.getAsBoolean(json, "trackVelocity", true)),
                doubles(key, json, "attributes", "attribute"),
                GsonHelper.getAsBoolean(json, "hostile", false),
                GsonHelper.getAsBoolean(json, "passive", false),
                Json.strings(json, "targets"),
                json.has("tint") ? ContentParser.color(GsonHelper.getAsString(json, "tint", ""), key + " tint") : 0,
                tintParts,
                new EntityVariantDef.Combat(GsonHelper.getAsBoolean(json, "explodes", false), Math.max(0.0F, GsonHelper.getAsFloat(json, "explosionPower", 3.0F)),
                        Math.max(1, GsonHelper.getAsInt(json, "explosionFuse", 30)), GsonHelper.getAsBoolean(json, "explosionFire", false),
                        GsonHelper.getAsBoolean(json, "throws", false), Math.max(0, GsonHelper.getAsInt(json, "throwReload", 0)) * 20, Math.max(0, GsonHelper.getAsInt(json, "throwRetreat", 0)) * 20,
                        Math.max(0, GsonHelper.getAsInt(json, "throwAmmo", 0)), Math.max(0.0F, GsonHelper.getAsFloat(json, "throwPower", 1.0F)), GsonHelper.getAsFloat(json, "throwArc", 0.35F),
                        GsonHelper.getAsBoolean(json, "charges", false), GsonHelper.getAsBoolean(json, "pounces", false), Math.max(0, GsonHelper.getAsInt(json, "sniffs", 0)),
                        GsonHelper.getAsBoolean(json, "sleepsByDay", false), Math.max(0, GsonHelper.getAsInt(json, "home", 0)), Mth.clamp(GsonHelper.getAsFloat(json, "fleesWhenHurt", 0.0F), 0.0F, 1.0F),
                        GsonHelper.getAsBoolean(json, "patrols", false), GsonHelper.getAsBoolean(json, "swoops", false), GsonHelper.getAsBoolean(json, "gusts", false), Math.max(0.0F, GsonHelper.getAsFloat(json, "gustPower", 1.5F))),
                Math.max(0, GsonHelper.getAsInt(json, "threatLeast", 0)),
                Math.max(0, GsonHelper.getAsInt(json, "threatHostile", 0)),
                equipment(key, json),
                spawns(key, json),
                Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"), Json.strings(json, "requires"),
                ContentTasks.parse(key, json));
    }

    private static float baby(JsonObject json) {
        if (!json.has("baby")) { return 0.0F; }
        JsonElement held = json.get("baby");
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { return held.getAsBoolean() ? 1.0F : 0.0F; }
        return Mth.clamp(GsonHelper.getAsFloat(json, "baby", 0.0F), 0.0F, 1.0F);
    }

    private static List<PickDef> picks(ResourceLocation key, JsonObject json) {
        List<PickDef> picks = new ArrayList<>();
        if (!json.has("becomes")) { return picks; }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "becomes")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A becomes entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String name = GsonHelper.getAsString(entry, "variant", "").trim();
            if (name.isEmpty()) {
                ContentLog.LOGGER.error("A becomes entry in {} names no variant, skipping it", key);
                continue;
            }
            picks.add(new PickDef(name, Math.max(1, GsonHelper.getAsInt(entry, "weight", 1))));
        }
        return picks;
    }

    private static Map<String, Integer> effects(ResourceLocation key, JsonObject json) {
        Map<String, Integer> effects = new LinkedHashMap<>();
        if (!json.has("effects")) { return effects; }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "effects")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject effect = element.getAsJsonObject();
            effects.put(GsonHelper.getAsString(effect, "potion", "").trim(), Math.max(0, GsonHelper.getAsInt(effect, "amplifier", 0)));
        }
        return effects;
    }

    private static List<SpawnEntryDef> spawns(ResourceLocation key, JsonObject json) {
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (!json.has("spawns")) { return spawns; }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "spawns")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            spawns.add(new SpawnEntryDef(GsonHelper.getAsString(entry, "creatureType", "creature").trim().toLowerCase(Locale.ROOT),
                    Math.max(1, GsonHelper.getAsInt(entry, "weight", 8)), Math.max(1, GsonHelper.getAsInt(entry, "min", 1)), Math.max(1, GsonHelper.getAsInt(entry, "max", 4))));
        }
        return spawns;
    }

    private static Map<String, Double> doubles(ResourceLocation key, JsonObject json, String member, String what) {
        Map<String, Double> found = new LinkedHashMap<>();
        if (!json.has(member)) { return found; }
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, member).entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                ContentLog.LOGGER.error("Entity variant {} sets {} '{}' to something that is not a number, ignoring it", key, what, entry.getKey());
                continue;
            }
            found.put(entry.getKey(), entry.getValue().getAsDouble());
        }
        return found;
    }

    private static Map<String, Float> pathPriorities(ResourceLocation key, JsonObject json) {
        Map<String, Float> found = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : doubles(key, json, "pathPriorities", "path priority").entrySet()) { found.put(entry.getKey(), entry.getValue().floatValue()); }
        return found;
    }

    private static Map<String, String> equipment(ResourceLocation key, JsonObject json) {
        Map<String, String> found = new LinkedHashMap<>();
        if (!json.has("equipment")) { return found; }
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "equipment").entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("Entity variant {} sets slot '{}' to something that is not a name, ignoring it", key, entry.getKey());
                continue;
            }
            found.put(entry.getKey().trim().toLowerCase(Locale.ROOT), entry.getValue().getAsString().trim());
        }
        return found;
    }

    private static List<String> lowered(List<String> values) {
        List<String> out = new ArrayList<>(values.size());
        for (String value : values) { out.add(value.trim().toLowerCase(Locale.ROOT)); }
        return out;
    }
}
