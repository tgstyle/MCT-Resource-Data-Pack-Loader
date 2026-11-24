package mctmods.resourcedatapackloader.common.util.entity;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public final class Walking {
    private static final double STROLL = 1.0D;
    private static final double VILLAGER_STROLL = 0.5D;

    private Walking() {}

    public static boolean toBlock(PathfinderMob mob, BlockPos at, double speed) { return mob.getNavigation().moveTo(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, speed); }

    public static boolean toward(PathfinderMob mob, Vec3 aim, int radius, int height, double speed, Predicate<Vec3> accept) { return moveTo(mob, DefaultRandomPos.getPosTowards(mob, radius, height, aim, Math.PI / 2.0D), speed, accept); }

    public static void awayFrom(PathfinderMob mob, Vec3 threat, int radius, int height, double speed) { moveTo(mob, DefaultRandomPos.getPosAway(mob, radius, height, threat), speed, spot -> true); }

    private static boolean moveTo(PathfinderMob mob, @Nullable Vec3 spot, double speed, Predicate<Vec3> accept) { return spot != null && accept.test(spot) && mob.getNavigation().moveTo(spot.x, spot.y, spot.z, speed); }

    public static double followRange(PathfinderMob mob) { return mob.getAttributeValue(Attributes.FOLLOW_RANGE); }

    public static double pace(PathfinderMob mob) {
        if (mob instanceof Villager) { return VILLAGER_STROLL; }
        RandomStrollGoal stroll = Goals.first(mob.goalSelector, RandomStrollGoal.class);
        return stroll == null ? STROLL : stroll.speedModifier;
    }

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
}
