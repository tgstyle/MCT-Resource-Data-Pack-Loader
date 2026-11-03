package mctmods.resourcedatapackloader.common.blocks.compress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public final class CompressedOrder {
    private CompressedOrder() {}

    public static void arrange(NonNullList<ItemStack> list) {
        if (ContentCompressables.sets().isEmpty()) { return; }
        Map<CompressedSet, List<ItemStack>> found = new LinkedHashMap<>();
        for (ItemStack stack : list) {
            if (!(stack.getItem() instanceof ItemBlock)) { continue; }
            ResourceLocation name = Block.REGISTRY.getNameForObject(((ItemBlock) stack.getItem()).getBlock());
            CompressedSet set = ContentCompressables.owner(name);
            if (set != null) { found.computeIfAbsent(set, key -> new ArrayList<>()).add(stack); }
        }
        for (Map.Entry<CompressedSet, List<ItemStack>> entry : found.entrySet()) {
            CompressedSet set = entry.getKey();
            List<ItemStack> layers = entry.getValue();
            ItemStack base = set.stack(0);
            boolean failed = CompressedBase.failed(set);
            if (!failed && (base.isEmpty() || indexOf(list, base) < 0)) { continue; }
            Set<ItemStack> moved = Collections.newSetFromMap(new IdentityHashMap<>());
            moved.addAll(layers);
            list.removeIf(moved::contains);
            if (failed) { continue; }
            layers.sort(Comparator.comparingInt(stack -> layer(set, stack)));
            list.addAll(indexOf(list, base) + 1, layers);
        }
    }

    private static int layer(CompressedSet set, ItemStack stack) { return set.layerOf(Block.REGISTRY.getNameForObject(((ItemBlock) stack.getItem()).getBlock()), stack.getMetadata()); }

    private static int indexOf(List<ItemStack> list, ItemStack base) {
        for (int index = 0; index < list.size(); index++) {
            if (ItemStack.areItemsEqual(list.get(index), base)) { return index; }
        }
        return -1;
    }
}
