package mctmods.resourcedatapackloader.mixin.world.inventory;

import mctmods.resourcedatapackloader.common.trades.ContentTradeGate;

import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MerchantContainer.class) public abstract class MerchantContainerMixin {
    @Redirect(method = "updateSellItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/Merchant;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;")) private MerchantOffers rdpl$shown(Merchant trader) { return ContentTradeGate.shown(trader, trader.getOffers()); }
}
