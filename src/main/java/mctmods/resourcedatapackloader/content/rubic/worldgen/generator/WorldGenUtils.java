package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import java.util.Random;

public class WorldGenUtils {
    public static IBlockState getRandomBedrockReplacement(World world, Random rand, IBlockState state, int blockY,
            int bedrockLevels, boolean topBedrock, boolean bottomBedrock) {
        if (bottomBedrock) {
            int heightAboveBottom = blockY - ((IMinMaxHeight) world).rdpl$getMinHeight();
            if (heightAboveBottom < bedrockLevels) {
                int bedrockChance = Math.max(1, heightAboveBottom + 1);
                if (rand.nextInt(bedrockChance) == 0) { return Blocks.BEDROCK.getDefaultState(); }
            }
        }
        if (topBedrock) {
            int heightBelowTop =  ((IMinMaxHeight) world).rdpl$getMaxHeight() - blockY - 1;
            if (heightBelowTop < bedrockLevels) {
                int bedrockChance = Math.max(1, heightBelowTop + 1);
                if (rand.nextInt(bedrockChance) == 0) { return Blocks.BEDROCK.getDefaultState(); }
            }
        }
        return state;
    }
}
