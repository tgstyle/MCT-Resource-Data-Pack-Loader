package mctmods.resourcedatapackloader.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public final class SwoopMoveControl extends MoveControl {
    private static final float TURN = 10.0F;

    public SwoopMoveControl(Mob mob) { super(mob); }

    @Override public void tick() {
        if (operation != Operation.MOVE_TO) {
            mob.setNoGravity(false);
            mob.setYya(0.0F);
            mob.setZza(0.0F);
            return;
        }
        operation = Operation.WAIT;
        mob.setNoGravity(true);
        double dx = wantedX - mob.getX();
        double dy = wantedY - mob.getY();
        double dz = wantedZ - mob.getZ();
        if (dx * dx + dy * dy + dz * dz < 2.5000003E-7D) {
            mob.setYya(0.0F);
            mob.setZza(0.0F);
            return;
        }
        mob.setYRot(rotlerp(mob.getYRot(), (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D), TURN));
        float speed = (float) (speedModifier * mob.getAttributeValue(mob.onGround() ? Attributes.MOVEMENT_SPEED : Attributes.FLYING_SPEED));
        mob.setSpeed(speed);
        mob.setXRot(rotlerp(mob.getXRot(), (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))), TURN));
        mob.setYya(dy > 0.0D ? speed : -speed);
    }
}
