package mctmods.resourcedatapackloader.content.extra;

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

    public ContentTrade(TradeDef def, ItemStack buy, ItemStack buySecondary, ItemStack sell) {
        this.def = def;
        this.buy = buy;
        this.buySecondary = buySecondary;
        this.sell = sell;
    }

    public TradeDef getDef() { return def; }

    @Override public void addMerchantRecipe(@Nonnull IMerchant merchant, @Nonnull MerchantRecipeList list, @Nonnull Random rand) {
        list.add(new MerchantRecipe(sized(buy, def.buy, rand), sized(buySecondary, def.buySecondary, rand), sized(sell, def.sell, rand), 0, def.maxUses));
    }

    private static ItemStack sized(ItemStack stack, TradeStackDef def, Random rand) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        ItemStack copy = stack.copy();
        copy.setCount(def.max <= def.min ? def.min : def.min + rand.nextInt(def.max - def.min + 1));
        return copy;
    }
}
