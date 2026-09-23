package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFluid.class) public abstract class MixinLavaFluid {
    @Inject(method = "isFlammable(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), cancellable = true, remap = false) @SuppressWarnings("deprecation") private void rdpl$burnsByMaterial(LevelReader p_level, BlockPos p_pos, Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (p_pos.getY() >= p_level.getMinBuildHeight() && p_pos.getY() < p_level.getMaxBuildHeight() && !p_level.hasChunkAt(p_pos)) { return; }
        BlockState state = p_level.getBlockState(p_pos);
        if (ContentRegistry.entry(state.getBlock()) != null) { cir.setReturnValue(state.ignitedByLava()); }
    }
}
