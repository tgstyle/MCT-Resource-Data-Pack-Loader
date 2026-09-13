package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PathShortcut {
    private static final int LOOKAHEAD = 8;
    private static final double STEP = 0.5D;

    private PathShortcut() {}

    public static void ahead(Mob mob, Level level, Path path) {
        if (path.isDone()) { return; }
        Vec3 from = mob.position();
        int start = path.getNextNodeIndex();
        int end = Math.min(path.getNodeCount(), start + LOOKAHEAD);
        int floor = Mth.floor(from.y);
        for (int i = start; i < end; i++) {
            if (path.getNode(i).y != floor) {
                end = i;
                break;
            }
        }
        for (int j = end - 1; j > start; j--) {
            if (clear(mob, level, from, path.getEntityPosAtNode(mob, j))) {
                path.setNextNodeIndex(j);
                return;
            }
        }
    }

    private static boolean clear(Mob mob, Level level, Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        int steps = Mth.ceil(Math.sqrt(dx * dx + dz * dz) / STEP);
        AABB box = mob.getBoundingBox();
        for (int s = 1; s <= steps; s++) {
            double part = (double) s / steps;
            double x = from.x + dx * part;
            double z = from.z + dz * part;
            if (!level.noCollision(mob, box.move(x - from.x, 0.0D, z - from.z))) { return false; }
            BlockPos below = BlockPos.containing(x, from.y - 0.5D, z);
            if (level.getBlockState(below).getCollisionShape(level, below).isEmpty()) { return false; }
            if (!level.getFluidState(below.above()).isEmpty()) { return false; }
        }
        return true;
    }
}
