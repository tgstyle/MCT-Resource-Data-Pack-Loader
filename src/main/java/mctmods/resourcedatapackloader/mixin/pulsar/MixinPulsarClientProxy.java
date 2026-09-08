package mctmods.resourcedatapackloader.mixin.pulsar;

import mctmods.resourcedatapackloader.content.rubic.lighting.PulsarStandDown;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo @Mixin(targets = "com.sumirelabs.pulsar.proxy.ClientProxy", remap = false) public abstract class MixinPulsarClientProxy {
    @Dynamic @Inject(method = "isRealMainWorld", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void rdpl$leaveRubicAlone(World world, CallbackInfoReturnable<Boolean> cir) {
        if (PulsarStandDown.holds(world)) { cir.setReturnValue(false); }
    }
}
