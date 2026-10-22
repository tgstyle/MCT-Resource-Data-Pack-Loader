package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.interfaces.IPathKindMemo;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwimNodeEvaluator.class) public abstract class MixinSwimNodeEvaluator {
    @Inject(method = "getCachedBlockType", at = @At("HEAD"), cancellable = true) private void rdpl$sharedKind(int x, int y, int z, CallbackInfoReturnable<BlockPathTypes> cir) {
        BlockPathTypes held = ((IPathKindMemo) this).rdpl$known(x, y, z);
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "getCachedBlockType", at = @At("RETURN")) private void rdpl$rememberKind(int x, int y, int z, CallbackInfoReturnable<BlockPathTypes> cir) { ((IPathKindMemo) this).rdpl$remember(x, y, z, cir.getReturnValue()); }
}
