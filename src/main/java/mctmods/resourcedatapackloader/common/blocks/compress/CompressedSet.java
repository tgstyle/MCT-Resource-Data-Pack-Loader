package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public final class CompressedSet {
    public final CompressableDef def;
    public final CompressedSource source;
    public final String name;
    public final int layers;

    CompressedSet(CompressableDef def, CompressedSource source) {
        this.def = def;
        this.source = source;
        this.name = def.key().getPath();
        this.layers = def.layers();
    }

    public ResourceLocation blockId(int layer) { return ResourceLocation.fromNamespaceAndPath(def.key().getNamespace(), name + "_compressed_" + layer); }

    public String variantName(int layer) { return "layer_" + layer; }

    public String assetName(int layer) { return name + "_compressed_" + layer; }

    public int layerOf(Block block) { return layerOf(BuiltInRegistries.BLOCK.getKey(block)); }

    public int layerOf(Item item) { return layerOf(BuiltInRegistries.ITEM.getKey(item)); }

    private int layerOf(ResourceLocation id) {
        for (int layer = 1; layer <= layers; layer++) {
            if (blockId(layer).equals(id)) { return layer; }
        }
        return 0;
    }

    public ItemStack stack(int layer) {
        if (layer > 0) {
            Item item = Registered.find(BuiltInRegistries.ITEM, blockId(layer));
            return item == null ? ItemStack.EMPTY : new ItemStack(item);
        }
        Block block = Registered.find(BuiltInRegistries.BLOCK, source.baseBlock);
        Item item = block == null ? Items.AIR : block.asItem();
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }
}
