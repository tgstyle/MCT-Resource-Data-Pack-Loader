package mctmods.resourcedatapackloader.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.function.Predicate;

public final class TileScan {
    private TileScan() {}

    public static void within(ServerLevel level, BlockPos center, int radius, Predicate<BlockEntity> visit) {
        for (int cx = center.getX() - radius >> 4; cx <= center.getX() + radius >> 4; cx++) {
            for (int cz = center.getZ() - radius >> 4; cz <= center.getZ() + radius >> 4; cz++) {
                if (!level.hasChunk(cx, cz)) { continue; }
                for (BlockEntity tile : level.getChunk(cx, cz).getBlockEntities().values()) {
                    if (!visit.test(tile)) { return; }
                }
            }
        }
    }
}
