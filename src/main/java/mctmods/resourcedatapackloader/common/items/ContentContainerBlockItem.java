package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;

import javax.annotation.Nonnull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class ContentContainerBlockItem extends BlockItem {
    public ContentContainerBlockItem(Block block, Properties properties) { super(block, properties); }

    public static boolean holdsKept(ItemStack stack) { return stack.getItem() instanceof ContentContainerBlockItem item && item.getBlock() instanceof ContentContainerBlock held && held.container().keepContents() && (stack.has(DataComponents.CONTAINER) || stack.has(DataComponents.BLOCK_ENTITY_DATA)); }

    @Override public int getMaxStackSize(@Nonnull ItemStack stack) { return holdsKept(stack) ? 1 : super.getMaxStackSize(stack); }
}
