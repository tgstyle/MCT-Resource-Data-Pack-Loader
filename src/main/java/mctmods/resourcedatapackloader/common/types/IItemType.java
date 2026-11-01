package mctmods.resourcedatapackloader.common.types;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;

import java.util.List;
import net.minecraft.item.Item;

public interface IItemType { List<Item> create(ItemDef def); }
