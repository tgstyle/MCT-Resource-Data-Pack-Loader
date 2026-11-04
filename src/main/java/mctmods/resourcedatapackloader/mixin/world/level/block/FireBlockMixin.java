package mctmods.resourcedatapackloader.mixin.world.level.block;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class) public abstract class FireBlockMixin {
    @Inject(method = "tryCatchFire(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;ILnet/minecraft/core/Direction;)V", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$guardCatchFire(Level p_53432_, BlockPos p_53433_, int p_53434_, RandomSource p_53435_, int p_53436_, Direction face, CallbackInfo ci) {
        if (!ClaimGuard.fireMaySpread(p_53432_, p_53433_.relative(face), p_53433_)) { ci.cancel(); }
    }

    @Redirect(method = "tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1)) private boolean rdpl$guardSpread(ServerLevel serverLevel, BlockPos target, BlockState newState, int flags, BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        return ClaimGuard.fireMaySpread(serverLevel, pos, target) && serverLevel.setBlock(target, newState, flags);
    }
}
