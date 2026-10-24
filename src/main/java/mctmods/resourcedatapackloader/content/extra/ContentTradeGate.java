package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.PriceScaleDef;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTradeGate {
    private static final Map<String, TradeDef> GATED = new HashMap<>();

    private ContentTradeGate() {}

    public static boolean active() { return !GATED.isEmpty(); }

    public static boolean clashes(TradeDef def, ItemStack buy, ItemStack buySecondary, ItemStack sell) {
        if (!def.gated()) { return false; }
        String key = key(def.profession, buy, buySecondary, sell);
        TradeDef prior = GATED.putIfAbsent(key, def);
        if (prior == null || prior == def) { return false; }
        ContentLog.LOGGER.error("Trade in {} uses the same items as the conditional trade in {}, which tells them apart by items, skipping it", def.key, prior.key);
        return true;
    }

    public static void summarize() { if (active()) { Summary.info("content_trade_gate", "Gated " + GATED.size() + " villager trade(s) by player condition"); } }

    private static String key(String profession, ItemStack buy, ItemStack buySecondary, ItemStack sell) {
        return new ResourceLocation(profession) + "|" + id(buy) + "|" + id(buySecondary) + "|" + id(sell);
    }

    private static String id(ItemStack stack) {
        if (stack.isEmpty()) { return ""; }
        ResourceLocation name = stack.getItem().getRegistryName();
        return name + "@" + stack.getMetadata();
    }

    @SubscribeEvent public static void onOffers(MerchantTradeOffersEvent event) {
        MerchantRecipeList list = event.getList();
        EntityPlayer player = event.getPlayer();
        if (list == null || player == null || player.world.isRemote || !(event.getMerchant() instanceof EntityVillager)) { return; }
        VillagerRegistry.VillagerProfession profession = ((EntityVillager) event.getMerchant()).getProfessionForge();
        ResourceLocation professionName = profession.getRegistryName();
        if (professionName == null) { return; }
        for (int i = list.size() - 1; i >= 0; i--) {
            MerchantRecipe recipe = list.get(i);
            TradeDef def = GATED.get(professionName + "|" + id(recipe.getItemToBuy()) + "|" + id(recipe.getSecondItemToBuy()) + "|" + id(recipe.getItemToSell()));
            if (def == null) { continue; }
            if (!passes(def, player)) { list.remove(i); }
            else if (def.scale != null) { list.set(i, scaled(recipe, def.scale, player)); }
        }
    }

    private static boolean passes(TradeDef def, EntityPlayer player) {
        if (!def.advancement.isEmpty() && !Advancements.has(player, def.advancement)) { return false; }
        return !def.standing.any() || def.standing.earnedBy(player);
    }

    private static MerchantRecipe scaled(MerchantRecipe recipe, PriceScaleDef scale, EntityPlayer player) {
        @Nullable Integer score = Standing.score(player, scale.objective);
        ItemStack buy = recipe.getItemToBuy().copy();
        buy.setCount(scale.apply(buy.getCount(), score == null ? 0 : score, buy.getMaxStackSize()));
        return new ScaledRecipe(recipe, buy);
    }
}
