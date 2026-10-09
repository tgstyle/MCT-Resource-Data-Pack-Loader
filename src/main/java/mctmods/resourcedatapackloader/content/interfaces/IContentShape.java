package mctmods.resourcedatapackloader.content.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.Random;

public interface IContentShape {
    boolean generate(World world, Random random, BlockPos origin);

    default boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return false; }
}
