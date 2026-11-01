package mctmods.resourcedatapackloader.common.types;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import java.util.List;
import net.minecraft.block.Block;

public interface IBlockType {
    List<Block> create(BlockDef def);

    default int maxVariants() { return BlockDef.MAX_META; }
}
