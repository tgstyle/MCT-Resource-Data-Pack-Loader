package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenFlowers.class) public class MixinWorldGenFlowers {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 255)) private int rdpl$getMinHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMaxHeight() - 1;
    }
}
