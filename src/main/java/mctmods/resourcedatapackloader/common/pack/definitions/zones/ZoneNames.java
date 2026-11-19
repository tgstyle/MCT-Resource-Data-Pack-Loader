package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import mctmods.resourcedatapackloader.common.util.ItemSelection;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public final class ZoneNames {
    private final ItemSelection selection;
    @Nullable private Set<ResourceLocation> blocks;
    @Nullable private Set<ResourceLocation> items;

    public ZoneNames(ItemSelection selection) { this.selection = selection; }

    public boolean hasBlock(BlockState state) {
        if (blocks == null) { blocks = selection.blocks(); }
        if (blocks.contains(ForgeRegistries.BLOCKS.getKey(state.getBlock()))) { return true; }
        for (ResourceLocation tag : selection.tags()) {
            if (state.is(TagKey.create(Registries.BLOCK, tag))) { return true; }
        }
        return false;
    }

    public boolean hasItem(ItemStack stack) {
        if (items == null) { items = selection.items(); }
        if (items.contains(ForgeRegistries.ITEMS.getKey(stack.getItem()))) { return true; }
        for (ResourceLocation tag : selection.tags()) {
            if (stack.is(TagKey.create(Registries.ITEM, tag))) { return true; }
        }
        return false;
    }
}
