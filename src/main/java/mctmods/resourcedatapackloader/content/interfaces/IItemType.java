package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.def.ItemDef;

import net.minecraft.item.Item;
import java.util.List;

public interface IItemType {
    List<Item> create(ItemDef def);
}
