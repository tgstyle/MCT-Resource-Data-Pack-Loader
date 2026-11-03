package mctmods.resourcedatapackloader.common.blocks.compress;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public final class CompressedOrder {
    private CompressedOrder() {}

    public static void arrange(BuildCreativeModeTabContentsEvent event) {
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
        Map<Item, ItemStack> last = new HashMap<>();
        for (CompressedSet set : ContentCompressables.sets()) {
            if (CompressedBase.failed(set)) { continue; }
            ItemStack base = set.stack(0);
            CreativeModeTab.TabVisibility visibility = entries.get(base);
            if (visibility == null) { continue; }
            ItemStack previous = last.getOrDefault(base.getItem(), base);
            for (int layer = 1; layer <= set.layers; layer++) {
                ItemStack stack = set.stack(layer);
                if (stack.isEmpty()) { continue; }
                entries.putAfter(previous, stack, visibility);
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
