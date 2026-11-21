package mctmods.resourcedatapackloader.common.trades;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.PriceScaleDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.TradeClearDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.TradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.TradeStackDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillagerDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.strings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public final class ContentVillagers {
    private static final List<String> VILLAGER_KEYS = Collections.unmodifiableList(Arrays.asList("careers", "texture", "zombieTexture", "requires"));
    private static final List<String> TRADE_FILE_KEYS = Collections.unmodifiableList(Arrays.asList("trades", "clear"));
    private static final List<String> TRADE_KEYS = Collections.unmodifiableList(Arrays.asList("level", "sell", "buy", "buySecondary", "profession", "career", "maxUses", "requires", "advancement", "scoreAtLeast", "dietAtLeast", "dietCount", "team", "objective", "priceScale"));
    private static final List<String> CLEAR_KEYS = Collections.unmodifiableList(Arrays.asList("profession", "career", "level", "requires"));
    private static final List<String> STACK_KEYS = Collections.unmodifiableList(Arrays.asList("item", "min", "max"));
    private static final List<String> SCALE_KEYS = Collections.unmodifiableList(Arrays.asList("objective", "perPoint", "min", "max"));
    private static final int MAX_CAREERS = 32;
    private static final Map<ResourceLocation, VillagerDef> VILLAGERS = new LinkedHashMap<>();
    private static final List<TradeDef> TRADES = new ArrayList<>();
    private static final List<TradeClearDef> CLEARS = new ArrayList<>();
    @Nullable private static final Field CAREER_TRADES = careerTrades();
    private static boolean loaded;

    private ContentVillagers() {}

    public static boolean load() {
        if (loaded) { return wanted(); }
        loaded = true;
        if (!Config.content.villagers) { return false; }
        if (Config.registersToClients()) {
            Json.eachFile(PackManager.VILLAGERS, "villager file", (key, contents) -> {
                if (!ContentOwners.reserved(key)) { readVillager(key, contents); }
            });
        }
        Json.eachFile(PackManager.TRADES, "trade file", (key, contents) -> {
            if (!ContentOwners.reserved(key)) { readTrades(key, contents); }
        });
        if (!VILLAGERS.isEmpty()) { Summary.info("villagers", "Loaded " + VILLAGERS.size() + " villager profession(s) from packs"); }
        if (!TRADES.isEmpty()) { Summary.info("trades", "Loaded " + TRADES.size() + " villager trade(s) from packs"); }
        if (!CLEARS.isEmpty()) { Summary.info("trade_clears", "Loaded " + CLEARS.size() + " villager trade clear(s) from packs"); }
        return wanted();
    }

    public static boolean wanted() { return !VILLAGERS.isEmpty() || !TRADES.isEmpty() || !CLEARS.isEmpty(); }

    private static void readVillager(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "villager file");
        if (json == null) { return; }
        Json.unknownKeys(json, "the top level", VILLAGER_KEYS);
        List<String> careers = strings(json, "careers");
        if (careers.isEmpty()) {
            ContentLog.LOGGER.error("Villager profession {} lists no careers, ignoring it. A profession with no career cannot be assigned to a villager", key);
            return;
        }
        VILLAGERS.put(key, new VillagerDef(key,
                Json.getString(json, "texture", "minecraft:textures/entity/villager/villager.png"),
                Json.getString(json, "zombieTexture", "minecraft:textures/entity/zombie_villager/zombie_villager.png"),
                careers,
                strings(json, "requires")));
    }

    private static void readTrades(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "trade file");
        if (json == null) { return; }
        Json.unknownKeys(json, "the top level", TRADE_FILE_KEYS);
        if (json.has("trades")) {
            for (JsonElement element : Json.getJsonArray(json, "trades")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A trade in {} is not an object, skipping it", key);
                    continue;
                }
                Json.unknownKeys(element.getAsJsonObject(), "trades", TRADE_KEYS);
                TradeDef def = trade(key, element.getAsJsonObject());
                if (def != null) { TRADES.add(def); }
            }
        }
        if (json.has("clear")) { readClears(key, json); }
    }

    private static void readClears(ResourceLocation key, JsonObject json) {
        for (JsonElement element : Json.getJsonArray(json, "clear")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A clear in {} is not an object, skipping it", key);
                continue;
            }
            TradeClearDef def = clear(key, element.getAsJsonObject());
            if (def != null) { CLEARS.add(def); }
        }
    }

    @Nullable private static TradeClearDef clear(ResourceLocation key, JsonObject entry) {
        Json.unknownKeys(entry, "clear", CLEAR_KEYS);
        String profession = Json.getString(entry, "profession", "").trim();
        if (profession.isEmpty()) {
            ContentLog.LOGGER.error("A clear in {} names no profession, skipping it", key);
            return null;
        }
        int level = entry.has("level") ? Json.getInt(entry, "level", TradeClearDef.ALL_LEVELS) : TradeClearDef.ALL_LEVELS;
        if (entry.has("level") && level < 1) {
            ContentLog.LOGGER.error("A clear in {} has level {}, but levels start at 1, skipping it", key, level);
            return null;
        }
        return new TradeClearDef(key, profession, Json.getString(entry, "career", "").trim(), level, strings(entry, "requires"));
    }

    @Nullable public static TradeDef trade(ResourceLocation key, JsonObject entry) {
        int level = Json.getInt(entry, "level", 1);
        if (level < 1) {
            ContentLog.LOGGER.error("A trade in {} has level {}, but levels start at 1, skipping it", key, level);
            return null;
        }
        TradeStackDef sell = stack(entry, "sell");
        TradeStackDef buy = stack(entry, "buy");
        if (sell.isEmpty() || buy.isEmpty()) {
            ContentLog.LOGGER.error("A trade in {} needs both a buy and a sell item, skipping it", key);
            return null;
        }
        return new TradeDef(key,
                Json.getString(entry, "profession", ""),
                Json.getString(entry, "career", ""),
                level, buy, stack(entry, "buySecondary"), sell,
                Math.max(1, Json.getInt(entry, "maxUses", 12)),
                strings(entry, "requires"),
                Json.getString(entry, "advancement", "").trim(),
                Standing.parse(entry),
                scale(key, entry));
    }

    @Nullable private static PriceScaleDef scale(ResourceLocation key, JsonObject entry) {
        if (!entry.has("priceScale")) { return null; }
        JsonObject json = Json.getJsonObject(entry, "priceScale");
        Json.unknownKeys(json, "priceScale", SCALE_KEYS);
        String objective = Json.getString(json, "objective", "").trim();
        if (objective.isEmpty() || !json.has("perPoint")) {
            ContentLog.LOGGER.error("A priceScale in {} needs an objective and a perPoint, ignoring it", key);
            return null;
        }
        return new PriceScaleDef(objective, Json.getFloat(json, "perPoint"), Json.getFloat(json, "min", 0.0F), Json.getFloat(json, "max", 10.0F));
    }

    private static TradeStackDef stack(JsonObject json, String name) {
        if (!json.has(name)) { return new TradeStackDef("", 1, 1); }
        JsonObject entry = Json.getJsonObject(json, name);
        Json.unknownKeys(entry, name, STACK_KEYS);
        int min = Math.max(1, Json.getInt(entry, "min", 1));
        return new TradeStackDef(Json.getString(entry, "item", ""), min, Math.max(min, Json.getInt(entry, "max", min)));
    }

    @SubscribeEvent public static void registerProfessions(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
        int count = 0;
        for (VillagerDef def : VILLAGERS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            if (ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(def.registryName)) {
                ContentLog.LOGGER.debug("Villager profession {} is already registered, leaving it alone", def.registryName);
                continue;
            }
            ContentOwners.as(def.registryName.getNamespace(), () -> {
                VillagerRegistry.VillagerProfession profession = new VillagerRegistry.VillagerProfession(def.registryName.toString(), def.texture, def.zombieTexture);
                for (String career : def.careers) { new VillagerRegistry.VillagerCareer(profession, career); }
                event.getRegistry().register(profession);
            });
            count++;
        }
        if (count > 0) { Summary.info("content_villagers", "Registered " + count + " villager profession(s) from packs"); }
    }

    public static void applyTrades() {
        if (!load()) { return; }
        clearTrades();
        int count = 0;
        for (TradeDef def : TRADES) {
            ContentTrade listing = listing(def);
            if (listing == null) { continue; }
            VillagerRegistry.VillagerCareer career = career(def);
            if (career == null) { continue; }
            if (listing.clashes()) { continue; }
            career.addTrade(def.level, listing);
            count++;
        }
        ContentTradeGate.summarize();
        if (count > 0) { Summary.info("content_trades", "Added " + count + " villager trade(s) from packs"); }
    }

    @Nullable public static ContentTrade listing(TradeDef def) {
        if (!ContentRegistry.available(def.requires, def.key)) { return null; }
        ItemStack buy = ContentStacks.parse(def.key, def.buy.item, def.buy.min);
        ItemStack sell = ContentStacks.parse(def.key, def.sell.item, def.sell.min);
        if (buy.isEmpty() || sell.isEmpty()) { return null; }
        ItemStack buySecondary = def.buySecondary.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(def.key, def.buySecondary.item, def.buySecondary.min);
        return new ContentTrade(def, buy, buySecondary, sell);
    }

    public static boolean known(TradeDef def) { return career(def) != null; }

    @Nullable private static VillagerRegistry.VillagerProfession profession(ResourceLocation key, String what, String name) {
        ResourceLocation location = new ResourceLocation(name);
        if (!ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(location)) {
            ContentLog.LOGGER.error("{} in {} names profession '{}', which is not registered, skipping it", what, key, name);
            return null;
        }
        return ForgeRegistries.VILLAGER_PROFESSIONS.getValue(location);
    }

    private static List<VillagerRegistry.VillagerCareer> careers(VillagerRegistry.VillagerProfession profession) {
        List<VillagerRegistry.VillagerCareer> careers = new ArrayList<>();
        VillagerRegistry.VillagerCareer first = profession.getCareer(0);
        for (int id = 0; id < MAX_CAREERS; id++) {
            VillagerRegistry.VillagerCareer career = profession.getCareer(id);
            if (id > 0 && career == first) { break; }
            careers.add(career);
        }
        return careers;
    }

    @Nullable private static VillagerRegistry.VillagerCareer career(TradeDef def) {
        VillagerRegistry.VillagerProfession profession = profession(def.key, "Trade", def.profession);
        if (profession == null) { return null; }
        List<VillagerRegistry.VillagerCareer> careers = careers(profession);
        List<String> names = new ArrayList<>();
        for (VillagerRegistry.VillagerCareer career : careers) {
            names.add(career.getName());
            if (career.getName().equals(def.career)) { return career; }
        }
        ContentLog.LOGGER.error("Trade in {} names career '{}' of profession '{}', which has no such career. It offers {}", def.key, def.career, def.profession, names);
        return null;
    }

    private static void clearTrades() {
        Field trades = CAREER_TRADES;
        if (trades == null) { return; }
        int count = 0;
        for (TradeClearDef def : CLEARS) {
            if (!ContentRegistry.available(def.requires, def.key)) { continue; }
            VillagerRegistry.VillagerProfession profession = profession(def.key, "Clear", def.profession);
            if (profession == null) { continue; }
            boolean found = false;
            for (VillagerRegistry.VillagerCareer career : careers(profession)) {
                if (!def.career.isEmpty() && !career.getName().equals(def.career)) { continue; }
                found = true;
                count += clearLevels(trades, career, def.level);
            }
            if (!found) { ContentLog.LOGGER.error("Clear in {} names career '{}' of profession '{}', which has no such career", def.key, def.career, def.profession); }
        }
        if (count > 0) { Summary.info("content_trade_clears", "Cleared " + count + " villager trade list(s) from packs"); }
    }

    @SuppressWarnings("unchecked") private static int clearLevels(Field trades, VillagerRegistry.VillagerCareer career, int level) {
        try {
            List<List<EntityVillager.ITradeList>> levels = (List<List<EntityVillager.ITradeList>>) trades.get(career);
            int count = 0;
            for (int index = 0; index < levels.size(); index++) {
                if (level != TradeClearDef.ALL_LEVELS && index != level - 1) { continue; }
                count += levels.get(index).size();
                levels.get(index).clear();
            }
            return count;
        } catch (IllegalAccessException e) {
            ContentLog.LOGGER.error("Could not clear the trades of career {}", career.getName(), e);
            return 0;
        }
    }

    @Nullable private static Field careerTrades() {
        try {
            Field field = VillagerRegistry.VillagerCareer.class.getDeclaredField("trades");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException | RuntimeException e) {
            ContentLog.LOGGER.error("Could not reach the villager career trade lists, trade clearing is off", e);
            return null;
        }
    }
}
