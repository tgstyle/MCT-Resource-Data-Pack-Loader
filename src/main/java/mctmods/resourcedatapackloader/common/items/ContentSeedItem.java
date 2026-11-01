package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation") public class ContentSeedItem extends ItemSeeds implements IContentItem {
    private final ItemDef def;

    public ContentSeedItem(ItemDef def, Block crop, Block soil) {
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
