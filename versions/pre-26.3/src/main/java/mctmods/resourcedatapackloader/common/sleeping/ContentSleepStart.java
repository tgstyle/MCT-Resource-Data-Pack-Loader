package mctmods.resourcedatapackloader.common.sleeping;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

final class ContentSleepStart {
    private ContentSleepStart() {}

    static void start(ServerPlayer player, BlockPos target) { player.startSleeping(target); }
}
