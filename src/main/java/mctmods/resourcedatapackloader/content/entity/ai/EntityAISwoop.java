package mctmods.resourcedatapackloader.content.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import javax.annotation.Nullable;

public final class EntityAISwoop extends EntityAIBase {
    private static final double RING = 8.0D;
    private static final double HEIGHT = 6.0D;
    private static final int DIVE_MOST = 60;
    private static final double CIRCLE_SPEED = 1.0D;
    private static final double DIVE_SPEED = 1.6D;
    private final EntityCreature mob;
    @Nullable private EntityLivingBase target;
    private boolean circling;
    private double angle;
    private int ticks;
    private int circleFor;

    public EntityAISwoop(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        EntityLivingBase found = mob.getAttackTarget();
        return found != null && found.isEntityAlive() && mob.getDistanceSq(found) < 1024.0D;
    }

    @Override public boolean shouldContinueExecuting() { return target != null && target.isEntityAlive() && mob.getAttackTarget() == target; }

    @Override public void startExecuting() {
        target = mob.getAttackTarget();
        angle = mob.getRNG().nextDouble() * Math.PI * 2.0D;
        circle();
    }

    private void circle() {
        circling = true;
        ticks = 0;
        circleFor = 100 + mob.getRNG().nextInt(100);
    }

    @Override public void resetTask() {
        target = null;
        circling = true;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        if (target == null) { return; }
        mob.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        ticks++;
        if (circling) {
            angle += 0.08D;
            mob.getMoveHelper().setMoveTo(target.posX + Math.cos(angle) * RING, target.posY + HEIGHT, target.posZ + Math.sin(angle) * RING, CIRCLE_SPEED);
            if (ticks >= circleFor && mob.getEntitySenses().canSee(target)) {
                circling = false;
                ticks = 0;
            }
            return;
        }
        mob.getMoveHelper().setMoveTo(target.posX, target.posY + target.height * 0.5D, target.posZ, DIVE_SPEED);
        if (mob.getDistanceSq(target) < 6.25D) {
            EntityAIStrike.hit(mob, target);
            circle();
            return;
        }
        if (ticks > DIVE_MOST || mob.collidedHorizontally || mob.onGround) { circle(); }
    }
}
