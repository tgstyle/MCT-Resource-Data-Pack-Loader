package mctmods.resourcedatapackloader.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

final class ContentSleepStart {
    private ContentSleepStart() {}

    static void start(ServerPlayer player, BlockPos target) {
        if (player.isPassenger()) { player.stopRiding(); }
        player.setPose(Pose.SLEEPING);
        player.setPos(target.getX() + 0.5D, target.getY() + 0.05D, target.getZ() + 0.5D);
        player.setSleepingPos(target);
        player.setDeltaMovement(Vec3.ZERO);
        player.needsSync = true;
    }
}
