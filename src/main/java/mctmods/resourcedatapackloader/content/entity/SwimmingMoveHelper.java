package mctmods.resourcedatapackloader.content.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

public class SwimmingMoveHelper extends EntityMoveHelper {
    private final EntityLiving swimmer;

    public SwimmingMoveHelper(EntityLiving swimmer) {
        super(swimmer);
        this.swimmer = swimmer;
    }

    @Override public void onUpdateMoveHelper() {
        if (action != EntityMoveHelper.Action.MOVE_TO || swimmer.getNavigator().noPath()) {
            swimmer.setMoveForward(0.0F);
            action = EntityMoveHelper.Action.WAIT;
            return;
        }

        double toX = posX - swimmer.posX;
        double toY = posY - swimmer.posY;
        double toZ = posZ - swimmer.posZ;
        double away = Math.sqrt(toX * toX + toY * toY + toZ * toZ);
        if (away < 0.1D) {
            swimmer.setMoveForward(0.0F);
            action = EntityMoveHelper.Action.WAIT;
            return;
        }

        swimmer.rotationYaw = limitAngle(swimmer.rotationYaw, (float) (MathHelper.atan2(toZ, toX) * (180D / Math.PI)) - 90.0F, 10.0F);
        swimmer.renderYawOffset = swimmer.rotationYaw;
        swimmer.rotationYawHead = swimmer.rotationYaw;

        float wanted = (float) (speed * swimmer.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        swimmer.setAIMoveSpeed(swimmer.isInWater() ? wanted * 0.6F : wanted);
        swimmer.motionY += swimmer.getAIMoveSpeed() * (toY / away) * 0.1D;
    }
}
