package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.compress.CompressedNames;
import mctmods.resourcedatapackloader.common.blocks.compress.CompressedSet;

import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class CompressedBlockItem extends BlockItem {
    private final CompressedSet set;

    public CompressedBlockItem(Block block, Properties properties, CompressedSet set) {
        super(block, properties);
        this.set = set;
    }

    @Override @Nonnull public Component getName(@Nonnull ItemStack stack) { return CompressedNames.of(set, set.layerOf(getBlock()), getDescriptionId()); }
}
