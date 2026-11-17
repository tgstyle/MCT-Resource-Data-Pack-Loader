package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class RaidOwner {
    private RaidOwner() {}

    public static int of(@Nullable MinecraftServer server, int handle) { return server == null ? 0 : OwnerTable.get(server).resolve(handle); }

    public static int of(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        int handle = OwnerTable.handleOf(player, ClaimSettings.any() ? ClaimSettings.who() : OwnershipSettings.who());
        return of(server, handle != 0 ? handle : OwnerTable.handleForPlayer(server, player.getUUID(), player.getGameProfile().name()));
    }

    public static boolean same(@Nullable MinecraftServer server, int first, int second) {
        int resolved = of(server, first);
        return resolved != 0 && resolved == of(server, second);
    }
}
