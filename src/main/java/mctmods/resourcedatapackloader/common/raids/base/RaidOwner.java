package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public final class RaidOwner {
    private RaidOwner() {}

    public static int of(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        return table == null ? 0 : table.resolve(handle);
    }

    public static int of(EntityPlayer player) {
        int handle = OwnerTable.handleOf(player, ClaimSettings.any() ? ClaimSettings.who() : OwnershipSettings.who());
        return of(player.getServer(), handle != 0 ? handle : OwnerTable.handleForPlayer(player.getServer(), player.getUniqueID(), player.getName()));
    }

    public static boolean same(@Nullable MinecraftServer server, int first, int second) {
        int resolved = of(server, first);
        return resolved != 0 && resolved == of(server, second);
    }
}
