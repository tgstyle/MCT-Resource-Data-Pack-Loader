package mctmods.resourcedatapackloader.common.soulbound;

import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class SoulboundMark {
    enum Kind { ENCHANTMENT, ITEMS, NBT }

    final Kind kind;
    final Identifier key;
    final String name;
    final List<ItemStack> items;
    final float loseChance;
    final boolean rollOnKeepInventory;
    final boolean grave;
    final boolean loseFirst;
    final boolean keepVanishing;
    final String tooltip;
    private boolean unknownLogged;

    SoulboundMark(Kind kind, Identifier key, String name, List<ItemStack> items, float loseChance, boolean rollOnKeepInventory, boolean grave, boolean loseFirst, boolean keepVanishing, String tooltip) {
        this.kind = kind;
        this.key = key;
        this.name = name;
        this.items = items;
        this.loseChance = loseChance;
        this.rollOnKeepInventory = rollOnKeepInventory;
        this.grave = grave;
        this.loseFirst = loseFirst;
        this.keepVanishing = keepVanishing;
        this.tooltip = tooltip;
    }

    @Nullable private Holder<Enchantment> enchantment(HolderLookup.Provider access) {
        Identifier id = ContentParser.location(name);
        Holder<Enchantment> enchantment = id == null ? null : access.lookupOrThrow(Registries.ENCHANTMENT).get(ResourceKey.create(Registries.ENCHANTMENT, id)).orElse(null);
        if (enchantment == null && !unknownLogged) {
            unknownLogged = true;
            ContentLog.LOGGER.error("Soulbound file {} names unknown enchantment '{}', so it marks nothing", key, name);
        }
        return enchantment;
    }

    boolean matches(HolderLookup.Provider access, ItemStack stack) {
        if (kind == Kind.ITEMS) {
            for (ItemStack item : items) {
                if (ContentStacks.matches(stack, item)) { return true; }
            }
            return false;
        }
        if (kind == Kind.NBT) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            return data != null && data.copyTag().getBooleanOr(name, false);
        }
        Holder<Enchantment> enchantment = enchantment(access);
        return enchantment != null && EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack) > 0;
    }

    void lose(HolderLookup.Provider access, ItemStack stack, RandomSource random) {
        if (kind == Kind.ITEMS || random.nextFloat() >= loseChance) { return; }
        if (kind == Kind.NBT) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(name));
            return;
        }
        Holder<Enchantment> enchantment = enchantment(access);
        if (enchantment == null) { return; }
        int level = EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
        if (level > 0) { EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(enchantment, level - 1)); }
    }
}
