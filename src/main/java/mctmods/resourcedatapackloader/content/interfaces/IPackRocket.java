package mctmods.resourcedatapackloader.content.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IPackRocket {

    void packCargo(NonNullList<ItemStack> cargo);
}
