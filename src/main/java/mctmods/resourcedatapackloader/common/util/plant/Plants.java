package mctmods.resourcedatapackloader.common.util.plant;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class Plants {
    private static final BlockMatch TREE_SAPLING = BlockMatch.group("treeSapling");

    private Plants() {}

    public static boolean sapling(Block block) { return block instanceof BlockSapling || block instanceof ContentSaplingBlock; }

    public static boolean treeSapling(Block block, World world, BlockPos pos) { return sapling(block) || TREE_SAPLING.matches(block, world, pos) || DynamicTrees.isSapling(block); }

    public static boolean crop(Block block) { return block instanceof BlockCrops || block instanceof BlockStem; }
}
