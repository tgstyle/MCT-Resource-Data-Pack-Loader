package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.compat.Compat;
import mctmods.resourcedatapackloader.content.ContentGenerated;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeStackDef;
import mctmods.resourcedatapackloader.content.def.VillagerDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentVillagers {
    private static final byte[] PLAIN_SKIN = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAJ0lEQVR42u3BAQ0AAADCoPdPbQ43oAAAAAAAAAAAAAAAAAAAAIB3A0BAAAGveg7oAAAAAElFTkSuQmCC");
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<Identifier, VillagerDef> VILLAGERS = new LinkedHashMap<>();
    private static final List<TradeDef> TRADES = new ArrayList<>();
    private static final Set<Identifier> JOB_SITES = new LinkedHashSet<>();
    private static final Set<VillagerProfession> JOBLESS = new HashSet<>();
    private static final Set<Identifier> OWN = new LinkedHashSet<>();
    private static final String TRADE_SETS = "trade_set";
    private static final String VILLAGER_TRADES = "villager_trade";
    private static final String TRADE_TAGS = "tags/villager_trade";
    private static final float OFFERS_PER_LEVEL = 2.0F;
    private static final int HIGHEST_LEVEL = 5;
    private static final int KEEPS_PROFESSION_XP = 1;
    private static final Map<String, String> CAREERS = Map.ofEntries(
            Map.entry("minecraft:farmer/farmer", "minecraft:farmer"), Map.entry("minecraft:farmer/fisherman", "minecraft:fisherman"),
            Map.entry("minecraft:farmer/shepherd", "minecraft:shepherd"), Map.entry("minecraft:farmer/fletcher", "minecraft:fletcher"),
            Map.entry("minecraft:librarian/librarian", "minecraft:librarian"), Map.entry("minecraft:librarian/cartographer", "minecraft:cartographer"),
            Map.entry("minecraft:priest/cleric", "minecraft:cleric"), Map.entry("minecraft:smith/armor", "minecraft:armorer"),
            Map.entry("minecraft:smith/weapon", "minecraft:weaponsmith"), Map.entry("minecraft:smith/tool", "minecraft:toolsmith"),
            Map.entry("minecraft:butcher/butcher", "minecraft:butcher"), Map.entry("minecraft:butcher/leather", "minecraft:leatherworker"),
            Map.entry("minecraft:nitwit/nitwit", "minecraft:nitwit"));
    private static final Set<String> CAREERED = Set.of("minecraft:farmer", "minecraft:librarian", "minecraft:priest", "minecraft:smith", "minecraft:butcher", "minecraft:nitwit");
    private static final Set<String> RETIRED = Set.of("minecraft:priest", "minecraft:smith");
    private static boolean loaded;
    private static boolean professionsChecked;

    private ContentVillagers() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.villagers()) { return; }
        if (!Config.contentOff()) {
            Json.eachFile(PackManager.VILLAGERS, "villager file", (key, contents) -> {
                if (!ContentRegistry.reserved(key)) { readVillager(key, contents); }
            });
        }
        Json.eachFile(PackManager.TRADES, "trade file", (key, contents) -> {
            if (!ContentRegistry.reserved(key)) { readTrades(key, contents); }
        });
        if (!VILLAGERS.isEmpty()) { Summary.info("villagers", "Loaded " + VILLAGERS.size() + " villager profession(s) from packs"); }
        if (!TRADES.isEmpty()) { Summary.info("trades", "Loaded " + TRADES.size() + " villager trade(s) from packs"); }
    }

    private static void readVillager(Identifier key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Villager file {} is empty, ignoring it", key);
            return;
        }
        if (json.has("careers")) { ContentLog.LOGGER.warn("Villager profession {} lists careers, which this line does not read. There are no careers now, so give each one its own villager file and name it in the trade's 'profession'", key); }
        skin(key, "villager", GsonHelper.getAsString(json, "texture", "").trim());
        skin(key, "zombie_villager", GsonHelper.getAsString(json, "zombieTexture", "").trim());
        String jobSite = GsonHelper.getAsString(json, "jobSite", "").trim();
        VILLAGERS.put(key, new VillagerDef(key,
                GsonHelper.getAsString(json, "texture", ""),
                GsonHelper.getAsString(json, "zombieTexture", ""),
                Json.strings(json, "careers"),
                jobSite,
                GsonHelper.getAsString(json, "workSound", "").trim(),
                Json.strings(json, "requires")));
    }

    private static void skin(Identifier key, String entity, String named) {
        String skin = "textures/entity/" + entity + "/profession/" + key.getPath() + ".png";
        if (!PackManager.get().holders(PackType.CLIENT_RESOURCES, key.getNamespace(), skin).isEmpty()) { return; }
        Identifier texture = named.isEmpty() ? null : Identifier.tryParse(named);
        byte[] worn = texture == null ? null : PackManager.get().bytes(PackType.CLIENT_RESOURCES, texture.getNamespace(), texture.getPath());
        if (texture != null && worn == null && !Identifier.DEFAULT_NAMESPACE.equals(texture.getNamespace())) { ContentLog.LOGGER.error("Villager profession {} names the {} texture {}, which no pack provides, so it wears none", key, entity, named); }
        GeneratedResources.put(PackType.CLIENT_RESOURCES, key.getNamespace(), skin, worn == null ? PLAIN_SKIN : worn);
        if (named.isEmpty() && "villager".equals(entity)) { ContentLog.LOGGER.info("Villager profession {} ships no texture, so villagers taking the job wear none. Ship assets/{}/{} to dress them", key, key.getNamespace(), skin); }
    }

    private static void readTrades(Identifier key, String contents) {
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
            if (level < 1) {
                ContentLog.LOGGER.error("A trade in {} has level {}, but levels start at 1, skipping it", key, level);
                continue;
            }
            if (level > HIGHEST_LEVEL) {
                ContentLog.LOGGER.warn("A trade in {} has level {}, past the highest a villager reaches, so it joins level {}", key, level, HIGHEST_LEVEL);
                level = HIGHEST_LEVEL;
            }
            String profession = GsonHelper.getAsString(entry, "profession", "").trim();
            String career = GsonHelper.getAsString(entry, "career", "").trim();
            if (!career.isEmpty() && CAREERED.contains(profession)) {
                String moved = CAREERS.get(profession + "/" + career);
                if (moved == null) {
                    ContentLog.LOGGER.error("A trade in {} names career '{}' of profession '{}', which has no such career, skipping it", key, career, profession);
                    continue;
                }
                profession = moved;
            }
            else if (RETIRED.contains(profession)) {
                ContentLog.LOGGER.error("A trade in {} names profession '{}' without a career, and that profession only exists through its careers now, skipping it", key, profession);
                continue;
            }
            else if (!career.isEmpty()) { ContentLog.LOGGER.warn("A trade in {} names a career, which this line does not read. Name the profession itself in 'profession'", key); }
            TradeStackDef sell = stack(entry, "sell");
            TradeStackDef buy = stack(entry, "buy");
            if (sell.isEmpty() || buy.isEmpty()) {
                ContentLog.LOGGER.error("A trade in {} needs both a buy and a sell item, skipping it", key);
                continue;
            }
            TRADES.add(new TradeDef(key, profession, career,
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
            if (!ContentRegistry.available(def.requires(), def.key()) || def.jobSite().isEmpty()) { continue; }
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
            boolean jobless = def.jobSite().isEmpty();
            if (!jobless && block(def) == null) { continue; }
            if (BuiltInRegistries.VILLAGER_PROFESSION.containsKey(def.key())) {
                ContentLog.LOGGER.debug("Villager profession {} is already registered, leaving it alone", def.key());
                continue;
            }
            ResourceKey<PoiType> site = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, def.key());
            Predicate<Holder<PoiType>> works = jobless ? _ -> false : holder -> holder.is(site);
            Int2ObjectMap<ResourceKey<TradeSet>> sets = new Int2ObjectOpenHashMap<>();
            for (int level = 1; level <= HIGHEST_LEVEL; level++) { sets.put(level, ResourceKey.create(Registries.TRADE_SET, tradeSet(def.key(), level))); }
            String owner = Identifier.DEFAULT_NAMESPACE.equals(def.key().getNamespace()) ? "" : def.key().getNamespace() + ".";
            Component name = Component.translatable(Compat.villager().getDescriptionId() + "." + owner + def.key().getPath());
            VillagerProfession profession = new VillagerProfession(name, works, works, ImmutableSet.of(), ImmutableSet.of(), workSound(def), sets);
            helper.register(def.key(), profession);
            OWN.add(def.key());
            if (jobless) { JOBLESS.add(profession); }
            count++;
        }
        if (count > 0) { Summary.info("content_villagers", "Registered " + count + " villager profession(s) from packs"); }
    }

    private static Identifier tradeSet(Identifier profession, int level) { return profession.withSuffix("/level_" + level); }

    private static List<Holder<VillagerProfession>> professions() {
        List<Holder<VillagerProfession>> professions = new ArrayList<>();
        for (VillagerProfession profession : BuiltInRegistries.VILLAGER_PROFESSION) {
            Holder<VillagerProfession> holder = BuiltInRegistries.VILLAGER_PROFESSION.wrapAsHolder(profession);
            if (!holder.is(VillagerProfession.NONE)) { professions.add(holder); }
        }
        return professions;
    }

    public static void spawned(Villager villager, EntitySpawnReason type) {
        if (JOBLESS.isEmpty()) { return; }
        VillagerData data = villager.getVillagerData();
        if (data.profession().is(VillagerProfession.NONE) && type != EntitySpawnReason.STRUCTURE && type != EntitySpawnReason.CONVERSION) {
            List<Holder<VillagerProfession>> professions = professions();
            Holder<VillagerProfession> picked = professions.get(villager.getRandom().nextInt(professions.size()));
            if (JOBLESS.contains(picked.value())) { villager.setVillagerData(data.withProfession(picked)); }
        }
        keepsJob(villager);
    }

    public static void professed(Entity made, RandomSource random) {
        List<Holder<VillagerProfession>> professions = professions();
        if (professions.isEmpty()) { return; }
        Holder<VillagerProfession> picked = professions.get(random.nextInt(professions.size()));
        if (made instanceof Villager villager) {
            villager.setVillagerData(villager.getVillagerData().withProfession(picked));
            if (villager.getVillagerXp() == 0) { villager.setVillagerXp(KEEPS_PROFESSION_XP); }
        }
        else if (made instanceof ZombieVillager zombie) { zombie.setVillagerData(zombie.getVillagerData().withProfession(picked)); }
    }

    public static void keepsJob(Villager villager) {
        if (JOBLESS.contains(villager.getVillagerData().profession().value()) && villager.getVillagerXp() == 0) { villager.setVillagerXp(KEEPS_PROFESSION_XP); }
    }

    public static void generateTrades() {
        load();
        Map<Identifier, List<String>> tags = new LinkedHashMap<>();
        for (Identifier own : OWN) {
            for (int level = 1; level <= HIGHEST_LEVEL; level++) {
                Identifier set = tradeSet(own, level);
                JsonObject json = new JsonObject();
                json.addProperty("amount", OFFERS_PER_LEVEL);
                json.addProperty("trades", "#" + set);
                GeneratedResources.put(PackType.SERVER_DATA, set.getNamespace(), TRADE_SETS + "/" + set.getPath() + ".json", json.toString());
                tags.put(set, new ArrayList<>());
            }
        }
        if (TRADES.isEmpty() && tags.isEmpty()) { return; }
        checkProfessions();
        Map<Identifier, Integer> made = new LinkedHashMap<>();
        Map<Identifier, Integer> numbered = new LinkedHashMap<>();
        for (TradeDef def : TRADES) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            Identifier named = Identifier.tryParse(def.profession());
            VillagerProfession profession = named == null || !named.toString().equals(def.profession()) ? null : Registered.find(BuiltInRegistries.VILLAGER_PROFESSION, named);
            if (profession == null) { continue; }
            Item buy = ContentStacks.find(def.key(), def.buy().item());
            Item sell = ContentStacks.find(def.key(), def.sell().item());
            if (buy == null || sell == null) { continue; }
            Item buySecondary = def.buySecondary().isEmpty() ? null : ContentStacks.find(def.key(), def.buySecondary().item());
            ResourceKey<TradeSet> set = profession.getTrades(def.level());
            if (set == null) {
                ContentLog.LOGGER.error("Trade in {} asks for level {} of profession '{}', which that profession does not offer, skipping it", def.key(), def.level(), def.profession());
                continue;
            }
            int number = numbered.merge(def.key(), 1, Integer::sum);
            Identifier trade = def.key().withSuffix("/" + number);
            GeneratedResources.put(PackType.SERVER_DATA, trade.getNamespace(), VILLAGER_TRADES + "/" + trade.getPath() + ".json", ContentTrade.json(def, buy, buySecondary, sell).toString());
            tags.computeIfAbsent(set.identifier(), _ -> new ArrayList<>()).add(trade.toString());
            made.merge(named, 1, Integer::sum);
        }
        for (Map.Entry<Identifier, List<String>> tag : tags.entrySet()) {
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();
            for (String value : tag.getValue()) { values.add(value); }
            json.add("values", values);
            GeneratedResources.put(PackType.SERVER_DATA, tag.getKey().getNamespace(), TRADE_TAGS + "/" + tag.getKey().getPath() + ".json", json.toString());
        }
        for (Map.Entry<Identifier, Integer> added : made.entrySet()) { Summary.info("content_trades." + added.getKey(), "Added " + added.getValue() + " villager trade(s) from packs to " + added.getKey()); }
    }

    private static void checkProfessions() {
        if (professionsChecked) { return; }
        professionsChecked = true;
        for (TradeDef def : TRADES) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            Identifier named = Identifier.tryParse(def.profession());
            if (named == null || !named.toString().equals(def.profession()) || !BuiltInRegistries.VILLAGER_PROFESSION.containsKey(named)) { ContentLog.LOGGER.error("Trade in {} names profession '{}', which is not registered, skipping it", def.key(), def.profession()); }
        }
    }

    @Nullable private static Block block(VillagerDef def) {
        Block block = Registered.find(BuiltInRegistries.BLOCK, Identifier.tryParse(def.jobSite()));
        if (block == null) {
            ContentLog.LOGGER.error("Villager profession {} names job site block '{}', which is not registered, skipping the profession", def.key(), def.jobSite());
            return null;
        }
        return block;
    }

    @Nullable private static SoundEvent workSound(VillagerDef def) {
        if (def.workSound().isEmpty()) { return null; }
        Identifier name = Identifier.tryParse(def.workSound());
        SoundEvent sound = Registered.find(BuiltInRegistries.SOUND_EVENT, name);
        if (sound == null) { ContentLog.LOGGER.error("Villager profession {} names work sound '{}', which is not registered, leaving it silent", def.key(), def.workSound()); }
        return sound;
    }
}
