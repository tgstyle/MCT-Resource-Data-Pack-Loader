package mctmods.resourcedatapackloader.common.soulbound;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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

    @Nullable private Enchantment enchantment() {
        Enchantment enchantment = Registries.find(ForgeRegistries.ENCHANTMENTS, new ResourceLocation(name));
        if (enchantment == null && !unknownLogged) {
            unknownLogged = true;
            ContentLog.LOGGER.error("Soulbound file {} names unknown enchantment '{}', so it marks nothing", key, name);
        }
        return enchantment;
    }

    boolean matches(ItemStack stack) {
        if (kind == Kind.ITEMS) {
            for (ItemStack item : items) {
                if (ContentStacks.matches(stack, item.getItem(), item.getMetadata())) { return true; }
            }
            return false;
        }
        if (kind == Kind.NBT) {
            NBTTagCompound tag = stack.getTagCompound();
            return tag != null && tag.getBoolean(name);
        }
        Enchantment enchantment = enchantment();
        return enchantment != null && EnchantmentHelper.getEnchantmentLevel(enchantment, stack) > 0;
    }

    void lose(ItemStack stack, Random random) {
        if (kind == Kind.ITEMS || random.nextFloat() >= loseChance) { return; }
        if (kind == Kind.NBT) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) { return; }
            tag.removeTag(name);
            if (tag.getKeySet().isEmpty()) { stack.setTagCompound(null); }
            return;
        }
        Enchantment enchantment = enchantment();
        if (enchantment == null) { return; }
        Map<Enchantment, Integer> levels = EnchantmentHelper.getEnchantments(stack);
        Integer level = levels.get(enchantment);
        if (level == null) { return; }
        if (level <= 1) { levels.remove(enchantment); }
        else { levels.put(enchantment, level - 1); }
        EnchantmentHelper.setEnchantments(levels, stack);
    }
}
