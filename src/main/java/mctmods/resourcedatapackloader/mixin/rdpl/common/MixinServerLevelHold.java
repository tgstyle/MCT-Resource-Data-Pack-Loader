package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class) public abstract class MixinServerLevelHold {
    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true) private void rdpl$standStillWhileLandIsMade(Entity p_entity, CallbackInfo ci) { if (ContentPregen.busy() || ContentEntityTicks.frozen(p_entity)) { ci.cancel(); } }

    @Redirect(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V")) private void rdpl$slowDistantProjectile(Entity entity) {
        if (entity instanceof Projectile && ContentEntityTicks.slowedNow(entity)) { return; }
        entity.tick();
    }

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true) private void rdpl$holdWeatherWhileLandIsMade(CallbackInfo ci) { if (ContentPregen.busyIn((ServerLevel) (Object) this)) { ci.cancel(); } }

    @Redirect(method = {"tickTime()V", "tick(Ljava/util/function/BooleanSupplier;)V"}, require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WritableLevelData;getDayTime()J")) private long rdpl$advanceFromLockedTime(WritableLevelData data) {
        long locked = ContentTerrain.lockedTime((ServerLevel) (Object) this);
        return locked >= 0 ? locked : data.getDayTime();
    }
}
