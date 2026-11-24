package mctmods.resourcedatapackloader.common.util.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public final class Looking {
    private static final float TURN = 30.0F;

    private Looking() {}

    public static void at(Mob mob, Entity target) { mob.getLookControl().setLookAt(target, TURN, TURN); }

    public static void at(Mob mob, BlockPos pos) { mob.getLookControl().setLookAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, TURN, TURN); }
}
