package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.PriceScaleDef;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTradeGate {
    private static final Map<String, TradeDef> GATED = new HashMap<>();

    private ContentTradeGate() {}

    public static boolean register(TradeDef def, ItemStack buy, ItemStack buySecondary, ItemStack sell) {
        if (!def.gated()) { return true; }
        TradeDef prior = GATED.putIfAbsent(key(def.profession(), buy, buySecondary, sell), def);
        if (prior == null || prior.equals(def)) { return true; }
        ContentLog.LOGGER.error("Trade in {} uses the same items as the conditional trade in {}, which tells them apart by items, skipping it", def.key(), prior.key());
        return false;
    }

    public static void summarize() { if (!GATED.isEmpty()) { Summary.info("content_trade_gate", "Gated " + GATED.size() + " villager trade(s) by player condition"); } }

    private static String key(String profession, ItemStack buy, ItemStack buySecondary, ItemStack sell) { return profession + "|" + BuiltInRegistries.ITEM.getKey(buy.getItem()) + "|" + BuiltInRegistries.ITEM.getKey(buySecondary.getItem()) + "|" + BuiltInRegistries.ITEM.getKey(sell.getItem()); }

    @Nullable private static TradeDef find(Villager villager, MerchantOffer offer) { return GATED.get(key(String.valueOf(BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession())), offer.getBaseCostA(), offer.getCostB(), offer.getResult())); }

    private static boolean passes(TradeDef def, ServerPlayer player) {
        if (!def.advancement().isEmpty() && !Advancements.has(player, def.advancement())) { return false; }
        return !def.standing().any() || def.standing().earnedBy(player);
    }

    public static MerchantOffers shown(Merchant trader, MerchantOffers offers) {
        if (GATED.isEmpty() || !(trader instanceof Villager villager) || !(villager.getTradingPlayer() instanceof ServerPlayer player)) { return offers; }
        MerchantOffers kept = new MerchantOffers();
        boolean hidden = false;
        for (MerchantOffer offer : offers) {
            TradeDef def = find(villager, offer);
            if (def == null || passes(def, player)) { kept.add(offer); }
            else { hidden = true; }
        }
        return hidden ? kept : offers;
    }

    public static void scale(Villager villager, ServerPlayer player) {
        if (GATED.isEmpty()) { return; }
        for (MerchantOffer offer : villager.getOffers()) {
            TradeDef def = find(villager, offer);
            if (def == null) { continue; }
            PriceScaleDef scale = def.scale();
            if (scale == null || !passes(def, player)) { continue; }
            Integer score = Standing.score(player, scale.objective());
            ItemStack base = offer.getBaseCostA();
            offer.addToSpecialPriceDiff(scale.apply(base.getCount(), score == null ? 0 : score, base.getMaxStackSize()) - base.getCount());
        }
    }
}
