package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

public final class PathShortcut {
    private static final double REACH = 8.0D;
    private static final float AVOIDED = 8.0F;

    private PathShortcut() {}

    public static void ahead(Mob mob, Level level, Path path) {
        if (path.isDone()) { return; }
        Vec3 from = mob.position();
        int start = path.getNextNodeIndex();
        int end = path.getNodeCount();
        int floor = Mth.floor(from.y);
        for (int i = start; i < end; i++) {
            if (path.getNode(i).y != floor) {
                end = i;
                break;
            }
        }
        int width = Mth.ceil(mob.getBbWidth());
        int height = Mth.ceil(mob.getBbHeight());
        for (int j = end - 1; j > start; j--) {
            Vec3 to = path.getEntityPosAtNode(mob, j);
            if (to.distanceToSqr(from) <= REACH * REACH && direct(mob, level, from, to, width, height)) {
                path.setNextNodeIndex(j);
                return;
            }
        }
    }

    private static boolean direct(Mob mob, Level level, Vec3 from, Vec3 to, int width, int height) {
        int x = Mth.floor(from.x);
        int y = Mth.floor(from.y);
        int z = Mth.floor(from.z);
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        double length = dx * dx + dz * dz;
        if (length < 1.0E-8D) { return false; }
        double scale = 1.0D / Math.sqrt(length);
        dx *= scale;
        dz *= scale;
        if (unsafe(mob, level, x, y, z, width + 2, height, from, dx, dz)) { return false; }
        double stepX = 1.0D / Math.abs(dx);
        double stepZ = 1.0D / Math.abs(dz);
        double nextX = x - from.x;
        double nextZ = z - from.z;
        if (dx >= 0.0D) { nextX++; }
        if (dz >= 0.0D) { nextZ++; }
        nextX /= dx;
        nextZ /= dz;
        int signX = dx < 0.0D ? -1 : 1;
        int signZ = dz < 0.0D ? -1 : 1;
        int endX = Mth.floor(to.x);
        int endZ = Mth.floor(to.z);
        int leftX = endX - x;
        int leftZ = endZ - z;
        while (leftX * signX > 0 || leftZ * signZ > 0) {
            if (nextX < nextZ) {
                nextX += stepX;
                x += signX;
                leftX = endX - x;
            }
            else {
                nextZ += stepZ;
                z += signZ;
                leftZ = endZ - z;
            }
            if (unsafe(mob, level, x, y, z, width, height, from, dx, dz)) { return false; }
        }
        return true;
    }

    private static boolean unsafe(Mob mob, Level level, int x, int y, int z, int size, int height, Vec3 from, double dx, double dz) {
        int minX = x - size / 2;
        int minZ = z - size / 2;
        if (blocked(level, minX, y, minZ, size, height, from, dx, dz)) { return true; }
        for (int cx = minX; cx < minX + size; cx++) {
            for (int cz = minZ; cz < minZ + size; cz++) {
                if (behind(cx, cz, from, dx, dz)) { continue; }
                BlockPathTypes ground = type(mob, level, cx, y - 1, cz, size, height);
                if (ground == BlockPathTypes.WATER || ground == BlockPathTypes.LAVA || ground == BlockPathTypes.OPEN) { return true; }
                BlockPathTypes feet = type(mob, level, cx, y, cz, size, height);
                float malus = mob.getPathfindingMalus(feet);
                if (malus < 0.0F || malus >= AVOIDED) { return true; }
                if (feet == BlockPathTypes.DAMAGE_FIRE || feet == BlockPathTypes.DANGER_FIRE || feet == BlockPathTypes.DAMAGE_OTHER) { return true; }
            }
        }
        return false;
    }

    private static boolean blocked(Level level, int x, int y, int z, int size, int height, Vec3 from, double dx, double dz) {
        for (BlockPos pos : BlockPos.betweenClosed(x, y, z, x + size - 1, y + height - 1, z + size - 1)) {
            if (behind(pos.getX(), pos.getZ(), from, dx, dz)) { continue; }
            if (!level.getBlockState(pos).isPathfindable(level, pos, PathComputationType.LAND)) { return true; }
        }
        return false;
    }

    private static boolean behind(int x, int z, Vec3 from, double dx, double dz) { return (x + 0.5D - from.x) * dx + (z + 0.5D - from.z) * dz < 0.0D; }

    private static BlockPathTypes type(Mob mob, Level level, int x, int y, int z, int size, int height) {
        EnumSet<BlockPathTypes> seen = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes first = BlockPathTypes.BLOCKED;
        BlockPos at = mob.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < size; k++) {
                    BlockPathTypes type = WalkNodeEvaluator.getBlockPathTypeStatic(level, pos.set(x + i, y + j, z + k));
                    if (type == BlockPathTypes.DOOR_WOOD_CLOSED) { type = BlockPathTypes.WALKABLE; }
                    if (type == BlockPathTypes.RAIL && !(level.getBlockState(at).getBlock() instanceof BaseRailBlock) && !(level.getBlockState(at.below()).getBlock() instanceof BaseRailBlock)) { type = BlockPathTypes.FENCE; }
                    if (i == 0 && j == 0 && k == 0) { first = type; }
                    seen.add(type);
                }
            }
        }
        if (seen.contains(BlockPathTypes.FENCE)) { return BlockPathTypes.FENCE; }
        BlockPathTypes worst = BlockPathTypes.BLOCKED;
        for (BlockPathTypes type : seen) {
            float malus = mob.getPathfindingMalus(type);
            if (malus < 0.0F) { return type; }
            if (malus >= mob.getPathfindingMalus(worst)) { worst = type; }
        }
        return first == BlockPathTypes.OPEN && mob.getPathfindingMalus(worst) == 0.0F ? BlockPathTypes.OPEN : worst;
    }
}
