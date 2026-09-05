package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentOverrides;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class) public abstract class MixinBlockStateBase {
    @Shadow public abstract Block getBlock();

    @Inject(method = "getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", at = @At("HEAD"), cancellable = true) private void rdpl$lightBlock(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        Integer held = ContentOverrides.lightBlock(getBlock());
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "getSeed", at = @At("HEAD"), cancellable = true) private void rdpl$seedByGroup(BlockPos pos, CallbackInfoReturnable<Long> cir) {
        if (!ContentHardness.anyRolls()) { return; }
        Long held = ContentHardness.modelSeed((BlockState) (Object) this, pos);
        if (held != null) { cir.setReturnValue(held); }
    }
}
