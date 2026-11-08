package mctmods.resourcedatapackloader.mixin.integration.tconstruct;

import mctmods.resourcedatapackloader.common.blocks.ContentVeins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tools.tools.LumberAxe;

@Mixin(value = LumberAxe.class, remap = false) public abstract class MixinLumberAxe {
    @Inject(method = "fellTree(Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;)Z", at = @At("HEAD"), cancellable = true) private static void rdpl$noFellInVein(ItemStack itemstack, BlockPos start, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (ContentVeins.veining(player)) { cir.setReturnValue(false); }
    }
}
