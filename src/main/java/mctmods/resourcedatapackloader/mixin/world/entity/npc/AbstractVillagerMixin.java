package mctmods.resourcedatapackloader.mixin.world.entity.npc;

import mctmods.resourcedatapackloader.common.rules.ContentTradeSwitch;
import mctmods.resourcedatapackloader.common.util.ContentDisabled;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractVillager.class) public abstract class AbstractVillagerMixin {
    @Inject(method = "getOffers", at = @At("RETURN"), cancellable = true) private void rdpl$offers(CallbackInfoReturnable<MerchantOffers> cir) {
        AbstractVillager self = (AbstractVillager) (Object) this;
        MerchantOffers shown = self instanceof Villager villager ? ContentTradeSwitch.offers(villager) : null;
        MerchantOffers out = shown == null ? cir.getReturnValue() : shown;
        out.removeIf(AbstractVillagerMixin::rdpl$disabled);
        if (shown != null) { cir.setReturnValue(shown); }
    }

    @Unique private static boolean rdpl$disabled(MerchantOffer offer) { return ContentDisabled.disabled(offer.getBaseCostA()) || ContentDisabled.disabled(offer.getCostB()) || ContentDisabled.disabled(offer.getResult()); }
}
