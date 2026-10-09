package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenDoublePlant.class) public class MixinWorldGenDoublePlants {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 254)) private int rdpl$getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.ceiling(worldIn, orig + 2) - 2; }
}
