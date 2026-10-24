package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.DietDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentDiet {
    private static final String SECTION = "rdplDiet";
    private static final String MEALS = "meals";
    private static final Map<UUID, float[]> BEFORE = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    @Nullable private static DietDef diet;
    @Nullable private static Identifier source;
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
        GENERATION.stale();
        Json.eachObject(PackManager.DIETS, "diet file", ContentDiet::read);
        DietDef loaded = diet;
        if (loaded != null) { Summary.info("diet", "Food variety from " + source + ": remembers " + loaded.memory() + " meal(s)"); }
        Standing.settleDiet();
    }

    private static void read(Identifier key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
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
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (GENERATION.stale()) { reload(); }
        if (diet == null) { return; }
        FoodData food = player.getFoodData();
        float[] seen = BEFORE.computeIfAbsent(player.getUUID(), _ -> new float[2]);
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
        List<String> meals = meals(section);
        String key = rules.kind(event.getItem(), ContentStacks.id(event.getItem().getItem()));
        int count = 0;
        for (String meal : meals) { if (meal.equals(key)) { count++; } }
        int distinctBefore = new HashSet<>(meals).size();
        meals.add(key);
        while (meals.size() > rules.memory()) { meals.removeFirst(); }
        int distinctAfter = new HashSet<>(meals).size();
        float repeat = rules.repeatMultiplier(count);
        float variety = rules.varietyMultiplier(distinctAfter);
        float multiplier = repeat * variety;
        String message = variety > 1.0F ? rules.varietyMessage() : repeat < 1.0F ? rules.repeatMessage() : "";
        if (!message.isEmpty()) { player.sendOverlayMessage(Component.literal(Lang.tr(player, message))); }
        int level = Mth.clamp((int) seen[0] + Math.round(Math.max(0, foodGain) * multiplier), 0, 20);
        food.setFoodLevel(level);
        food.setSaturation(Mth.clamp(seen[1] + Math.max(0, satGain) * multiplier, 0.0F, level));
        ListTag written = new ListTag();
        for (String meal : meals) { written.add(StringTag.valueOf(meal)); }
        section.put(MEALS, written);
        ContentHearts.onMeal(player, distinctBefore, distinctAfter);
        reward(player, rules.bestTier(distinctBefore), rules.bestTier(distinctAfter));
    }

    private static void reward(ServerPlayer player, @Nullable DietDef.Tier before, @Nullable DietDef.Tier after) {
        if (after == null) { return; }
        for (PotionEffectDef effect : after.effects()) {
            Holder<MobEffect> held = ContentEffects.find(effect.potion());
            if (held != null) { player.addEffect(new MobEffectInstance(held, effect.duration(), effect.amplifier(), effect.ambient(), effect.showParticles())); }
        }
        if (!after.runs().isEmpty() && (before == null || after.distinct() > before.distinct())) { Functions.runAs(player, after.runs(), "Diet tier " + after.distinct() + " in " + source); }
    }

    public static boolean ran() { return ran; }

    public static boolean loaded() { return diet != null; }

    public static boolean hasTier(int distinct) { return diet != null && diet.hasTier(distinct); }

    public static int distinct(Player player) { return diet == null ? 0 : new HashSet<>(meals(PlayerPersisted.section(player, SECTION))).size(); }

    private static List<String> meals(CompoundTag section) {
        ListTag stored = section.getListOrEmpty(MEALS);
        List<String> meals = new ArrayList<>(stored.size());
        for (int i = 0; i < stored.size(); i++) { meals.add(stored.getStringOr(i, "")); }
        return meals;
    }
}
