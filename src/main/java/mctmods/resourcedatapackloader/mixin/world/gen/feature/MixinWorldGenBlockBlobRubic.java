package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenBlockBlob.class) public class MixinWorldGenBlockBlobRubic {
    @Unique private int rdpl$minY;

    @Inject(method = "generate", at = @At("HEAD")) private void rdpl$onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) { this.rdpl$minY = Coords.getMinCubePopulationPos(position.getY()) - 1; }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 3, ordinal = 0)) private int rdpl$getMinGenHeight0(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.rubicOr(worldIn, rdpl$minY, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 3, ordinal = 1)) private int rdpl$getMinGenHeight1(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.rubicOr(worldIn, rdpl$minY, orig); }
}
