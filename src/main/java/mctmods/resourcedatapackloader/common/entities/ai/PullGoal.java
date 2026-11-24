package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.PullingHand;
import mctmods.resourcedatapackloader.common.util.entity.Looking;
import mctmods.resourcedatapackloader.common.util.entity.Walking;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

public final class PullGoal extends Goal {
    private static final double NEAR_SQ = 16.0D;
    private final PathfinderMob mob;
    private final ItemStack stack;
    private final int reload;
    private final float power;
    private final boolean follows;
    private long next;

    public PullGoal(PathfinderMob mob, ItemStack stack, int reload, float power, boolean follows) {
        this.mob = mob;
        this.stack = stack;
        this.reload = reload;
        this.power = power;
        this.follows = follows;
    }

    @Nullable private Player target() {
        if (mob.getTarget() instanceof Player aimed && !aimed.isCreative() && !aimed.isSpectator()) { return aimed; }
        return follows ? mob.level().getNearestPlayer(TargetingConditions.forCombat().range(Walking.followRange(mob)), mob) : null;
    }

    @Override public boolean canUse() {
        if (next == 0L) { next = mob.level().getGameTime() + reload; }
        if (mob.level().getGameTime() < next) { return false; }
        Player target = target();
        if (target == null) { return false; }
        double away = mob.distanceToSqr(target);
        double range = Walking.followRange(mob);
        return away > NEAR_SQ && away <= range * range && mob.getSensing().hasLineOfSight(target);
    }

    @Override public boolean canContinueToUse() { return false; }

    @Override public void start() {
        Player target = target();
        if (target == null) { return; }
        next = mob.level().getGameTime() + reload;
        Looking.at(mob, target);
        PullingHand hand = new PullingHand(mob.level(), mob, stack, power);
        hand.aim(new Vec3(target.getX(), target.getEyeY(), target.getZ()));
        mob.level().addFreshEntity(hand);
        SoundEvent own = ContentEntities.sound(mob, ContentEntities.THROW);
        if (own != null) { mob.playSound(own, 1.0F, 1.0F); }
        mob.swing(InteractionHand.MAIN_HAND);
    }
}
