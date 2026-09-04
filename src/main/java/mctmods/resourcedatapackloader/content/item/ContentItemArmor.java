package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentItemArmor extends ItemArmor implements IContentItem {
    private final ItemDef def;

    public ContentItemArmor(ItemDef def, ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, 0, slot);
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.apply(this, def.creativeTab);
    }

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
