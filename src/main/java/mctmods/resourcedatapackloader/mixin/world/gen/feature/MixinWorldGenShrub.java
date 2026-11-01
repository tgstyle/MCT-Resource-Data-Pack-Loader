package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenShrub;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldGenShrub.class) public class MixinWorldGenShrub {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO, ordinal = 0)) private int rdpl$getMinScanHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.rubic(worldIn) ? Coords.getMinCubePopulationPos(position.getY()) : orig; }
}
