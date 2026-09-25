package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServerRules {
    @Inject(method = "isLevelEnabled", at = @At("HEAD"), cancellable = true) private void rdpl$nether(Level level, CallbackInfoReturnable<Boolean> cir) {
        if (level.dimension() == Level.NETHER && Boolean.FALSE.equals(ContentServer.nether())) { cir.setReturnValue(false); }
    }

    @Inject(method = "isSpawningMonsters", at = @At("HEAD"), cancellable = true) private void rdpl$spawnMonsters(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentServer.spawnMonsters())) { cir.setReturnValue(false); }
    }

    @Inject(method = "isSpawningAnimals", at = @At("HEAD"), cancellable = true) private void rdpl$spawnAnimals(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentServer.spawnAnimals())) { cir.setReturnValue(false); }
    }

    @Inject(method = "areNpcsEnabled", at = @At("HEAD"), cancellable = true) private void rdpl$spawnNpcs(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentServer.spawnNpcs())) { cir.setReturnValue(false); }
    }

    @Inject(method = "getAbsoluteMaxWorldSize", at = @At("HEAD"), cancellable = true) private void rdpl$maxSize(CallbackInfoReturnable<Integer> cir) {
        int asked = ContentServer.maxSize();
        if (asked > 0) { cir.setReturnValue(Math.min(asked, 29999984)); }
    }
}
