package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.util.ContentDisabled;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityVillager.class) public abstract class MixinEntityVillager {
    @Inject(method = "getRecipes", at = @At("RETURN")) private void rdpl$stripDisabled(EntityPlayer player, CallbackInfoReturnable<MerchantRecipeList> cir) {
        MerchantRecipeList trades = cir.getReturnValue();
        if (trades != null && ContentDisabled.any()) { trades.removeIf(MixinEntityVillager::rdpl$disabled); }
    }

    @Unique private static boolean rdpl$disabled(MerchantRecipe trade) { return ContentDisabled.disabled(trade.getItemToBuy()) || ContentDisabled.disabled(trade.getSecondItemToBuy()) || ContentDisabled.disabled(trade.getItemToSell()); }
}
