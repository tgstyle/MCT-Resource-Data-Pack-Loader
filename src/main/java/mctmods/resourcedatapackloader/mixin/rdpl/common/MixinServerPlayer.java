package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayer.class) public abstract class MixinServerPlayer {
    @ModifyVariable(method = "sendMerchantOffers", at = @At("HEAD"), argsOnly = true) private MerchantOffers rdpl$shown(MerchantOffers shown, int containerId, MerchantOffers offers, int level, int xp, boolean showProgress, boolean canRestock) { return ((ServerPlayer) (Object) this).containerMenu instanceof MerchantMenu menu && menu.containerId == containerId ? menu.getOffers() : offers; }
}
