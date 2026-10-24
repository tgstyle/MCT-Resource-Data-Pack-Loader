package mctmods.resourcedatapackloader.content.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class TableMath {
    public static final int VANILLA_SHELF_CAP = 15;

    private TableMath() {}

    public static final class Shape {
        private final boolean off;
        private final int weight;
        private final int least;
        private final int most;

        public Shape(boolean off, int weight, int least, int most) {
            this.off = off;
            this.weight = weight;
            this.least = least;
            this.most = most;
        }
    }

    public static int level(Random rand, int slot, int power, int enchantability, int shelfCap) {
        if (enchantability <= 0) { return 0; }
        int held = Math.min(power, shelfCap);
        int roll = rand.nextInt(8) + 1 + (held >> 1) + rand.nextInt(held + 1);
        if (slot == 0) { return Math.max(roll / 3, 1); }
        return slot == 1 ? roll * 2 / 3 + 1 : Math.max(roll, held * 2);
    }

    public static List<EnchantmentData> forSlot(Random rand, int seed, int slot, ItemStack stack, int level, int enchantability, @Nullable Set<Enchantment> allowed, Map<Enchantment, Shape> shapes) {
        rand.setSeed(seed + slot);
        List<EnchantmentData> list = rolled(rand, stack, level, enchantability, allowed, shapes);
        if (stack.getItem() == Items.BOOK && list.size() > 1) { list.remove(rand.nextInt(list.size())); }
        return list;
    }

    private static List<EnchantmentData> rolled(Random rand, ItemStack stack, int level, int enchantability, @Nullable Set<Enchantment> allowed, Map<Enchantment, Shape> shapes) {
        List<EnchantmentData> list = new ArrayList<>();
        if (enchantability <= 0) { return list; }
        int power = level + 1 + rand.nextInt(enchantability / 4 + 1) + rand.nextInt(enchantability / 4 + 1);
        float spread = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
        power = MathHelper.clamp(Math.round(power + power * spread), 1, Integer.MAX_VALUE);
        List<EnchantmentData> offered = offered(power, stack, allowed, shapes);
        if (offered.isEmpty()) { return list; }
        list.add(WeightedRandom.getRandomItem(rand, offered));
        while (rand.nextInt(50) <= power) {
            EnchantmentHelper.removeIncompatible(offered, list.get(list.size() - 1));
            if (offered.isEmpty()) { break; }
            list.add(WeightedRandom.getRandomItem(rand, offered));
            power /= 2;
        }
        return list;
    }

    private static List<EnchantmentData> offered(int power, ItemStack stack, @Nullable Set<Enchantment> allowed, Map<Enchantment, Shape> shapes) {
        List<EnchantmentData> list = new ArrayList<>();
        boolean book = stack.getItem() == Items.BOOK;
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            Shape shape = shapes.get(enchantment);
            if (shape != null && shape.off || enchantment.isTreasureEnchantment()) { continue; }
            boolean fits = allowed != null ? allowed.contains(enchantment) : enchantment.canApplyAtEnchantingTable(stack) || book && enchantment.isAllowedOnBooks();
            if (!fits) { continue; }
            int top = shape != null && shape.most > 0 ? shape.most : enchantment.getMaxLevel();
            int bottom = shape != null && shape.least > 0 ? shape.least : enchantment.getMinLevel();
            for (int at = top; at >= bottom; at--) {
                if (power >= enchantment.getMinEnchantability(at) && power <= enchantment.getMaxEnchantability(at)) {
                    EnchantmentData data = new EnchantmentData(enchantment, at);
                    if (shape != null && shape.weight > 0) { data.itemWeight = shape.weight; }
                    list.add(data);
                    break;
                }
            }
        }
        return list;
    }
}
