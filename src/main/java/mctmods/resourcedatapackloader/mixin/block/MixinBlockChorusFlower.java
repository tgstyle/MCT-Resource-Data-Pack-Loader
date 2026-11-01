package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.Random;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockChorusFlower.class) public class MixinBlockChorusFlower {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 256)) private int rdpl$updateTick(int maxY, World worldIn, BlockPos pos, IBlockState state, Random rand) { return GenHeights.ceiling(worldIn, maxY); }
}
