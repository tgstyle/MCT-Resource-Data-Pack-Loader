package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentGrowSwitch;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(BlockSapling.class) public class MixinBlockSapling {
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true) private void rdpl$growSteps(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) { if (ContentGrowSwitch.skips(worldIn, pos, state.getBlock(), rand)) { ci.cancel(); } }
}
