package mctmods.resourcedatapackloader.mixin.client.world;

import mctmods.resourcedatapackloader.common.environment.ContentClouds;
import mctmods.resourcedatapackloader.common.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class) public abstract class MixinWorldProvider {
    @Shadow protected World world;

    @Inject(method = "getVoidFogYFactor", at = @At(value = "HEAD"), cancellable = true) private void rdpl$getVoidFogYFactor_injectReplace(CallbackInfoReturnable<Double> cir) {
        if (GenHeights.rubic(this.world)) { cir.setReturnValue(Double.NaN); }
    }

    @Inject(method = "getHorizon", at = @At(value = "HEAD"), cancellable = true, remap = false) private void rdpl$getHorizon_injectReplace(CallbackInfoReturnable<Double> cir) {
        if (GenHeights.rubic(this.world)) { cir.setReturnValue((double) GenHeights.floor(this.world, 0)); }
    }

    @Inject(method = "getCloudHeight", at = @At("RETURN"), cancellable = true) private void rdpl$getCloudHeight_fromPackOrTerrainOffset(CallbackInfoReturnable<Float> cir) {
        Integer asked = ContentClouds.heightFor(this.world.provider.getDimension());
        if (asked != null) {
            cir.setReturnValue((float) asked);
            return;
        }
        if (GenHeights.rubic(this.world)) { cir.setReturnValue(cir.getReturnValue() + RubicWorldControl.terrainOffsetBlocks()); }
    }
}
