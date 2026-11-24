package mctmods.resourcedatapackloader.common.enchantments;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.EnchantmentDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.EnchantmentEffectDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.strings;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentEnchantments {
    private static final int MOST = 255;
    private static final double DEFAULT_DURATION = 100.0D;
    private static final List<String> SLOTS = Collections.unmodifiableList(Stream.of(EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET).map(EntityEquipmentSlot::getName).collect(Collectors.toList()));
    private static final List<String> ARMOR_SLOTS = Collections.unmodifiableList(Arrays.asList("head", "chest", "legs", "feet"));
    private static final List<String> EFFECT_TYPES = Collections.unmodifiableList(Arrays.asList("protection", "damage", "apply_effect"));
    private static final List<String> TRIGGERS = Collections.unmodifiableList(Arrays.asList("attack", "hurt"));
    private static final List<String> ENCHANTMENT_KEYS = Collections.unmodifiableList(Arrays.asList("name", "category", "items", "slots", "maxLevel", "minCost", "maxCost", "rarity", "treasure", "effects", "requires"));
    private static final List<String> EFFECT_KEYS = Collections.unmodifiableList(Arrays.asList("type", "amount", "from", "mob", "on", "potion", "duration", "amplifier"));
    private static final List<String> LINEAR_KEYS = Collections.unmodifiableList(Arrays.asList("base", "perLevel"));
    private static final Map<ResourceLocation, EnchantmentDef> ENCHANTMENTS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentEnchantments() {}

    public static boolean load() {
        if (loaded) { return wanted(); }
        loaded = true;
        if (!Config.loads(Config.content.enchantments)) { return false; }
        Json.eachObject(PackManager.ENCHANTMENTS, "enchantment file", (key, json) -> {
            if (!ContentOwners.reserved(key)) { read(key, json); }
        });
        Summary.loaded("enchantments", ENCHANTMENTS.size(), "enchantment(s) from packs");
        return wanted();
    }

    public static boolean wanted() { return !ENCHANTMENTS.isEmpty(); }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", ENCHANTMENT_KEYS);
        String category = Json.choice(json, "category", "", key, ContentEnchantment.CATEGORIES.keySet());
        List<String> items = strings(json, "items");
        if (category.isEmpty() && items.isEmpty()) {
            ContentLog.LOGGER.error("Enchantment {} has no usable category and no items, so it can go on nothing. Skipping it", key);
            return;
        }
        List<String> slots = new ArrayList<>();
        for (String slot : strings(json, "slots")) {
            if (SLOTS.contains(slot)) { slots.add(slot); }
            else { ContentLog.LOGGER.error("Enchantment {} names slot '{}', which is not one of {}, skipping that slot", key, slot, SLOTS); }
        }
        if (slots.isEmpty()) {
            if (json.has("slots")) {
                ContentLog.LOGGER.error("Enchantment {} has no usable slot, skipping it", key);
                return;
            }
            slots.add(EntityEquipmentSlot.MAINHAND.getName());
        }
        int maxLevel = Json.getInt(json, "maxLevel", 1);
        if (maxLevel < 1 || maxLevel > MOST) {
            ContentLog.LOGGER.error("Enchantment {} sets maxLevel to {}, which is outside 1 to 255, so it is clamped", key, maxLevel);
            maxLevel = MathHelper.clamp(maxLevel, 1, MOST);
        }
        double[] min = linear(json, "minCost", 11, 10);
        double[] max = linear(json, "maxCost", (int) min[0] + 5, (int) min[1]);
        List<EnchantmentEffectDef> effects = new ArrayList<>();
        if (json.has("effects")) {
            for (JsonObject element : Json.objectsOrSkip(Json.getJsonArray(json, "effects"), "An effect in {} is not an object, skipping it", key)) {
                EnchantmentEffectDef effect = readEffect(key, element, slots);
                if (effect != null) { effects.add(effect); }
            }
        }
        ENCHANTMENTS.put(key, new EnchantmentDef(key,
                Json.getString(json, "name", "enchantment." + key.getNamespace() + "." + key.getPath()),
                maxLevel,
                Json.choice(json, "rarity", "common", key, ContentEnchantment.RARITIES.keySet()),
                Collections.unmodifiableList(slots),
                category,
                items,
                (int) min[0], (int) min[1], (int) max[0], (int) max[1],
                Json.getBoolean(json, "treasure", false),
                Collections.unmodifiableList(effects),
                strings(json, "requires")));
    }

    private static EnchantmentEffectDef readEffect(ResourceLocation key, JsonObject entry, List<String> slots) {
        Json.unknownKeys(entry, "effects", EFFECT_KEYS);
        String type = Json.choice(entry, "type", "", key, EFFECT_TYPES);
        if (type.isEmpty()) {
            ContentLog.LOGGER.error("An effect in {} has no usable type, skipping it", key);
            return null;
        }
        double[] amount = linear(entry, "amount", 0, 0);
        List<String> from = new ArrayList<>();
        String potion = "";
        double[] duration = {DEFAULT_DURATION, 0};
        if (type.equals("protection")) {
            for (String slot : slots) {
                if (!ARMOR_SLOTS.contains(slot)) {
                    ContentLog.LOGGER.error("A protection effect in {} needs every slot to be head, chest, legs or feet, because protection only counts on worn armor. Skipping it", key);
                    return null;
                }
            }
            for (String kind : strings(entry, "from")) {
                if (ContentEnchantment.PROTECTIONS.containsKey(kind)) { from.add(kind); }
                else { ContentLog.LOGGER.error("A protection effect in {} names '{}', which is not one of {}, skipping that entry", key, kind, ContentEnchantment.PROTECTIONS.keySet()); }
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
            duration = linear(entry, "duration", DEFAULT_DURATION, 0);
        }
        return new EnchantmentEffectDef(type, Collections.unmodifiableList(from),
                Json.choice(entry, "mob", "any", key, ContentEnchantment.MOBS.keySet()),
                Json.choice(entry, "on", "attack", key, TRIGGERS),
                potion, amount[0], amount[1], duration[0], duration[1],
                MathHelper.clamp(Json.getInt(entry, "amplifier", 0), 0, MOST));
    }

    private static double[] linear(JsonObject json, String member, double base, double perLevel) {
        JsonObject object = Json.getJsonObject(json, member, new JsonObject());
        Json.unknownKeys(object, member, LINEAR_KEYS);
        return new double[]{Json.getFloat(object, "base", (float) base), Json.getFloat(object, "perLevel", (float) perLevel)};
    }

    @SubscribeEvent public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        int count = 0;
        for (EnchantmentDef def : ENCHANTMENTS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            if (ForgeRegistries.ENCHANTMENTS.containsKey(def.registryName)) {
                ContentLog.LOGGER.debug("Enchantment {} is already registered, leaving it alone", def.registryName);
                continue;
            }
            List<EnchantmentEffectDef> usable = new ArrayList<>();
            for (EnchantmentEffectDef effect : def.effects) {
                if (effect.type.equals("apply_effect") && Potion.getPotionFromResourceLocation(effect.potion) == null) {
                    ContentLog.LOGGER.error("Enchantment {} names potion '{}', which is not registered, skipping that effect", def.registryName, effect.potion);
                    continue;
                }
                usable.add(effect);
            }
            Registries.register(event.getRegistry(), def.registryName.getNamespace(), () -> new ContentEnchantment(def, usable));
            count++;
        }
        if (count > 0) { Summary.info("content_enchantments", "Registered " + count + " enchantment(s) from packs"); }
    }
}
