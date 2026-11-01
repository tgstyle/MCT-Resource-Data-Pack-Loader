package mctmods.resourcedatapackloader.mixin.world.entity.animal;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Rabbit.class) public abstract class RabbitMixin {
    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true) private void rdpl$walks(CallbackInfo ci) { if (ContentEntities.walks((Rabbit) (Object) this)) { ci.cancel(); } }
}
