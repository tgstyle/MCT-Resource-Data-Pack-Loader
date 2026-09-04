package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IWorldEntitySpawner;

import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(WorldEntitySpawner.class) public class MixinWorldEntitySpawner implements IWorldEntitySpawner.IHandler {
    @Unique @Nullable private IWorldEntitySpawner rdpl$customSpawner;

    @Override public void rdpl$setEntitySpawner(@Nullable IWorldEntitySpawner spawner) { this.rdpl$customSpawner = spawner; }

    @Inject(method = "findChunksForSpawning", cancellable = true, at = @At("HEAD")) private void onSpawnMobs(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir) {
        if (this.rdpl$customSpawner != null) {
            int ret = this.rdpl$customSpawner.findChunksForSpawning(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
            cir.setReturnValue(ret);
        }
    }
}
