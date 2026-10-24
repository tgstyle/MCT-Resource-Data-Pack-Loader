package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.DietDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentDiet {
    private static final String SECTION = "rdplDiet";
    private static final String MEALS = "meals";
    private static final Map<UUID, float[]> BEFORE = new HashMap<>();
    private static final Map<PotionEffectDef, MobEffect> EFFECTS = new IdentityHashMap<>();
    @Nullable private static DietDef diet;
    @Nullable private static ResourceLocation source;
    private static boolean ran;

    private ContentDiet() {}

    public static void load() {
        ran = true;
        diet = null;
        source = null;
        BEFORE.clear();
        EFFECTS.clear();
        Json.eachObject(PackManager.DIETS, "diet file", ContentDiet::read);
        DietDef loaded = diet;
        if (loaded != null) { Summary.info("diet", "Food variety from " + source + ": remembers " + loaded.memory() + " meal(s)"); }
        Standing.settleDiet();
    }

    private static void read(ResourceLocation key, JsonObject json) {
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

    public static void onTick(LivingEntityUseItemEvent.Tick event) {
        if (diet == null || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        FoodData food = player.getFoodData();
        float[] seen = BEFORE.computeIfAbsent(player.getUUID(), id -> new float[2]);
        seen[0] = food.getFoodLevel();
        seen[1] = food.getSaturationLevel();
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { BEFORE.remove(event.getEntity().getUUID()); }

    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        DietDef rules = diet;
        if (rules == null || !(event.getEntity() instanceof ServerPlayer player)) { return; }
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
