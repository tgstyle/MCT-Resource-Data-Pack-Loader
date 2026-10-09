package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.RocketDef;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RocketSlot extends Slot {
    private static final int PLAYER_SLOTS = 36;
    private final RocketDef def;

    public RocketSlot(RocketDef def, IInventory rocket, int index, int x, int y) {
        super(rocket, index, x, y);
        this.def = def;
    }

    public static void filter(List<Slot> slots, IInventory rocket) {
        RocketDef def = RocketCargo.def(rocket);
        if (def == null) { return; }
        for (int index = 0; index < slots.size(); index++) {
            Slot plain = slots.get(index);
            if (plain.inventory != rocket) { continue; }
            Slot filtered = new RocketSlot(def, rocket, plain.getSlotIndex(), plain.xPos, plain.yPos);
            filtered.slotNumber = plain.slotNumber;
            slots.set(index, filtered);
        }
    }

    public static int cargo(List<Slot> slots) { return slots.size() - PLAYER_SLOTS; }

    @Nullable public static ItemStack moving(List<Slot> slots, IInventory rocket, int index) {
        RocketDef def = RocketCargo.def(rocket);
        if (def == null || index < cargo(slots)) { return null; }
        ItemStack held = slots.get(index).getStack();
        int room = Math.min(held.getCount(), RocketCargo.room(def, rocket, held));
        return room > 0 ? held.splitStack(room) : ItemStack.EMPTY;
    }

    public static void settle(Slot from, ItemStack left) {
        ItemStack held = from.getStack();
        held.grow(left.getCount());
        if (held.isEmpty()) { from.putStack(ItemStack.EMPTY); }
        else { from.onSlotChanged(); }
    }

    @Override public boolean isItemValid(@Nonnull ItemStack stack) { return RocketCargo.room(def, inventory, stack) > 0; }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) {
        ItemStack here = getStack();
        long most = (long) RocketCargo.room(def, inventory, stack) + (ItemStack.areItemsEqual(here, stack) ? here.getCount() : 0);
        return (int) Math.min(super.getItemStackLimit(stack), most);
    }
}
