package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(BlockStaticLiquid.class) public class MixinBlockStaticLiquid {
    @ModifyConstant(method = "updateTick", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO)) private int rdpl$getMinHeightTick(int zero, World worldIn, BlockPos pos, IBlockState state, Random rand) { return GenHeights.floor(worldIn, zero); }

    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 256)) private int rdpl$getMaxHeightTick(int _256, World worldIn, BlockPos pos, IBlockState state, Random rand) { return GenHeights.ceiling(worldIn, _256); }

    @ModifyConstant(method = "getCanBlockBurn", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO)) private int rdpl$getMinHeightBurn(int zero, World worldIn, BlockPos pos) { return GenHeights.floor(worldIn, zero); }

    @ModifyConstant(method = "getCanBlockBurn", constant = @Constant(intValue = 256)) private int rdpl$getMaxHeightBurn(int _256, World worldIn, BlockPos pos) { return GenHeights.ceiling(worldIn, _256); }
}
