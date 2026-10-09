package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldGenHugeTrees.class) public class MixinWorldGenHugeTrees {
    @ModifyConstant(method = "isSpaceAt", constant = @Constant(intValue = 1, ordinal = 1)) private int rdpl$isSpace_getMinHeight(int val, World worldIn, BlockPos leavesPos, int height) { return GenHeights.floor(worldIn, val); }

    @ModifyConstant(method = "isSpaceAt", constant = @Constant(intValue = 256)) private int rdpl$isSpace_getMaxHeight(int val, World worldIn, BlockPos leavesPos, int height) { return GenHeights.ceiling(worldIn, val); }

    @ModifyConstant(method = "isSpaceAt", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.LESS_THAN_ZERO, ordinal = 1)) private int rdpl$getMinScanHeight(int orig, World worldIn, BlockPos leavesPos, int height) { return GenHeights.floor(worldIn, orig); }
}
