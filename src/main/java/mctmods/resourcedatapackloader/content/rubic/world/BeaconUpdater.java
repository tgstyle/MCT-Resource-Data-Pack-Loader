package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldServer;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class BeaconUpdater {
    private BeaconUpdater() {}

    public static void run(World world, BlockPos glassPos) {
        final int blockX = glassPos.getX();
        final int blockZ = glassPos.getZ();
        int blockY = glassPos.getY();
        final int cubeX = Coords.blockToCube(blockX);
        final int cubeZ = Coords.blockToCube(blockZ);
        int cubeY = Coords.blockToCube(glassPos.getY());
        ICubeProviderServer cubeProvider = ((IRubicWorldServer) world).rdpl$getCubeCache();
        ICube cube = cubeProvider.getCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.GET_CACHED);
        while (cube != null) {
            final BlockPos blockpos = new BlockPos(blockX, blockY, blockZ);
            if (!cube.getColumn().canSeeSky(blockpos)) { break; }
            IBlockState block = cube.getBlockState(blockpos);
            if (block.getBlock() == Blocks.BEACON) {
                ((WorldServer) world).addScheduledTask(() -> {
                    TileEntity tileentity = world.getTileEntity(blockpos);
                    if (tileentity instanceof TileEntityBeacon) {
                        ((TileEntityBeacon) tileentity).updateBeacon();
                        world.addBlockEvent(blockpos, Blocks.BEACON, 1, 0);
                    }
                });
            }
            blockY--;
            cubeY = Coords.blockToCube(blockY);
            cube = cubeProvider.getCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.GET_CACHED);
        }
    }
}
