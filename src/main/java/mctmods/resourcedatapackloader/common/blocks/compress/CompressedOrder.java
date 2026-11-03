package mctmods.resourcedatapackloader.common.blocks.compress;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class CompressedOrder {
    private CompressedOrder() {}

    public static void arrange(BuildCreativeModeTabContentsEvent event) {
        Map<Item, ItemStack> last = new HashMap<>();
        for (CompressedSet set : ContentCompressables.sets()) {
            if (CompressedBase.failed(set)) { continue; }
            ItemStack base = set.stack(0);
            boolean parent = event.getParentEntries().contains(base);
            boolean search = event.getSearchEntries().contains(base);
            if (!parent && !search) { continue; }
            CreativeModeTab.TabVisibility visibility = parent && search ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : parent ? CreativeModeTab.TabVisibility.PARENT_TAB_ONLY : CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
            ItemStack previous = last.getOrDefault(base.getItem(), base);
            for (int layer = 1; layer <= set.layers; layer++) {
                ItemStack stack = set.stack(layer);
                if (stack.isEmpty()) { continue; }
                boolean present = parent && event.getParentEntries().contains(stack) || search && event.getSearchEntries().contains(stack);
                if (!present) { event.insertAfter(previous, stack, visibility); }
                previous = stack;
            }
            last.put(base.getItem(), previous);
        }
    }

    public static void layers(Item item, Consumer<ItemStack> out) {
        for (CompressedSet set : ContentCompressables.sets()) {
            if (set.stack(0).getItem() != item || CompressedBase.failed(set)) { continue; }
            for (int layer = 1; layer <= set.layers; layer++) {
                ItemStack stack = set.stack(layer);
                if (!stack.isEmpty()) { out.accept(stack); }
            }
        }
    }
}
