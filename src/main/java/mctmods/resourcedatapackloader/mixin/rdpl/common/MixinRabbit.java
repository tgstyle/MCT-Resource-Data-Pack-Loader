package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Rabbit.class) public abstract class MixinRabbit {
    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void rdpl$walks(CallbackInfo ci) {
        if (ContentEntities.walks(Rabbit.class.cast(this))) { ci.cancel(); }
    }
}
