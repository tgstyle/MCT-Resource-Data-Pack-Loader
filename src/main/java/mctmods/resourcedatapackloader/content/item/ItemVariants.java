package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemVariants {
    private ItemVariants() {}

    @Nullable public static ItemVariant at(ItemDef def, ItemStack stack) { return def.at(stack.getMetadata()); }

    public static void applyPotion(@Nullable ItemVariant value, EntityLivingBase entity) {
        if (value == null) { return; }
        PotionEffect effect = value.getResolvedPotion();
        if (effect == null) { return; }
        entity.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(), false));
    }

    @Nullable public static ItemVariant single(ItemDef def) { return def.visible.isEmpty() ? null : def.visible.get(0); }

    @Nonnull public static String key(Item item, ItemDef def, ItemStack stack) {
        ItemVariant value = at(def, stack);
        return item.getTranslationKey() + "." + (value == null ? ContentParser.PLACEHOLDER : value.name);
    }

    @Nonnull public static String singleKey(Item item, ItemDef def) {
        ItemVariant value = single(def);
        return value == null ? item.getTranslationKey() : item.getTranslationKey() + "." + value.name;
    }

    public static void subItems(Item item, ItemDef def, NonNullList<ItemStack> list) {
        for (ItemVariant value : def.visible) { list.add(new ItemStack(item, 1, value.meta)); }
    }
}
