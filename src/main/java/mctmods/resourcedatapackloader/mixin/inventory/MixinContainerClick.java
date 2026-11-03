package mctmods.resourcedatapackloader.mixin.inventory;

import mctmods.resourcedatapackloader.common.weights.WeightEnforce;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class) public abstract class MixinContainerClick {
    @Inject(method = "slotClick", at = @At("HEAD")) private void rdpl$noteClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) { WeightEnforce.beginClick(player, clickTypeIn, dragType); }

    @Inject(method = "slotClick", at = @At("RETURN")) private void rdpl$endClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) { WeightEnforce.endClick(player); }
}
