package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentGenerated;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeStackDef;
import mctmods.resourcedatapackloader.content.def.VillagerDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentVillagers {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, VillagerDef> VILLAGERS = new LinkedHashMap<>();
    private static final List<TradeDef> TRADES = new ArrayList<>();
    private static final Set<ResourceLocation> JOB_SITES = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentVillagers() {}

    public static boolean load() {
        if (loaded) { return wanted(); }
        loaded = true;
        if (Config.contentOff() || !Config.content.villagers()) { return false; }
        Json.eachFile(PackManager.VILLAGERS, "villager file", (key, contents) -> {
            if (!ContentRegistry.reserved(key)) { readVillager(key, contents); }
        });
        Json.eachFile(PackManager.TRADES, "trade file", (key, contents) -> {
            if (!ContentRegistry.reserved(key)) { readTrades(key, contents); }
        });
        if (!VILLAGERS.isEmpty()) { Summary.info("villagers", "Loaded " + VILLAGERS.size() + " villager profession(s) from packs"); }
        if (!TRADES.isEmpty()) { Summary.info("trades", "Loaded " + TRADES.size() + " villager trade(s) from packs"); }
        return wanted();
    }

    public static boolean wanted() { return !VILLAGERS.isEmpty() || !TRADES.isEmpty(); }

    public static Set<ResourceLocation> jobSites() { return Collections.unmodifiableSet(JOB_SITES); }

    private static void readVillager(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Villager file {} is empty, ignoring it", key);
            return;
        }
        if (json.has("careers")) { ContentLog.LOGGER.warn("Villager profession {} lists careers, which this line does not read. There are no careers now, so give each one its own villager file and name it in the trade's 'profession'", key); }
        if (json.has("texture") || json.has("zombieTexture")) { ContentLog.LOGGER.warn("Villager profession {} sets a texture, which this line does not read. Ship it as assets/{}/textures/entity/villager/profession/{}.png instead", key, key.getNamespace(), key.getPath()); }
        String jobSite = GsonHelper.getAsString(json, "jobSite", "").trim();
        if (jobSite.isEmpty()) {
            ContentLog.LOGGER.error("Villager profession {} names no jobSite block, ignoring it. A profession with no job site can never be taken by a villager", key);
            return;
        }
        VILLAGERS.put(key, new VillagerDef(key,
                GsonHelper.getAsString(json, "texture", ""),
                GsonHelper.getAsString(json, "zombieTexture", ""),
                Json.strings(json, "careers"),
                jobSite,
                GsonHelper.getAsString(json, "workSound", "").trim(),
                Json.strings(json, "requires")));
    }

    private static void readTrades(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Trade file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "trades")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A trade in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            int level = GsonHelper.getAsInt(entry, "level", 1);
            if (level < 1 || level > 5) {
                ContentLog.LOGGER.error("A trade in {} has level {}, but levels run from 1 to 5, skipping it", key, level);
                continue;
            }
            if (entry.has("career")) { ContentLog.LOGGER.warn("A trade in {} names a career, which this line does not read. Name the profession itself in 'profession'", key); }
            TradeStackDef sell = stack(entry, "sell");
            TradeStackDef buy = stack(entry, "buy");
            if (sell.isEmpty() || buy.isEmpty()) {
                ContentLog.LOGGER.error("A trade in {} needs both a buy and a sell item, skipping it", key);
                continue;
            }
            TRADES.add(new TradeDef(key,
                    GsonHelper.getAsString(entry, "profession", ""),
                    GsonHelper.getAsString(entry, "career", ""),
                    level, buy, stack(entry, "buySecondary"), sell,
                    Math.max(1, GsonHelper.getAsInt(entry, "maxUses", 12)),
                    Math.max(0, GsonHelper.getAsInt(entry, "xp", 2)),
                    Json.strings(entry, "requires")));
        }
    }

    private static TradeStackDef stack(JsonObject json, String name) {
        if (!json.has(name)) { return new TradeStackDef("", 1, 1); }
        JsonObject entry = GsonHelper.getAsJsonObject(json, name);
        int min = Math.max(1, GsonHelper.getAsInt(entry, "min", 1));
        return new TradeStackDef(GsonHelper.getAsString(entry, "item", ""), min, Math.max(min, GsonHelper.getAsInt(entry, "max", min)));
    }

    public static void registerJobSites(RegisterEvent.RegisterHelper<PoiType> helper) {
        load();
        int count = 0;
        for (VillagerDef def : VILLAGERS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            Block block = block(def);
            if (block == null) { continue; }
            Set<BlockState> states = new LinkedHashSet<>(block.getStateDefinition().getPossibleStates());
            helper.register(def.key(), new PoiType(states, 1, 1));
            JOB_SITES.add(def.key());
            count++;
        }
        ContentGenerated.jobSites(JOB_SITES);
        if (count > 0) { Summary.info("content_job_sites", "Registered " + count + " villager job site(s) from packs"); }
    }

    public static void registerProfessions(RegisterEvent.RegisterHelper<VillagerProfession> helper) {
        load();
        int count = 0;
        for (VillagerDef def : VILLAGERS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            if (block(def) == null) { continue; }
            if (ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(def.key())) {
                ContentLog.LOGGER.warn("A villager profession named {} is already registered, skipping the pack entry", def.key());
                continue;
            }
            ResourceKey<PoiType> site = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, def.key());
            helper.register(def.key(), new VillagerProfession(def.key().getPath(),
                    holder -> holder.is(site), holder -> holder.is(site),
                    ImmutableSet.of(), ImmutableSet.of(), workSound(def)));
            count++;
        }
        if (count > 0) { Summary.info("content_villagers", "Registered " + count + " villager profession(s) from packs"); }
    }

    public static void applyTrades(VillagerTradesEvent event) {
        load();
        if (TRADES.isEmpty()) { return; }
        ResourceLocation profession = ForgeRegistries.VILLAGER_PROFESSIONS.getKey(event.getType());
        if (profession == null) { return; }
        int count = 0;
        for (TradeDef def : TRADES) {
            if (!profession.toString().equals(def.profession()) || !ContentRegistry.available(def.requires(), def.key())) { continue; }
            ItemStack buy = ContentStacks.parse(def.key(), def.buy().item(), def.buy().min());
            ItemStack sell = ContentStacks.parse(def.key(), def.sell().item(), def.sell().min());
            if (buy.isEmpty() || sell.isEmpty()) { continue; }
            ItemStack buySecondary = def.buySecondary().isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(def.key(), def.buySecondary().item(), def.buySecondary().min());
            List<VillagerTrades.ItemListing> listings = event.getTrades().get(def.level());
            if (listings == null) {
                ContentLog.LOGGER.error("Trade in {} asks for level {} of profession '{}', which that profession does not offer, skipping it", def.key(), def.level(), def.profession());
                continue;
            }
            listings.add(new ContentTrade(def, buy, buySecondary, sell));
            count++;
        }
        if (count > 0) { Summary.info("content_trades." + profession, "Added " + count + " villager trade(s) from packs to " + profession); }
    }

    @Nullable private static Block block(VillagerDef def) {
        Block block = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(def.jobSite()));
        if (block == null) {
            ContentLog.LOGGER.error("Villager profession {} names job site block '{}', which is not registered, skipping the profession", def.key(), def.jobSite());
            return null;
        }
        return block;
    }

    @Nullable private static SoundEvent workSound(VillagerDef def) {
        if (def.workSound().isEmpty()) { return null; }
        ResourceLocation name = ResourceLocation.tryParse(def.workSound());
        SoundEvent sound = Registered.find(ForgeRegistries.SOUND_EVENTS, name);
        if (sound == null) { ContentLog.LOGGER.error("Villager profession {} names work sound '{}', which is not registered, leaving it silent", def.key(), def.workSound()); }
        return sound;
    }
}
