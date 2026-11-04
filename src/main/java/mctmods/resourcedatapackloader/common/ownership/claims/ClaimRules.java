package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.world.Coords;

import javax.annotation.Nullable;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    @Nullable public static String canClaim(World world, int chunkX, int chunkZ, int handle, boolean op) {
        if (ClaimSettings.dimensionBarred(world.provider.getDimension())) { return DIMENSION; }
        if (ClaimSettings.yieldsToForeign()) {
            if (ClaimSettings.ftbPolicy() == ClaimSettings.FtbPolicy.EXCLUSIVE) { return FTB_ONLY; }
            if (ForeignClaimsHolder.get().isClaimed(world, chunkX, chunkZ)) { return FTB_LAND; }
        }
        int spawnRadius = ClaimSettings.spawnRadius();
        if (!op && spawnRadius > 0) {
            BlockPos spawn = world.getSpawnPoint();
            if (nearest(chunkX - Coords.blockToCube(spawn.getX()), chunkZ - Coords.blockToCube(spawn.getZ())) <= spawnRadius) { return SPAWN; }
        }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index == null) { return null; }
        int held = index.ownerAt(chunkX, chunkZ);
        if (held != 0) { return held == handle ? OWN : TAKEN; }
        if (index.pendingAt(chunkX, chunkZ) != null) { return PENDING; }
        if (op) { return null; }
        int count = index.claimCountOf(handle);
        if (count >= limitFor(world.getMinecraftServer(), handle)) { return LIMIT; }
        if (ClaimSettings.mustTouch() && count > 0 && !touches(index, chunkX, chunkZ, handle)) { return TOUCH; }
        return crowded(index, chunkX, chunkZ, handle) ? DISTANCE : null;
    }

    public static int limitFor(@Nullable MinecraftServer server, int handle) {
        switch (ClaimSettings.limitMode()) {
            case PERMEMBER: return ClaimSettings.limitPerMember() * members(server, handle);
            case SCORE: return scoreOf(server, handle);
            default: return ClaimSettings.limit();
        }
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

    @Nullable private static ScorePlayerTeam teamOf(MinecraftServer server, OwnerTable table, int handle) {
        return table.isTeam(handle) ? ContentTeams.board(server).getTeam(table.keyOf(handle)) : null;
    }

    private static int members(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        if (table == null || !table.isLive(handle)) { return 0; }
        if (!table.isTeam(handle)) { return 1; }
        ScorePlayerTeam team = teamOf(server, table, handle);
        return team == null ? 0 : team.getMembershipCollection().size();
    }

    private static int scoreOf(@Nullable MinecraftServer server, int handle) {
        OwnerTable table = OwnerTable.get(server);
        if (server == null || table == null || !table.isLive(handle)) { return 0; }
        Scoreboard board = ContentTeams.board(server);
        ScoreObjective objective = board.getObjective(ClaimSettings.limitScore());
        if (objective == null) { return 0; }
        if (!table.isTeam(handle)) { return score(board, objective, table.labelOf(handle)); }
        ScorePlayerTeam team = teamOf(server, table, handle);
        int total = 0;
        if (team != null) {
            for (String member : team.getMembershipCollection()) { total += score(board, objective, member); }
        }
        return total;
    }

    private static int score(Scoreboard board, ScoreObjective objective, String name) {
        return board.entityHasObjective(name, objective) ? board.getOrCreateScore(name, objective).getScorePoints() : 0;
    }
}
