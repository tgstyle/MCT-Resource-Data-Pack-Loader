package mctmods.resourcedatapackloader.mixin.world.level.block.state;

import mctmods.resourcedatapackloader.common.blocks.ContentOverrides;
import mctmods.resourcedatapackloader.common.hardness.ContentHardness;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class) public abstract class BlockStateBaseMixin {
    @Shadow public abstract Block getBlock();

    @Inject(method = "getLightDampening()I", at = @At("HEAD"), cancellable = true) private void rdpl$lightBlock(CallbackInfoReturnable<Integer> cir) {
        Integer held = rdpl$heldLight();
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "propagatesSkylightDown()Z", at = @At("HEAD"), cancellable = true) private void rdpl$skylight(CallbackInfoReturnable<Boolean> cir) {
        Integer held = rdpl$heldLight();
        if (held != null) { cir.setReturnValue(held == 0); }
    }

    @Unique @Nullable private Integer rdpl$heldLight() {
        Integer held = ContentOverrides.lightBlock(getBlock());
        return held == null ? ContentRegistry.lightBlock(getBlock()) : held;
    }

    @SuppressWarnings("ConstantValue") @Inject(method = "getSeed", at = @At("HEAD"), cancellable = true) private void rdpl$seedByGroup(BlockPos pos, CallbackInfoReturnable<Long> cir) {
        if (!ContentHardness.anyRolls() || !((Object) this instanceof BlockState state)) { return; }
        Long held = ContentHardness.modelSeed(state, pos);
        if (held != null) { cir.setReturnValue(held); }
    }
}
