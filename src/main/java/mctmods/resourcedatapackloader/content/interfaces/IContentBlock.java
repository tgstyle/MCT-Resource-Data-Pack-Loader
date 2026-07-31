package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.item.Item;
import javax.annotation.Nullable;

public interface IContentBlock {
    BlockDef getDef();

    @Nullable Item createItem();
}
