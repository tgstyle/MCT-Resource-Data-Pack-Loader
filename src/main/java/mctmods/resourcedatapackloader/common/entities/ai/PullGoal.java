package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.PullingHand;
import mctmods.resourcedatapackloader.common.util.entity.Stalking;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PullGoal extends Goal {
    private static final double NEAREST = 4.0D;
    private static final float SPEED = 1.5F;
    private final PathfinderMob mob;
    private final ItemStack stack;
    private final int reload;
    private final float power;
    private final boolean follows;
    @Nullable private Player target;
    private int cooldown;
    private int wait;

    public PullGoal(PathfinderMob mob, ItemStack stack, int reload, float power, boolean follows) {
        this.mob = mob;
        this.stack = stack;
        this.reload = reload;
        this.power = power;
        this.follows = follows;
        cooldown = reload;
        setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Nullable private Player pick() {
        Player found = mob.getTarget() instanceof Player aimed ? aimed : follows ? Stalking.nearest(mob) : null;
        if (found == null || found.isCreative() || found.isSpectator() || !found.isAlive()) { return null; }
        double range = Stalking.range(mob);
        double away = mob.distanceToSqr(found);
        return away > NEAREST * NEAREST && away < range * range && mob.getSensing().hasLineOfSight(found) ? found : null;
    }

    @Override public boolean canUse() {
        if (wait > 0) {
            wait--;
            return false;
        }
        wait = reducedTickDelay(10);
        target = pick();
        return target != null;
    }

    @Override public boolean canContinueToUse() { return target != null && target.isAlive() && pick() != null; }

    @Override public void stop() { target = null; }

    @Override public void tick() {
        if (cooldown > 0) { cooldown--; }
        if (cooldown > 0 || target == null) { return; }
        cooldown = reload;
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        PullingHand hand = new PullingHand(mob.level(), mob, stack, power, Stalking.range(mob));
        hand.aim(target.getX() - hand.getX(), target.getEyeY() - hand.getY(), target.getZ() - hand.getZ(), SPEED);
        mob.level().addFreshEntity(hand);
        SoundEvent own = ContentEntities.sound(mob, ContentEntities.THROW);
        if (own != null) { mob.playSound(own, 1.0F, 1.0F); }
        LineCompat.swing(mob);
    }
}
