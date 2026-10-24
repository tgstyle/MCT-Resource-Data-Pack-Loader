package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import javax.annotation.Nullable;

public final class AnvilMath {
    public static final Cost VANILLA_COST = new Cost(-1, 1.0D);
    public static final int VANILLA_GROWTH = 2;
    public static final int NO_GROWTH = -1;
    private static final int VANILLA_UNITS = 4;
    private static final int MERGE_BONUS = 12;

    private AnvilMath() {}

    public static final class Cost {
        private final int flat;
        private final double multiplier;

        public Cost(int flat, double multiplier) {
            this.flat = flat;
            this.multiplier = multiplier;
        }

        private int of(int vanilla) {
            if (vanilla <= 0) { return vanilla; }
            if (flat >= 0) { return flat; }
            return multiplier == 1.0D ? vanilla : Math.max(1, (int) Math.round(vanilla * multiplier));
        }
    }

    public static final class Rule {
        @Nullable private final ItemMatch repairWith;
        private final double perUnit;
        private final Cost repairCost;
        private final Cost combineCost;
        private final Cost bookCost;
        private final int growth;
        private final boolean repair;
        private final boolean combine;

        public Rule(@Nullable ItemMatch repairWith, double perUnit, Cost repairCost, Cost combineCost, Cost bookCost, int growth, boolean repair, boolean combine) {
            this.repairWith = repairWith;
            this.perUnit = perUnit;
            this.repairCost = repairCost;
            this.combineCost = combineCost;
            this.bookCost = bookCost;
            this.growth = growth;
            this.repair = repair;
            this.combine = combine;
        }

        private boolean repairsWith(ItemStack left, ItemStack right) { return repairWith == null ? left.getItem().getIsRepairable(left, right) : repairWith.matches(right); }

        private int step(ItemStack worked) {
            int unit = perUnit > 0.0D ? (int) (worked.getMaxDamage() * perUnit) : worked.getMaxDamage() / VANILLA_UNITS;
            return Math.min(worked.getItemDamage(), unit);
        }

        private int grown(int cost) {
            if (growth == NO_GROWTH) { return cost; }
            return (int) Math.min((long) cost * growth + 1L, Integer.MAX_VALUE);
        }
    }

    public static final class Worked {
        public final ItemStack output;
        public final int cost;
        public final int material;

        private Worked(ItemStack output, int cost, int material) {
            this.output = output;
            this.cost = cost;
            this.material = material;
        }
    }

    private static Worked refused(int cost) { return new Worked(ItemStack.EMPTY, cost, 0); }

    public static Worked work(ItemStack left, ItemStack right, @Nullable String name, EntityPlayer player, Rule rule) {
        ItemStack result = left.copy();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        int tax = left.getRepairCost() + right.getRepairCost();
        int cap = AnvilCosts.cap(player.world.isRemote);
        boolean creative = player.capabilities.isCreativeMode;
        int price = 0;
        int material = 0;
        boolean book = right.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(right).isEmpty();
        if (result.isItemStackDamageable() && rule.repairsWith(left, right)) {
            if (!rule.repair) { return refused(0); }
            int step = rule.step(result);
            if (step <= 0) { return refused(0); }
            int used;
            for (used = 0; step > 0 && used < right.getCount(); used++) {
                result.setItemDamage(result.getItemDamage() - step);
                price++;
                step = rule.step(result);
            }
            material = used;
            price = rule.repairCost.of(price);
        }
        else {
            if (!book && (result.getItem() != right.getItem() || !result.isItemStackDamageable())) { return refused(0); }
            if (!book && !rule.combine) { return refused(0); }
            if (result.isItemStackDamageable() && !book) {
                int kept = left.getMaxDamage() - left.getItemDamage();
                int added = right.getMaxDamage() - right.getItemDamage() + result.getMaxDamage() * MERGE_BONUS / 100;
                int damage = Math.max(0, result.getMaxDamage() - (kept + added));
                if (damage < result.getItemDamage()) {
                    result.setItemDamage(damage);
                    price += 2;
                }
            }
            boolean applied = false;
            boolean clashed = false;
            Map<Enchantment, Integer> offered = EnchantmentHelper.getEnchantments(right);
            for (Enchantment enchantment : offered.keySet()) {
                if (enchantment == null) { continue; }
                int had = enchantments.getOrDefault(enchantment, 0);
                int level = offered.get(enchantment);
                level = had == level ? level + 1 : Math.max(level, had);
                boolean fits = enchantment.canApply(left) || creative || left.getItem() == Items.ENCHANTED_BOOK;
                for (Enchantment other : enchantments.keySet()) {
                    if (other != enchantment && !enchantment.isCompatibleWith(other)) {
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
                enchantments.put(enchantment, level);
                int fee = fee(enchantment);
                if (book) { fee = Math.max(1, fee / 2); }
                price += fee * level;
                if (left.getCount() > 1) { price = cap; }
            }
            if (clashed && !applied) { return refused(0); }
            if (!applied || left.getCount() <= 1) { price = (book ? rule.bookCost : rule.combineCost).of(price); }
        }
        int naming = 0;
        if (StringUtils.isBlank(name)) {
            if (left.hasDisplayName()) {
                naming = 1;
                result.clearCustomName();
            }
        }
        else if (!name.equals(left.getDisplayName())) {
            naming = 1;
            result.setStackDisplayName(name);
        }
        price += naming;
        if (book && !result.getItem().isBookEnchantable(result, right)) { result = ItemStack.EMPTY; }
        int cost = tax + price;
        if (price <= 0) { result = ItemStack.EMPTY; }
        if (naming == price && naming > 0 && cost >= cap) { cost = cap - 1; }
        if (cost >= cap && !creative) { result = ItemStack.EMPTY; }
        if (result.isEmpty()) { return refused(cost); }
        int penalty = Math.max(result.getRepairCost(), right.getRepairCost());
        if (naming != price || naming == 0) { penalty = rule.grown(penalty); }
        result.setRepairCost(penalty);
        EnchantmentHelper.setEnchantments(enchantments, result);
        return new Worked(result, cost, material);
    }

    private static int fee(Enchantment enchantment) {
        switch (enchantment.getRarity()) {
            case COMMON: return 1;
            case UNCOMMON: return 2;
            case RARE: return 4;
            case VERY_RARE: return 8;
            default: return 0;
        }
    }
}
