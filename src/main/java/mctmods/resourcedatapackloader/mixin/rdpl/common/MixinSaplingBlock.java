package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentGrowSwitch;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaplingBlock.class) public abstract class MixinSaplingBlock {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true) private void rdpl$growSteps(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) { if (ContentGrowSwitch.skips(level, pos, state.getBlock(), random)) { ci.cancel(); } }
}
