package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentRegistry;

import net.minecraft.core.BlockPos;
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

    @Inject(method = "getLightDampening()I", at = @At("HEAD"), cancellable = true) private void rdpl$lightBlock(CallbackInfoReturnable<Integer> cir) {
        Integer held = ContentOverrides.lightBlock(getBlock());
        if (held == null) { held = ContentRegistry.lightBlock(getBlock()); }
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "propagatesSkylightDown()Z", at = @At("HEAD"), cancellable = true) private void rdpl$skylight(CallbackInfoReturnable<Boolean> cir) {
        Integer held = ContentRegistry.lightBlock(getBlock());
        if (held != null) { cir.setReturnValue(held == 0); }
    }

    @Inject(method = "getSeed", at = @At("HEAD"), cancellable = true) private void rdpl$seedByGroup(BlockPos pos, CallbackInfoReturnable<Long> cir) {
        if (!ContentHardness.anyRolls()) { return; }
        Long held = ContentHardness.modelSeed(BlockState.class.cast(this), pos);
        if (held != null) { cir.setReturnValue(held); }
    }
}
