package mctmods.resourcedatapackloader.mixin.server.level;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayer.class) public abstract class ServerPlayerMixin {
    @ModifyVariable(method = "sendMerchantOffers", at = @At("HEAD"), name = "offers", argsOnly = true) private MerchantOffers rdpl$shown(MerchantOffers offers, int containerId) { return ((ServerPlayer) (Object) this).containerMenu instanceof MerchantMenu menu && menu.containerId == containerId ? menu.getOffers() : offers; }

    @WrapOperation(method = "findRespawnAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer$RespawnConfig;Z)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BedRule;canSetSpawn(Lnet/minecraft/world/level/Level;)Z")) private static boolean rdpl$bedSpawnWhereBedsWork(BedRule rule, Level level, Operation<Boolean> original) { return original.call(rule, level) || ContentDimensions.bedsWork(level); }
}
