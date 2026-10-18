package mctmods.resourcedatapackloader.mixin.galaxyspace;

import mctmods.resourcedatapackloader.util.BodyRings;

import galaxyspace.systems.BarnardsSystem.core.events.BRClientEventHandler;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BRClientEventHandler.class, remap = false) public abstract class MixinBRClientEventHandler {
    @Inject(method = "onRenderPlanetPost(Lmicdoodle8/mods/galacticraft/api/event/client/CelestialBodyRenderEvent$Post;)V", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$overriddenRing(CelestialBodyRenderEvent.Post event, CallbackInfo ci) {
        if (BodyRings.overrides(event.celestialBody.getName())) { ci.cancel(); }
    }
}
