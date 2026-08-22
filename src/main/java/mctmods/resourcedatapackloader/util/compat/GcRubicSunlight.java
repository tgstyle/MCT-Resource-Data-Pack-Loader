package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GcRubicSunlight {
    private GcRubicSunlight() {}

    private static Block brightAir;
    private static boolean unlooked = true;

    public static int lightValue(IBlockState state, World world, BlockPos pos) {
        if (unlooked) {
            unlooked = false;
            brightAir = Block.getBlockFromName("galacticraftcore:bright_air");
        }
        if (brightAir == null || state.getBlock() != brightAir) { return state.getLightValue(world, pos); }
        IRubicWorld rubic = (IRubicWorld) world;
        int run = 0;
        while (run < 5) {
            int x = pos.getX() - 1 - run;
            ICube cube = rubic.rdpl$getCubeCache().getLoadedCube(Coords.blockToCube(x), Coords.blockToCube(pos.getY()), Coords.blockToCube(pos.getZ()));
            if (cube != null && cube.getStorage() != null) {
                IBlockState held = cube.getStorage().get(x & 15, pos.getY() & 15, pos.getZ() & 15);
                if (held.getMaterial() != Material.AIR) { break; }
            }
            run++;
        }
        return Math.min(4 + run * 2, 14);
    }
}
