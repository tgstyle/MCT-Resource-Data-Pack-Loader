package mctmods.resourcedatapackloader.content.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

public class SwimmingMoveHelper extends EntityMoveHelper {
    public SwimmingMoveHelper(EntityLiving swimmer) { super(swimmer); }

    @Override public void onUpdateMoveHelper() {
        if (action != EntityMoveHelper.Action.MOVE_TO || entity.getNavigator().noPath()) {
            entity.setMoveForward(0.0F);
            action = EntityMoveHelper.Action.WAIT;
            return;
        }
        double toX = posX - entity.posX;
        double toY = posY - entity.posY;
        double toZ = posZ - entity.posZ;
        double away = Math.sqrt(toX * toX + toY * toY + toZ * toZ);
        if (away < 0.1D) {
            entity.setMoveForward(0.0F);
            action = EntityMoveHelper.Action.WAIT;
            return;
        }
        entity.rotationYaw = limitAngle(entity.rotationYaw, (float) (MathHelper.atan2(toZ, toX) * (180D / Math.PI)) - 90.0F, 10.0F);
        entity.renderYawOffset = entity.rotationYaw;
        entity.rotationYawHead = entity.rotationYaw;
        float wanted = (float) (speed * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        entity.setAIMoveSpeed(entity.isInWater() ? wanted * 0.6F : wanted);
        entity.motionY += entity.getAIMoveSpeed() * (toY / away) * 0.1D;
    }
}
