package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentAnvils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class) public abstract class MixinAnvilMenu {
    @Unique private ItemStack rdpl$leftKept = ItemStack.EMPTY;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void rdpl$noteWhatStays(Player player, ItemStack taken, CallbackInfo ci) { rdpl$leftKept = ContentAnvils.leftAfterWork((AnvilMenu) (Object) this, taken); }

    @ModifyArg(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0), index = 1)
    private ItemStack rdpl$keepWhatStays(ItemStack emptied) { return rdpl$leftKept; }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void rdpl$offerTheNext(Player player, ItemStack taken, CallbackInfo ci) {
        if (rdpl$leftKept.isEmpty()) { return; }
        rdpl$leftKept = ItemStack.EMPTY;
        ((AnvilMenu) (Object) this).createResult();
    }
}
