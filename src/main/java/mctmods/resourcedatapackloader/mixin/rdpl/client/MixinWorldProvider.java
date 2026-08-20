package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class) public abstract class MixinWorldProvider {
    @Shadow protected World world;

    @Inject(method = "getVoidFogYFactor", at = @At(value = "HEAD"), cancellable = true) private void getVoidFogYFactor_injectReplace(CallbackInfoReturnable<Double> cir) {
        if (rdpl$rubicWorld().rdpl$isRubicWorld()) { cir.setReturnValue(Double.NaN); }
    }

    @Unique private IRubicWorld rdpl$rubicWorld() { return (IRubicWorld) this.world; }
}
