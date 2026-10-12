package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentStates;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public final class GrassSpread {
    private static final int ATTEMPTS = 4;
    private static final int DIE_LIGHT = 4;
    private static final int DIE_OPACITY = 2;
    private static final int SPREAD_LIGHT = 9;

    private GrassSpread() {}

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, RandomSource random, List<String> onto, Object owner) {
        if (onto.isEmpty() || !level.isAreaLoaded(pos, 3)) { return; }
        BlockPos above = pos.above();
        int light = level.getMaxLocalRawBrightness(above);
        if (light < DIE_LIGHT && level.getBlockState(above).getLightDampening() > DIE_OPACITY) {
            BlockState cover = ContentStates.known(onto.getFirst(), owner);
            if (cover != null) { level.setBlockAndUpdate(pos, cover); }
        }
        else if (light >= SPREAD_LIGHT) { spread(level, pos, state, random, onto, owner); }
    }

    private static void spread(ServerLevel level, BlockPos pos, BlockState state, RandomSource random, List<String> onto, Object owner) {
        for (int i = 0; i < ATTEMPTS; i++) {
            BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
            if (!level.isLoaded(target)) { return; }
            BlockPos above = target.above();
            if (level.getMaxLocalRawBrightness(above) >= DIE_LIGHT && level.getBlockState(above).getLightDampening() <= DIE_OPACITY && accepts(level.getBlockState(target), onto, owner)) {
                level.setBlockAndUpdate(target, state);
            }
        }
    }

    private static boolean accepts(BlockState found, List<String> onto, Object owner) {
        for (String name : onto) {
            BlockState wanted = ContentStates.known(name, owner);
            if (wanted != null && ContentStates.alike(wanted, found)) { return true; }
        }
        return false;
    }
}
