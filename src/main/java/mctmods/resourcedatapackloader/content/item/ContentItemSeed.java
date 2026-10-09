package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public class ContentItemSeed extends ItemSeeds implements IContentItem {
    private final ItemDef def;

    public ContentItemSeed(ItemDef def, Block crop, Block soil) {
        super(crop, soil);
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.apply(this, def.creativeTab);
    }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) { return ItemVariants.singleKey(this, def); }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) {
        ItemVariant value = ItemVariants.single(def);
        return value == null ? EnumRarity.COMMON : value.rarity;
    }
}
