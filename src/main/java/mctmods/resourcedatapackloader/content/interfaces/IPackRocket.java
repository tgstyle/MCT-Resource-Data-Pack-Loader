package mctmods.resourcedatapackloader.content.interfaces;

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IPackRocket {
    void packCargo(NonNullList<ItemStack> cargo);

    void packType(EnumRocketType type);
}
