package mctmods.resourcedatapackloader.common.ownership;

import net.minecraft.server.level.ServerPlayer;

public interface OwnerSource {
    int teamOf(ServerPlayer player);
}
