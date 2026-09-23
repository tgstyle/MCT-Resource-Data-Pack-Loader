package mctmods.resourcedatapackloader.content.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class SwimmingMoveControl extends MoveControl {
    public SwimmingMoveControl(Mob swimmer) { super(swimmer); }

    @Override public void tick() {
        if (operation != Operation.MOVE_TO || mob.getNavigation().isDone()) {
            mob.setZza(0.0F);
            operation = Operation.WAIT;
            return;
        }
        double toX = wantedX - mob.getX();
        double toY = wantedY - mob.getY();
        double toZ = wantedZ - mob.getZ();
        double away = Math.sqrt(toX * toX + toY * toY + toZ * toZ);
        if (away < 0.1D) {
            mob.setZza(0.0F);
            operation = Operation.WAIT;
            return;
        }
        mob.setYRot(rotlerp(mob.getYRot(), (float) (Mth.atan2(toZ, toX) * (180.0D / Math.PI)) - 90.0F, 10.0F));
        mob.yBodyRot = mob.getYRot();
        mob.yHeadRot = mob.getYRot();
        float wanted = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        mob.setSpeed(mob.isInWater() ? wanted * 0.6F : wanted);
        mob.setDeltaMovement(mob.getDeltaMovement().add(0.0D, mob.getSpeed() * (toY / away) * 0.1D, 0.0D));
    }
}
