package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

@SuppressWarnings("deprecation") public class ContentToolItem extends ItemTool implements IContentItem {
    private final ItemDef def;

    public ContentToolItem(ItemDef def, Item.ToolMaterial material, float damage, float speed, Set<Block> effective) {
        super(damage, speed, material, effective == null ? Collections.emptySet() : effective);
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
