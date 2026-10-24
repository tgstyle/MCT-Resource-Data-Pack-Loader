package mctmods.resourcedatapackloader.content.extra;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ScaledRecipe extends MerchantRecipe {
    private final MerchantRecipe source;

    public ScaledRecipe(MerchantRecipe source, ItemStack scaledBuy) {
        super(scaledBuy, source.getSecondItemToBuy(), source.getItemToSell(), source.getToolUses(), source.getMaxTradeUses());
        this.source = source;
    }

    @Override public int getToolUses() { return source.getToolUses(); }

    @Override public int getMaxTradeUses() { return source.getMaxTradeUses(); }

    @Override public void incrementToolUses() { source.incrementToolUses(); }

    @Override public void increaseMaxTradeUses(int increment) { source.increaseMaxTradeUses(increment); }

    @Override public boolean isRecipeDisabled() { return source.isRecipeDisabled(); }

    @Override @SideOnly(Side.CLIENT) public void compensateToolUses() { source.compensateToolUses(); }

    @Override public boolean getRewardsExp() { return source.getRewardsExp(); }
}
