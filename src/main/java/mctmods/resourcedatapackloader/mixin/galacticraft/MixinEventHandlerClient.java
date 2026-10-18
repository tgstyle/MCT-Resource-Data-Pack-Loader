package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.BodyRings;

import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.core.client.EventHandlerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EventHandlerClient.class, remap = false) public abstract class MixinEventHandlerClient {
    @Inject(method = "onRenderPlanetPost(Lmicdoodle8/mods/galacticraft/api/event/client/CelestialBodyRenderEvent$Post;)V", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$overriddenRing(CelestialBodyRenderEvent.Post event, CallbackInfo ci) {
        if (BodyRings.overrides(event.celestialBody.getName())) { ci.cancel(); }
    }
}
