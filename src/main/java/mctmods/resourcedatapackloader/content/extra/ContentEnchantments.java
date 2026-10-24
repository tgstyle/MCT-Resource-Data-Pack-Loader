package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.compat.Compat;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.EnchantmentDef;
import mctmods.resourcedatapackloader.content.def.EnchantmentEffectDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentEnchantments {
    private static final String MINECRAFT = "minecraft";
    private static final String ELEMENTS = Registries.elementsDirPath(Registries.ENCHANTMENT);
    private static final String TAGS = Registries.tagsDirPath(Registries.ENCHANTMENT);
    private static final String PROTECTION = "minecraft:damage_protection";
    private static final String DAMAGE = "minecraft:damage";
    private static final String POST_ATTACK = "minecraft:post_attack";
    private static final String DAMAGE_SOURCE = "minecraft:damage_source_properties";
    private static final String BYPASS = "bypasses_invulnerability";
    private static final List<String> RARITIES = List.of("common", "uncommon", "rare", "very_rare");
    private static final int[] WEIGHTS = {10, 5, 2, 1};
    private static final int[] ANVIL_COSTS = {1, 2, 4, 8};
    private static final List<String> SLOTS = List.of("mainhand", "offhand", "head", "chest", "legs", "feet");
    private static final List<String> ARMOR_SLOTS = List.of("head", "chest", "legs", "feet");
    private static final Map<String, List<String>> CATEGORIES = Map.ofEntries(
            Map.entry("armor", List.of("minecraft:enchantable/armor")),
            Map.entry("armor_head", List.of("minecraft:enchantable/head_armor")),
            Map.entry("armor_chest", List.of("minecraft:enchantable/chest_armor")),
            Map.entry("armor_legs", List.of("minecraft:enchantable/leg_armor")),
            Map.entry("armor_feet", List.of("minecraft:enchantable/foot_armor")),
            Map.entry("weapon", List.of("minecraft:swords")),
            Map.entry("digger", List.of("minecraft:axes", "minecraft:pickaxes", "minecraft:shovels")),
            Map.entry("fishing_rod", List.of("minecraft:enchantable/fishing")),
            Map.entry("breakable", List.of("minecraft:enchantable/durability")),
            Map.entry("bow", List.of("minecraft:enchantable/bow")),
            Map.entry("wearable", List.of("minecraft:enchantable/equippable")));
    private static final List<String> EFFECT_TYPES = List.of("protection", "damage", "apply_effect");
    private static final Map<String, String> PROTECTIONS = Map.of("fire", "is_fire", "fall", "is_fall", "explosion", "is_explosion", "projectile", "is_projectile");
    private static final List<String> MOBS = List.of("any", "undead", "arthropod", "illager");
    private static final List<String> TRIGGERS = List.of("attack", "hurt");
    private static final Map<Identifier, EnchantmentDef> ENCHANTMENTS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentEnchantments() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff() || !Config.content.enchantments()) { return; }
        Json.eachFile(PackManager.ENCHANTMENTS, "enchantment file", (key, contents) -> {
            if (!ContentRegistry.reserved(key)) { read(key, contents); }
        });
        if (!ENCHANTMENTS.isEmpty()) { Summary.info("enchantments", "Loaded " + ENCHANTMENTS.size() + " enchantment(s) from packs"); }
    }

    private static void read(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "enchantment file");
        if (json == null) { return; }
        String category = Json.choice(json, "category", "", key, CATEGORIES.keySet());
        List<String> items = Json.strings(json, "items");
        if (category.isEmpty() && items.isEmpty()) {
            ContentLog.LOGGER.error("Enchantment {} has no usable category and no items, so it can go on nothing. Skipping it", key);
            return;
        }
        List<String> slots = new ArrayList<>();
        for (String slot : Json.strings(json, "slots")) {
            if (SLOTS.contains(slot)) { slots.add(slot); }
            else { ContentLog.LOGGER.error("Enchantment {} names slot '{}', which is not one of {}, skipping that slot", key, slot, SLOTS); }
        }
        if (slots.isEmpty()) {
            if (json.has("slots")) {
                ContentLog.LOGGER.error("Enchantment {} has no usable slot, skipping it", key);
                return;
            }
            slots.add("mainhand");
        }
        int maxLevel = Json.getInt(json, "maxLevel", 1);
        if (maxLevel < 1 || maxLevel > 255) {
            ContentLog.LOGGER.error("Enchantment {} sets maxLevel to {}, which is outside 1 to 255, so it is clamped", key, maxLevel);
            maxLevel = Math.clamp(maxLevel, 1, 255);
        }
        double[] min = linear(json, "minCost", 11, 10);
        double[] max = linear(json, "maxCost", (int) min[0] + 5, (int) min[1]);
        List<EnchantmentEffectDef> effects = new ArrayList<>();
        if (json.has("effects")) {
            for (JsonElement element : Json.getJsonArray(json, "effects")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                    continue;
                }
                EnchantmentEffectDef effect = readEffect(key, element.getAsJsonObject(), slots);
                if (effect != null) { effects.add(effect); }
            }
        }
        ENCHANTMENTS.put(key, new EnchantmentDef(key,
                Json.getString(json, "name", "enchantment." + key.getNamespace() + "." + key.getPath()),
                maxLevel,
                Json.choice(json, "rarity", "common", key, RARITIES),
                Collections.unmodifiableList(slots),
                category,
                items,
                (int) min[0], (int) min[1], (int) max[0], (int) max[1],
                Json.getBoolean(json, "treasure", false),
                Collections.unmodifiableList(effects),
                Json.strings(json, "requires")));
    }

    private static EnchantmentEffectDef readEffect(Identifier key, JsonObject entry, List<String> slots) {
        String type = Json.choice(entry, "type", "", key, EFFECT_TYPES);
        if (type.isEmpty()) {
            ContentLog.LOGGER.error("An effect in {} has no usable type, skipping it", key);
            return null;
        }
        double[] amount = linear(entry, "amount", 0, 0);
        List<String> from = new ArrayList<>();
        String potion = "";
        double[] duration = {100, 0};
        if (type.equals("protection")) {
            for (String slot : slots) {
                if (!ARMOR_SLOTS.contains(slot)) {
                    ContentLog.LOGGER.error("A protection effect in {} needs every slot to be head, chest, legs or feet, because protection only counts on worn armor. Skipping it", key);
                    return null;
                }
            }
            for (String kind : Json.strings(entry, "from")) {
                if (kind.equals("all") || PROTECTIONS.containsKey(kind)) { from.add(kind); }
                else { ContentLog.LOGGER.error("A protection effect in {} names '{}', which is not all, fire, fall, explosion or projectile, skipping that entry", key, kind); }
            }
            if (from.isEmpty()) { from.add("all"); }
            amount = new double[]{(int) amount[0], (int) amount[1]};
        }
        if (type.equals("apply_effect")) {
            potion = Settings.lower(Json.getString(entry, "potion", ""));
            if (potion.isEmpty()) {
                ContentLog.LOGGER.error("An apply_effect in {} names no potion, skipping it", key);
                return null;
            }
            duration = linear(entry, "duration", 100, 0);
        }
        return new EnchantmentEffectDef(type, Collections.unmodifiableList(from),
                Json.choice(entry, "mob", "any", key, MOBS),
                Json.choice(entry, "on", "attack", key, TRIGGERS),
                potion, amount[0], amount[1], duration[0], duration[1],
                Math.clamp(Json.getInt(entry, "amplifier", 0), 0, 255));
    }

    private static double[] linear(JsonObject json, String member, double base, double perLevel) {
        JsonObject object = Json.getJsonObject(json, member, new JsonObject());
        return new double[]{Json.getFloat(object, "base", (float) base), Json.getFloat(object, "perLevel", (float) perLevel)};
    }

    public static void generate() {
        Map<String, Set<String>> tags = new LinkedHashMap<>();
        int made = 0;
        for (EnchantmentDef def : ENCHANTMENTS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            String namespace = def.key().getNamespace();
            GeneratedResources.put(PackType.SERVER_DATA, namespace, ELEMENTS + "/" + def.key().getPath() + PackManager.JSON_SUFFIX, build(def).toString());
            GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.ITEM_TAGS + "/enchantable/" + def.key().getPath() + PackManager.JSON_SUFFIX, supported(def).toString());
            for (String tag : def.treasure() ? List.of("treasure", "tradeable", "on_random_loot") : List.of("non_treasure")) {
                tags.computeIfAbsent(tag, _ -> new LinkedHashSet<>()).add(def.key().toString());
            }
            made++;
        }
        for (Map.Entry<String, Set<String>> tag : tags.entrySet()) {
            JsonObject json = Json.obj("replace", false, "values", new ArrayList<>(tag.getValue()));
            GeneratedResources.put(PackType.SERVER_DATA, MINECRAFT, TAGS + "/" + tag.getKey() + PackManager.JSON_SUFFIX, json.toString());
        }
        if (made > 0) { Summary.info("enchantments.generated", "Generated " + made + " enchantment(s) from pack definitions"); }
    }

    private static JsonObject supported(EnchantmentDef def) {
        JsonArray values = new JsonArray();
        if (!def.category().isEmpty()) {
            for (String tag : CATEGORIES.get(def.category())) { values.add("#" + tag); }
        }
        for (String item : def.items()) { values.add(Json.obj("id", Settings.lower(item), "required", false)); }
        return Json.obj("replace", false, "values", values);
    }

    private static JsonObject build(EnchantmentDef def) {
        int rarity = RARITIES.indexOf(def.rarity());
        JsonObject json = Json.obj("description", Json.obj("translate", def.name()),
                "weight", WEIGHTS[rarity],
                "anvil_cost", ANVIL_COSTS[rarity],
                "max_level", def.maxLevel(),
                "min_cost", cost(def.minBase(), def.minPerLevel()),
                "max_cost", cost(def.maxBase(), def.maxPerLevel()),
                "slots", def.slots(),
                "supported_items", "#" + def.key().getNamespace() + ":enchantable/" + def.key().getPath());
        Map<String, JsonArray> effects = new LinkedHashMap<>();
        for (EnchantmentEffectDef effect : def.effects()) {
            switch (effect.type()) {
                case "protection" -> effects.computeIfAbsent(PROTECTION, _ -> new JsonArray()).add(Json.obj("effect", add(effect), "requirements", protection(effect.from())));
                case "damage" -> effects.computeIfAbsent(DAMAGE, _ -> new JsonArray()).add(Json.obj("effect", add(effect), "requirements", damage(effect.mob())));
                default -> {
                    if (ContentEffects.find(effect.potion()) == null) {
                        ContentLog.LOGGER.error("Enchantment {} names potion '{}', which is not registered, skipping that effect", def.key(), effect.potion());
                        continue;
                    }
                    effects.computeIfAbsent(POST_ATTACK, _ -> new JsonArray()).add(apply(effect));
                }
            }
        }
        if (!effects.isEmpty()) {
            JsonObject held = new JsonObject();
            effects.forEach(held::add);
            json.add("effects", held);
        }
        return json;
    }

    private static JsonObject cost(int base, int perLevel) { return Json.obj("base", base, "per_level_above_first", perLevel); }

    private static JsonObject level(double base, double perLevel) { return Json.obj("type", "minecraft:linear", "base", base, "per_level_above_first", perLevel); }

    private static JsonObject add(EnchantmentEffectDef effect) { return Json.obj("type", "minecraft:add", "value", level(effect.amountBase(), effect.amountPerLevel())); }

    private static JsonObject apply(EnchantmentEffectDef effect) {
        boolean attack = effect.on().equals("attack");
        JsonObject duration = level(effect.durationBase() / 20.0D, effect.durationPerLevel() / 20.0D);
        return Json.obj("enchanted", attack ? "attacker" : "victim",
                "affected", attack ? "victim" : "attacker",
                "effect", Json.obj("type", "minecraft:apply_mob_effect",
                        "to_apply", List.of(effect.potion()),
                        "min_duration", duration, "max_duration", duration,
                        "min_amplifier", effect.amplifier(), "max_amplifier", effect.amplifier()));
    }

    private static JsonObject condition(String type, JsonObject body) {
        JsonObject json = new JsonObject();
        json.addProperty(Compat.conditionKey(), type);
        body.entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
        return json;
    }

    private static JsonObject tag(boolean expected, String id) { return Json.obj("expected", expected, "id", Compat.damageTagId(MINECRAFT + ":" + id)); }

    private static JsonObject sourceTags(String kind) {
        List<JsonObject> tags = new ArrayList<>();
        if (!kind.isEmpty()) { tags.add(tag(true, PROTECTIONS.get(kind))); }
        tags.add(tag(false, BYPASS));
        return condition(DAMAGE_SOURCE, Json.obj("predicate", Json.obj("tags", tags)));
    }

    private static JsonObject protection(List<String> from) {
        if (from.contains("all")) { return sourceTags(""); }
        if (from.size() == 1) { return sourceTags(from.getFirst()); }
        List<JsonObject> terms = new ArrayList<>();
        for (String kind : from) { terms.add(sourceTags(kind)); }
        return condition("minecraft:any_of", Json.obj("terms", terms));
    }

    private static JsonObject damage(String mob) {
        JsonObject direct = condition(DAMAGE_SOURCE, Json.obj("predicate", Json.obj("is_direct", true)));
        if (mob.equals("any")) { return direct; }
        JsonObject entity = condition("minecraft:entity_properties", Json.obj("entity", "this", "predicate", Json.obj(Compat.entityTypeKey(), "#minecraft:" + mob)));
        return condition("minecraft:all_of", Json.obj("terms", List.of(direct, entity)));
    }
}
