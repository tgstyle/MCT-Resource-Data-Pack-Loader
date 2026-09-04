package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeStackDef;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import javax.annotation.Nullable;

public final class ContentTrade implements VillagerTrades.ItemListing {
    private final TradeDef def;
    private final ItemStack buy;
    private final ItemStack buySecondary;
    private final ItemStack sell;
    private final AmountDef buyCount;
    private final AmountDef buySecondaryCount;
    private final AmountDef sellCount;

    public ContentTrade(TradeDef def, ItemStack buy, ItemStack buySecondary, ItemStack sell) {
        this.def = def;
        this.buy = buy;
        this.buySecondary = buySecondary;
        this.sell = sell;
        this.buyCount = amount(def.buy());
        this.buySecondaryCount = amount(def.buySecondary());
        this.sellCount = amount(def.sell());
    }

    private static AmountDef amount(TradeStackDef def) { return def.max() <= def.min() ? AmountDef.of(def.min()) : new AmountDef(def.min(), def.max()); }

    @Nullable @Override public MerchantOffer getOffer(@Nullable Entity trader, RandomSource random) {
        return new MerchantOffer(sized(buy, buyCount, random), sized(buySecondary, buySecondaryCount, random), sized(sell, sellCount, random), def.maxUses(), def.xp(), 0.05F);
    }

    private static ItemStack sized(ItemStack stack, AmountDef count, RandomSource random) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack copy = stack.copy();
        copy.setCount(count.pick(random));
        return copy;
    }
}
