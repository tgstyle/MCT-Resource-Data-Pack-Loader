package mctmods.resourcedatapackloader.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import java.util.function.Predicate;

public final class TileScan {
    private TileScan() {}

    public static void within(World world, BlockPos center, int radius, Predicate<TileEntity> visit) {
        for (int cx = center.getX() - radius >> 4; cx <= center.getX() + radius >> 4; cx++) {
            for (int cz = center.getZ() - radius >> 4; cz <= center.getZ() + radius >> 4; cz++) {
                if (!world.isBlockLoaded(new BlockPos(cx << 4, 0, cz << 4))) { continue; }
                Chunk chunk = world.getChunk(cx, cz);
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    if (!visit.test(tile)) { return; }
                }
            }
        }
    }
}
