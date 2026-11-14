package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraHeights;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class, priority = 900) public abstract class EntityRendererMixin {
    @Inject(method = "shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z", at = @At("HEAD"), cancellable = true) private void rdpl$exitHide(Entity livingEntity, Frustum camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        Rocket rocket = AdAstraHeights.climbing(livingEntity);
        if (rocket == null) { return; }
        if (rocket.getY() > AdAstraHeights.exit(rocket.level())) { cir.setReturnValue(false); } else if (rocket.getY() > AdAstraConfig.atmosphereLeave) { cir.setReturnValue(rdpl$visible(livingEntity, camera, camX, camY, camZ)); }
    }

    @Unique private static boolean rdpl$visible(Entity entity, Frustum frustum, double camX, double camY, double camZ) {
        if (!entity.shouldRender(camX, camY, camZ)) { return false; }
        if (entity.noCulling) { return true; }
        AABB box = entity.getBoundingBoxForCulling().inflate(0.5);
        if (box.hasNaN() || box.getSize() == 0.0) { box = new AABB(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0); }
        return frustum.isVisible(box);
    }
}
