package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;

import javax.annotation.Nonnull;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation") public class ContentArmorItem extends ItemArmor implements IContentItem {
    private final ItemDef def;

    public ContentArmorItem(ItemDef def, ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, 0, slot);
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
