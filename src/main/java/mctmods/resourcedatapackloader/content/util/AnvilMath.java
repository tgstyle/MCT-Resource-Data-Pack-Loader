package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.Map;
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
        private boolean repairsWith(ItemStack left, ItemStack right) { return repairWith == null ? left.getItem().isValidRepairItem(left, right) : repairWith.matches(right); }

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

    public static Worked work(ItemStack left, ItemStack right, @Nullable String name, Player player, Rule rule) {
        ItemStack result = left.copy();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        int tax = left.getBaseRepairCost() + right.getBaseRepairCost();
        int cap = AnvilCosts.cap(player.level().isClientSide);
        boolean creative = player.getAbilities().instabuild;
        int price = 0;
        int material = 0;
        boolean book = right.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(right).isEmpty();
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
            Map<Enchantment, Integer> offered = EnchantmentHelper.getEnchantments(right);
            for (Enchantment enchantment : offered.keySet()) {
                if (enchantment == null) { continue; }
                int had = enchantments.getOrDefault(enchantment, 0);
                int level = offered.get(enchantment);
                level = had == level ? level + 1 : Math.max(level, had);
                boolean fits = enchantment.canEnchant(left) || creative || left.is(Items.ENCHANTED_BOOK);
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
            if (!applied || left.getCount() <= 1) { price = (book ? rule.bookCost() : rule.combineCost()).of(price); }
        }
        int naming = 0;
        if (name != null && !Util.isBlank(name)) {
            if (!name.equals(left.getHoverName().getString())) {
                naming = 1;
                result.setHoverName(Component.literal(name));
            }
        }
        else if (left.hasCustomHoverName()) {
            naming = 1;
            result.resetHoverName();
        }
        price += naming;
        if (book && !result.isBookEnchantable(right)) { result = ItemStack.EMPTY; }
        int cost = tax + price;
        if (price <= 0) { result = ItemStack.EMPTY; }
        if (naming == price && naming > 0 && cost >= cap) { cost = cap - 1; }
        if (cost >= cap && !creative) { result = ItemStack.EMPTY; }
        if (result.isEmpty()) { return refused(cost); }
        int penalty = Math.max(result.getBaseRepairCost(), right.getBaseRepairCost());
        if (naming != price || naming == 0) { penalty = rule.grown(penalty); }
        result.setRepairCost(penalty);
        EnchantmentHelper.setEnchantments(enchantments, result);
        return new Worked(result, cost, material);
    }

    private static int fee(Enchantment enchantment) {
        return switch (enchantment.getRarity()) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 4;
            case VERY_RARE -> 8;
        };
    }
}
