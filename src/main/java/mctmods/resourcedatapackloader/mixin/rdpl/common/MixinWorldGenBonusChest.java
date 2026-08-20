package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGeneratorBonusChest.class) public class MixinWorldGenBonusChest {
    @ModifyConstant(method = "generate", constant = {@Constant(intValue = 1, ordinal = 0), @Constant(intValue = 1, ordinal = 1)}, require = 2)
    private int rdpl$lowestY(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMinHeight() + orig; }
}
