package mctmods.resourcedatapackloader.content.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;

public class ContainerPouch extends ContainerPack {
    public interface ISource { ItemStack held(EntityPlayer player); }
    private final ISource source;
    private final ItemStack opened;
    private int worn = -1;

    public int worn() { return worn; }

    public ContainerPouch worn(int slot) {
        worn = slot;
        return this;
    }

    public ContainerPouch(InventoryPlayer player, PouchInventory held, ISource source, int rows, int columns) {
        super(player, held, rows, columns);
        this.source = source;
        this.opened = held.pouch();
    }

    @Override protected Slot playerSlot(InventoryPlayer player, int index, int x, int y) {
        return new Slot(player, index, x, y) {
            @Override public boolean canTakeStack(@Nonnull EntityPlayer taking) { return getStack() != opened; }
            @Override public boolean isItemValid(@Nonnull ItemStack stack) { return getStack() != opened; }
        };
    }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return source.held(player) == opened; }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getStack() == opened) { return ItemStack.EMPTY; }
        return super.transferStackInSlot(player, index);
    }
}
