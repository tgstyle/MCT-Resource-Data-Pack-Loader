package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.misc.feature.BlackAsh;

@Mixin(value = BlackAsh.class, remap = false) public abstract class MixinBlackAsh {
    @Inject(method = "onSpawnCheck", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$skipUnloaded(LivingSpawnEvent.CheckSpawn event, CallbackInfo ci) {
        if (!ContentCascade.loaded(event.getWorld(), event.getEntity().getPosition())) { ci.cancel(); }
    }
}
