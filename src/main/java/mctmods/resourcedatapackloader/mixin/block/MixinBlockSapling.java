package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.rules.ContentGrowSwitch;

import java.util.Random;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSapling.class) public class MixinBlockSapling {
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true) private void rdpl$growSteps(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) { if (ContentGrowSwitch.skips(worldIn, pos, state.getBlock(), rand)) { ci.cancel(); } }
}
