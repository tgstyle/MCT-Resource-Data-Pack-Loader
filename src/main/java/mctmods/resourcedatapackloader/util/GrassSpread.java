package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentStates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.Random;

public final class GrassSpread {
    private static final int ATTEMPTS = 4;
    private static final int DIE_LIGHT = 4;
    private static final int DIE_OPACITY = 2;
    private static final int SPREAD_LIGHT = 9;

    private GrassSpread() {}

    public static void tick(World world, BlockPos pos, IBlockState state, Random random, List<String> onto, Object owner) {
        if (world.isRemote || onto.isEmpty() || !world.isAreaLoaded(pos, 3)) { return; }
        BlockPos above = pos.up();
        int light = world.getLightFromNeighbors(above);
        if (light < DIE_LIGHT && world.getBlockState(above).getLightOpacity(world, above) > DIE_OPACITY) {
            IBlockState cover = ContentStates.parse(onto.get(0), owner);
            if (cover != null) { world.setBlockState(pos, cover); }
        }
        else if (light >= SPREAD_LIGHT) { spread(world, pos, state, random, onto, owner); }
    }

    private static void spread(World world, BlockPos pos, IBlockState state, Random random, List<String> onto, Object owner) {
        for (int i = 0; i < ATTEMPTS; i++) {
            BlockPos target = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
            if (target.getY() >= 0 && target.getY() < 256 && !world.isBlockLoaded(target)) { return; }
            BlockPos above = target.up();
            if (world.getLightFromNeighbors(above) >= DIE_LIGHT && world.getBlockState(above).getLightOpacity(world, above) <= DIE_OPACITY && accepts(world.getBlockState(target), onto, owner)) {
                world.setBlockState(target, state);
            }
        }
    }

    private static boolean accepts(IBlockState found, List<String> onto, Object owner) {
        for (String name : onto) {
            IBlockState wanted = ContentStates.parse(name, owner);
            if (wanted != null && ContentStates.alike(wanted, found)) { return true; }
        }
        return false;
    }
}
