package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldGenCanopyTree.class) public class MixinWorldGenCanopyTree {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 1, ordinal = 0)) private int rdpl$getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256, ordinal = 0)) private int rdpl$getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.ceiling(worldIn, orig); }
}
