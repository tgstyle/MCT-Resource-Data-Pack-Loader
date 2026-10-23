package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.entity.ContentTasks;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.BossBars;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ContentParserEntities {
    private static final Set<String> PHASE_KEYS = new LinkedHashSet<>(Arrays.asList("below", "attributes", "tasks", "charges", "pounces", "gusts", "gustPower", "bossBar"));

    private ContentParserEntities() {}

    public static Map<String, Integer> effects(ResourceLocation key, JsonObject json) {
        Map<String, Integer> effects = new LinkedHashMap<>();
        if (json.has("effects")) {
            for (JsonElement element : Json.getJsonArray(json, "effects")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject effect = element.getAsJsonObject();
                effects.put(Json.getString(effect, "potion", ""), Math.max(0, Json.getInt(effect, "amplifier", 0)));
            }
        }
        return effects;
    }

    @Nullable public static EntityVariantDef entityVariant(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "entity file");
        if (json == null) { return null; }
        String base = Json.getString(json, "entity", "");
        if (base.isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} names no entity to copy, ignoring it", key);
            return null;
        }
        Map<String, Double> attributes = numbers(key, json, "attributes", "attribute", JsonElement::getAsDouble);
        List<String> tintParts = new ArrayList<>();
        for (String part : strings(json, "tintParts")) { tintParts.add(Settings.lower(part)); }
        if (tintParts.isEmpty()) { tintParts.add(EntityVariantDef.BODY); }
        Map<String, String> equipment = new LinkedHashMap<>();
        if (json.has("equipment")) {
            for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "equipment").entrySet()) {
                if (!entry.getValue().isJsonPrimitive()) {
                    ContentLog.LOGGER.error("Entity variant {} sets slot '{}' to something that is not an item name, ignoring it", key, entry.getKey());
                    continue;
                }
                equipment.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        List<SpawnEntryDef> spawns = ContentParser.spawns(key, json, "creatureType", false);
        JsonObject sounds = Json.getJsonObject(json, "sounds", new JsonObject());
        Map<String, Integer> effects = effects(key, json);
        Map<String, Float> priorities = numbers(key, json, "pathPriorities", "path priority", JsonElement::getAsFloat);
        JsonObject egg = json.has("egg") && json.get("egg").isJsonObject() ? Json.getJsonObject(json, "egg") : null;
        BossBarDef bar = BossBars.read("Entity variant " + key, json);
        boolean wantsEgg = !json.has("egg") || egg != null || Json.getBoolean(json, "egg", true);
        return new EntityVariantDef(key, new ResourceLocation(base),
                Json.getString(json, "name", ""),
                Json.getString(json, "texture", ""),
                Json.getString(json, "lootTable", ""),
                Json.getString(json, "profession", ""),
                Math.max(0, Json.getInt(json, "career", 0)),
                baby(json),
                Json.getBoolean(json, "keepsBaseBaby", false),
                ContentParser.picks(json, "becomes", "variant"),
                Json.getString(sounds, "ambient", ""),
                Json.getString(sounds, "hurt", ""),
                Json.getString(sounds, "death", ""),
                strings(json, "immuneTo"),
                Math.max(0.1F, Json.getFloat(json, "jumpMultiplier", 1.0F)),
                Math.max(0.0F, Json.getFloat(json, "fallDamage", 1.0F)),
                Json.getBoolean(json, "hitEffects", true),
                strings(json, "ignoresEffects"),
                Math.max(0.0F, Json.getFloat(json, "soundVolume", 1.0F)),
                Math.max(0.1F, Json.getFloat(json, "soundPitch", 1.0F)),
                Math.max(0.0F, Json.getFloat(json, "waterSlowdown", 0.8F)),
                Math.max(-1, Json.getInt(json, "experience", -1)),
                Math.max(-1, Json.getInt(json, "maxFallHeight", -1)),
                Math.max(0.0F, Json.getFloat(json, "absorption", 0.0F)),
                Json.getString(json, "creatureAttribute", ""),
                Json.getBoolean(json, "breathesUnderwater", false),
                Json.getBoolean(json, "swims", false),
                Json.getBoolean(json, "amphibious", false),
                Json.getBoolean(json, "despawns", true),
                Math.max(0, Json.getInt(json, "despawnAfter", 0)) * 20,
                Json.getBoolean(json, "noAI", false),
                Json.getBoolean(json, "leftHanded", false),
                Json.getBoolean(json, "fireproof", false),
                Json.getBoolean(json, "invulnerable", false),
                Json.getBoolean(json, "glowing", false),
                Json.getBoolean(json, "invisible", false),
                MathHelper.clamp(Json.getFloat(json, "dropChance", 0.0F), 0.0F, 1.0F),
                Math.max(0.05F, Json.getFloat(json, "scale", 1.0F)),
                Math.max(0.05F, Json.getFloat(json, "angryScale", Json.getFloat(json, "scale", 1.0F))),
                Json.getBoolean(json, "leashable", false),
                Json.getBoolean(json, "steerable", false),
                Math.max(0.0F, Json.getFloat(json, "width", 0.0F)),
                Math.max(0.0F, Json.getFloat(json, "height", 0.0F)),
                effects, priorities,
                wantsEgg,
                egg == null || !egg.has("primary") ? -1 : ContentTypes.color(Json.getString(egg, "primary", ""), key + " egg primary"),
                egg == null || !egg.has("secondary") ? -1 : ContentTypes.color(Json.getString(egg, "secondary", ""), key + " egg secondary"),
                Math.max(1, Json.getInt(json, "trackingRange", 80)),
                Math.max(1, Json.getInt(json, "trackingFrequency", 3)),
                Json.getBoolean(json, "trackVelocity", true),
                attributes,
                Json.getBoolean(json, "hostile", false),
                Json.getBoolean(json, "passive", false),
                Json.getBoolean(json, "ignoresSpawnRules", false),
                strings(json, "targets"),
                Json.getBoolean(json, "persistent", false),
                Json.getBoolean(json, "silent", false),
                Json.getBoolean(json, "picksUpLoot", false),
                Json.getBoolean(json, "hideArmor", false),
                Json.getBoolean(json, "hideHeld", false),
                json.has("tint") ? ContentTypes.color(Json.getString(json, "tint", ""), key + " tint") : 0,
                tintParts,
                Json.getBoolean(json, "showName", false),
                Json.getBoolean(json, "explodes", false),
                Json.getBoolean(json, "throws", false),
                Math.max(0, Json.getInt(json, "throwReload", 0)) * 20,
                Math.max(0, Json.getInt(json, "throwRetreat", 0)) * 20,
                Math.max(0, Json.getInt(json, "throwAmmo", 0)),
                Math.max(0.0F, Json.getFloat(json, "throwPower", 1.0F)),
                Json.getFloat(json, "throwArc", 0.35F),
                Math.max(0.0F, Json.getFloat(json, "explosionPower", 3.0F)),
                Math.max(1, Json.getInt(json, "explosionFuse", 30)),
                Json.getBoolean(json, "explosionFire", false),
                equipment, spawns,
                strings(json, "biomes"), strings(json, "biomeTypes"), strings(json, "requires"),
                Json.getBoolean(json, "charges", false),
                Json.getBoolean(json, "pounces", false),
                Math.max(0, Json.getInt(json, "sniffs", 0)),
                Json.getBoolean(json, "sleepsByDay", false),
                Math.max(0, Json.getInt(json, "home", 0)),
                MathHelper.clamp(Json.getFloat(json, "fleesWhenHurt", 0.0F), 0.0F, 1.0F),
                Json.getBoolean(json, "patrols", false),
                Json.getBoolean(json, "swoops", false),
                Json.getBoolean(json, "gusts", false),
                Math.max(0.1F, Json.getFloat(json, "gustPower", 1.5F)),
                Math.max(0, Json.getInt(json, "threatLeast", 0)),
                Math.max(0, Json.getInt(json, "threatHostile", 0)),
                ContentTasks.parse(key, json),
                Math.max(0.0F, Json.getFloat(json, "attackReach", 0.0F)),
                Math.max(-1, Json.getInt(json, "hurtResistance", -1)),
                json.has("stepHeight") ? Math.max(0.0F, Json.getFloat(json, "stepHeight", 0.0F)) : -1.0F,
                Json.getBoolean(json, "hitFire", true),
                json.has("climbs") ? Json.getBoolean(json, "climbs", false) : null,
                Json.getBoolean(json, "teleports", true),
                json.has("knockback") ? Math.max(0.0F, Json.getFloat(json, "knockback", 0.4F)) : -1.0F,
                json.has("explosionPower") || json.has("explosionFuse"),
                Json.getBoolean(json, "digs", false),
                Json.getString(sounds, "target", ""),
                Json.getString(sounds, "explode", ""),
                Math.max(0.0F, Json.getFloat(sounds, "targetVaries", 0.0F)),
                Json.getBoolean(json, "bright", false),
                Json.getBoolean(json, "collectsExperience", false),
                Json.getBoolean(json, "walks", false),
                Json.getBoolean(json, "throwReturns", false),
                Json.getString(sounds, "throw", ""),
                rocket(key, json), storage(key, json), SpawnGateDef.read(key, json, "Entity variant"), bar, phases(key, json, bar != null));
    }

    private static List<PhaseDef> phases(ResourceLocation key, JsonObject json, boolean hasBar) {
        List<PhaseDef> phases = new ArrayList<>();
        boolean hostile = Json.getBoolean(json, "hostile", false) && !Json.getBoolean(json, "passive", false);
        for (JsonElement element : Json.getJsonArray(json, "phases", new JsonArray())) {
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
            boolean repeated = false;
            for (PhaseDef earlier : phases) { repeated |= earlier.below == below; }
            if (repeated) {
                ContentLog.LOGGER.error("Entity variant {} has two phases with below {}, so the later one is dropped", key, below);
                continue;
            }
            Map<String, Double> attributes = numbers(key, entry, "attributes", "phase attribute", JsonElement::getAsDouble);
            attributes.keySet().removeIf(name -> {
                boolean health = ContentAttributes.lookup(name) == SharedMonsterAttributes.MAX_HEALTH;
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
        phases.sort((a, b) -> Float.compare(b.below, a.below));
        return phases;
    }

    private static <T> Map<String, T> numbers(ResourceLocation key, JsonObject json, String member, String what, Function<JsonElement, T> read) {
        Map<String, T> found = new LinkedHashMap<>();
        if (!json.has(member)) { return found; }
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, member).entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                ContentLog.LOGGER.error("Entity variant {} sets {} '{}' to something that is not a number, ignoring it", key, what, entry.getKey());
                continue;
            }
            found.put(entry.getKey(), read.apply(entry.getValue()));
        }
        return found;
    }

    @Nullable private static RocketDef rocket(ResourceLocation key, JsonObject json) {
        if (!json.has("galacticraft")) { return null; }
        JsonObject rocket = Json.getJsonObject(json, "galacticraft");
        int slots = Json.getInt(rocket, "cargoSlots", 0);
        if (slots != 0 && slots != 27 && slots != 36 && slots != 54) {
            ContentLog.LOGGER.error("Entity variant {} sets cargoSlots to {}, which is not one of 0, 27, 36 or 54, using 0", key, slots);
            slots = 0;
        }
        return new RocketDef(Math.max(-1, Json.getInt(rocket, "tier", -1)), Math.max(-1, Json.getInt(rocket, "fuelTank", -1)), slots,
                new FilterDef(entries(key, rocket, "cargo", "max", 0)), entries(key, rocket, "requiredPayload", "count", 1), entries(key, rocket, "payload", "count", 1));
    }

    @Nullable private static StorageDef storage(ResourceLocation key, JsonObject json) {
        if (!json.has("storage")) { return null; }
        JsonObject storage = Json.getJsonObject(json, "storage");
        JsonObject items = Json.getJsonObject(storage, "items", new JsonObject());
        JsonObject fluid = Json.getJsonObject(storage, "fluid", new JsonObject());
        int fluidCapacity = Math.max(0, Json.getInt(fluid, "capacity", 0));
        JsonObject energy = Json.getJsonObject(storage, "energy", new JsonObject());
        int energyCapacity = Math.max(0, Json.getInt(energy, "capacity", 0));
        int energyTransfer = MathHelper.clamp(Json.getInt(energy, "transfer", energyCapacity), 0, energyCapacity);
        int gauges = (fluidCapacity > 0 ? 1 : 0) + (energyCapacity > 0 ? 1 : 0);
        boolean hasItems = storage.has("items");
        if (!hasItems && gauges == 0) {
            ContentLog.LOGGER.error("The storage on entity variant {} holds no items, fluid or energy, ignoring it", key);
            return null;
        }
        return new StorageDef(hasItems, new FilterDef(entries(key, items, "filter", "max", 0)), fluidCapacity, new FilterDef(entries(key, fluid, "filter", "max", 0)), energyCapacity, energyTransfer,
                Json.getBoolean(fluid, "buckets", false), Json.getBoolean(storage, "dropsOnDeath", true), Math.max(0, Json.getInt(fluid, "use", 0)),
                Math.max(0, Json.getInt(energy, "use", 0)), runsDry(key, storage));
    }

    private static StorageDef.Dry runsDry(ResourceLocation key, JsonObject storage) {
        String named = Json.getString(storage, "runsDry", "stops");
        StorageDef.Dry dry = Enums.byName(StorageDef.Dry.class, named);
        if (dry != null) { return dry; }
        ContentLog.LOGGER.error("The storage on entity variant {} sets runsDry to '{}', which is not stops, slows or hurts, so it stops", key, named);
        return StorageDef.Dry.STOPS;
    }

    private static List<FilterDef.Entry> entries(ResourceLocation key, JsonObject json, String list, String amount, int least) {
        List<FilterDef.Entry> entries = new ArrayList<>();
        if (!json.has(list)) { return entries; }
        for (JsonElement element : Json.getJsonArray(json, list)) {
            JsonObject entry = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
            String item = Json.getString(entry, "item", "");
            String oreDict = Json.getString(entry, "oreDict", "");
            String fluid = Json.getString(entry, "fluid", "");
            if (item.isEmpty() && oreDict.isEmpty() && fluid.isEmpty()) {
                ContentLog.LOGGER.error("A {} entry in {} names no item, oreDict or fluid, skipping it", list, key);
                continue;
            }
            entries.add(new FilterDef.Entry(key, item, oreDict, fluid, Math.max(least, Json.getInt(entry, amount, least))));
        }
        return entries;
    }

    private static float baby(JsonObject json) {
        if (!json.has("baby")) { return 0.0F; }
        JsonElement held = json.get("baby");
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isBoolean()) { return held.getAsBoolean() ? 1.0F : 0.0F; }
        return MathHelper.clamp(Json.getFloat(json, "baby", 0.0F), 0.0F, 1.0F);
    }
}
