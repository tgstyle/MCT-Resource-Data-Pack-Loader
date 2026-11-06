package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class ContentContainerBlockItem extends BlockItem {
    public ContentContainerBlockItem(Block block, Properties properties) { super(block, properties); }

    public static boolean holdsKept(ItemStack stack) { return stack.getItem() instanceof ContentContainerBlockItem item && item.getBlock() instanceof ContentContainerBlock held && held.container().keepContents() && stack.getTagElement(ContentPouchInventory.HELD) != null; }

    @Override public int getMaxStackSize(ItemStack stack) { return holdsKept(stack) ? 1 : super.getMaxStackSize(stack); }
}
