package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCombinerMenu.class) public abstract class MixinItemCombinerMenu {
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void rdpl$onlyWorkPaidFor(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        ItemCombinerMenu self = (ItemCombinerMenu) (Object) this;
        if (!(self instanceof AnvilMenu menu) || index != menu.getResultSlot()) { return; }
        if (!menu.getSlot(index).mayPickup(player)) { cir.setReturnValue(ItemStack.EMPTY); }
    }
}
