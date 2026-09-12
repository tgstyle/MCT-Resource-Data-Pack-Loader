package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerBaseLogic.class) public abstract class MixinMobSpawnerBaseLogic {
    @Inject(method = "updateSpawner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilChunkLoader;spawnEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;)V")) private void rdpl$spawnerAdds(CallbackInfo ci) { ContentEntities.fromSpawner(true); }

    @Inject(method = "updateSpawner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilChunkLoader;spawnEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;)V", shift = At.Shift.AFTER)) private void rdpl$spawnerAdded(CallbackInfo ci) { ContentEntities.fromSpawner(false); }
}
