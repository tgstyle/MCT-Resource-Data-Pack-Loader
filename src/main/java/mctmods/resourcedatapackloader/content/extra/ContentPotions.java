package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentOwners;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.def.BrewingDef;
import mctmods.resourcedatapackloader.content.def.PotionDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.def.PotionTypeDef;
import mctmods.resourcedatapackloader.content.item.ContentItemPotion;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import static mctmods.resourcedatapackloader.util.Json.strings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentPotions {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, PotionDef> POTIONS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, PotionTypeDef> TYPES = new LinkedHashMap<>();
    private static final List<BrewingDef> BREWING = new ArrayList<>();
    private static boolean loaded;

    private ContentPotions() {}

    public static boolean load() {
        if (loaded) { return wanted(); }
        loaded = true;
        if (!Config.registersToClients() || !Config.content.potions) { return false; }
        PackManager.get().forEach(PackManager.POTIONS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try { readPotion(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in potion file {}, ignoring it", key, ex); }
        });
        PackManager.get().forEach(PackManager.POTION_TYPES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try { readType(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in potion type file {}, ignoring it", key, ex); }
        });
        if (Config.content.brewing) {
            PackManager.get().forEach(PackManager.BREWING, PackManager.JSON, (namespace, path, contents) -> {
                ResourceLocation key = new ResourceLocation(namespace, path);
                if (ContentOwners.reserved(key)) { return; }
                try { readBrewing(key, contents); }
                catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in brewing file {}, ignoring it", key, ex); }
            });
        }
        if (!POTIONS.isEmpty()) { Summary.info("potions", "Loaded " + POTIONS.size() + " potion effect(s) from packs"); }
        if (!TYPES.isEmpty()) { Summary.info("potion_types", "Loaded " + TYPES.size() + " potion type(s) from packs"); }
        if (!BREWING.isEmpty()) { Summary.info("brewing", "Loaded " + BREWING.size() + " brewing recipe(s) from packs"); }
        return wanted();
    }

    public static boolean wanted() { return !POTIONS.isEmpty() || !TYPES.isEmpty() || !BREWING.isEmpty(); }

    private static void readPotion(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Potion file {} is empty, ignoring it", key);
            return;
        }
        List<AttributeDef> attributes = new ArrayList<>();
        if (json.has("attributes")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "attributes")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An attribute in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                String uuid = JsonUtils.getString(entry, "uuid", "");
                if (uuid.isEmpty()) {
                    ContentLog.LOGGER.error("An attribute in {} has no uuid, skipping it. Generate one once and keep it, because it identifies the modifier", key);
                    continue;
                }
                attributes.add(new AttributeDef(JsonUtils.getString(entry, "attribute", ""), uuid,
                        JsonUtils.getFloat(entry, "amount", 0.0F),
                        JsonUtils.getInt(entry, "operation", 0)));
            }
        }
        JsonObject icon = JsonUtils.getJsonObject(json, "icon", new JsonObject());
        POTIONS.put(key, new PotionDef(key,
                JsonUtils.getString(json, "name", "effect." + key.getNamespace() + "." + key.getPath()),
                JsonUtils.getBoolean(json, "badEffect", false),
                JsonUtils.getBoolean(json, "beneficial", false),
                ContentTypes.color(JsonUtils.getString(json, "color", "FFFFFF"), key.toString()),
                JsonUtils.getInt(icon, "x", 0),
                JsonUtils.getInt(icon, "y", 0),
                JsonUtils.getString(json, "iconTexture", ""),
                JsonUtils.getBoolean(json, "instant", false),
                JsonUtils.getFloat(json, "effectiveness", 0.5F),
                Collections.unmodifiableList(attributes),
                strings(json, "requires")));
    }

    private static void readType(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Potion type file {} is empty, ignoring it", key);
            return;
        }
        List<PotionEffectDef> effects = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(json, "effects")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String potion = JsonUtils.getString(entry, "potion", "");
            if (potion.isEmpty()) {
                ContentLog.LOGGER.error("An effect in {} names no potion, skipping it", key);
                continue;
            }
            effects.add(new PotionEffectDef(potion,
                    Math.max(1, JsonUtils.getInt(entry, "duration", 3600)),
                    Math.max(0, JsonUtils.getInt(entry, "amplifier", 0)),
                    JsonUtils.getBoolean(entry, "ambient", false),
                    JsonUtils.getBoolean(entry, "showParticles", true)));
        }
        if (effects.isEmpty()) {
            ContentLog.LOGGER.error("Potion type {} has no usable effects, ignoring it", key);
            return;
        }
        TYPES.put(key, new PotionTypeDef(key,
                JsonUtils.getString(json, "baseName", key.getNamespace() + "." + key.getPath()),
                Collections.unmodifiableList(effects),
                strings(json, "requires")));
    }

    private static void readBrewing(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Brewing file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : JsonUtils.getJsonArray(json, "brewing")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A brewing recipe in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            BrewingDef def = new BrewingDef(key,
                    JsonUtils.getString(entry, "from", ""),
                    JsonUtils.getString(entry, "to", ""),
                    JsonUtils.getString(entry, "ingredient", ""),
                    JsonUtils.getString(entry, "input", ""),
                    JsonUtils.getString(entry, "output", ""),
                    strings(entry, "requires"));
            if (def.ingredient.isEmpty()) {
                ContentLog.LOGGER.error("A brewing recipe in {} has no ingredient, skipping it", key);
                continue;
            }
            if (!def.isMix() && (def.input.isEmpty() || def.output.isEmpty())) {
                ContentLog.LOGGER.error("A brewing recipe in {} needs either from and to, or input and output, skipping it", key);
                continue;
            }
            BREWING.add(def);
        }
    }

    @SubscribeEvent public static void registerPotions(RegistryEvent.Register<Potion> event) {
        int count = 0;
        for (PotionDef def : POTIONS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            if (ForgeRegistries.POTIONS.containsKey(def.registryName)) {
                ContentLog.LOGGER.debug("Potion {} is already registered, leaving it alone", def.registryName);
                continue;
            }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(def.registryName.getNamespace()));
                event.getRegistry().register(new ContentPotion(def));
            }
            finally { Loader.instance().setActiveModContainer(previous); }
            count++;
        }
        if (count > 0) { Summary.info("content_potions", "Registered " + count + " potion effect(s) from packs"); }
    }

    @SubscribeEvent public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        int count = 0;
        for (PotionTypeDef def : TYPES.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            if (ForgeRegistries.POTION_TYPES.containsKey(def.registryName)) {
                ContentLog.LOGGER.debug("Potion type {} is already registered, leaving it alone", def.registryName);
                continue;
            }
            List<PotionEffect> effects = new ArrayList<>();
            for (PotionEffectDef entry : def.effects) {
                Potion potion = Potion.getPotionFromResourceLocation(entry.potion);
                if (potion == null) {
                    ContentLog.LOGGER.error("Potion type {} names potion '{}', which is not registered, skipping that effect", def.registryName, entry.potion);
                    continue;
                }
                effects.add(new PotionEffect(potion, entry.duration, entry.amplifier, entry.ambient, entry.showParticles));
            }
            if (effects.isEmpty()) {
                ContentLog.LOGGER.error("Potion type {} has no effect whose potion exists, skipping it", def.registryName);
                continue;
            }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(def.registryName.getNamespace()));
                PotionType type = new PotionType(def.baseName, effects.toArray(new PotionEffect[0]));
                type.setRegistryName(def.registryName);
                event.getRegistry().register(type);
            }
            finally { Loader.instance().setActiveModContainer(previous); }
            count++;
        }
        if (count > 0) { Summary.info("content_potion_types", "Registered " + count + " potion type(s) from packs"); }
    }

    public static void registerContainers() {
        int count = 0;
        for (Map.Entry<ResourceLocation, net.minecraft.item.Item> entry : ContentRegistry.registeredItems()) {
            if (!(entry.getValue() instanceof ContentItemPotion)) { continue; }
            PotionHelper.addContainer((ContentItemPotion) entry.getValue());
            count++;
        }
        if (count > 0) { Summary.info("content_potion_containers", "Registered " + count + " potion container item(s) from packs"); }
    }

    public static void applyBrewing() {
        int count = 0;
        for (BrewingDef def : BREWING) {
            if (!ContentRegistry.available(def.requires, def.key)) { continue; }
            ItemStack ingredient = ContentStacks.parse(def.key, def.ingredient, 1);
            if (ingredient.isEmpty()) { continue; }
            if (def.isMix()) {
                PotionType from = type(def.key, def.from);
                PotionType to = type(def.key, def.to);
                if (from == null || to == null) { continue; }
                PotionHelper.addMix(from, ingredient.getItem(), to);
                count++;
                continue;
            }
            ItemStack input = ContentStacks.parse(def.key, def.input, 1);
            ItemStack output = ContentStacks.parse(def.key, def.output, 1);
            if (input.isEmpty() || output.isEmpty()) { continue; }
            BrewingRecipeRegistry.addRecipe(input, ingredient, output);
            count++;
        }
        if (count > 0) { Summary.info("content_brewing", "Added " + count + " brewing recipe(s) from packs"); }
    }

    @Nullable private static PotionType type(ResourceLocation key, String name) {
        ResourceLocation location = new ResourceLocation(name);
        if (!ForgeRegistries.POTION_TYPES.containsKey(location)) {
            ContentLog.LOGGER.error("Brewing recipe in {} names potion type '{}', which is not registered, skipping it", key, name);
            return null;
        }
        return ForgeRegistries.POTION_TYPES.getValue(location);
    }
}
