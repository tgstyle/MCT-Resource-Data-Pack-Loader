package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(BlockStaticLiquid.class) public class MixinBlockStaticLiquid {
    @ModifyConstant(method = "updateTick",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int getMinHeightTick(int zero, World worldIn, BlockPos pos, IBlockState state, Random rand) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }

    @ModifyConstant(method = "updateTick",
            constant = @Constant(intValue = 256))
    private int getMaxHeightTick(int _256, World worldIn, BlockPos pos, IBlockState state, Random rand) { return ((IRubicWorld) worldIn).rdpl$getMaxHeight(); }

    @ModifyConstant(method = "getCanBlockBurn",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int getMinHeightBurn(int zero, World worldIn, BlockPos pos) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }

    @ModifyConstant(method = "getCanBlockBurn",
            constant = @Constant(intValue = 256))
    private int getMaxHeightBurn(int _256, World worldIn, BlockPos pos) { return ((IRubicWorld) worldIn).rdpl$getMaxHeight(); }
}
