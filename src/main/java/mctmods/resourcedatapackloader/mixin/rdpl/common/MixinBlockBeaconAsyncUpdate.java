package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.BeaconUpdater;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unresolvable-target") @Mixin(targets = "net.minecraft.block.BlockBeacon$1") public class MixinBlockBeaconAsyncUpdate {
    @Shadow(remap = false, aliases = "field_180358_a") @Final World val$worldIn;
    @Shadow(remap = false, aliases = "field_180357_b") @Final BlockPos val$glassPos;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true) private void runRubic(CallbackInfo ci) {
        if (!((IRubicWorld) val$worldIn).rdpl$isRubicWorld()) { return; }
        ci.cancel();
        BeaconUpdater.run(val$worldIn, val$glassPos);
    }
}
