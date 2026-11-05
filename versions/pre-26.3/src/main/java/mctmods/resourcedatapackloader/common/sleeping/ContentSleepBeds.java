package mctmods.resourcedatapackloader.common.sleeping;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.block.BedBlock;

final class ContentSleepBeds {
    private ContentSleepBeds() {}

    static boolean setsSpawn(ServerLevel level, BlockPos pos) { return level.getBlockState(pos).getBlock() instanceof BedBlock && level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos).canSetSpawn(level); }
}
