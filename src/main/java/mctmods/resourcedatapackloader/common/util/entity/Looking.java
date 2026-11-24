package mctmods.resourcedatapackloader.common.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;

public final class Looking {
    private static final float TURN = 30.0F;

    private Looking() {}

    public static void at(EntityLiving mob, Entity target) { mob.getLookHelper().setLookPositionWithEntity(target, TURN, TURN); }

    public static void at(EntityLiving mob, BlockPos pos) { mob.getLookHelper().setLookPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, TURN, TURN); }
}
