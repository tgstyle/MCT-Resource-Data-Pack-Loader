package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;

public final class ClaimOwners {
    private ClaimOwners() {}

    public static String labelOf(MinecraftServer server, int handle) { return OwnerTable.get(server).labelOf(handle); }

    public static int claimantOf(ServerPlayer player) { return OwnerTable.handleOf(player, ClaimSettings.who()); }

    public static boolean isFake(Player player) { return player instanceof FakePlayer || player instanceof ServerPlayer member && unconnected(member); }

    @SuppressWarnings("ConstantValue") private static boolean unconnected(ServerPlayer player) { return player.connection == null; }

    public static boolean isMember(Player player, int claimHandle) {
        if (!(player instanceof ServerPlayer member) || isFake(player)) { return false; }
        OwnerTable table = OwnerTable.get(member.server);
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        if (claim == table.resolve(ScoreboardOwnerSource.current().teamOf(member)) || claim == table.resolve(table.playerHandle(member.getUUID()))) { return true; }
        return ClaimSettings.alliesPass() && ScoreboardOwnerSource.forSource(table.sourceOf(claim)).allies(member, claim);
    }

    private static boolean carriesMember(ServerPlayer fake, int claimHandle) {
        MinecraftServer server = fake.server;
        OwnerTable table = OwnerTable.get(server);
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        if (claim == table.resolve(table.playerHandle(fake.getUUID()))) { return true; }
        if (server.getProfileCache() == null) { return false; }
        return server.getProfileCache().get(fake.getUUID()).map(profile -> ScoreboardOwnerSource.forSource(table.sourceOf(claim)).memberNames(server, claim).contains(profile.getName())).orElse(false);
    }

    public static boolean bypasses(Player player) { return !isFake(player) && player.hasPermissions(ClaimSettings.opLevel()); }

    public static boolean passes(Player player, int claimHandle) {
        if (isFake(player)) { return ClaimSettings.fakePlayersAllowed() || ClaimSettings.fakePlayersOwner() && player instanceof ServerPlayer fake && carriesMember(fake, claimHandle); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        return player instanceof ServerPlayer member && OwnerTable.get(member.server).resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }
}
