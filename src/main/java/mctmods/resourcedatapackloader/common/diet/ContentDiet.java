package mctmods.resourcedatapackloader.common.diet;

import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.DietDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.PlayerValueSync;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class ContentDiet {
    private static final String SECTION = "rdplDiet";
    private static final String MEALS = "meals";
    private static final String EATEN = "eaten";
    private static final String MEMORY = "memory";
    private static final String LIFETIME = "lifetime";
    private static final List<String> COUNTS = Arrays.asList(MEMORY, LIFETIME);
    private static final List<String> DIET_KEYS = Collections.unmodifiableList(Arrays.asList("requires", "keepOnDeath", "memory", "repeat", "variety", "exempt", "groups", "messages", "tooltip"));
    private static final List<String> KEEP_KEYS = Collections.unmodifiableList(Arrays.asList("hunger", "saturation"));
    private static final PlayerValueSync<Set<String>> SYNC = new PlayerValueSync<>(ContentDiet::eaten, ContentDiet::tooltip, RDPLNetwork::sendEaten);
    private static final Map<UUID, float[]> BEFORE = new HashMap<>();
    private static final Map<PotionEffectDef, Potion> EFFECTS = new IdentityHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final String KEEP_ON_DEATH = "keepOnDeath";
    private static final float FULL = 20.0F;
    private static DietDef diet;
    private static ResourceLocation source;
    private static ResourceLocation keepSource;
    private static float[] keepFood;
    private static float[] keepSaturation;

    private ContentDiet() {}

    public static void load() {
        BEFORE.clear();
        reload();
    }

    private static void reload() {
        diet = null;
        source = null;
        keepFood = null;
        keepSaturation = null;
        EFFECTS.clear();
        GENERATION.stale();
        Json.eachObject(PackManager.DIETS, "diet file", ContentDiet::read);
        if (diet != null) { Summary.info("diet", "Food variety from " + source + ": remembers " + diet.memory + " meal(s)"); }
        if (keepFood != null) { Summary.info("dietKeep", "A death respawn keeps hunger " + keepFood[0] + " to " + keepFood[1] + " and saturation " + keepSaturation[0] + " to " + keepSaturation[1]); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", DIET_KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (json.has(KEEP_ON_DEATH)) { readKeep(key, json); }
        if (onlyKeep(json)) { return; }
        if (diet != null) {
            ContentLog.LOGGER.error("Diet file {} found, but {} already set the diet, so the first one is kept", key, source);
            return;
        }
        try {
            diet = DietDef.parse(key, json);
            source = key;
        }
        catch (IllegalArgumentException ex) { ContentLog.LOGGER.error("Diet file {} is skipped: {}", key, ex.getMessage()); }
    }

    private static boolean onlyKeep(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) { if (!KEEP_ON_DEATH.equals(entry.getKey()) && !"requires".equals(entry.getKey())) { return false; } }
        return true;
    }

    private static void readKeep(ResourceLocation key, JsonObject json) {
        if (keepFood != null) {
            ContentLog.LOGGER.error("Diet file {} sets keepOnDeath, but {} already did, so the first one is kept", key, keepSource);
            return;
        }
        if (!json.get(KEEP_ON_DEATH).isJsonObject()) {
            ContentLog.LOGGER.error("Diet file {} has a keepOnDeath that is not an object, so it is ignored", key);
            return;
        }
        JsonObject keep = json.getAsJsonObject(KEEP_ON_DEATH);
        Json.unknownKeys(keep, KEEP_ON_DEATH, KEEP_KEYS);
        keepFood = range(keep, "hunger", key);
        keepSaturation = range(keep, "saturation", key);
        keepSource = key;
    }

    private static float[] range(JsonObject keep, String member, ResourceLocation owner) {
        float[] full = {0.0F, FULL};
        if (!keep.has(member)) { return full; }
        JsonArray pair = Json.getJsonArray(keep, member, new JsonArray());
        if (pair.size() == 2) {
            float low = Json.asFloat(pair.get(0), member, -1.0F);
            float high = Json.asFloat(pair.get(1), member, -1.0F);
            if (low >= 0.0F && low <= high && high <= FULL) { return new float[] {low, high}; }
        }
        ContentLog.LOGGER.error("Diet file {} keepOnDeath {} must be [min, max] within 0 to {}, so the full range is used", owner, member, (int) FULL);
        return full;
    }

    @SubscribeEvent public static void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        if (GENERATION.stale()) { reload(); }
        if (keepFood == null) { return; }
        FoodStats old = event.getOriginal().getFoodStats();
        int level = Math.round(MathHelper.clamp(old.getFoodLevel(), keepFood[0], keepFood[1]));
        float saturation = Math.min(level, MathHelper.clamp(old.getSaturationLevel(), keepSaturation[0], keepSaturation[1]));
        FoodStats food = event.getEntityPlayer().getFoodStats();
        NBTTagCompound stats = new NBTTagCompound();
        food.writeNBT(stats);
        stats.setInteger("foodLevel", level);
        stats.setFloat("foodSaturationLevel", saturation);
        food.readNBT(stats);
    }

    @SubscribeEvent(receiveCanceled = true) public static void onTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        if (GENERATION.stale()) { reload(); }
        if (diet == null) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        FoodStats food = player.getFoodStats();
        float[] seen = BEFORE.computeIfAbsent(player.getUniqueID(), id -> new float[2]);
        seen[0] = food.getFoodLevel();
        seen[1] = food.getSaturationLevel();
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        BEFORE.remove(event.player.getUniqueID());
        SYNC.forget(event.player);
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        if (GENERATION.stale()) { reload(); }
        SYNC.fresh((EntityPlayerMP) event.player);
    }

    public static void refresh(MinecraftServer server) {
        if (GENERATION.stale()) { reload(); }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { SYNC.fresh(player); }
    }

    @SubscribeEvent public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        if (GENERATION.stale()) { reload(); }
        if (diet == null) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        float[] seen = BEFORE.remove(player.getUniqueID());
        if (seen == null || diet.exempts(event.getItem())) { return; }
        FoodStats food = player.getFoodStats();
        int foodBefore = (int) seen[0];
        float foodGain = food.getFoodLevel() - seen[0];
        float satGain = food.getSaturationLevel() - seen[1];
        if (foodGain <= 0 && satGain <= 0 && notFood(event.getItem())) { return; }
        NBTTagCompound section = PlayerPersisted.section(player, SECTION);
        List<String> meals = meals(section);
        Set<String> eaten = eaten(section, meals);
        String key = diet.kind(event.getItem(), key(event.getItem()));
        int count = 0;
        for (String meal : meals) { if (meal.equals(key)) { count++; } }
        int distinctBefore = new HashSet<>(meals).size();
        meals.add(key);
        while (meals.size() > diet.memory) { meals.remove(0); }
        int distinctAfter = new HashSet<>(meals).size();
        int everBefore = eaten.size();
        eaten.add(key);
        NBTTagList ever = new NBTTagList();
        for (String kind : eaten) { ever.appendTag(new NBTTagString(kind)); }
        section.setTag(EATEN, ever);
        float repeat = diet.repeatMultiplier(count);
        float variety = diet.varietyMultiplier(distinctAfter);
        float multiplier = repeat * variety;
        String message = variety > 1.0F ? diet.varietyMessage : repeat < 1.0F ? diet.repeatMessage : "";
        if (!message.isEmpty()) { player.sendStatusMessage(new TextComponentString(Lang.tr(player, message)), true); }
        int level = Math.max(0, Math.min(20, foodBefore + Math.round(Math.max(0, foodGain) * multiplier)));
        float saturation = Math.max(0, Math.min(level, seen[1] + Math.max(0, satGain) * multiplier));
        NBTTagCompound stats = new NBTTagCompound();
        food.writeNBT(stats);
        stats.setInteger("foodLevel", level);
        stats.setFloat("foodSaturationLevel", saturation);
        food.readNBT(stats);
        NBTTagList written = new NBTTagList();
        for (String meal : meals) { written.appendTag(new NBTTagString(meal)); }
        section.setTag(MEALS, written);
        SYNC.push(player);
        ContentHearts.onMeal(player, distinctBefore, distinctAfter, everBefore, eaten.size());
        reward(player, diet.bestTier(distinctBefore), diet.bestTier(distinctAfter));
    }

    private static void reward(EntityPlayerMP player, @Nullable DietDef.Tier before, @Nullable DietDef.Tier after) {
        if (after == null) { return; }
        for (PotionEffectDef effect : after.effects) {
            Potion potion = EFFECTS.computeIfAbsent(effect, held -> Potion.getPotionFromResourceLocation(held.potion));
            if (potion != null) { player.addPotionEffect(new PotionEffect(potion, effect.duration, effect.amplifier, effect.ambient, effect.showParticles)); }
        }
        if (!after.runs.isEmpty() && (before == null || after.distinct > before.distinct)) { Functions.runAs(player, after.runs, "Diet tier " + after.distinct + " in " + source); }
    }

    public static boolean loaded() { return diet != null; }

    public static boolean hasTier(int distinct) { return diet != null && diet.hasTier(distinct); }

    public static int distinct(EntityPlayer player) { return diet == null ? 0 : new HashSet<>(meals(PlayerPersisted.section(player, SECTION))).size(); }

    public static int eatenEver(EntityPlayer player) { return diet == null ? 0 : eaten(player).size(); }

    public static boolean tooltip() {
        DietDef rules = diet;
        return rules != null && rules.tooltip;
    }

    public static String counted(ItemStack stack) {
        DietDef rules = diet;
        return rules == null || rules.exempts(stack) || notFood(stack) ? "" : rules.kind(stack, key(stack));
    }

    public static boolean lifetime(JsonObject json, Object owner) { return LIFETIME.equals(Json.choice(json, "dietCount", MEMORY, owner, COUNTS)); }

    private static Set<String> eaten(EntityPlayer player) {
        NBTTagCompound section = PlayerPersisted.read(player, SECTION);
        return eaten(section, meals(section));
    }

    private static Set<String> eaten(NBTTagCompound section, List<String> meals) {
        Set<String> eaten = new LinkedHashSet<>();
        if (!section.hasKey(EATEN)) { eaten.addAll(meals); }
        NBTTagList stored = section.getTagList(EATEN, Constants.NBT.TAG_STRING);
        for (int i = 0; i < stored.tagCount(); i++) { eaten.add(stored.getStringTagAt(i)); }
        return eaten;
    }

    private static boolean notFood(ItemStack stack) { return !(stack.getItem() instanceof ItemFood) || ((ItemFood) stack.getItem()).getHealAmount(stack) < 1; }

    private static List<String> meals(NBTTagCompound section) {
        NBTTagList stored = section.getTagList(MEALS, Constants.NBT.TAG_STRING);
        List<String> meals = new ArrayList<>();
        for (int i = 0; i < stored.tagCount(); i++) { meals.add(stored.getStringTagAt(i)); }
        return meals;
    }

    private static String key(ItemStack stack) {
        Item item = stack.getItem();
        return item.getRegistryName() + (item.getHasSubtypes() ? "@" + stack.getMetadata() : "");
    }
}
