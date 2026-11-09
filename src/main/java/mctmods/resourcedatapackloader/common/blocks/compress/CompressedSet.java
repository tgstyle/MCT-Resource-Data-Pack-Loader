package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.core.util.Registries;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class CompressedSet {
    public final CompressableDef def;
    public final CompressedSource source;
    public final String name;
    public final int layers;

    CompressedSet(CompressableDef def, CompressedSource source) {
        this.def = def;
        this.source = source;
        this.name = def.key.getPath();
        this.layers = def.layers;
    }

    public int low() { return source.fromItem ? 0 : 1; }

    public int oreLevel(int layer) { return layer + 1 - low(); }

    public int scale(int layer) { return layer + 2 - low(); }

    public ItemStack source() { return stack(low() - 1); }

    public int blockCount() { return (layers - low() + BlockDef.MAX_META) / BlockDef.MAX_META; }

    public ResourceLocation blockName(int layer) {
        int index = (layer - low()) / BlockDef.MAX_META;
        return new ResourceLocation(def.key.getNamespace(), name + "_compressed" + (index == 0 ? "" : "_" + (index + 1)));
    }

    public int meta(int layer) { return (layer - low()) % BlockDef.MAX_META; }

    public String variantName(int layer) { return "layer_" + layer; }

    public String assetName(int layer) { return name + "_compressed_" + layer; }

    public int layerOf(ResourceLocation block, int meta) {
        for (int index = 0; index < blockCount(); index++) {
            if (blockName(index * BlockDef.MAX_META + low()).equals(block)) { return Math.min(layers, index * BlockDef.MAX_META + Math.max(0, meta) + low()); }
        }
        return low() - 1;
    }

    public ItemStack stack(int layer) {
        if (layer >= low()) {
            Item item = Registries.find(ForgeRegistries.ITEMS, blockName(layer));
            return item == null ? ItemStack.EMPTY : new ItemStack(item, 1, meta(layer));
        }
        if (source.fromItem) {
            Item item = Registries.find(ForgeRegistries.ITEMS, source.base);
            return item == null ? ItemStack.EMPTY : new ItemStack(item, 1, source.baseMeta);
        }
        Block block = Registries.find(ForgeRegistries.BLOCKS, source.base);
        Item item = block == null ? Items.AIR : Item.getItemFromBlock(block);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item, 1, source.baseMeta);
    }
}
