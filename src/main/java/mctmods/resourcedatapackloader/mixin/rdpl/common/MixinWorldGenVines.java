package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenVines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenVines.class) public class MixinWorldGenVines {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 128), require = 1) private int rdpl$maxVineY(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMaxHeight(); }
}
