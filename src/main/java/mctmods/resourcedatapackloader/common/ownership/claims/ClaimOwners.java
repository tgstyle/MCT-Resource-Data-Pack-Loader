package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.neoforged.neoforge.common.util.FakePlayer;

public final class ClaimOwners {
    private ClaimOwners() {}

    public static String labelOf(MinecraftServer server, int handle) { return OwnerTable.get(server).labelOf(handle); }

    public static int claimantOf(ServerPlayer player) { return OwnerTable.handleOf(player, ClaimSettings.who()); }

    public static boolean isFake(ServerPlayer player) { return player instanceof FakePlayer || player.getClass() != ServerPlayer.class; }

    public static boolean isMember(ServerPlayer player, int claimHandle) {
        if (isFake(player)) { return false; }
        MinecraftServer server = player.level().getServer();
        OwnerTable table = OwnerTable.get(server);
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        if (claim == table.resolve(ScoreboardOwnerSource.current().teamOf(player)) || claim == table.resolve(table.playerHandle(player.getUUID()))) { return true; }
        return ClaimSettings.alliesPass() && ScoreboardOwnerSource.of(server, claim).allies(player, claim);
    }

    public static boolean bypasses(ServerPlayer player) { return !isFake(player) && CommandShared.allowed(player.createCommandSourceStack(), ClaimSettings.opLevel()); }

    public static boolean passes(ServerPlayer player, int claimHandle) {
        if (isFake(player)) { return ClaimSettings.fakePlayersAllowed() || ClaimSettings.fakePlayersOwner() && fakeOfOwner(player, claimHandle); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        return OwnerTable.get(player.level().getServer()).resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }

    private static boolean fakeOfOwner(ServerPlayer player, int claimHandle) {
        MinecraftServer server = player.level().getServer();
        int claim = OwnerTable.get(server).resolve(claimHandle);
        if (claim == 0) { return false; }
        String name = server.services().nameToIdCache().get(player.getUUID()).map(NameAndId::name).orElse("");
        for (String member : ScoreboardOwnerSource.of(server, claim).memberNames(claim)) {
            if (member.equalsIgnoreCase(name) || member.equalsIgnoreCase(player.getUUID().toString())) { return true; }
        }
        return false;
    }
}
