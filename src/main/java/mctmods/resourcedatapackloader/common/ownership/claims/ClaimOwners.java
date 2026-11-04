package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.ownership.ScoreboardOwnerSource;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

public final class ClaimOwners {
    private ClaimOwners() {}

    public static String labelOf(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        return table == null ? "" : table.labelOf(handle);
    }

    public static int claimantOf(EntityPlayer player) { return OwnerTable.handleOf(player, ClaimSettings.who()); }

    public static boolean isMember(EntityPlayer player, int claimHandle) {
        OwnerTable table = OwnerTable.get(player.getServer());
        if (table == null || player instanceof FakePlayer) { return false; }
        int claim = table.resolve(claimHandle);
        if (claim == 0) { return false; }
        return claim == table.resolve(ScoreboardOwnerSource.current().teamOf(player)) || claim == table.resolve(table.playerHandle(player.getUniqueID()));
    }

    public static boolean bypasses(EntityPlayer player) { return !(player instanceof FakePlayer) && player.canUseCommand(ClaimSettings.opLevel(), ""); }

    public static boolean passes(EntityPlayer player, int claimHandle) {
        if (player instanceof FakePlayer) { return ClaimSettings.fakePlayersAllowed(); }
        if (bypasses(player) || isMember(player, claimHandle)) { return true; }
        OwnerTable table = OwnerTable.get(player.getServer());
        return table != null && table.resolve(claimHandle) == 0 && ClaimSettings.orphansOpen();
    }
}
