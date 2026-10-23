package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.PhaseDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.content.def.SpawnGateDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.BossBars;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentEntityParser {
    private static final Set<String> PHASE_KEYS = new LinkedHashSet<>(List.of("below", "attributes", "tasks", "charges", "pounces", "gusts", "gustPower", "bossBar"));

    private ContentEntityParser() {}

    @Nullable public static EntityVariantDef parse(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "entity file");
        if (json == null) { return null; }
        ResourceLocation base = ContentParser.location(Json.getString(json, "entity", ""));
        if (base == null) {
            ContentLog.LOGGER.error("Entity variant {} names no entity to copy, ignoring it", key);
            return null;
        }
        List<String> tintParts = new ArrayList<>();
        for (String part : Json.strings(json, "tintParts")) {
            String named = Settings.lower(part);
            if (EntityVariantDef.PARTS.contains(named)) { tintParts.add(named); }
            else { ContentLog.LOGGER.error("Entity variant {} tints part '{}', which is not one of {}, leaving it out", key, part, EntityVariantDef.PARTS); }
        }
        if (tintParts.isEmpty()) { tintParts.add(EntityVariantDef.BODY); }
        JsonObject sounds = Json.getJsonObject(json, "sounds", new JsonObject());
        JsonObject egg = GsonHelper.isObjectNode(json, "egg") ? Json.getJsonObject(json, "egg") : null;
        BossBarDef bar = BossBars.read("Entity variant " + key, json);
        boolean wantsEgg = !json.has("egg") || egg != null || Json.getBoolean(json, "egg", true);
        float scale = Math.max(0.05F, Json.getFloat(json, "scale", 1.0F));
        return new EntityVariantDef(key, base,
                Json.getString(json, "name", ""),
                Json.getBoolean(json, "showName", false),
                Json.getString(json, "texture", "").trim(),
                Json.getString(json, "lootTable", "").trim(),
                Json.getString(json, "profession", "").trim(),
                Math.max(0, Json.getInt(json, "career", 0)),
                baby(json),
                picks(key, json),
                new EntityVariantDef.Sounds(Json.getString(sounds, "ambient", "").trim(), Json.getString(sounds, "hurt", "").trim(), Json.getString(sounds, "death", "").trim(),
                        Json.getString(sounds, "target", "").trim(), Json.getString(sounds, "explode", "").trim(), Json.getString(sounds, "throw", "").trim(), Math.max(0.0F, Json.getFloat(sounds, "targetVaries", 0.0F)),
                        Math.max(0.0F, Json.getFloat(json, "soundVolume", 1.0F)), Math.max(0.1F, Json.getFloat(json, "soundPitch", 1.0F))),
                Settings.lowered(Json.strings(json, "immuneTo")),
                Settings.lowered(Json.strings(json, "ignoresEffects")),
                new EntityVariantDef.Physics(Math.max(0.1F, Json.getFloat(json, "jumpMultiplier", 1.0F)), Math.max(0.0F, Json.getFloat(json, "fallDamage", 1.0F)),
                        Math.max(-1, Json.getInt(json, "maxFallHeight", -1)), Math.max(0.0F, Json.getFloat(json, "waterSlowdown", 0.8F)),
                        Json.getBoolean(json, "breathesUnderwater", false), Json.getBoolean(json, "swims", false), Json.getBoolean(json, "amphibious", false),
                        json.has("stepHeight") ? Math.max(0.0F, Json.getFloat(json, "stepHeight", 0.0F)) : -1.0F,
                        json.has("climbs") ? Json.getBoolean(json, "climbs", false) : null, Json.getBoolean(json, "teleports", true),
                        Math.max(-1, Json.getInt(json, "hurtResistance", -1)), Json.getBoolean(json, "walks", false)),
                Math.max(-1, Json.getInt(json, "experience", -1)),
                Math.max(0.0F, Json.getFloat(json, "absorption", 0.0F)),
                Settings.lower(Json.getString(json, "creatureAttribute", "")),
                effects(key, json),
                Json.getBoolean(json, "despawns", true),
                Math.max(0, Json.getInt(json, "despawnAfter", 0)) * SharedConstants.TICKS_PER_SECOND,
                new EntityVariantDef.Flags(Json.getBoolean(json, "noAI", false), Json.getBoolean(json, "leftHanded", false), Json.getBoolean(json, "fireproof", false),
                        Json.getBoolean(json, "invulnerable", false), Json.getBoolean(json, "glowing", false), Json.getBoolean(json, "invisible", false),
                        Json.getBoolean(json, "persistent", false), Json.getBoolean(json, "silent", false), Json.getBoolean(json, "picksUpLoot", false),
                        Json.getBoolean(json, "hideArmor", false), Json.getBoolean(json, "hideHeld", false), Json.getBoolean(json, "leashable", false),
                        Json.getBoolean(json, "steerable", false), Json.getBoolean(json, "ignoresSpawnRules", false),
                        Json.getBoolean(json, "bright", false), Json.getBoolean(json, "keepsBaseBaby", false),
                        Json.getBoolean(json, "collectsExperience", false)),
                Mth.clamp(Json.getFloat(json, "dropChance", 0.0F), 0.0F, 1.0F),
                scale,
                Math.max(0.05F, Json.getFloat(json, "angryScale", scale)),
                Math.max(0.0F, Json.getFloat(json, "width", 0.0F)),
                Math.max(0.0F, Json.getFloat(json, "height", 0.0F)),
                pathPriorities(key, json),
                new EntityVariantDef.Egg(wantsEgg, egg == null || !egg.has("primary") ? -1 : ContentParser.color(Json.getString(egg, "primary", ""), key + " egg primary"),
                        egg == null || !egg.has("secondary") ? -1 : ContentParser.color(Json.getString(egg, "secondary", ""), key + " egg secondary")),
                new EntityVariantDef.Tracking(Math.max(1, Json.getInt(json, "trackingRange", 80)), Math.max(1, Json.getInt(json, "trackingFrequency", 3)), Json.getBoolean(json, "trackVelocity", true)),
                doubles(key, json, "attributes", "attribute"),
                Json.getBoolean(json, "hostile", false),
                Json.getBoolean(json, "passive", false),
                Json.strings(json, "targets"),
                json.has("tint") ? ContentParser.color(Json.getString(json, "tint", ""), key + " tint") : 0,
                tintParts,
                new EntityVariantDef.Combat(Json.getBoolean(json, "explodes", false), Math.max(0.0F, Json.getFloat(json, "explosionPower", 3.0F)),
                        Math.max(1, Json.getInt(json, "explosionFuse", 30)), Json.getBoolean(json, "explosionFire", false),
                        Json.getBoolean(json, "throws", false), Math.max(0, Json.getInt(json, "throwReload", 0)) * SharedConstants.TICKS_PER_SECOND, Math.max(0, Json.getInt(json, "throwRetreat", 0)) * SharedConstants.TICKS_PER_SECOND,
                        Math.max(0, Json.getInt(json, "throwAmmo", 0)), Math.max(0.0F, Json.getFloat(json, "throwPower", 1.0F)), Json.getFloat(json, "throwArc", 0.35F), Json.getBoolean(json, "throwReturns", false),
                        Json.getBoolean(json, "charges", false), Json.getBoolean(json, "pounces", false), Math.max(0, Json.getInt(json, "sniffs", 0)),
                        Json.getBoolean(json, "sleepsByDay", false), Math.max(0, Json.getInt(json, "home", 0)), Mth.clamp(Json.getFloat(json, "fleesWhenHurt", 0.0F), 0.0F, 1.0F),
                        Json.getBoolean(json, "patrols", false), Json.getBoolean(json, "swoops", false), Json.getBoolean(json, "gusts", false), Math.max(0.1F, Json.getFloat(json, "gustPower", 1.5F)),
                        Math.max(0.0F, Json.getFloat(json, "attackReach", 0.0F)),
                        json.has("knockback") ? Math.max(0.0F, Json.getFloat(json, "knockback", 0.4F)) : -1.0F, Json.getBoolean(json, "hitEffects", true),
                        Json.getBoolean(json, "hitFire", true), Json.getBoolean(json, "digs", false),
                        json.has("explosionPower") || json.has("explosionFuse")),
                Math.max(0, Json.getInt(json, "threatLeast", 0)),
                Math.max(0, Json.getInt(json, "threatHostile", 0)),
                equipment(key, json),
                spawns(key, json),
                Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"), Json.strings(json, "requires"),
                ContentTasks.parse(key, json), storage(key, json), gate(key, json), bar, phases(key, json, bar != null));
    }

    private static List<PhaseDef> phases(ResourceLocation key, JsonObject json, boolean hasBar) {
        List<PhaseDef> phases = new ArrayList<>();
        if (!json.has("phases")) { return phases; }
        boolean hostile = Json.getBoolean(json, "hostile", false) && !Json.getBoolean(json, "passive", false);
        for (JsonElement element : Json.getJsonArray(json, "phases")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("Entity variant {} has a phases entry that is not an object, ignoring it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> member : entry.entrySet()) {
                if (!PHASE_KEYS.contains(member.getKey())) { ContentLog.LOGGER.error("Entity variant {} has a phase key '{}', which is not one of {}, so it is ignored", key, member.getKey(), PHASE_KEYS); }
            }
            float below = Json.getFloat(entry, "below", 0.0F);
            if (below <= 0.0F || below >= 1.0F) {
                ContentLog.LOGGER.error("Entity variant {} has a phase whose below is {}, which is not above 0 and under 1, so the phase is dropped", key, below);
                continue;
            }
            if (phases.stream().anyMatch(earlier -> earlier.below() == below)) {
                ContentLog.LOGGER.error("Entity variant {} has two phases with below {}, so the later one is dropped", key, below);
                continue;
            }
            Map<String, Double> attributes = doubles(key, entry, "attributes", "phase attribute");
            attributes.keySet().removeIf(name -> {
                boolean health = ContentAttributes.find(name, key) == Attributes.MAX_HEALTH;
                if (health) { ContentLog.LOGGER.error("Entity variant {} sets maxHealth in the phase below {}, which would move the threshold itself, so it is ignored", key, below); }
                return health;
            });
            BossBarDef bar = BossBars.read("Entity variant " + key + " phase below " + below, entry);
            if (bar != null && !hasBar) {
                ContentLog.LOGGER.error("Entity variant {} sets a bossBar in the phase below {}, but the variant has no bossBar of its own, so the phase's is dropped", key, below);
                bar = null;
            }
            boolean charges = Json.getBoolean(entry, "charges", false);
            boolean pounces = Json.getBoolean(entry, "pounces", false);
            boolean gusts = Json.getBoolean(entry, "gusts", false);
            if (!hostile && (charges || pounces || gusts)) {
                ContentLog.LOGGER.error("Entity variant {} asks for attack behaviors in the phase below {}, but is not hostile, so they are ignored", key, below);
                charges = false;
                pounces = false;
                gusts = false;
            }
            phases.add(new PhaseDef(below, attributes, ContentTasks.parse(key, entry), charges, pounces, gusts, Math.max(0.1F, Json.getFloat(entry, "gustPower", 1.5F)), bar));
        }
        phases.sort((a, b) -> Float.compare(b.below(), a.below()));
        return phases;
    }

    private static SpawnGateDef gate(ResourceLocation key, JsonObject json) {
        String objective = Json.getString(json, "objective", "").trim();
        Integer least = json.has("scoreAtLeast") ? Json.getInt(json, "scoreAtLeast") : null;
        if (objective.isEmpty() != (least == null)) {
            ContentLog.LOGGER.error("Entity variant {} sets only one of objective and scoreAtLeast, so it leaves the score condition out", key);
            objective = "";
            least = null;
        }
        List<String> dimensions = new ArrayList<>();
        for (String named : Json.strings(json, "dimensions")) { dimensions.add(CardPlace.dimensionId(named)); }
        SpawnGateDef gate = new SpawnGateDef(dimensions, Json.strings(json, "structures"), Math.max(1, Json.getInt(json, "radius", CardPlace.NEAR)),
                CardPlace.tickOf(json, "timeFrom"), CardPlace.tickOf(json, "timeTo"), json.has("dayAtLeast") ? Math.max(0L, Json.getInt(json, "dayAtLeast")) : -1L,
                Json.getString(json, "advancement", "").trim(), objective, least);
        return gate.any() ? gate : SpawnGateDef.NONE;
    }

    @Nullable private static StorageDef storage(ResourceLocation key, JsonObject json) {
        if (!json.has("storage")) { return null; }
        JsonObject storage = Json.getJsonObject(json, "storage");
        JsonObject items = Json.getJsonObject(storage, "items", new JsonObject());
        JsonObject fluid = Json.getJsonObject(storage, "fluid", new JsonObject());
        int fluidCapacity = Math.max(0, Json.getInt(fluid, "capacity", 0));
        JsonObject energy = Json.getJsonObject(storage, "energy", new JsonObject());
        int energyCapacity = Math.max(0, Json.getInt(energy, "capacity", 0));
        int energyTransfer = Mth.clamp(Json.getInt(energy, "transfer", energyCapacity), 0, energyCapacity);
        boolean hasItems = storage.has("items");
        StorageDef def = new StorageDef(hasItems, new FilterDef(entries(key, items)), fluidCapacity, new FilterDef(entries(key, fluid)), energyCapacity, energyTransfer,
                Json.getBoolean(fluid, "buckets", false), Json.getBoolean(storage, "dropsOnDeath", true), Math.max(0, Json.getInt(fluid, "use", 0)),
                Math.max(0, Json.getInt(energy, "use", 0)), runsDry(key, storage));
        if (!hasItems && def.gauges() == 0) {
            ContentLog.LOGGER.error("The storage on entity variant {} holds no items, fluid or energy, ignoring it", key);
            return null;
        }
        return def;
    }

    private static StorageDef.Dry runsDry(ResourceLocation key, JsonObject storage) {
        String named = Json.getString(storage, "runsDry", "stops");
        StorageDef.Dry dry = Enums.byName(StorageDef.Dry.class, named);
        if (dry != null) { return dry; }
        ContentLog.LOGGER.error("The storage on entity variant {} sets runsDry to '{}', which is not stops, slows or hurts, so it stops", key, named);
        return StorageDef.Dry.STOPS;
    }

    private static List<FilterDef.Entry> entries(ResourceLocation key, JsonObject json) {
        List<FilterDef.Entry> entries = new ArrayList<>();
        if (!json.has("filter")) { return entries; }
        for (JsonElement element : Json.getJsonArray(json, "filter")) {
            JsonObject entry = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
            if (entry.has("oreDict")) { ContentLog.LOGGER.warn("A filter entry in {} uses 'oreDict', which this line does not read. Name a tag under 'tag' instead, such as c:ingots/iron or forge:ingots/iron", key); }
            String item = Json.getString(entry, "item", "").trim();
            ResourceLocation tag = ContentParser.location(Json.getString(entry, "tag", "").trim());
            ResourceLocation held = ContentParser.location(Json.getString(entry, "fluid", "").trim());
            if (item.isEmpty() && tag == null && held == null) {
                ContentLog.LOGGER.error("A filter entry in {} names no item, tag or fluid, skipping it", key);
                continue;
            }
            entries.add(new FilterDef.Entry(key, item, tag == null ? null : TagKey.create(Registries.ITEM, tag), held, Math.max(0, Json.getInt(entry, "max", 0))));
        }
        return entries;
    }

    private static float baby(JsonObject json) {
        if (!json.has("baby")) { return 0.0F; }
        JsonElement held = json.get("baby");
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { return held.getAsBoolean() ? 1.0F : 0.0F; }
        return Mth.clamp(Json.getFloat(json, "baby", 0.0F), 0.0F, 1.0F);
    }

    private static List<PickDef> picks(ResourceLocation key, JsonObject json) {
        List<PickDef> picks = new ArrayList<>();
        if (!json.has("becomes")) { return picks; }
        for (JsonElement element : Json.getJsonArray(json, "becomes")) {
            if (!element.isJsonObject() && !element.isJsonPrimitive()) {
                ContentLog.LOGGER.error("A becomes entry in {} is neither a variant name nor an object, skipping it", key);
                continue;
            }
            String name = Settings.lower(element.isJsonObject() ? Json.getString(element.getAsJsonObject(), "variant", "") : element.getAsString());
            if (name.isEmpty()) {
                ContentLog.LOGGER.error("A becomes entry in {} names no variant, skipping it", key);
                continue;
            }
            picks.add(new PickDef(name, element.isJsonObject() ? Math.max(1, Json.getInt(element.getAsJsonObject(), "weight", 1)) : 1));
        }
        return picks;
    }

    static Map<String, Integer> effects(ResourceLocation key, JsonObject json) {
        Map<String, Integer> effects = new LinkedHashMap<>();
        if (!json.has("effects")) { return effects; }
        for (JsonElement element : Json.getJsonArray(json, "effects")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject effect = element.getAsJsonObject();
            effects.put(Json.getString(effect, "potion", "").trim(), Math.max(0, Json.getInt(effect, "amplifier", 0)));
        }
        return effects;
    }

    private static List<SpawnEntryDef> spawns(ResourceLocation key, JsonObject json) {
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (!json.has("spawns")) { return spawns; }
        for (JsonElement element : Json.getJsonArray(json, "spawns")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            int least = Math.max(1, Json.getInt(entry, "min", 1));
            spawns.add(new SpawnEntryDef(Settings.lower(Json.getString(entry, "creatureType", "creature")),
                    Math.max(1, Json.getInt(entry, "weight", 8)), least, Math.max(least, Json.getInt(entry, "max", 4))));
        }
        return spawns;
    }

    private static Map<String, Double> doubles(ResourceLocation key, JsonObject json, String member, String what) {
        Map<String, Double> found = new LinkedHashMap<>();
        if (!json.has(member)) { return found; }
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, member).entrySet()) {
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
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "equipment").entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("Entity variant {} sets slot '{}' to something that is not a name, ignoring it", key, entry.getKey());
                continue;
            }
            found.put(Settings.lower(entry.getKey()),entry.getValue().getAsString().trim());
        }
        return found;
    }
}
