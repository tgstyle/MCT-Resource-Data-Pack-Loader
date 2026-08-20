package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenSpikes.class) public class MixinWorldGenSpikes {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 65), require = 1) private int rdpl$clearAboveY(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMinHeight() + orig; }
}
