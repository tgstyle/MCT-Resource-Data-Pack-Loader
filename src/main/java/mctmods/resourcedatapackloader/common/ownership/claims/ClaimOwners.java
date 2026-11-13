package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

public final class ClaimOwners {
    private ClaimOwners() {}

    public static String labelOf(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        return table == null ? "" : table.labelOf(handle);
    }

    public static int claimantOf(EntityPlayer player) { return OwnerTable.handleOf(player, ClaimSettings.who()); }

    public static boolean isFake(EntityPlayer player) { return player instanceof FakePlayer || player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection == null; }

    public static boolean isMember(EntityPlayer player, int claimHandle) {
        OwnerTable table = OwnerTable.get(player.getServer());
        boolean fake = isFake(player);
        if (table == null || fake && !ClaimSettings.fakePlayersOwner()) { return false; }
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        if (claim == table.resolve(ScoreboardOwnerSource.current().teamOf(player)) || claim == table.resolve(table.playerHandle(player.getUniqueID()))) { return true; }
        return !fake && ClaimSettings.alliesPass() && ScoreboardOwnerSource.of(player.getServer(), claim).allies(player, claim);
    }

    public static boolean bypasses(EntityPlayer player) { return !isFake(player) && player.canUseCommand(ClaimSettings.opLevel(), ""); }

    public static boolean passes(EntityPlayer player, int claimHandle) {
        if (isFake(player)) { return ClaimSettings.fakePlayersAllowed() || isMember(player, claimHandle); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        OwnerTable table = OwnerTable.get(player.getServer());
        return table != null && table.resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }
}
