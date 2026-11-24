package mctmods.resourcedatapackloader.common.entities.ai;

import mctmods.resourcedatapackloader.common.util.entity.Looking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;

final class BlockBreak {
    private static final int SWING = 6;

    private BlockBreak() {}

    static void look(PathfinderMob mob, BlockPos pos) { Looking.at(mob, pos); }

    static void clear(PathfinderMob mob, BlockPos pos) { mob.level().destroyBlockProgress(mob.getId(), pos, -1); }

    static int step(PathfinderMob mob, BlockPos pos, int total, int left) {
        look(mob, pos);
        if ((total - left) % SWING == 0) { mob.swing(InteractionHand.MAIN_HAND); }
        int remaining = left - 1;
        mob.level().destroyBlockProgress(mob.getId(), pos, (int) (9.0F * (total - remaining) / total));
        return remaining;
    }
}
