package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.DietDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentDiet {
    private static final String SECTION = "rdplDiet";
    private static final String MEALS = "meals";
    private static final Map<UUID, float[]> BEFORE = new HashMap<>();
    private static final Map<PotionEffectDef, Potion> EFFECTS = new IdentityHashMap<>();
    private static DietDef diet;
    private static ResourceLocation source;

    private ContentDiet() {}

    public static boolean load() {
        diet = null;
        source = null;
        BEFORE.clear();
        EFFECTS.clear();
        Json.eachObject(PackManager.DIETS, "diet file", ContentDiet::read);
        if (diet != null) { Summary.info("diet", "Food variety from " + source + ": remembers " + diet.memory + " meal(s)"); }
        return diet != null;
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

    @SubscribeEvent(receiveCanceled = true) public static void onTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        FoodStats food = player.getFoodStats();
        float[] seen = BEFORE.computeIfAbsent(player.getUniqueID(), id -> new float[2]);
        seen[0] = food.getFoodLevel();
        seen[1] = food.getSaturationLevel();
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { BEFORE.remove(event.player.getUniqueID()); }

    @SubscribeEvent public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        float[] seen = BEFORE.remove(player.getUniqueID());
        if (seen == null || diet.exempts(event.getItem())) { return; }
        FoodStats food = player.getFoodStats();
        int foodBefore = (int) seen[0];
        float foodGain = food.getFoodLevel() - seen[0];
        float satGain = food.getSaturationLevel() - seen[1];
        if (foodGain <= 0 && satGain <= 0) { return; }
        NBTTagCompound section = PlayerPersisted.section(player, SECTION);
        List<String> meals = meals(section);
        String key = diet.kind(event.getItem(), key(event.getItem()));
        int count = 0;
        for (String meal : meals) { if (meal.equals(key)) { count++; } }
        int distinctBefore = new HashSet<>(meals).size();
        meals.add(key);
        while (meals.size() > diet.memory) { meals.remove(0); }
        int distinctAfter = new HashSet<>(meals).size();
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
        ContentHearts.onMeal(player, distinctBefore, distinctAfter);
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
