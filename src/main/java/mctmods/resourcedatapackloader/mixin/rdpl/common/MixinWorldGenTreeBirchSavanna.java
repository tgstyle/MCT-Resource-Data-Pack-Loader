package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin({WorldGenBirchTree.class, WorldGenSavannaTree.class}) public class MixinWorldGenTreeBirchSavanna {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 1, ordinal = 1)) private int getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight() + 1;
    }

    @ModifyConstant(method = "generate",
                    constant = @Constant(intValue = 0, ordinal = 1,
                                         expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int getMinGenHeightCompareZero(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256)) private int getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMaxHeight();
    }
}
