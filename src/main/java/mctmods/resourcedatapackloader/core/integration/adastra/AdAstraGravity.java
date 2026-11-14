package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.environment.ContentPhysics;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import net.minecraft.world.entity.projectile.ThrowableProjectile;

final class AdAstraGravity {
    private AdAstraGravity() {}

    static void register() {
        AdAstraEvents.EntityGravityEvent.register((entity, gravity) -> entity instanceof ThrowableProjectile ? ContentPhysics.scaledFall(entity, gravity) : gravity);
    }
}
