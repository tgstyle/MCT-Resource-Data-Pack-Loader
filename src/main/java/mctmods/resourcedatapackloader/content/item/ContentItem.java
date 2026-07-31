package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ContentItem extends Item implements IContentItem {
    private final ItemDef def;

    public ContentItem(ItemDef def) {
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        setHasSubtypes(true);
        setMaxDamage(0);
        ContentSetup.apply(this, def.creativeTab);
    }

    @Override public ItemDef getDef() { return def; }

    @Nullable protected ItemVariant variant(ItemStack stack) { return def.byMeta.get(stack.getMetadata()); }

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        if (!isInCreativeTab(tab)) { return; }
        for (ItemVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return super.getTranslationKey() + "." + (value == null ? ContentParser.PLACEHOLDER : value.name);
    }

    @Override @Nonnull public EnumRarity getRarity(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? EnumRarity.COMMON : value.rarity;
    }

    @Override public int getItemStackLimit(@Nonnull ItemStack stack) {
        ItemVariant value = variant(stack);
        return value == null ? 64 : value.maxSize;
    }

    @Override public int getMetadata(int damage) { return damage; }
}
