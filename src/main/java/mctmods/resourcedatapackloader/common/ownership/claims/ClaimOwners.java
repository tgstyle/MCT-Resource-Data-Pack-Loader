package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerSource;
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

    @SuppressWarnings("ConstantValue") public static boolean isFake(Player player) { return player instanceof FakePlayer || player instanceof ServerPlayer member && member.connection == null; }

    public static boolean isMember(Player player, int claimHandle) {
        if (!(player instanceof ServerPlayer member) || isFake(player)) { return false; }
        OwnerTable table = OwnerTable.get(member.server);
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        OwnerSource source = ScoreboardOwnerSource.current();
        return claim == table.resolve(source.teamOf(member)) || claim == table.resolve(table.playerHandle(member.getUUID())) || ClaimSettings.alliesPass() && source.allies(member, claim);
    }

    private static boolean carriesMember(Player player, int claimHandle) {
        if (!(player instanceof ServerPlayer fake) || fake.server.getProfileCache() == null) { return false; }
        return fake.server.getProfileCache().get(fake.getUUID()).map(profile -> ScoreboardOwnerSource.current().memberNames(fake.server, claimHandle).stream().anyMatch(profile.getName()::equalsIgnoreCase)).orElse(false);
    }

    public static boolean bypasses(Player player) { return !isFake(player) && player.hasPermissions(ClaimSettings.opLevel()); }

    public static boolean passes(Player player, int claimHandle) {
        if (isFake(player)) { return ClaimSettings.fakePlayersAllowed() || ClaimSettings.fakePlayersOwner() && carriesMember(player, claimHandle); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        return player instanceof ServerPlayer member && OwnerTable.get(member.server).resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }
}
