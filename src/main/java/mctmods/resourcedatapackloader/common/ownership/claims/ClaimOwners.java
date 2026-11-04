package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;

public final class ClaimOwners {
    private ClaimOwners() {}

    public static String labelOf(MinecraftServer server, int handle) { return OwnerTable.get(server).labelOf(handle); }

    public static int claimantOf(ServerPlayer player) { return OwnerTable.handleOf(player, ClaimSettings.who()); }

    public static boolean isMember(ServerPlayer player, int claimHandle) {
        if (player instanceof FakePlayer) { return false; }
        OwnerTable table = OwnerTable.get(player.level().getServer());
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        return claim == table.resolve(ScoreboardOwnerSource.current().teamOf(player)) || claim == table.resolve(table.playerHandle(player.getUUID()));
    }

    public static boolean bypasses(ServerPlayer player) { return !(player instanceof FakePlayer) && CommandShared.allowed(player.createCommandSourceStack(), ClaimSettings.opLevel()); }

    public static boolean passes(ServerPlayer player, int claimHandle) {
        if (player instanceof FakePlayer) { return ClaimSettings.fakePlayersAllowed(); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        return OwnerTable.get(player.level().getServer()).resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }
}
