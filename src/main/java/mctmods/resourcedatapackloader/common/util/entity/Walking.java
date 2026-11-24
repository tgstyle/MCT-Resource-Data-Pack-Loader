package mctmods.resourcedatapackloader.common.util.entity;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class Walking {
    private static final double STROLL = 1.0D;

    private Walking() {}

    public static Vec3d feet(BlockPos at) { return new Vec3d(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D); }

    public static double feetDistanceSq(Entity mob, BlockPos at) {
        Vec3d feet = feet(at);
        return mob.getDistanceSq(feet.x, feet.y, feet.z);
    }

    public static boolean toBlock(EntityCreature mob, BlockPos at, double speed) {
        Vec3d feet = feet(at);
        return mob.getNavigator().tryMoveToXYZ(feet.x, feet.y, feet.z, speed);
    }

    public static boolean toward(EntityCreature mob, Vec3d aim, int radius, int height, double speed) { return toward(mob, aim, radius, height, speed, spot -> true); }

    public static boolean toward(EntityCreature mob, Vec3d aim, int radius, int height, double speed, Predicate<Vec3d> accept) { return moveTo(mob, RandomPositionGenerator.findRandomTargetBlockTowards(mob, radius, height, aim), speed, accept); }

    public static void awayFrom(EntityCreature mob, Vec3d threat, int radius, int height, double speed) { moveTo(mob, RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, radius, height, threat), speed, spot -> true); }

    private static boolean moveTo(EntityCreature mob, @Nullable Vec3d spot, double speed, Predicate<Vec3d> accept) { return spot != null && accept.test(spot) && mob.getNavigator().tryMoveToXYZ(spot.x, spot.y, spot.z, speed); }

    @Nullable public static <T> T nearest(Iterable<? extends T> from, ToDoubleFunction<T> distSq, double capSq) { return nearest(from, any -> true, distSq, capSq); }

    @Nullable public static <T> T nearest(Iterable<? extends T> from, Predicate<T> accept, ToDoubleFunction<T> distSq, double capSq) {
        T best = null;
        double nearest = Double.MAX_VALUE;
        for (T candidate : from) {
            if (!accept.test(candidate)) { continue; }
            double away = distSq.applyAsDouble(candidate);
            if (away > capSq || away >= nearest) { continue; }
            nearest = away;
            best = candidate;
        }
        return best;
    }

    public static double pace(EntityCreature mob) {
        EntityAIWander wander = AiTasks.first(mob.tasks, EntityAIWander.class);
        return wander == null ? STROLL : wander.speed;
    }
}
