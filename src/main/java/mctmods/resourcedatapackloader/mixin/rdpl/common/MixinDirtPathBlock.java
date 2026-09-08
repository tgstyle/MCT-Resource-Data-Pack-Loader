package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirtPathBlock.class) public abstract class MixinDirtPathBlock {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true) private void rdpl$keepPath(BlockState p_221070_, ServerLevel p_221071_, BlockPos p_221072_, RandomSource p_221073_, CallbackInfo ci) {
        if (Config.tweaks.lenientPaths()) { ci.cancel(); }
    }
}
