package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Unique private static final int rdpl$FULL_BRIGHT = 0xF000F0;

    @Inject(method = "getBrightnessForRender", at = @At("RETURN"), cancellable = true) private void rdpl$fullBright(CallbackInfoReturnable<Integer> cir) {
        if (ContentEntities.bright((Entity) (Object) this)) { cir.setReturnValue(rdpl$FULL_BRIGHT); }
    }
}
