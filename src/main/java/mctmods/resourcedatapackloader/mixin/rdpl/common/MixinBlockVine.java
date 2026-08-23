package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(BlockVine.class) public class MixinBlockVine {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 255)) private int rdpl$spreadUpCap(int orig, World worldIn, BlockPos pos, IBlockState state, Random rand) {
        IRubicWorld rubic = (IRubicWorld) worldIn;
        return rubic.rdpl$isRubicWorld() ? rubic.rdpl$getMaxHeight() - 1 : orig;
    }

    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 1, ordinal = 1)) private int rdpl$spreadDownFloor(int orig, World worldIn, BlockPos pos, IBlockState state, Random rand) {
        IRubicWorld rubic = (IRubicWorld) worldIn;
        return rubic.rdpl$isRubicWorld() ? rubic.rdpl$getMinHeight() + orig : orig;
    }
}
