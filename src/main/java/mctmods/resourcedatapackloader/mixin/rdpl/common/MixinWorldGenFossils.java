package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFossils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Random;

@Mixin(WorldGenFossils.class) public class MixinWorldGenFossils {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 0, ordinal = 0), require = 1) private int rdpl$boxMinY(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256), require = 2) private int rdpl$boxMaxY(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.ceiling(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 10, ordinal = 1), require = 1) private int rdpl$lowestY(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }
}
