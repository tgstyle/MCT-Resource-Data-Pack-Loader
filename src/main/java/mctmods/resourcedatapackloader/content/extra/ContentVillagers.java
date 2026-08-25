package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentOwners;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeStackDef;
import mctmods.resourcedatapackloader.content.def.VillagerDef;
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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentVillagers {
    private static final Gson GSON = new GsonBuilder().create();
    private static final int MAX_CAREERS = 32;
    private static final Map<ResourceLocation, VillagerDef> VILLAGERS = new LinkedHashMap<>();
    private static final List<TradeDef> TRADES = new ArrayList<>();
    private static boolean loaded;

    private ContentVillagers() {}

    public static boolean load() {
        if (loaded) { return wanted(); }
        loaded = true;
        if (!Config.registersToClients() || !Config.content.villagers) { return false; }
        PackManager.get().forEach(PackManager.VILLAGERS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try { readVillager(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in villager file {}, ignoring it", key, ex); }
        });
        PackManager.get().forEach(PackManager.TRADES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try { readTrades(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in trade file {}, ignoring it", key, ex); }
        });
        if (!VILLAGERS.isEmpty()) { Summary.info("villagers", "Loaded " + VILLAGERS.size() + " villager profession(s) from packs"); }
        if (!TRADES.isEmpty()) { Summary.info("trades", "Loaded " + TRADES.size() + " villager trade(s) from packs"); }
        return wanted();
    }

    public static boolean wanted() { return !VILLAGERS.isEmpty() || !TRADES.isEmpty(); }

    private static void readVillager(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Villager file {} is empty, ignoring it", key);
            return;
        }
        List<String> careers = strings(json, "careers");
        if (careers.isEmpty()) {
            ContentLog.LOGGER.error("Villager profession {} lists no careers, ignoring it. A profession with no career cannot be assigned to a villager", key);
            return;
        }
        VILLAGERS.put(key, new VillagerDef(key,
                JsonUtils.getString(json, "texture", "minecraft:textures/entity/villager/villager.png"),
                JsonUtils.getString(json, "zombieTexture", "minecraft:textures/entity/zombie_villager/zombie_villager.png"),
                careers,
                strings(json, "requires")));
    }

    private static void readTrades(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Trade file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : JsonUtils.getJsonArray(json, "trades")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A trade in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            int level = JsonUtils.getInt(entry, "level", 1);
            if (level < 1) {
                ContentLog.LOGGER.error("A trade in {} has level {}, but levels start at 1, skipping it", key, level);
                continue;
            }
            TradeStackDef sell = stack(entry, "sell");
            TradeStackDef buy = stack(entry, "buy");
            if (sell.isEmpty() || buy.isEmpty()) {
                ContentLog.LOGGER.error("A trade in {} needs both a buy and a sell item, skipping it", key);
                continue;
            }
            TRADES.add(new TradeDef(key,
                    JsonUtils.getString(entry, "profession", ""),
                    JsonUtils.getString(entry, "career", ""),
                    level, buy, stack(entry, "buySecondary"), sell,
                    Math.max(1, JsonUtils.getInt(entry, "maxUses", 12)),
                    strings(entry, "requires")));
        }
    }

    private static TradeStackDef stack(JsonObject json, String name) {
        if (!json.has(name)) { return new TradeStackDef("", 1, 1); }
        JsonObject entry = JsonUtils.getJsonObject(json, name);
        int min = Math.max(1, JsonUtils.getInt(entry, "min", 1));
        return new TradeStackDef(JsonUtils.getString(entry, "item", ""), min, Math.max(min, JsonUtils.getInt(entry, "max", min)));
    }

    @SubscribeEvent public static void registerProfessions(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
        int count = 0;
        for (VillagerDef def : VILLAGERS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            if (ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(def.registryName)) {
                ContentLog.LOGGER.debug("Villager profession {} is already registered, leaving it alone", def.registryName);
                continue;
            }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(def.registryName.getNamespace()));
                VillagerRegistry.VillagerProfession profession = new VillagerRegistry.VillagerProfession(def.registryName.toString(), def.texture, def.zombieTexture);
                for (String career : def.careers) { new VillagerRegistry.VillagerCareer(profession, career); }
                event.getRegistry().register(profession);
            }
            finally { Loader.instance().setActiveModContainer(previous); }
            count++;
        }
        if (count > 0) { Summary.info("content_villagers", "Registered " + count + " villager profession(s) from packs"); }
    }

    public static void applyTrades() {
        int count = 0;
        for (TradeDef def : TRADES) {
            if (!ContentRegistry.available(def.requires, def.key)) { continue; }
            VillagerRegistry.VillagerCareer career = career(def);
            if (career == null) { continue; }
            ItemStack buy = ContentStacks.parse(def.key, def.buy.item, def.buy.min);
            ItemStack sell = ContentStacks.parse(def.key, def.sell.item, def.sell.min);
            if (buy.isEmpty() || sell.isEmpty()) { continue; }
            ItemStack buySecondary = def.buySecondary.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(def.key, def.buySecondary.item, def.buySecondary.min);
            career.addTrade(def.level, new ContentTrade(def, buy, buySecondary, sell));
            count++;
        }
        if (count > 0) { Summary.info("content_trades", "Added " + count + " villager trade(s) from packs"); }
    }

    @Nullable private static VillagerRegistry.VillagerCareer career(TradeDef def) {
        ResourceLocation location = new ResourceLocation(def.profession);
        if (!ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(location)) {
            ContentLog.LOGGER.error("Trade in {} names profession '{}', which is not registered, skipping it", def.key, def.profession);
            return null;
        }
        VillagerRegistry.VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(location);
        if (profession == null) { return null; }
        List<String> names = new ArrayList<>();
        VillagerRegistry.VillagerCareer first = profession.getCareer(0);
        for (int id = 0; id < MAX_CAREERS; id++) {
            VillagerRegistry.VillagerCareer career = profession.getCareer(id);
            if (id > 0 && career == first) { break; }
            names.add(career.getName());
            if (career.getName().equals(def.career)) { return career; }
        }
        ContentLog.LOGGER.error("Trade in {} names career '{}' of profession '{}', which has no such career. It offers {}", def.key, def.career, def.profession, names);
        return null;
    }
}
