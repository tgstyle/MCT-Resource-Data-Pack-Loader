package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.block.Block;
import java.util.List;

public interface IBlockType {
    List<Block> create(BlockDef def);

    default int maxVariants() { return BlockDef.MAX_META; }
}
