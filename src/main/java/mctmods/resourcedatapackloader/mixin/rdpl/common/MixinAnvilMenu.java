package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.AnvilCosts;
import mctmods.resourcedatapackloader.content.ContentAnvils;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(AnvilMenu.class) public abstract class MixinAnvilMenu extends ItemCombinerMenu {
    @Unique private ItemStack rdpl$leftKept = ItemStack.EMPTY;
    @Unique private ItemStack rdpl$rightKept = ItemStack.EMPTY;

    protected MixinAnvilMenu(@Nullable MenuType<?> type, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slots) { super(type, containerId, inventory, access, slots); }

    @ModifyConstant(method = "createResultInternal", constant = @Constant(intValue = 40)) private int rdpl$tooExpensive(int vanilla) { return AnvilCosts.cap(player.level().isClientSide()); }

    @ModifyConstant(method = "createResultInternal", constant = @Constant(intValue = 39)) private int rdpl$renamingStaysBelow(int vanilla) { return AnvilCosts.cap(player.level().isClientSide()) - 1; }

    @Inject(method = "onTake", at = @At("HEAD")) private void rdpl$noteWhatStays(Player player, ItemStack carried, CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        rdpl$leftKept = ContentAnvils.leftAfterWork(menu, carried);
        rdpl$rightKept = ContentAnvils.rightAfterWork(menu, carried);
    }

    @ModifyArg(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0), index = 1) private ItemStack rdpl$keepWhatStays(ItemStack emptied) { return rdpl$leftKept; }

    @Inject(method = "onTake", at = @At("RETURN")) private void rdpl$offerTheNext(Player player, ItemStack carried, CallbackInfo ci) {
        if (!rdpl$rightKept.isEmpty()) {
            getSlot(1).set(rdpl$rightKept);
            rdpl$rightKept = ItemStack.EMPTY;
        }
        if (rdpl$leftKept.isEmpty()) { return; }
        rdpl$leftKept = ItemStack.EMPTY;
        createResult();
    }
}
