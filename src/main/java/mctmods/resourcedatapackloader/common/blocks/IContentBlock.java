package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import javax.annotation.Nullable;
import net.minecraft.item.Item;

public interface IContentBlock {
    BlockDef getDef();

    @Nullable Item createItem();
}
