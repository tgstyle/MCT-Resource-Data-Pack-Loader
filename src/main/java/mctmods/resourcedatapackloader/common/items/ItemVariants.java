package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public final class ItemVariants {
    private ItemVariants() {}

    @Nullable public static ItemVariant at(ItemDef def, ItemStack stack) { return def.at(stack.getMetadata()); }

    public static void applyPotion(@Nullable ItemVariant value, EntityLivingBase entity) {
        if (value == null) { return; }
        PotionEffect effect = value.getResolvedPotion();
        if (effect == null) { return; }
        entity.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(), false));
    }

    public static void cooldown(EntityLivingBase entity, Item item, int ticks) {
        if (ticks > 0 && entity instanceof EntityPlayer) { ((EntityPlayer) entity).getCooldownTracker().setCooldown(item, ticks); }
    }

    @Nullable public static World where(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase holder) {
        if (world != null) { return world; }
        Entity entity = holder != null ? holder : stack.getItemFrame();
        return entity == null ? null : entity.world;
    }

    @Nullable public static ItemVariant single(ItemDef def) { return def.visible.isEmpty() ? null : def.visible.get(0); }

    @Nonnull public static EnumRarity rarity(@Nullable ItemVariant value) { return value == null ? EnumRarity.COMMON : value.rarity; }

    public static int stackLimit(@Nullable ItemVariant value) { return value == null ? 64 : value.maxSize; }

    public static int heal(@Nullable ItemVariant value) { return value == null ? 0 : value.healAmount; }

    public static float saturation(@Nullable ItemVariant value) { return value == null ? 0.0F : value.saturation; }

    @Nonnull public static EnumRarity singleRarity(ItemDef def) { return rarity(single(def)); }

    @Nonnull public static String key(Item item, ItemDef def, ItemStack stack) {
        ItemVariant value = at(def, stack);
        return item.getTranslationKey() + "." + (value == null ? ContentParser.PLACEHOLDER : value.name);
    }

    @Nonnull public static String singleKey(Item item, ItemDef def) {
        ItemVariant value = single(def);
        return value == null ? item.getTranslationKey() : item.getTranslationKey() + "." + value.name;
    }

    public static void subItems(Item item, ItemDef def, CreativeTabs tab, NonNullList<ItemStack> list) {
        if (!inTab(item, tab)) { return; }
        for (ItemVariant value : def.visible) { list.add(new ItemStack(item, 1, value.meta)); }
    }

    private static boolean inTab(Item item, CreativeTabs target) {
        for (CreativeTabs tab : item.getCreativeTabs()) {
            if (tab == target) { return true; }
        }
        CreativeTabs own = item.getCreativeTab();
        return own != null && (target == CreativeTabs.SEARCH || target == own);
    }
}
