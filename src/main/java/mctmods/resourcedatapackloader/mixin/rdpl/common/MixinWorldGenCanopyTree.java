package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenCanopyTree.class) public class MixinWorldGenCanopyTree {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 1, ordinal = 0)) private int getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight() + 1;
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256, ordinal = 0)) private int getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMaxHeight();
    }
}
