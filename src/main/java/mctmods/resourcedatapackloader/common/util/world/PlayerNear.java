package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public final class PlayerNear {
    private PlayerNear() {}

    public static boolean within(ServerLevel level, AABB box, int distance) {
        double limit = (double) distance * distance;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) { continue; }
            double dx = player.getX() - Mth.clamp(player.getX(), box.minX, box.maxX);
            double dy = player.getY() - Mth.clamp(player.getY(), box.minY, box.maxY);
            double dz = player.getZ() - Mth.clamp(player.getZ(), box.minZ, box.maxZ);
            if (dx * dx + dy * dy + dz * dz <= limit) { return true; }
        }
        return false;
    }
}
