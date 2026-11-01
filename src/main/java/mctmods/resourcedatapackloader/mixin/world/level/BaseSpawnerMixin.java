package mctmods.resourcedatapackloader.mixin.world.level;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class) public abstract class BaseSpawnerMixin {
    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z")) private void rdpl$spawnerAdds(ServerLevel level, BlockPos pos, CallbackInfo ci) { ContentEntities.fromSpawner(true); }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.AFTER)) private void rdpl$spawnerAdded(ServerLevel level, BlockPos pos, CallbackInfo ci) { ContentEntities.fromSpawner(false); }
}
