package mctmods.resourcedatapackloader.content.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;

final class BlockBreak {
    private static final int SWING = 6;

    private BlockBreak() {}

    static void look(PathfinderMob mob, BlockPos pos) { mob.getLookControl().setLookAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 30.0F, 30.0F); }

    static int step(PathfinderMob mob, BlockPos pos, int total, int left) {
        look(mob, pos);
        if ((total - left) % SWING == 0) { mob.swing(InteractionHand.MAIN_HAND); }
        int remaining = left - 1;
        mob.level().destroyBlockProgress(mob.getId(), pos, (int) (9.0F * (total - remaining) / total));
        return remaining;
    }
}
