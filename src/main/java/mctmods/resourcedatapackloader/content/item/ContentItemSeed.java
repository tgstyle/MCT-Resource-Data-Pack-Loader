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
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentItemSeed extends ItemSeeds implements IContentItem {
    private final ItemDef def;

    public ContentItemSeed(ItemDef def, Block crop, Block soil) {
        super(crop, soil);
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.apply(this, def.creativeTab);
    }

    @Override public ItemDef getDef() { return def; }

    @Nullable private ItemVariant single() { return def.visible.isEmpty() ? null : def.visible.get(0); }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) {
        ItemVariant value = single();
        return value == null ? super.getTranslationKey() : super.getTranslationKey() + "." + value.name;
    }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) {
        ItemVariant value = single();
        return value == null ? EnumRarity.COMMON : value.rarity;
    }
}
