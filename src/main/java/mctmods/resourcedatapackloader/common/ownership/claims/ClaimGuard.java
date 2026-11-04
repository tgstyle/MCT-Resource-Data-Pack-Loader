package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.world.Coords;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ClaimGuard {
    private ClaimGuard() {}

    public static int covers(World world, BlockPos pos) {
        if (world.isRemote || !ClaimSettings.any()) { return 0; }
        ClaimIndex index = ClaimIndex.obtain(world);
        if (index == null) { return 0; }
        int chunkX = Coords.blockToCube(pos.getX());
        int chunkZ = Coords.blockToCube(pos.getZ());
        int owner = index.ownerAt(chunkX, chunkZ);
        return owner != 0 && ClaimSettings.yieldsToForeign() && (ClaimSettings.ftbPolicy() == ClaimSettings.FtbPolicy.EXCLUSIVE || ForeignClaimsHolder.get().isClaimed(world, chunkX, chunkZ)) ? 0 : owner;
    }

    public static boolean passes(EntityPlayer player, World world, BlockPos pos) {
        int claim = covers(world, pos);
        return claim == 0 || ClaimOwners.passes(player, claim);
    }

    public static boolean refusesGriefing(Entity mob) { return ClaimSettings.protectMobGriefing() && covers(mob.world, mob.getPosition()) != 0; }

    public static boolean sameOwnerAcross(World world, BlockPos from, BlockPos to) {
        int target = covers(world, to);
        return target == 0 || target == covers(world, from);
    }

    private static boolean claimless(World world) {
        if (world.isRemote) { return true; }
        ClaimIndex index = ClaimIndex.obtain(world);
        return index == null || index.isEmpty();
    }

    public static boolean fluidMayFlow(World world, BlockPos from, BlockPos to) { return ClaimSettings.fluidsOpen() || claimless(world) || sameOwnerAcross(world, from, to); }

    public static boolean fireMaySpread(World world, BlockPos from, BlockPos to) { return !ClaimSettings.protectFire() || claimless(world) || sameOwnerAcross(world, from, to); }

    public static boolean pistonMayMove(World world, BlockPos piston, BlockPos firstBlock, EnumFacing direction, List<BlockPos> moved, List<BlockPos> destroyed) {
        if (!ClaimSettings.protectPistons() || claimless(world)) { return true; }
        BlockPos next = piston.offset(direction);
        if (!sameOwnerAcross(world, piston, next.equals(firstBlock) ? next : piston.offset(direction.getOpposite()))) { return false; }
        for (BlockPos pos : moved) {
            if (!sameOwnerAcross(world, piston, pos) || !sameOwnerAcross(world, piston, pos.offset(direction))) { return false; }
        }
        for (BlockPos pos : destroyed) {
            if (!sameOwnerAcross(world, piston, pos)) { return false; }
        }
        return true;
    }
}
