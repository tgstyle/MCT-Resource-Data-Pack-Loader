package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.RocketDef;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;

public final class RocketSlot extends Slot {
    private final RocketDef def;

    public RocketSlot(RocketDef def, IInventory rocket, int index, int x, int y) {
        super(rocket, index, x, y);
        this.def = def;
    }

    @Override public boolean isItemValid(@Nonnull ItemStack stack) { return RocketCargo.room(def, inventory, stack) > 0; }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) {
        ItemStack here = getStack();
        long most = (long) RocketCargo.room(def, inventory, stack) + (ItemStack.areItemsEqual(here, stack) ? here.getCount() : 0);
        return (int) Math.min(super.getItemStackLimit(stack), most);
    }
}
