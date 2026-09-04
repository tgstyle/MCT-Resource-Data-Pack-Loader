package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.TradeDef;
import mctmods.resourcedatapackloader.content.def.TradeStackDef;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import java.util.Random;
import javax.annotation.Nonnull;

public class ContentTrade implements EntityVillager.ITradeList {
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
        this.buyCount = amount(def.buy);
        this.buySecondaryCount = amount(def.buySecondary);
        this.sellCount = amount(def.sell);
    }

    private static AmountDef amount(TradeStackDef def) { return def.max <= def.min ? AmountDef.of(def.min) : new AmountDef(def.min, def.max); }

    @Override public void addMerchantRecipe(@Nonnull IMerchant merchant, @Nonnull MerchantRecipeList list, @Nonnull Random rand) {
        list.add(new MerchantRecipe(sized(buy, buyCount, rand), sized(buySecondary, buySecondaryCount, rand), sized(sell, sellCount, rand), 0, def.maxUses));
    }

    private static ItemStack sized(ItemStack stack, AmountDef count, Random rand) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack copy = stack.copy();
        copy.setCount(count.pick(rand));
        return copy;
    }
}
