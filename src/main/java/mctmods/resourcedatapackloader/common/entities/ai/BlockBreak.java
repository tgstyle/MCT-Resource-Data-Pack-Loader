package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Looking;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

final class BlockBreak {
    private static final int SWING = 6;

    private BlockBreak() {}

    static void look(EntityCreature mob, BlockPos pos) { Looking.at(mob, pos); }

    static void clear(EntityCreature mob, BlockPos pos) { mob.world.sendBlockBreakProgress(mob.getEntityId(), pos, -1); }

    static int step(EntityCreature mob, BlockPos pos, int total, int left) {
        look(mob, pos);
        if ((total - left) % SWING == 0) { mob.swingArm(EnumHand.MAIN_HAND); }
        int remaining = left - 1;
        mob.world.sendBlockBreakProgress(mob.getEntityId(), pos, (int) (9.0F * (total - remaining) / total));
        return remaining;
    }
}
