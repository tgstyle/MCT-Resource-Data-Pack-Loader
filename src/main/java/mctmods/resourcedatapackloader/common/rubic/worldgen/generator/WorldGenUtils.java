package mctmods.resourcedatapackloader.common.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IMinMaxHeight;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenUtils {
    private static final int BEDROCK_LEVELS = 5;

    public static IBlockState getRandomBedrockReplacement(World world, Random rand, IBlockState state, int blockY,
            boolean topBedrock, boolean bottomBedrock) {
        if (bottomBedrock && rollsBedrock(rand, blockY - ((IMinMaxHeight) world).rdpl$getMinHeight())) { return Blocks.BEDROCK.getDefaultState(); }
        if (topBedrock && rollsBedrock(rand, ((IMinMaxHeight) world).rdpl$getMaxHeight() - blockY - 1)) { return Blocks.BEDROCK.getDefaultState(); }
        return state;
    }

    private static boolean rollsBedrock(Random rand, int distance) { return distance < BEDROCK_LEVELS && rand.nextInt(Math.max(1, distance + 1)) == 0; }
}
