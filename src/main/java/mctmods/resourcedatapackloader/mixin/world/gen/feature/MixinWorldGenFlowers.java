package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldGenFlowers.class) public class MixinWorldGenFlowers {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 255)) private int rdpl$getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.ceiling(worldIn, orig + 1) - 1; }
}
