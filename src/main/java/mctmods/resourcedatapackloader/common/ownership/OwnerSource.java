package mctmods.resourcedatapackloader.common.ownership;

import net.minecraft.entity.player.EntityPlayer;

public interface OwnerSource {
    int teamOf(EntityPlayer player);
}
