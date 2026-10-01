package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.ContentStacks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import java.util.List;
import javax.annotation.Nullable;

public final class FilterDef {
    public final List<Entry> entries;

    public FilterDef(List<Entry> entries) { this.entries = entries; }

    public int room(ItemStack stack, Iterable<ItemStack> held) {
        if (entries.isEmpty()) { return Integer.MAX_VALUE; }
        for (Entry entry : entries) {
            if (!entry.matches(stack)) { continue; }
            return entry.amount <= 0 ? Integer.MAX_VALUE : Math.max(0, entry.amount - entry.held(held));
        }
        return 0;
    }

    public int room(FluidStack fluid, int held) {
        if (entries.isEmpty()) { return Integer.MAX_VALUE; }
        for (Entry entry : entries) {
            if (!entry.fluid.equals(fluid.getFluid().getName())) { continue; }
            return entry.amount <= 0 ? Integer.MAX_VALUE : Math.max(0, entry.amount - held);
        }
        return 0;
    }

    public static final class Entry {
        public final String oreDict;
        public final String fluid;
        public final int amount;
        private final ResourceLocation owner;
        private final String item;
        @Nullable private ItemStack stack;
        @Nullable private NonNullList<ItemStack> ores;

        public Entry(ResourceLocation owner, String item, String oreDict, String fluid, int amount) {
            this.owner = owner;
            this.item = item;
            this.oreDict = oreDict;
            this.fluid = fluid;
            this.amount = amount;
        }

        public ItemStack stack() {
            if (stack == null) { stack = ContentStacks.parse(owner, item, 1); }
            return stack;
        }

        public boolean matches(ItemStack held) {
            if (oreDict.isEmpty()) { return ContentStacks.matches(held, stack().getItem(), stack().getItemDamage()); }
            if (ores == null) { ores = OreDictionary.getOres(oreDict); }
            return OreDictionary.containsMatch(false, ores, held);
        }

        public int held(Iterable<ItemStack> stacks) {
            int held = 0;
            for (ItemStack one : stacks) {
                if (matches(one)) { held += one.getCount(); }
            }
            return held;
        }
    }
}
