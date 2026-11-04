package mctmods.resourcedatapackloader.common.ownership.claims;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class ClaimGuard {
    private ClaimGuard() {}

    public static int covers(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel served) || !ClaimSettings.any()) { return 0; }
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        int owner = ClaimIndex.obtain(served).ownerAt(chunkX, chunkZ);
        return owner != 0 && ClaimSettings.yieldsToForeign() && (ClaimSettings.ftbPolicy() == ClaimSettings.FtbPolicy.EXCLUSIVE || ForeignClaimsHolder.get().isClaimed(served, chunkX, chunkZ)) ? 0 : owner;
    }

    public static boolean passes(ServerPlayer player, Level level, BlockPos pos) {
        int claim = covers(level, pos);
        return claim == 0 || ClaimOwners.passes(player, claim);
    }

    public static boolean refusesGriefing(Entity mob) { return ClaimSettings.protectMobGriefing() && covers(mob.level(), mob.blockPosition()) != 0; }

    public static boolean sameOwnerAcross(Level level, BlockPos from, BlockPos to) {
        int target = covers(level, to);
        return target == 0 || target == covers(level, from);
    }

    private static boolean claimless(Level level) { return !(level instanceof ServerLevel served) || ClaimIndex.obtain(served).isEmpty(); }

    public static boolean fluidMayFlow(Level level, BlockPos from, BlockPos to) { return ClaimSettings.fluidsOpen() || claimless(level) || sameOwnerAcross(level, from, to); }

    public static boolean fireMaySpread(Level level, BlockPos from, BlockPos to) { return !ClaimSettings.protectFire() || claimless(level) || sameOwnerAcross(level, from, to); }

    public static boolean pistonMayMove(Level level, BlockPos piston, BlockPos firstBlock, Direction direction, List<BlockPos> moved, List<BlockPos> destroyed) {
        if (!ClaimSettings.protectPistons() || claimless(level)) { return true; }
        BlockPos next = piston.relative(direction);
        if (!sameOwnerAcross(level, piston, next.equals(firstBlock) ? next : piston.relative(direction.getOpposite()))) { return false; }
        for (BlockPos pos : moved) {
            if (!sameOwnerAcross(level, piston, pos) || !sameOwnerAcross(level, piston, pos.relative(direction))) { return false; }
        }
        for (BlockPos pos : destroyed) {
            if (!sameOwnerAcross(level, piston, pos)) { return false; }
        }
        return true;
    }
}
