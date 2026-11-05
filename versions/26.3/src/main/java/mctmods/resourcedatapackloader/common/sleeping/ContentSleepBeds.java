package mctmods.resourcedatapackloader.common.sleeping;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AbstractBedBlock;

final class ContentSleepBeds {
    private ContentSleepBeds() {}

    static boolean setsSpawn(ServerLevel level, BlockPos pos) { return level.getBlockState(pos).getBlock() instanceof AbstractBedBlock bed && bed.getBedRule(level, pos).canSetSpawn(level); }
}
