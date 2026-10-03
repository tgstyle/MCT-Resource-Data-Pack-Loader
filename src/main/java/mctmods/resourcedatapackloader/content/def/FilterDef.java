package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.ContentStacks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import java.util.List;
import javax.annotation.Nullable;

public record FilterDef(List<Entry> entries) {
    public static final FilterDef NONE = new FilterDef(List.of());

    public int room(ItemStack stack, Iterable<ItemStack> held) {
        if (entries.isEmpty()) { return Integer.MAX_VALUE; }
        for (Entry entry : entries) {
            if (!entry.matches(stack)) { continue; }
            return entry.amount <= 0 ? Integer.MAX_VALUE : Math.max(0, entry.amount - entry.held(held));
        }
        return 0;
    }

    public int room(Fluid fluid, int held) {
        if (entries.isEmpty()) { return Integer.MAX_VALUE; }
        Identifier name = BuiltInRegistries.FLUID.getKey(fluid);
        for (Entry entry : entries) {
            if (entry.fluid == null || !entry.fluid.equals(name)) { continue; }
            return entry.amount <= 0 ? Integer.MAX_VALUE : Math.max(0, entry.amount - held);
        }
        return 0;
    }

    public static final class Entry {
        private final Identifier owner;
        private final String item;
        @Nullable private final TagKey<Item> tag;
        @Nullable private final Identifier fluid;
        private final int amount;
        @Nullable private Item found;
        private boolean looked;

        public Entry(Identifier owner, String item, @Nullable TagKey<Item> tag, @Nullable Identifier fluid, int amount) {
            this.owner = owner;
            this.item = item;
            this.tag = tag;
            this.fluid = fluid;
            this.amount = amount;
        }

        @Nullable private Item item() {
            if (!looked) {
                looked = true;
                found = item.isEmpty() ? null : ContentStacks.find(owner, item);
            }
            return found;
        }

        public boolean matches(ItemStack held) {
            if (held.isEmpty()) { return false; }
            return tag == null ? held.getItem() == item() : held.is(tag);
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
