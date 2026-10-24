package mctmods.resourcedatapackloader.content.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TableMath {
    public static final int VANILLA_SHELF_CAP = 15;

    private TableMath() {}

    public record Shape(boolean off, int weight, int least, int most) {}

    public static int level(RandomSource rand, int slot, int power, int enchantability, int shelfCap) {
        if (enchantability <= 0) { return 0; }
        int held = Math.min(power, shelfCap);
        int roll = rand.nextInt(8) + 1 + (held >> 1) + rand.nextInt(held + 1);
        if (slot == 0) { return Math.max(roll / 3, 1); }
        return slot == 1 ? roll * 2 / 3 + 1 : Math.max(roll, held * 2);
    }

    public static List<EnchantmentInstance> forSlot(RandomSource rand, int seed, int slot, ItemStack stack, int level, int enchantability, Iterable<Holder<Enchantment>> pool, @Nullable Set<ResourceLocation> allowed, Map<ResourceLocation, Shape> shapes) {
        rand.setSeed(seed + slot);
        List<EnchantmentInstance> list = rolled(rand, stack, level, enchantability, pool, allowed, shapes);
        if (stack.is(Items.BOOK) && list.size() > 1) { list.remove(rand.nextInt(list.size())); }
        return list;
    }

    private static List<EnchantmentInstance> rolled(RandomSource rand, ItemStack stack, int level, int enchantability, Iterable<Holder<Enchantment>> pool, @Nullable Set<ResourceLocation> allowed, Map<ResourceLocation, Shape> shapes) {
        List<EnchantmentInstance> list = new ArrayList<>();
        if (enchantability <= 0) { return list; }
        int power = level + 1 + rand.nextInt(enchantability / 4 + 1) + rand.nextInt(enchantability / 4 + 1);
        float spread = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
        power = Mth.clamp(Math.round(power + power * spread), 1, Integer.MAX_VALUE);
        List<EnchantmentInstance> offered = offered(power, stack, pool, allowed, shapes);
        if (offered.isEmpty()) { return list; }
        WeightedRandom.getRandomItem(rand, offered).ifPresent(list::add);
        while (rand.nextInt(50) <= power) {
            if (!list.isEmpty()) { EnchantmentHelper.filterCompatibleEnchantments(offered, list.getLast()); }
            if (offered.isEmpty()) { break; }
            WeightedRandom.getRandomItem(rand, offered).ifPresent(list::add);
            power /= 2;
        }
        return list;
    }

    private static List<EnchantmentInstance> offered(int power, ItemStack stack, Iterable<Holder<Enchantment>> pool, @Nullable Set<ResourceLocation> allowed, Map<ResourceLocation, Shape> shapes) {
        List<EnchantmentInstance> list = new ArrayList<>();
        for (Holder<Enchantment> holder : pool) {
            ResourceLocation id = holder.unwrapKey().map(ResourceKey::location).orElse(null);
            Shape shape = id == null ? null : shapes.get(id);
            if (shape != null && shape.off) { continue; }
            boolean fits = allowed != null ? id != null && allowed.contains(id) : stack.isPrimaryItemFor(holder);
            if (!fits) { continue; }
            Enchantment enchantment = holder.value();
            int top = shape != null && shape.most > 0 ? shape.most : enchantment.getMaxLevel();
            int bottom = shape != null && shape.least > 0 ? shape.least : enchantment.getMinLevel();
            for (int at = top; at >= bottom; at--) {
                if (power >= enchantment.getMinCost(at) && power <= enchantment.getMaxCost(at)) {
                    list.add(shape != null && shape.weight > 0 ? weighted(holder, at, shape.weight) : new EnchantmentInstance(holder, at));
                    break;
                }
            }
        }
        return list;
    }

    private static EnchantmentInstance weighted(Holder<Enchantment> enchantment, int at, int weight) {
        Weight held = Weight.of(weight);
        return new EnchantmentInstance(enchantment, at) {
            @Override @Nonnull public Weight getWeight() { return held; }
        };
    }
}
