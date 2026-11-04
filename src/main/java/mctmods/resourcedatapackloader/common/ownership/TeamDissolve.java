package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimDissolve;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;
import mctmods.resourcedatapackloader.common.util.entity.Scores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class TeamDissolve {
    public interface Liveness {
        boolean isLive(MinecraftServer server, String key);
    }

    private static final Map<String, Liveness> TESTS = new ConcurrentHashMap<>();
    private static int counter;

    static { TESTS.put(OwnerRecord.SOURCE_SCOREBOARD, (server, key) -> Scores.board(server).getTeamNames().contains(key)); }

    private TeamDissolve() {}

    public static void register(String source, Liveness test) { TESTS.put(source, test); }

    public static void tick(MinecraftServer server) {
        if (++counter < OwnershipSettings.checkTicks()) { return; }
        counter = 0;
        check(server);
    }

    public static void check(MinecraftServer server) {
        OwnerTable table = OwnerTable.get(server);
        for (Map.Entry<Integer, OwnerRecord> team : table.liveTeams().entrySet()) {
            Liveness test = TESTS.get(team.getValue().source);
            if (test == null || test.isLive(server, team.getValue().key)) { continue; }
            dissolve(table, team.getKey(), team.getValue().source);
        }
        if (OwnershipSettings.joinKeep()) { return; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { onLogin(player); }
    }

    public static void dissolveForeign(MinecraftServer server, String source, String key) {
        OwnerTable table = OwnerTable.get(server);
        int handle = table.liveTeamHandle(source, key);
        if (handle != 0) { dissolve(table, handle, source); }
    }

    private static void dissolve(OwnerTable table, int handle, String source) {
        table.markDissolved(handle);
        ClaimDissolve.apply(handle, OwnerRecord.SOURCE_FTB.equals(source) ? OwnershipSettings.ftbDissolveMode(ClaimSettings.dissolveMode()) : ClaimSettings.dissolveMode());
    }

    public static void onLogin(ServerPlayer player) {
        if (OwnershipSettings.joinKeep()) { return; }
        OwnerTable table = OwnerTable.get(player.server);
        int playerHandle = table.playerHandle(player.getUUID());
        if (playerHandle == 0) { return; }
        int teamHandle = ScoreboardOwnerSource.current().teamOf(player);
        if (teamHandle != 0 && table.isLive(teamHandle)) { table.moveToTeam(playerHandle, teamHandle); }
    }
}
