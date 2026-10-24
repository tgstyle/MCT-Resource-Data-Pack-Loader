package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.extra.ContentTradeGate;

import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantMenu.class) public abstract class MixinMerchantMenu {
    @Shadow @Final private Merchant trader;

    @Inject(method = "getOffers", at = @At("RETURN"), cancellable = true) private void rdpl$shown(CallbackInfoReturnable<MerchantOffers> cir) { cir.setReturnValue(ContentTradeGate.shown(trader, cir.getReturnValue())); }
}
