package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.PullingHand;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class PullGoal extends Goal {
    private static final double NEAR = 4.0D;
    private static final double SPEED = 1.5D;
    private final PathfinderMob mob;
    private final ItemStack stack;
    private final int reload;
    private final float power;
    private final boolean follows;
    @Nullable private Player target;
    private long next;

    public PullGoal(PathfinderMob mob, ItemStack stack, int reload, float power, boolean follows) {
        this.mob = mob;
        this.stack = stack;
        this.reload = reload;
        this.power = power;
        this.follows = follows;
        setFlags(EnumSet.noneOf(Flag.class));
    }

    @Nullable private Player aimed() {
        double range = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        LivingEntity attack = mob.getTarget();
        Player found = attack instanceof Player player && !player.isCreative() && !player.isSpectator() ? player : null;
        if (found == null && follows && attack == null) { found = mob.level().getNearestPlayer(TargetingConditions.forCombat().range(range), mob); }
        if (found == null) { return null; }
        double away = mob.distanceToSqr(found);
        return away > NEAR * NEAR && away < range * range && mob.getSensing().hasLineOfSight(found) ? found : null;
    }

    @Override public boolean canUse() {
        if (next == 0L) { next = mob.level().getGameTime() + reload; }
        if (mob.level().getGameTime() < next) { return false; }
        target = aimed();
        return target != null;
    }

    @Override public boolean canContinueToUse() { return false; }

    @Override public void start() {
        next = mob.level().getGameTime() + reload;
        if (target == null || mob.level().isClientSide()) { return; }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        PullingHand hand = new PullingHand(mob.level(), mob, stack, power);
        Vec3 at = new Vec3(target.getX() - hand.getX(), target.getEyeY() - hand.getY(), target.getZ() - hand.getZ());
        hand.shoot(at.normalize().scale(SPEED));
        mob.level().addFreshEntity(hand);
        SoundEvent own = ContentEntities.sound(mob, ContentEntities.THROW);
        if (own != null) { mob.playSound(own, 1.0F, 1.0F); }
        mob.swing(InteractionHand.MAIN_HAND);
    }
}
