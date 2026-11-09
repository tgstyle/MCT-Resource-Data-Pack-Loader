package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

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

    public int layerOf(Block block) { return layerOf(ForgeRegistries.BLOCKS.getKey(block)); }

    public int layerOf(Item item) { return layerOf(ForgeRegistries.ITEMS.getKey(item)); }

    public int low() { return source.fromItem ? 0 : 1; }

    public int oreLevel(int layer) { return layer + 1 - low(); }

    public int scale(int layer) { return layer + 2 - low(); }

    public ItemStack source() { return stack(low() - 1); }

    private int layerOf(ResourceLocation id) {
        for (int layer = low(); layer <= layers; layer++) {
            if (blockId(layer).equals(id)) { return layer; }
        }
        return low() - 1;
    }

    public ItemStack stack(int layer) {
        if (layer >= low()) {
            Item item = Registered.find(ForgeRegistries.ITEMS, blockId(layer));
            return item == null ? ItemStack.EMPTY : new ItemStack(item);
        }
        if (source.fromItem) {
            Item item = Registered.find(ForgeRegistries.ITEMS, source.base);
            return item == null ? ItemStack.EMPTY : new ItemStack(item);
        }
        Block block = Registered.find(ForgeRegistries.BLOCKS, source.base);
        Item item = block == null ? Items.AIR : block.asItem();
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }
}
