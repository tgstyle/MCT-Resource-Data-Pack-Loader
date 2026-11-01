package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.rubic.lighting.interfaces.ILightingManager;
import mctmods.resourcedatapackloader.common.rubic.world.ClientHeightMap;
import mctmods.resourcedatapackloader.common.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class CubeBlockChangeMessageClient {
    private CubeBlockChangeMessageClient() {}

    public static void apply(World world, CubeProviderClient cubeCache, CubePos cubePos, short[] localAddresses, IBlockState[] blockStates, int[] heightAddresses, int[] heightValues) {
        WorldClient worldClient = (WorldClient) world;
        Cube cube = cubeCache.getCube(cubePos);
        if (cube instanceof BlankCube) {
            ContentLog.LOGGER.error("Ignored block update to blank cube {}", cubePos);
            return;
        }

        for (int i = 0; i < localAddresses.length; i++) {
            BlockPos pos = cube.localAddressToBlockPos(localAddresses[i]);
            worldClient.invalidateBlockReceiveRegion(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
            worldClient.setBlockState(pos, blockStates[i], 3);
        }

        ClientHeightMap index = (ClientHeightMap) cube.getColumn().getOpacityIndex();
        ILightingManager lm = ((IRubicWorldInternal.IClient) world).rdpl$getLightingManager();
        for (int h = 0; h < heightValues.length; h++) {
            int x = heightAddresses[h] & 0xF;
            int z = (heightAddresses[h] >> 4) & 0xF;
            int height = heightValues[h];
            int oldHeight = index.getTopBlockY(x, z);
            index.setHeight(x, z, height);
            if (oldHeight != height) { lm.updateLightBetween(cube.getColumn(), x, oldHeight, height, z); }
        }
        cube.getTileEntityMap().values().forEach(TileEntity::updateContainingBlockInfo);
    }
}
