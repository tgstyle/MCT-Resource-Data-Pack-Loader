package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.EnchantmentDef;
import mctmods.resourcedatapackloader.content.def.EnchantmentEffectDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ContentEnchantments {
    private static final List<String> RARITIES = List.of("common", "uncommon", "rare", "very_rare");
    private static final List<String> SLOTS = List.of("mainhand", "offhand", "head", "chest", "legs", "feet");
    private static final List<String> ARMOR_SLOTS = List.of("head", "chest", "legs", "feet");
    private static final List<String> CATEGORIES = List.of("armor", "armor_head", "armor_chest", "armor_legs", "armor_feet", "weapon", "digger", "fishing_rod", "breakable", "bow", "wearable");
    private static final List<String> EFFECT_TYPES = List.of("protection", "damage", "apply_effect");
    private static final List<String> PROTECTIONS = List.of("all", "fire", "fall", "explosion", "projectile");
    private static final List<String> MOBS = List.of("any", "undead", "arthropod", "illager");
    private static final List<String> TRIGGERS = List.of("attack", "hurt");
    private static final Map<ResourceLocation, EnchantmentDef> ENCHANTMENTS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentEnchantments() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.enchantments()) { return; }
        if (!Config.contentOff()) {
            Json.eachFile(PackManager.ENCHANTMENTS, "enchantment file", (key, contents) -> {
                if (!ContentRegistry.reserved(key)) { read(key, contents); }
            });
        }
        if (!ENCHANTMENTS.isEmpty()) { Summary.info("enchantments", "Loaded " + ENCHANTMENTS.size() + " enchantment(s) from packs"); }
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "enchantment file");
        if (json == null) { return; }
        String category = Json.choice(json, "category", "", key, CATEGORIES);
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
            maxLevel = Math.max(1, Math.min(255, maxLevel));
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

    private static EnchantmentEffectDef readEffect(ResourceLocation key, JsonObject entry, List<String> slots) {
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
                if (PROTECTIONS.contains(kind)) { from.add(kind); }
                else { ContentLog.LOGGER.error("A protection effect in {} names '{}', which is not one of {}, skipping that entry", key, kind, PROTECTIONS); }
            }
            if (from.isEmpty()) { from.add("all"); }
            amount = new double[]{(int) amount[0], (int) amount[1]};
        }
        if (type.equals("apply_effect")) {
            potion = Json.getString(entry, "potion", "");
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
                Math.max(0, Math.min(255, Json.getInt(entry, "amplifier", 0))));
    }

    private static double[] linear(JsonObject json, String member, double base, double perLevel) {
        JsonObject object = Json.getJsonObject(json, member, new JsonObject());
        return new double[]{Json.getFloat(object, "base", (float) base), Json.getFloat(object, "perLevel", (float) perLevel)};
    }

    public static void register(RegisterEvent.RegisterHelper<Enchantment> helper) {
        load();
        int count = 0;
        for (EnchantmentDef def : ENCHANTMENTS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            if (ForgeRegistries.ENCHANTMENTS.containsKey(def.key())) {
                ContentLog.LOGGER.debug("Enchantment {} is already registered, leaving it alone", def.key());
                continue;
            }
            List<EnchantmentEffectDef> usable = new ArrayList<>();
            for (EnchantmentEffectDef effect : def.effects()) {
                if (effect.type().equals("apply_effect") && ContentEffects.find(effect.potion()) == null) {
                    ContentLog.LOGGER.error("Enchantment {} names potion '{}', which is not registered, skipping that effect", def.key(), effect.potion());
                    continue;
                }
                usable.add(effect);
            }
            helper.register(def.key(), new ContentEnchantment(def, usable));
            count++;
        }
        if (count > 0) { Summary.info("content_enchantments", "Registered " + count + " enchantment(s) from packs"); }
    }
}
