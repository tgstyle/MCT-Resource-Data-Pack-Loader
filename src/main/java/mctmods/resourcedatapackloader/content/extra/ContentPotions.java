package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.content.def.BrewingDef;
import mctmods.resourcedatapackloader.content.def.PotionDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.def.PotionTypeDef;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public final class ContentPotions {
    private static final String ICON = "icon";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, PotionDef> POTIONS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, PotionTypeDef> TYPES = new LinkedHashMap<>();
    private static final List<BrewingDef> BREWING = new ArrayList<>();
    private static boolean loaded;

    private ContentPotions() {}

    public static boolean load() {
        if (loaded) { return wanted(); }
        loaded = true;
        if (Config.contentOff() || !Config.content.potions()) { return false; }
        Json.eachFile(PackManager.POTIONS, "potion file", (key, contents) -> {
            if (!ContentRegistry.reserved(key)) { readPotion(key, contents); }
        });
        Json.eachFile(PackManager.POTION_TYPES, "potion type file", (key, contents) -> {
            if (!ContentRegistry.reserved(key)) { readType(key, contents); }
        });
        if (Config.content.brewing()) {
            Json.eachFile(PackManager.BREWING, "brewing file", (key, contents) -> {
                if (!ContentRegistry.reserved(key)) { readBrewing(key, contents); }
            });
        }
        if (!POTIONS.isEmpty()) { Summary.info("potions", "Loaded " + POTIONS.size() + " potion effect(s) from packs"); }
        if (!TYPES.isEmpty()) { Summary.info("potion_types", "Loaded " + TYPES.size() + " potion type(s) from packs"); }
        if (!BREWING.isEmpty()) { Summary.info("brewing", "Loaded " + BREWING.size() + " brewing recipe(s) from packs"); }
        return wanted();
    }

    public static boolean wanted() { return !POTIONS.isEmpty() || !TYPES.isEmpty() || !BREWING.isEmpty(); }

    private static void readPotion(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Potion file {} is empty, ignoring it", key);
            return;
        }
        if (json.has(ICON) || json.has("iconTexture")) { ContentLog.LOGGER.warn("Potion {} sets an icon, which this line does not read. Ship the icon as assets/{}/textures/mob_effect/{}.png instead", key, key.getNamespace(), key.getPath()); }
        List<AttributeDef> attributes = new ArrayList<>();
        if (json.has("attributes")) {
            for (JsonElement element : GsonHelper.getAsJsonArray(json, "attributes")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An attribute in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                String uuid = GsonHelper.getAsString(entry, "uuid", "");
                if (uuid.isEmpty()) {
                    ContentLog.LOGGER.error("An attribute in {} has no uuid, skipping it. Generate one once and keep it, because it identifies the modifier", key);
                    continue;
                }
                attributes.add(new AttributeDef(GsonHelper.getAsString(entry, "attribute", ""), uuid,
                        GsonHelper.getAsFloat(entry, "amount", 0.0F),
                        GsonHelper.getAsInt(entry, "operation", 0)));
            }
        }
        JsonObject icon = GsonHelper.getAsJsonObject(json, ICON, new JsonObject());
        POTIONS.put(key, new PotionDef(key,
                GsonHelper.getAsString(json, "name", "effect." + key.getNamespace() + "." + key.getPath()),
                GsonHelper.getAsBoolean(json, "badEffect", false),
                GsonHelper.getAsBoolean(json, "beneficial", false),
                ContentParser.color(GsonHelper.getAsString(json, "color", "FFFFFF"), key.toString()),
                GsonHelper.getAsInt(icon, "x", 0),
                GsonHelper.getAsInt(icon, "y", 0),
                GsonHelper.getAsString(json, "iconTexture", ""),
                GsonHelper.getAsBoolean(json, "instant", false),
                GsonHelper.getAsFloat(json, "effectiveness", 0.5F),
                Collections.unmodifiableList(attributes),
                Json.strings(json, "requires")));
    }

    private static void readType(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Potion type file {} is empty, ignoring it", key);
            return;
        }
        List<PotionEffectDef> effects = ContentParser.effects(key, json);
        if (effects.isEmpty()) {
            ContentLog.LOGGER.error("Potion type {} has no usable effects, ignoring it", key);
            return;
        }
        TYPES.put(key, new PotionTypeDef(key,
                GsonHelper.getAsString(json, "baseName", key.getNamespace() + "." + key.getPath()),
                effects,
                Json.strings(json, "requires")));
    }

    private static void readBrewing(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Brewing file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "brewing")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A brewing recipe in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            BrewingDef def = new BrewingDef(key,
                    GsonHelper.getAsString(entry, "from", ""),
                    GsonHelper.getAsString(entry, "to", ""),
                    GsonHelper.getAsString(entry, "ingredient", ""),
                    GsonHelper.getAsString(entry, "input", ""),
                    GsonHelper.getAsString(entry, "output", ""),
                    Json.strings(entry, "requires"));
            if (def.ingredient().isEmpty()) {
                ContentLog.LOGGER.error("A brewing recipe in {} has no ingredient, skipping it", key);
                continue;
            }
            if (!def.isMix() && (def.input().isEmpty() || def.output().isEmpty())) {
                ContentLog.LOGGER.error("A brewing recipe in {} needs either from and to, or input and output, skipping it", key);
                continue;
            }
            BREWING.add(def);
        }
    }

    public static void registerPotions(RegisterEvent.RegisterHelper<MobEffect> helper) {
        load();
        int count = 0;
        for (PotionDef def : POTIONS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            if (BuiltInRegistries.MOB_EFFECT.containsKey(def.key())) {
                ContentLog.LOGGER.warn("A potion effect named {} is already registered, skipping the pack entry", def.key());
                continue;
            }
            helper.register(def.key(), new ContentPotion(def));
            count++;
        }
        if (count > 0) { Summary.info("content_potions", "Registered " + count + " potion effect(s) from packs"); }
    }

    public static void registerTypes(RegisterEvent.RegisterHelper<Potion> helper) {
        int count = 0;
        for (PotionTypeDef def : TYPES.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            if (BuiltInRegistries.POTION.containsKey(def.key())) {
                ContentLog.LOGGER.warn("A potion type named {} is already registered, skipping the pack entry", def.key());
                continue;
            }
            List<MobEffectInstance> effects = new ArrayList<>();
            for (PotionEffectDef entry : def.effects()) {
                ResourceLocation name = ResourceLocation.tryParse(entry.potion());
                Holder<MobEffect> effect = Registered.holder(BuiltInRegistries.MOB_EFFECT, name);
                if (effect == null) {
                    ContentLog.LOGGER.error("Potion type {} names potion '{}', which is not registered, skipping that effect", def.key(), entry.potion());
                    continue;
                }
                effects.add(new MobEffectInstance(effect, entry.duration(), entry.amplifier(), entry.ambient(), entry.showParticles()));
            }
            if (effects.isEmpty()) {
                ContentLog.LOGGER.error("Potion type {} has no effect whose potion exists, skipping it", def.key());
                continue;
            }
            helper.register(def.key(), new Potion(def.baseName(), effects.toArray(new MobEffectInstance[0])));
            count++;
        }
        if (count > 0) { Summary.info("content_potion_types", "Registered " + count + " potion type(s) from packs"); }
    }

    public static void applyBrewing(PotionBrewing.Builder builder) {
        load();
        int count = 0;
        for (BrewingDef def : BREWING) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            ItemStack ingredient = ContentStacks.parse(def.key(), def.ingredient(), 1);
            if (ingredient.isEmpty()) { continue; }
            if (def.isMix()) {
                Holder<Potion> from = type(def.key(), def.from());
                Holder<Potion> to = type(def.key(), def.to());
                if (from == null || to == null) { continue; }
                builder.addMix(from, ingredient.getItem(), to);
                count++;
                continue;
            }
            ItemStack input = ContentStacks.parse(def.key(), def.input(), 1);
            ItemStack output = ContentStacks.parse(def.key(), def.output(), 1);
            if (input.isEmpty() || output.isEmpty()) { continue; }
            builder.addRecipe(Ingredient.of(input), Ingredient.of(ingredient), output);
            count++;
        }
        containers(builder::addContainer);
        if (count > 0) { Summary.info("content_brewing", "Added " + count + " brewing recipe(s) from packs"); }
    }

    private static void containers(Consumer<Item> out) {
        int count = 0;
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (!(entry.item() instanceof ContentPotionItem)) { continue; }
            out.accept(entry.item());
            count++;
        }
        if (count > 0) { Summary.info("content_potion_containers", "Registered " + count + " potion container item(s) from packs"); }
    }

    @Nullable private static Holder<Potion> type(ResourceLocation key, String name) {
        ResourceLocation id = ResourceLocation.tryParse(name);
        Holder<Potion> potion = Registered.holder(BuiltInRegistries.POTION, id);
        if (potion == null) { ContentLog.LOGGER.error("Brewing recipe in {} names potion type '{}', which is not registered, skipping it", key, name); }
        return potion;
    }
}
