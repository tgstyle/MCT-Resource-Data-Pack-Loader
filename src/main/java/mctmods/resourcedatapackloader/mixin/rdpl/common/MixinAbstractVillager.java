package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.util.ContentDisabled;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractVillager.class) public abstract class MixinAbstractVillager {
    @Inject(method = "getOffers", at = @At("RETURN")) private void rdpl$stripDisabled(CallbackInfoReturnable<MerchantOffers> cir) { cir.getReturnValue().removeIf(MixinAbstractVillager::rdpl$disabled); }

    @Unique private static boolean rdpl$disabled(MerchantOffer offer) { return ContentDisabled.disabled(offer.getBaseCostA()) || ContentDisabled.disabled(offer.getCostB()) || ContentDisabled.disabled(offer.getResult()); }
}
