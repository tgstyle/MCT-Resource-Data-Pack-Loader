package mctmods.resourcedatapackloader.mixin.world.level.block;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.PathBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathBlock.class) public abstract class DirtPathBlockMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true) private void rdpl$keepPath(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) { if (Config.tweaks.lenientPaths()) { ci.cancel(); } }
}
