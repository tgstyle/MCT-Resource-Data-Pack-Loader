package mctmods.resourcedatapackloader.content.extra;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
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
    private static final Map<ResourceLocation, VillagerDef> VILLAGERS = new LinkedHashMap<>();
    private static final List<TradeDef> TRADES = new ArrayList<>();
    private static final Set<ResourceLocation> JOB_SITES = new LinkedHashSet<>();
    private static final Set<VillagerProfession> JOBLESS = new HashSet<>();
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

    private static void readVillager(ResourceLocation key, String contents) {
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

    private static void skin(ResourceLocation key, String entity, String named) {
        String skin = "textures/entity/" + entity + "/profession/" + key.getPath() + ".png";
        if (!PackManager.get().holders(PackType.CLIENT_RESOURCES, key.getNamespace(), skin).isEmpty()) { return; }
        ResourceLocation texture = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        byte[] worn = texture == null ? null : PackManager.get().bytes(PackType.CLIENT_RESOURCES, texture.getNamespace(), texture.getPath());
        if (texture != null && worn == null && !ResourceLocation.DEFAULT_NAMESPACE.equals(texture.getNamespace())) { ContentLog.LOGGER.error("Villager profession {} names the {} texture {}, which no pack provides, so it wears none", key, entity, named); }
        GeneratedResources.put(PackType.CLIENT_RESOURCES, key.getNamespace(), skin, worn == null ? PLAIN_SKIN : worn);
        if (named.isEmpty() && "villager".equals(entity)) { ContentLog.LOGGER.info("Villager profession {} ships no texture, so villagers taking the job wear none. Ship assets/{}/{} to dress them", key, key.getNamespace(), skin); }
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
            if (ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(def.key())) {
                ContentLog.LOGGER.debug("Villager profession {} is already registered, leaving it alone", def.key());
                continue;
            }
            ResourceKey<PoiType> site = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, def.key());
            Predicate<Holder<PoiType>> works = jobless ? holder -> false : holder -> holder.is(site);
            VillagerProfession profession = new VillagerProfession(def.key().getPath(), works, works, ImmutableSet.of(), ImmutableSet.of(), workSound(def));
            helper.register(def.key(), profession);
            if (jobless) { JOBLESS.add(profession); }
            count++;
        }
        if (count > 0) { Summary.info("content_villagers", "Registered " + count + " villager profession(s) from packs"); }
    }

    public static void spawned(Villager villager, MobSpawnType type) {
        if (JOBLESS.isEmpty()) { return; }
        VillagerData data = villager.getVillagerData();
        if (data.getProfession() == VillagerProfession.NONE && type != MobSpawnType.STRUCTURE && type != MobSpawnType.CONVERSION) {
            List<VillagerProfession> professions = new ArrayList<>();
            for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS.getValues()) {
                if (profession != VillagerProfession.NONE) { professions.add(profession); }
            }
            VillagerProfession picked = professions.get(villager.getRandom().nextInt(professions.size()));
            if (JOBLESS.contains(picked)) { villager.setVillagerData(data.setProfession(picked)); }
        }
        keepsJob(villager);
    }

    public static void professed(Entity made, RandomSource random) {
        List<VillagerProfession> professions = new ArrayList<>();
        for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS.getValues()) {
            if (profession != VillagerProfession.NONE) { professions.add(profession); }
        }
        if (professions.isEmpty()) { return; }
        VillagerProfession picked = professions.get(random.nextInt(professions.size()));
        if (made instanceof Villager villager) {
            villager.setVillagerData(villager.getVillagerData().setProfession(picked));
            if (villager.getVillagerXp() == 0) { villager.setVillagerXp(KEEPS_PROFESSION_XP); }
        }
        else if (made instanceof ZombieVillager zombie) { zombie.setVillagerData(zombie.getVillagerData().setProfession(picked)); }
    }

    public static void keepsJob(Villager villager) {
        if (JOBLESS.contains(villager.getVillagerData().getProfession()) && villager.getVillagerXp() == 0) { villager.setVillagerXp(KEEPS_PROFESSION_XP); }
    }

    public static void applyTrades(VillagerTradesEvent event) {
        load();
        if (TRADES.isEmpty()) { return; }
        checkProfessions();
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

    private static void checkProfessions() {
        if (professionsChecked) { return; }
        professionsChecked = true;
        for (TradeDef def : TRADES) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            ResourceLocation named = ResourceLocation.tryParse(def.profession());
            if (named == null || !named.toString().equals(def.profession()) || !ForgeRegistries.VILLAGER_PROFESSIONS.containsKey(named)) { ContentLog.LOGGER.error("Trade in {} names profession '{}', which is not registered, skipping it", def.key(), def.profession()); }
        }
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
