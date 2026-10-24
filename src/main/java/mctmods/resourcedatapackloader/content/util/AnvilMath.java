package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import javax.annotation.Nullable;

public final class AnvilMath {
    public static final Cost VANILLA_COST = new Cost(-1, 1.0D);
    public static final int VANILLA_GROWTH = 2;
    public static final int NO_GROWTH = -1;
    private static final int VANILLA_UNITS = 4;
    private static final int MERGE_BONUS = 12;

    private AnvilMath() {}

    public record Cost(int flat, double multiplier) {
        private int of(int vanilla) {
            if (vanilla <= 0) { return vanilla; }
            if (flat >= 0) { return flat; }
            return multiplier == 1.0D ? vanilla : Math.max(1, (int) Math.round(vanilla * multiplier));
        }
    }

    public record Rule(@Nullable ItemMatch repairWith, double perUnit, Cost repairCost, Cost combineCost, Cost bookCost, int growth, boolean repair, boolean combine) {
        private boolean repairsWith(ItemStack left, ItemStack right) { return repairWith == null ? left.isValidRepairItem(right) : repairWith.matches(right); }

        private int step(ItemStack worked) {
            int unit = perUnit > 0.0D ? (int) (worked.getMaxDamage() * perUnit) : worked.getMaxDamage() / VANILLA_UNITS;
            return Math.min(worked.getDamageValue(), unit);
        }

        private int grown(int cost) {
            if (growth == NO_GROWTH) { return cost; }
            return (int) Math.min((long) cost * growth + 1L, Integer.MAX_VALUE);
        }
    }

    public record Worked(ItemStack output, int cost, int material) {}

    private static Worked refused(int cost) { return new Worked(ItemStack.EMPTY, cost, 0); }

    public static boolean works(ItemStack left) { return EnchantmentHelper.canStoreEnchantments(left); }

    public static Worked work(ItemStack left, ItemStack right, @Nullable String name, Player player, Rule rule) {
        ItemStack result = left.copy();
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        long tax = (long) left.getOrDefault(DataComponents.REPAIR_COST, 0) + right.getOrDefault(DataComponents.REPAIR_COST, 0);
        int cap = AnvilCosts.cap(player.level().isClientSide());
        boolean creative = player.getAbilities().instabuild;
        int price = 0;
        int material = 0;
        boolean book = right.has(DataComponents.STORED_ENCHANTMENTS);
        if (result.isDamageableItem() && rule.repairsWith(left, right)) {
            if (!rule.repair()) { return refused(0); }
            int step = rule.step(result);
            if (step <= 0) { return refused(0); }
            int used;
            for (used = 0; step > 0 && used < right.getCount(); used++) {
                result.setDamageValue(result.getDamageValue() - step);
                price++;
                step = rule.step(result);
            }
            material = used;
            price = rule.repairCost().of(price);
        }
        else {
            if (!book && (!result.is(right.getItem()) || !result.isDamageableItem())) { return refused(0); }
            if (!book && !rule.combine()) { return refused(0); }
            if (result.isDamageableItem() && !book) {
                int kept = left.getMaxDamage() - left.getDamageValue();
                int added = right.getMaxDamage() - right.getDamageValue() + result.getMaxDamage() * MERGE_BONUS / 100;
                int damage = Math.max(0, result.getMaxDamage() - (kept + added));
                if (damage < result.getDamageValue()) {
                    result.setDamageValue(damage);
                    price += 2;
                }
            }
            boolean applied = false;
            boolean clashed = false;
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(right).entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                int had = enchantments.getLevel(holder);
                int level = entry.getIntValue();
                level = had == level ? level + 1 : Math.max(level, had);
                Enchantment enchantment = holder.value();
                boolean fits = left.supportsEnchantment(holder) || creative;
                for (Holder<Enchantment> other : enchantments.keySet()) {
                    if (!other.equals(holder) && !Enchantment.areCompatible(holder, other)) {
                        fits = false;
                        price++;
                    }
                }
                if (!fits) {
                    clashed = true;
                    continue;
                }
                applied = true;
                level = Math.min(level, enchantment.getMaxLevel());
                enchantments.set(holder, level);
                int fee = enchantment.getAnvilCost();
                if (book) { fee = Math.max(1, fee / 2); }
                price += fee * level;
                if (left.getCount() > 1) { price = cap; }
            }
            if (clashed && !applied) { return refused(0); }
            if (!applied || left.getCount() <= 1) { price = (book ? rule.bookCost() : rule.combineCost()).of(price); }
        }
        int naming = 0;
        if (name != null && !StringUtil.isBlank(name)) {
            if (!name.equals(left.getHoverName().getString())) {
                naming = 1;
                result.set(DataComponents.CUSTOM_NAME, Component.literal(name));
            }
        }
        else if (left.has(DataComponents.CUSTOM_NAME)) {
            naming = 1;
            result.remove(DataComponents.CUSTOM_NAME);
        }
        price += naming;
        int cost = price <= 0 ? 0 : Math.clamp(tax + price, 0, Integer.MAX_VALUE);
        if (price <= 0) { result = ItemStack.EMPTY; }
        if (naming == price && naming > 0 && cost >= cap) { cost = cap - 1; }
        if (cost >= cap && !player.hasInfiniteMaterials()) { result = ItemStack.EMPTY; }
        if (result.isEmpty()) { return refused(cost); }
        int penalty = Math.max(result.getOrDefault(DataComponents.REPAIR_COST, 0), right.getOrDefault(DataComponents.REPAIR_COST, 0));
        if (naming != price || naming == 0) { penalty = rule.grown(penalty); }
        result.set(DataComponents.REPAIR_COST, penalty);
        EnchantmentHelper.setEnchantments(result, enchantments.toImmutable());
        return new Worked(result, cost, material);
    }
}
