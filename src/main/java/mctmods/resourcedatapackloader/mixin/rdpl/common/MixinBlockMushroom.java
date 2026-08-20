package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.BlockMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockMushroom.class) public class MixinBlockMushroom {
    @ModifyConstant(method = "canBlockStay",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int rdpl$getMinHeight(int zero, World worldIn, BlockPos pos, IBlockState state) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }

    @ModifyConstant(method = "canBlockStay",
            constant = @Constant(intValue = 256))
    private int rdpl$getMaxHeight(int _256, World worldIn, BlockPos pos, IBlockState state) { return ((IRubicWorld) worldIn).rdpl$getMaxHeight(); }
}
