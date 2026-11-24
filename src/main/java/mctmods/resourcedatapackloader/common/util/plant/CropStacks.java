package mctmods.resourcedatapackloader.common.util.plant;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class CropStacks {
    private ItemStack seed = new ItemStack(Items.WHEAT_SEEDS);
    private ItemStack crop = new ItemStack(Items.WHEAT);

    public void resolve(ItemStack seedStack, ItemStack cropStack) {
        if (!seedStack.isEmpty()) { this.seed = seedStack.copy(); }
        if (!cropStack.isEmpty()) { this.crop = cropStack.copy(); }
    }

    public ItemStack seed() { return seed.copy(); }

    public ItemStack crop() { return crop.copy(); }

    public Item seedItem() { return seed.getItem(); }

    public Item cropItem() { return crop.getItem(); }
}
