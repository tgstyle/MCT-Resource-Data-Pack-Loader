package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.Random;

@Mixin(WorldGenBigMushroom.class) public class MixinWorldGenBigMushroom {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 1, ordinal = 1)) private int getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight() + 1;
    }

    @ModifyConstant(method = "generate",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getZ()I", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;setPos(III)"
                            + "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;", ordinal = 0)
            ), require = 1)
    private int getMinGenHeightCompareZero(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256)) private int getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMaxHeight();
    }
}
