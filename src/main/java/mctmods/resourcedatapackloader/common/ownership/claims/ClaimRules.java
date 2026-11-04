package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.util.entity.Scores;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class ClaimRules {
    public static final String DIMENSION = "rdpl.claim.deny.dimension";
    public static final String SPAWN = "rdpl.claim.deny.spawn";
    public static final String OWN = "rdpl.claim.deny.own";
    public static final String TAKEN = "rdpl.claim.deny.taken";
    public static final String PENDING = "rdpl.claim.deny.pending";
    public static final String LIMIT = "rdpl.claim.deny.limit";
    public static final String TOUCH = "rdpl.claim.deny.touch";
    public static final String DISTANCE = "rdpl.claim.deny.distance";
    public static final String FTB_LAND = "rdpl.claim.deny.ftbland";
    public static final String FTB_ONLY = "rdpl.claim.deny.ftbonly";

    private ClaimRules() {}

    @Nullable public static String canClaim(ServerLevel level, int chunkX, int chunkZ, int handle, boolean op) {
        if (ClaimSettings.dimensionBarred(level.dimension())) { return DIMENSION; }
        if (ClaimSettings.yieldsToForeign()) {
            if (ClaimSettings.ftbPolicy() == ClaimSettings.FtbPolicy.EXCLUSIVE) { return FTB_ONLY; }
            if (ForeignClaimsHolder.get().isClaimed(level, chunkX, chunkZ)) { return FTB_LAND; }
        }
        int spawnRadius = ClaimSettings.spawnRadius();
        if (!op && spawnRadius > 0) {
            BlockPos spawn = level.getRespawnData().pos();
            if (nearest(chunkX - (spawn.getX() >> 4), chunkZ - (spawn.getZ() >> 4)) <= spawnRadius) { return SPAWN; }
        }
        ClaimIndex index = ClaimIndex.obtain(level);
        int held = index.ownerAt(chunkX, chunkZ);
        if (held != 0) { return held == handle ? OWN : TAKEN; }
        if (index.pendingAt(chunkX, chunkZ) != null) { return PENDING; }
        if (op) { return null; }
        int count = index.claimCountOf(handle);
        if (count >= limitFor(level.getServer(), handle)) { return LIMIT; }
        if (ClaimSettings.mustTouch() && count > 0 && !touches(index, chunkX, chunkZ, handle)) { return TOUCH; }
        return crowded(index, chunkX, chunkZ, handle) ? DISTANCE : null;
    }

    public static int limitFor(MinecraftServer server, int handle) {
        return switch (ClaimSettings.limitMode()) {
            case PERMEMBER -> ClaimSettings.limitPerMember() * members(server, handle);
            case SCORE -> scoreOf(server, handle);
            default -> ClaimSettings.limit();
        };
    }

    private static int nearest(int dx, int dz) { return Math.max(Math.abs(dx), Math.abs(dz)); }

    private static boolean touches(ClaimIndex index, int chunkX, int chunkZ, int handle) {
        return index.ownerAt(chunkX + 1, chunkZ) == handle || index.ownerAt(chunkX - 1, chunkZ) == handle || index.ownerAt(chunkX, chunkZ + 1) == handle || index.ownerAt(chunkX, chunkZ - 1) == handle;
    }

    private static boolean crowded(ClaimIndex index, int chunkX, int chunkZ, int handle) {
        int distance = ClaimSettings.minDistance();
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dz = -distance; dz <= distance; dz++) {
                int other = index.ownerAt(chunkX + dx, chunkZ + dz);
                if (other != 0 && other != handle) { return true; }
            }
        }
        return false;
    }

    @Nullable private static PlayerTeam teamOf(MinecraftServer server, OwnerTable table, int handle) { return table.isTeam(handle) ? Scores.team(Scores.board(server), table.keyOf(handle)) : null; }

    private static int members(MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        if (!table.isLive(handle)) { return 0; }
        if (!table.isTeam(handle)) { return 1; }
        PlayerTeam team = teamOf(server, table, handle);
        return team == null ? 0 : Scores.members(team).size();
    }

    private static int scoreOf(MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        if (!table.isLive(handle)) { return 0; }
        Scoreboard board = Scores.board(server);
        Objective objective = Scores.objective(board, ClaimSettings.limitScore());
        if (objective == null) { return 0; }
        if (!table.isTeam(handle)) { return score(board, objective, table.labelOf(handle)); }
        PlayerTeam team = teamOf(server, table, handle);
        int total = 0;
        if (team != null) {
            for (String member : Scores.members(team)) { total += score(board, objective, member); }
        }
        return total;
    }

    private static int score(Scoreboard board, Objective objective, String name) { return Scores.lacks(board, name, objective) ? 0 : Scores.score(board, name, objective); }
}
