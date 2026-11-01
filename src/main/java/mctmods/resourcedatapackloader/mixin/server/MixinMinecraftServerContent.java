package mctmods.resourcedatapackloader.mixin.server;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServerContent {
    @Redirect(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z")) private boolean rdpl$holdTheSkyStill(GameRules rules, String name) {
        if ("doDaylightCycle".equals(name)) {
            for (WorldServer world : ((MinecraftServer) (Object) this).worlds) {
                if (ContentPregen.busyIn(world)) { return false; }
            }
        }
        return rules.getBoolean(name);
    }

    @Inject(method = "getAllowNether", at = @At("HEAD"), cancellable = true) private void rdpl$nether(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.nether())) { cir.setReturnValue(false); } }

    @Inject(method = "allowSpawnMonsters", at = @At("HEAD"), cancellable = true) private void rdpl$spawnMonsters(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.spawnMonsters())) { cir.setReturnValue(false); } }

    @Inject(method = "getMaxWorldSize", at = @At("HEAD"), cancellable = true) private void rdpl$maxSize(CallbackInfoReturnable<Integer> cir) {
        int asked = ContentServer.maxSize();
        if (asked > 0) { cir.setReturnValue(Math.min(asked, 29999984)); }
    }
}
