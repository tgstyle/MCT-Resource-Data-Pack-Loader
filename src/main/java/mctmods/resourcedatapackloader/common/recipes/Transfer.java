package mctmods.resourcedatapackloader.common.recipes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

final class Transfer {
    static final String ENCHANTMENTS = "enchantments";
    static final String NAME = "name";
    static final String DAMAGE = "damage";
    static final String NBT = "nbt";
    private static final String ENCHANTMENT_TAG = "Enchantments";
    private static final String STORED_TAG = "StoredEnchantments";
    private final Predicate<ItemStack> from;
    private final Predicate<ItemStack> source;
    private final Set<String> take;

    Transfer(Predicate<ItemStack> from, Predicate<ItemStack> source, Set<String> take) {
        this.from = from;
        this.source = source;
        this.take = take;
    }

    static void carryAll(List<Transfer> transfers, CraftingContainer matrix, ItemStack result) {
        for (Transfer transfer : transfers) { transfer.carry(matrix, result); }
    }

    private void carry(CraftingContainer matrix, ItemStack result) {
        int base = find(matrix, from, -1);
        int extra = find(matrix, source, base);
        if (base < 0 || extra < 0 || result.isEmpty()) { return; }
        ItemStack kept = matrix.getItem(base);
        ItemStack added = matrix.getItem(extra);
        CompoundTag tag = kept.getTag();
        result.setTag(tag == null ? null : tag.copy());
        if (result.isDamageableItem() && kept.isDamageableItem()) { result.setDamageValue(kept.getDamageValue()); }
        if (take.contains(NBT)) { mergeTag(result, added); }
        if (take.contains(DAMAGE) && result.isDamageableItem() && added.isDamageableItem()) { result.setDamageValue(added.getDamageValue()); }
        if (take.contains(NAME) && added.hasCustomHoverName()) { result.setHoverName(added.getHoverName()); }
        if (take.contains(ENCHANTMENTS)) { mergeEnchantments(result, added); }
    }

    private static int find(CraftingContainer matrix, Predicate<ItemStack> wanted, int skip) {
        for (int slot = 0; slot < matrix.getContainerSize(); slot++) {
            ItemStack stack = matrix.getItem(slot);
            if (slot != skip && !stack.isEmpty() && wanted.test(stack)) { return slot; }
        }
        return -1;
    }

    private static void mergeTag(ItemStack result, ItemStack added) {
        CompoundTag tag = added.getTag();
        if (tag == null) { return; }
        CompoundTag copy = tag.copy();
        copy.remove(ENCHANTMENT_TAG);
        copy.remove(STORED_TAG);
        CompoundTag target = result.getTag();
        if (target == null) { result.setTag(copy); }
        else { target.merge(copy); }
    }

    private static void mergeEnchantments(ItemStack result, ItemStack added) {
        Map<Enchantment, Integer> merged = EnchantmentHelper.getEnchantments(result);
        for (Map.Entry<Enchantment, Integer> wanted : EnchantmentHelper.getEnchantments(added).entrySet()) {
            Enchantment enchantment = wanted.getKey();
            if (!result.is(Items.ENCHANTED_BOOK) && !enchantment.canEnchant(result)) { continue; }
            if (clashes(merged, enchantment)) { continue; }
            merged.merge(enchantment, wanted.getValue(), Math::max);
        }
        EnchantmentHelper.setEnchantments(merged, result);
    }

    private static boolean clashes(Map<Enchantment, Integer> present, Enchantment enchantment) {
        for (Enchantment other : present.keySet()) {
            if (other != enchantment && !enchantment.isCompatibleWith(other)) { return true; }
        }
        return false;
    }
}
