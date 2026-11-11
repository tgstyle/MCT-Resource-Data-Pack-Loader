package mctmods.resourcedatapackloader.common.soulbound;

import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class SoulboundMark {
    enum Kind { ENCHANTMENT, ITEMS, NBT }

    final Kind kind;
    final ResourceLocation key;
    final String name;
    final List<ItemStack> items;
    final float loseChance;
    final boolean rollOnKeepInventory;
    final boolean grave;
    final boolean loseFirst;
    final boolean keepVanishing;
    final String tooltip;
    private boolean unknownLogged;

    SoulboundMark(Kind kind, ResourceLocation key, String name, List<ItemStack> items, float loseChance, boolean rollOnKeepInventory, boolean grave, boolean loseFirst, boolean keepVanishing, String tooltip) {
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

    @Nullable private Holder<Enchantment> enchantment(HolderLookup.Provider registries) {
        ResourceLocation id = ContentParser.location(name);
        Optional<Holder.Reference<Enchantment>> found = id == null ? Optional.empty() : registries.lookupOrThrow(Registries.ENCHANTMENT).get(ResourceKey.create(Registries.ENCHANTMENT, id));
        if (found.isEmpty() && !unknownLogged) {
            unknownLogged = true;
            ContentLog.LOGGER.error("Soulbound file {} names unknown enchantment '{}', so it marks nothing", key, name);
        }
        return found.orElse(null);
    }

    boolean matches(HolderLookup.Provider registries, ItemStack stack) {
        if (kind == Kind.ITEMS) {
            for (ItemStack item : items) {
                if (ContentStacks.matches(stack, item)) { return true; }
            }
            return false;
        }
        if (kind == Kind.NBT) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            return data != null && data.contains(name) && data.copyTag().getBoolean(name);
        }
        Holder<Enchantment> enchantment = enchantment(registries);
        return enchantment != null && stack.getEnchantmentLevel(enchantment) > 0;
    }

    void lose(HolderLookup.Provider registries, ItemStack stack, RandomSource random) {
        if (kind == Kind.ITEMS || random.nextFloat() >= loseChance) { return; }
        if (kind == Kind.NBT) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(name));
            return;
        }
        Holder<Enchantment> enchantment = enchantment(registries);
        if (enchantment == null) { return; }
        int level = stack.getEnchantmentLevel(enchantment);
        if (level > 0) { EnchantmentHelper.updateEnchantments(stack, levels -> levels.set(enchantment, level - 1)); }
    }
}
