package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.def.RocketDef;
import mctmods.resourcedatapackloader.content.entity.RocketCargo;
import mctmods.resourcedatapackloader.content.entity.RocketSlot;

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.core.inventory.ContainerRocketInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ContainerRocketInventory.class, remap = false) public abstract class MixinContainerRocketInventory extends Container {
    @Shadow @Final private IInventory spaceshipInv;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void rdpl$packCargoSlots(IInventory par1IInventory, IInventory par2IInventory, EnumRocketType rocketType, EntityPlayer player, CallbackInfo ci) {
        RocketDef def = RocketCargo.def(par2IInventory);
        if (def == null) { return; }
        for (int index = 0; index < inventorySlots.size(); index++) {
            Slot plain = inventorySlots.get(index);
            if (plain.inventory != par2IInventory) { continue; }
            Slot filtered = new RocketSlot(def, par2IInventory, plain.getSlotIndex(), plain.xPos, plain.yPos);
            filtered.slotNumber = plain.slotNumber;
            inventorySlots.set(index, filtered);
        }
    }

    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true, remap = true)
    private void rdpl$packCargoQuickMove(EntityPlayer entityPlayer, int par2, CallbackInfoReturnable<ItemStack> cir) {
        RocketDef def = RocketCargo.def(spaceshipInv);
        int cargo = inventorySlots.size() - 36;
        if (def == null || par2 < cargo) { return; }
        Slot from = inventorySlots.get(par2);
        ItemStack held = from.getStack();
        int room = Math.min(held.getCount(), RocketCargo.room(def, spaceshipInv, held));
        if (room > 0) {
            ItemStack moved = held.splitStack(room);
            mergeItemStack(moved, 0, cargo, false);
            held.grow(moved.getCount());
            if (held.isEmpty()) { from.putStack(ItemStack.EMPTY); }
            else { from.onSlotChanged(); }
        }
        cir.setReturnValue(ItemStack.EMPTY);
    }
}
