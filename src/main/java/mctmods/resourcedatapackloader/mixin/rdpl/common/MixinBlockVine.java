package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.Random;

@Mixin(BlockVine.class) public class MixinBlockVine {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 255)) private int rdpl$spreadUpCap(int orig, World worldIn, BlockPos pos, IBlockState state, Random rand) {
        return GenHeights.ceiling(worldIn, orig + 1) - 1;
    }

    @ModifyConstant(method = "updateTick",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I", ordinal = 1)),
            constant = @Constant(intValue = 1, ordinal = 0)) private int rdpl$spreadDownFloor(int orig, World worldIn, BlockPos pos, IBlockState state, Random rand) {
        return GenHeights.floor(worldIn, orig);
    }
}
