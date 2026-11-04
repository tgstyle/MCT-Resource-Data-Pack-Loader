package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;

public final class ClaimOwners {
    private ClaimOwners() {}

    public static String labelOf(MinecraftServer server, int handle) { return OwnerTable.get(server).labelOf(handle); }

    public static int claimantOf(ServerPlayer player) { return OwnerTable.handleOf(player, ClaimSettings.who()); }

    public static boolean isMember(Player player, int claimHandle) {
        if (!(player instanceof ServerPlayer member) || player instanceof FakePlayer) { return false; }
        OwnerTable table = OwnerTable.get(member.server);
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        return claim == table.resolve(ScoreboardOwnerSource.current().teamOf(member)) || claim == table.resolve(table.playerHandle(member.getUUID()));
    }

    public static boolean bypasses(Player player) { return !(player instanceof FakePlayer) && player.hasPermissions(ClaimSettings.opLevel()); }

    public static boolean passes(Player player, int claimHandle) {
        if (player instanceof FakePlayer) { return ClaimSettings.fakePlayersAllowed(); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        return player instanceof ServerPlayer member && OwnerTable.get(member.server).resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }
}
