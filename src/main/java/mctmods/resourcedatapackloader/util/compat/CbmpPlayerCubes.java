package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import javax.annotation.Nullable;

public final class CbmpPlayerCubes {
    @Nullable private static EntityPlayerMP current;

    private CbmpPlayerCubes() {}

    public static void begin(@Nullable EntityPlayerMP player) { current = player; }

    public static boolean watching(BlockPos pos) {
        EntityPlayerMP player = current;
        if (player == null) { return true; }
        WorldServer world = player.getServerWorld();
        if (!CbmpRubicParts.rubic(world)) { return true; }
        CubePos cube = CubePos.fromBlockCoords(pos);
        return ((PlayerCubeMap) world.getPlayerChunkMap()).isPlayerWatchingCube(player, cube.getX(), cube.getY(), cube.getZ());
    }
}
