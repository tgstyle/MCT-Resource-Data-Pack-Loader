package mctmods.resourcedatapackloader.common.diet;

import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.DietDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentDiet {
    private static final String SECTION = "rdplDiet";
    private static final String MEALS = "meals";
    private static final Map<UUID, float[]> BEFORE = new HashMap<>();
    private static final Map<PotionEffectDef, MobEffect> EFFECTS = new IdentityHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final String KEEP_ON_DEATH = "keepOnDeath";
    private static final float FULL = 20.0F;
    @Nullable private static DietDef diet;
    @Nullable private static ResourceLocation source;
    @Nullable private static ResourceLocation keepSource;
    @Nullable private static float[] keepFood;
    @Nullable private static float[] keepSaturation;
    private static boolean ran;

    private ContentDiet() {}

    public static void load() {
        ran = true;
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
        DietDef loaded = diet;
        if (loaded != null) { Summary.info("diet", "Food variety from " + source + ": remembers " + loaded.memory() + " meal(s)"); }
        float[] keptFood = keepFood;
        float[] keptSaturation = keepSaturation;
        if (keptFood != null && keptSaturation != null) { Summary.info("dietKeep", "A death respawn keeps hunger " + keptFood[0] + " to " + keptFood[1] + " and saturation " + keptSaturation[0] + " to " + keptSaturation[1]); }
        Standing.settleDiet();
    }

    private static void read(ResourceLocation key, JsonObject json) {
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

    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer)) { return; }
        if (GENERATION.stale()) { reload(); }
        float[] keptFood = keepFood;
        float[] keptSaturation = keepSaturation;
        if (keptFood == null || keptSaturation == null) { return; }
        FoodData old = event.getOriginal().getFoodData();
        int level = Math.round(Mth.clamp(old.getFoodLevel(), keptFood[0], keptFood[1]));
        FoodData food = event.getEntity().getFoodData();
        food.setFoodLevel(level);
        food.setSaturation(Math.min(level, Mth.clamp(old.getSaturationLevel(), keptSaturation[0], keptSaturation[1])));
    }

    public static void onTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (GENERATION.stale()) { reload(); }
        if (diet == null) { return; }
        FoodData food = player.getFoodData();
        float[] seen = BEFORE.computeIfAbsent(player.getUUID(), id -> new float[2]);
        seen[0] = food.getFoodLevel();
        seen[1] = food.getSaturationLevel();
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { BEFORE.remove(event.getEntity().getUUID()); }

    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (GENERATION.stale()) { reload(); }
        DietDef rules = diet;
        if (rules == null) { return; }
        float[] seen = BEFORE.remove(player.getUUID());
        if (seen == null || rules.exempts(event.getItem())) { return; }
        FoodData food = player.getFoodData();
        float foodGain = food.getFoodLevel() - seen[0];
        float satGain = food.getSaturationLevel() - seen[1];
        if (foodGain <= 0 && satGain <= 0) { return; }
        CompoundTag section = PlayerPersisted.section(player, SECTION);
        ListTag stored = section.getList(MEALS, Tag.TAG_STRING);
        List<String> meals = new ArrayList<>(stored.size());
        for (int i = 0; i < stored.size(); i++) { meals.add(stored.getString(i)); }
        String key = rules.kind(event.getItem(), ContentStacks.id(event.getItem().getItem()));
        int count = 0;
        for (String meal : meals) { if (meal.equals(key)) { count++; } }
        int distinctBefore = new HashSet<>(meals).size();
        meals.add(key);
        while (meals.size() > rules.memory()) { meals.remove(0); }
        int distinctAfter = new HashSet<>(meals).size();
        float repeat = rules.repeatMultiplier(count);
        float variety = rules.varietyMultiplier(distinctAfter);
        float multiplier = repeat * variety;
        String message = variety > 1.0F ? rules.varietyMessage() : repeat < 1.0F ? rules.repeatMessage() : "";
        if (!message.isEmpty()) { player.displayClientMessage(Component.literal(Lang.tr(player, message)), true); }
        int level = Math.max(0, Math.min(20, (int) seen[0] + Math.round(Math.max(0, foodGain) * multiplier)));
        food.setFoodLevel(level);
        food.setSaturation(Math.max(0, Math.min(level, seen[1] + Math.max(0, satGain) * multiplier)));
        ListTag written = new ListTag();
        for (String meal : meals) { written.add(StringTag.valueOf(meal)); }
        section.put(MEALS, written);
        ContentHearts.onMeal(player, distinctBefore, distinctAfter);
        reward(player, rules.bestTier(distinctBefore), rules.bestTier(distinctAfter));
    }

    private static void reward(ServerPlayer player, @Nullable DietDef.Tier before, @Nullable DietDef.Tier after) {
        if (after == null) { return; }
        for (PotionEffectDef effect : after.effects()) {
            MobEffect potion = EFFECTS.computeIfAbsent(effect, held -> Registered.find(ForgeRegistries.MOB_EFFECTS, ResourceLocation.tryParse(held.potion())));
            if (potion != null) { player.addEffect(new MobEffectInstance(potion, effect.duration(), effect.amplifier(), effect.ambient(), effect.showParticles())); }
        }
        if (!after.runs().isEmpty() && (before == null || after.distinct() > before.distinct())) { Functions.runAs(player, after.runs(), "Diet tier " + after.distinct() + " in " + source); }
    }

    public static boolean ran() { return ran; }

    public static boolean loaded() { return diet != null; }

    public static boolean hasTier(int distinct) {
        DietDef rules = diet;
        return rules != null && rules.hasTier(distinct);
    }

    public static int distinct(Player player) {
        if (diet == null) { return 0; }
        ListTag stored = PlayerPersisted.section(player, SECTION).getList(MEALS, Tag.TAG_STRING);
        Set<String> meals = new HashSet<>();
        for (int i = 0; i < stored.size(); i++) { meals.add(stored.getString(i)); }
        return meals.size();
    }
}
