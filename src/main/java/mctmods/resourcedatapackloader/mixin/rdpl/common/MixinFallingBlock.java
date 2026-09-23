package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlock.class) public abstract class MixinFallingBlock {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void rdpl$fallInstantlyWhileLandIsMade(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!ContentPregen.busy()) { return; }
        ContentPregenHold.fallInstantly(level, pos, state);
        ci.cancel();
    }
}
