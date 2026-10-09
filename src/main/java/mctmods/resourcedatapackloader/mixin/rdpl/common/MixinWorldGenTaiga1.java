package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenTaiga1.class) public class MixinWorldGenTaiga1 {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 1, ordinal = 2)) private int rdpl$getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 0, ordinal = 1, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO)) private int rdpl$getMinGenHeightCompareZero(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256)) private int rdpl$getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.ceiling(worldIn, orig); }
}
