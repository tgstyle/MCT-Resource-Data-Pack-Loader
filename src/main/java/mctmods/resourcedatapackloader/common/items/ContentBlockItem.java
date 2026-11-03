package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.compress.CompressedNames;
import mctmods.resourcedatapackloader.common.blocks.compress.CompressedSet;
import mctmods.resourcedatapackloader.common.blocks.compress.ContentCompressables;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation") public class ContentBlockItem extends ItemBlock {
    private final BlockDef def;

    public ContentBlockItem(Block block, BlockDef def) {
        super(block);
        this.def = def;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override @Nonnull public String getTranslationKey(ItemStack stack) { return super.getTranslationKey() + "." + def.at(stack.getMetadata()).name; }

    @Override @Nonnull public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        CompressedSet set = ContentCompressables.owner(def.registryName);
        return set == null ? super.getItemStackDisplayName(stack) : CompressedNames.of(set, set.layerOf(def.registryName, stack.getMetadata()), getTranslationKey(stack) + ".name");
    }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) { return def.at(stack.getMetadata()).rarity; }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) { return def.at(stack.getMetadata()).maxSize; }

    @Override public int getMetadata(int damage) { return damage; }
}
