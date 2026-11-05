package mctmods.resourcedatapackloader.common.util.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class SpawnPoints {
    private SpawnPoints() {}

    @Nullable public static BlockPos working(EntityPlayer player, World world, @Nullable BlockPos bed, int dimension) {
        if (bed == null) { return null; }
        return EntityPlayer.getBedSpawnLocation(world, bed, player.isSpawnForced(dimension));
    }
}
