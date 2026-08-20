package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public class ContentItemBlock extends ItemBlock {
    private final BlockDef def;

    public ContentItemBlock(Block block, BlockDef def) {
        super(block);
        this.def = def;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override @Nonnull public String getTranslationKey(ItemStack stack) { return super.getTranslationKey() + "." + def.at(stack.getMetadata()).name; }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) { return def.at(stack.getMetadata()).rarity; }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) { return def.at(stack.getMetadata()).maxSize; }

    @Override public int getMetadata(int damage) { return damage; }
}
