package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimDissolve;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public final class TeamDissolve {
    public interface Liveness {
        boolean isLive(MinecraftServer server, String key);
    }

    private static final Map<String, Liveness> TESTS = new ConcurrentHashMap<>();
    private static int counter;

    static { TESTS.put(OwnerRecord.SOURCE_SCOREBOARD, (server, key) -> ContentTeams.board(server).getTeamNames().contains(key)); }

    private TeamDissolve() {}

    public static void register(String source, Liveness test) { TESTS.put(source, test); }

    public static void tick(MinecraftServer server) {
        if (++counter < OwnershipSettings.checkTicks()) { return; }
        counter = 0;
        check(server);
    }

    public static void check(MinecraftServer server) {
        OwnerTable table = OwnerTable.get(server);
        if (table == null) { return; }
        for (Map.Entry<Integer, OwnerRecord> team : table.liveTeams().entrySet()) {
            Liveness test = TESTS.get(team.getValue().source);
            if (test == null || test.isLive(server, team.getValue().key)) { continue; }
            dissolve(table, team.getKey(), team.getValue().source);
        }
        if (OwnershipSettings.joinKeep()) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { onLogin(player); }
    }

    public static void dissolveForeign(MinecraftServer server, String source, String key) {
        OwnerTable table = OwnerTable.get(server);
        int handle = table == null ? 0 : table.liveTeamHandle(source, key);
        if (handle != 0) { dissolve(table, handle, source); }
    }

    private static void dissolve(OwnerTable table, int handle, String source) {
        table.markDissolved(handle);
        ClaimDissolve.apply(handle, OwnerRecord.SOURCE_FTB.equals(source) ? OwnershipSettings.ftbDissolveMode(ClaimSettings.dissolveMode()) : ClaimSettings.dissolveMode());
    }

    public static void onLogin(EntityPlayerMP player) {
        if (OwnershipSettings.joinKeep()) { return; }
        OwnerTable table = OwnerTable.get(player.getServer());
        if (table == null) { return; }
        int playerHandle = table.playerHandle(player.getUniqueID());
        if (playerHandle == 0) { return; }
        int teamHandle = ScoreboardOwnerSource.current().teamOf(player);
        if (teamHandle != 0 && table.isLive(teamHandle)) { table.moveToTeam(playerHandle, teamHandle); }
    }
}
