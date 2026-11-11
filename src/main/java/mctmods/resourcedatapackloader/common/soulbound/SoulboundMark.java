package mctmods.resourcedatapackloader.common.soulbound;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

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
        Enchantment enchantment = Registered.find(ForgeRegistries.ENCHANTMENTS, ResourceLocation.tryParse(name));
        if (enchantment == null && !unknownLogged) {
            unknownLogged = true;
            ContentLog.LOGGER.error("Soulbound file {} names unknown enchantment '{}', so it marks nothing", key, name);
        }
        return enchantment;
    }

    boolean matches(ItemStack stack) {
        if (kind == Kind.ITEMS) {
            for (ItemStack item : items) {
                if (ContentStacks.matches(stack, item)) { return true; }
            }
            return false;
        }
        if (kind == Kind.NBT) {
            CompoundTag tag = stack.getTag();
            return tag != null && tag.getBoolean(name);
        }
        Enchantment enchantment = enchantment();
        return enchantment != null && stack.getEnchantmentLevel(enchantment) > 0;
    }

    void lose(ItemStack stack, RandomSource random) {
        if (kind == Kind.ITEMS || random.nextFloat() >= loseChance) { return; }
        if (kind == Kind.NBT) {
            CompoundTag tag = stack.getTag();
            if (tag == null) { return; }
            tag.remove(name);
            if (tag.isEmpty()) { stack.setTag(null); }
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
