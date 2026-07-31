package mctmods.resourcedatapackloader.content.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public interface IContentShape {
    boolean generate(World world, Random random, BlockPos origin);
}
