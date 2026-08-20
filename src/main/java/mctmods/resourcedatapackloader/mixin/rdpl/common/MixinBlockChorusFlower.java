package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(BlockChorusFlower.class) public class MixinBlockChorusFlower {
    @ModifyConstant(method = "updateTick",
            constant = @Constant(intValue = 256))
    private int updateTick(int maxY, World worldIn, BlockPos pos, IBlockState state, Random rand) { return ((IRubicWorld) worldIn).rdpl$getMaxHeight(); }
}
